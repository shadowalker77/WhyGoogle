package ir.ayantech.whygoogle.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.util.Pools.SynchronizedPool;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * <p>Helper class for inflating layouts asynchronously. To use, construct
 * an instance of {@link AsyncLayoutInflater} on the UI thread and call
 * {@link #inflate(int, ViewGroup, AsyncLayoutInflater.OnInflateFinishedListener)}. The
 * {@link AsyncLayoutInflater.OnInflateFinishedListener} will be invoked on the UI thread
 * when the inflate request has completed.
 *
 * <p>This is intended for parts of the UI that are created lazily or in
 * response to user interactions. This allows the UI thread to continue
 * to be responsive & animate while the relatively heavy inflate
 * is being performed.
 *
 * <p>For a layout to be inflated asynchronously it needs to have a parent
 * whose {@link ViewGroup#generateLayoutParams(AttributeSet)} is thread-safe
 * and all the Views being constructed as part of inflation must not create
 * any {@link Handler}s or otherwise call {@link Looper#myLooper()}. If the
 * layout that is trying to be inflated cannot be constructed
 * asynchronously for whatever reason, {@link AsyncLayoutInflater} will
 * automatically fall back to inflating on the UI thread.
 *
 * <p>NOTE that the inflated View hierarchy is NOT added to the parent. It is
 * equivalent to calling {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
 * with attachToRoot set to false. Callers will likely want to call
 * {@link ViewGroup#addView(View)} in the {@link AsyncLayoutInflater.OnInflateFinishedListener}
 * callback at a minimum.
 *
 * <p>This inflater does not support setting a {@link LayoutInflater.Factory}
 * nor {@link LayoutInflater.Factory2}. Similarly it does not support inflating
 * layouts that contain fragments.
 */
public final class AsyncLayoutInflater {
    private static final String TAG = "AsyncLayoutInflater";

    LayoutInflater mInflater;
    Handler mHandler;
    InflateThread mInflateThread;

    public AsyncLayoutInflater(@NonNull Context context) {
        mInflater = new AsyncLayoutInflater.BasicInflater(context);
        mHandler = new Handler(mHandlerCallback);
        mInflateThread = AsyncLayoutInflater.InflateThread.getInstance();
    }

    @UiThread
    public void inflate(@LayoutRes int resid, @Nullable ViewGroup parent,
                        @NonNull AsyncLayoutInflater.OnInflateFinishedListener callback) {
        if (callback == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        AsyncLayoutInflater.InflateRequest request = mInflateThread.obtainRequest();
        request.inflater = this;
        request.resid = resid;
        request.parent = parent;
        request.callback = callback;
        mInflateThread.enqueue(request);
    }

    private Callback mHandlerCallback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            InflateRequest request = (AsyncLayoutInflater.InflateRequest) msg.obj;
            if (request.view == null) {
                request.view = mInflater.inflate(
                        request.resid, request.parent, false);
            }
            request.callback.onInflateFinished(
                    request.view, request.resid, request.parent);
            mInflateThread.releaseRequest(request);
            return true;
        }
    };

    public interface OnInflateFinishedListener {
        void onInflateFinished(@NonNull View view, @LayoutRes int resid, @Nullable ViewGroup parent);
    }

    private static class InflateRequest {
        AsyncLayoutInflater inflater;
        ViewGroup parent;
        int resid;
        View view;
        AsyncLayoutInflater.OnInflateFinishedListener callback;

        InflateRequest() {
        }
    }

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        BasicInflater(Context context) {
            super(context);
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return new AsyncLayoutInflater.BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }

            return super.onCreateView(name, attrs);
        }
    }

    private static class InflateThread extends Thread {
        private static final AsyncLayoutInflater.InflateThread sInstance;

        static {
            sInstance = new AsyncLayoutInflater.InflateThread();
            sInstance.start();
        }

        public static AsyncLayoutInflater.InflateThread getInstance() {
            return sInstance;
        }

        private ArrayBlockingQueue<AsyncLayoutInflater.InflateRequest> mQueue = new ArrayBlockingQueue<>(10);
        private SynchronizedPool<AsyncLayoutInflater.InflateRequest> mRequestPool = new SynchronizedPool<>(10);

        // Extracted to its own method to ensure locals have a constrained liveness
        // scope by the GC. This is needed to avoid keeping previous request references
        // alive for an indeterminate amount of time, see b/33158143 for details
        public void runInner() {
            AsyncLayoutInflater.InflateRequest request;
            try {
                request = mQueue.take();
            } catch (InterruptedException ex) {
                // Odd, just continue
                Log.w(TAG, ex);
                return;
            }

            try {
                request.view = request.inflater.mInflater.inflate(
                        request.resid, request.parent, false);
            } catch (RuntimeException ex) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(TAG, "Failed to inflate resource in the background! Retrying on the UI"
                        + " thread", ex);
            }
            Message.obtain(request.inflater.mHandler, 0, request)
                    .sendToTarget();
        }

        @Override
        public void run() {
            while (true) {
                runInner();
            }
        }

        public AsyncLayoutInflater.InflateRequest obtainRequest() {
            AsyncLayoutInflater.InflateRequest obj = mRequestPool.acquire();
            if (obj == null) {
                obj = new AsyncLayoutInflater.InflateRequest();
            }
            return obj;
        }

        public void releaseRequest(AsyncLayoutInflater.InflateRequest obj) {
            obj.callback = null;
            obj.inflater = null;
            obj.parent = null;
            obj.resid = 0;
            obj.view = null;
            mRequestPool.release(obj);
        }

        public void enqueue(AsyncLayoutInflater.InflateRequest request) {
            try {
                mQueue.put(request);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to enqueue async inflate request", e);
            }
        }
    }
}
