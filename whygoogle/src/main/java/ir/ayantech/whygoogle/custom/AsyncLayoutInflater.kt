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

typealias OnInflateFinishedListener = (viewBinding: ViewBinding, parent: ViewGroup?) -> Unit

class AsyncLayoutInflater(context: Context) {

    private val mHandlerCallback = Handler.Callback { msg ->
        val request = msg.obj as InflateRequest
        if (request.view == null) {
            request.view = request.resid?.invoke(mInflater, request.parent, false)
        }
        request.callback?.invoke(request.view!!, request.parent)
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