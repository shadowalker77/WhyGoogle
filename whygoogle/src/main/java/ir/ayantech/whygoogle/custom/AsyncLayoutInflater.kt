package ir.ayantech.whygoogle.custom

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.util.Pools
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.fragment.ViewBindingInflater
import java.util.concurrent.ArrayBlockingQueue

/**
 *
 * Helper class for inflating layouts asynchronously. To use, construct
 * an instance of [AsyncLayoutInflater] on the UI thread and call
 * [.inflate]. The
 * [AsyncLayoutInflater.OnInflateFinishedListener] will be invoked on the UI thread
 * when the inflate request has completed.
 *
 *
 * This is intended for parts of the UI that are created lazily or in
 * response to user interactions. This allows the UI thread to continue
 * to be responsive & animate while the relatively heavy inflate
 * is being performed.
 *
 *
 * For a layout to be inflated asynchronously it needs to have a parent
 * whose [ViewGroup.generateLayoutParams] is thread-safe
 * and all the Views being constructed as part of inflation must not create
 * any [Handler]s or otherwise call [Looper.myLooper]. If the
 * layout that is trying to be inflated cannot be constructed
 * asynchronously for whatever reason, [AsyncLayoutInflater] will
 * automatically fall back to inflating on the UI thread.
 *
 *
 * NOTE that the inflated View hierarchy is NOT added to the parent. It is
 * equivalent to calling [LayoutInflater.inflate]
 * with attachToRoot set to false. Callers will likely want to call
 * [ViewGroup.addView] in the [AsyncLayoutInflater.OnInflateFinishedListener]
 * callback at a minimum.
 *
 *
 * This inflater does not support setting a [LayoutInflater.Factory]
 * nor [LayoutInflater.Factory2]. Similarly it does not support inflating
 * layouts that contain fragments.
 */
class AsyncLayoutInflater(context: Context) {

    private val mHandlerCallback = Handler.Callback { msg ->
        val request = msg.obj as InflateRequest
        if (request.view == null) {
            request.view = request.resid?.invoke(mInflater, request.parent, false)
        }
        request.callback!!.onInflateFinished(
            request.view!!, request.parent
        )
        mInflateThread!!.releaseRequest(request)
        true
    }

    var mInflater: LayoutInflater = BasicInflater(context)
    var mHandler: Handler = Handler(mHandlerCallback)
    var mInflateThread: InflateThread? = InflateThread.instance

    @UiThread
    fun inflate(
        layoutBinder: ViewBindingInflater, parent: ViewGroup?,
        callback: OnInflateFinishedListener
    ) {
        if (callback == null) {
            throw NullPointerException("callback argument may not be null!")
        }
        val request = mInflateThread!!.obtainRequest()
        request.inflater = this
        request.resid = layoutBinder
        request.parent = parent
        request.callback = callback
        mInflateThread!!.enqueue(request)
    }

    interface OnInflateFinishedListener {
        fun onInflateFinished(viewBinding: ViewBinding, parent: ViewGroup?)
    }

    class InflateRequest internal constructor() {
        var inflater: AsyncLayoutInflater? = null
        var parent: ViewGroup? = null
        var resid: ViewBindingInflater? = null
        var view: ViewBinding? = null
        var callback: OnInflateFinishedListener? = null
    }

    class BasicInflater internal constructor(context: Context?) : LayoutInflater(context) {
        override fun cloneInContext(newContext: Context): LayoutInflater {
            return BasicInflater(newContext)
        }

        @Throws(ClassNotFoundException::class)
        override fun onCreateView(name: String, attrs: AttributeSet): View {
            for (prefix in sClassPrefixList) {
                try {
                    val view = createView(name, prefix, attrs)
                    if (view != null) {
                        return view
                    }
                } catch (e: ClassNotFoundException) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
            return super.onCreateView(name, attrs)
        }

        companion object {
            private val sClassPrefixList = arrayOf(
                "android.widget.",
                "android.webkit.",
                "android.app."
            )
        }
    }

    class InflateThread : Thread() {
        companion object {
            var instance: InflateThread? = null

            init {
                instance = InflateThread()
                instance?.start()
            }
        }

        private val mQueue = ArrayBlockingQueue<InflateRequest>(10)
        private val mRequestPool = Pools.SynchronizedPool<InflateRequest>(10)

        // Extracted to its own method to ensure locals have a constrained liveness
        // scope by the GC. This is needed to avoid keeping previous request references
        // alive for an indeterminate amount of time, see b/33158143 for details
        fun runInner() {
            val request: InflateRequest = try {
                mQueue.take()
            } catch (ex: InterruptedException) {
                // Odd, just continue
                Log.w(TAG, ex)
                return
            }
            try {
                request.view =
                    request.resid?.invoke(request.inflater!!.mInflater, request.parent, false)
            } catch (ex: RuntimeException) {
                // Probably a Looper failure, retry on the UI thread
                Log.w(
                    TAG, "Failed to inflate resource in the background! Retrying on the UI"
                            + " thread", ex
                )
            }
            Message.obtain(request.inflater!!.mHandler, 0, request)
                .sendToTarget()
        }

        override fun run() {
            while (true) {
                runInner()
            }
        }

        fun obtainRequest(): InflateRequest {
            var obj = mRequestPool.acquire()
            if (obj == null) {
                obj = InflateRequest()
            }
            return obj
        }

        fun releaseRequest(obj: InflateRequest) {
            obj.callback = null
            obj.inflater = null
            obj.parent = null
            obj.resid = null
            obj.view = null
            mRequestPool.release(obj)
        }

        fun enqueue(request: InflateRequest) {
            try {
                mQueue.put(request)
            } catch (e: InterruptedException) {
                throw RuntimeException("Failed to enqueue async inflate request", e)
            }
        }
    }

    companion object {
        private const val TAG = "AsyncLayoutInflater"
    }
}