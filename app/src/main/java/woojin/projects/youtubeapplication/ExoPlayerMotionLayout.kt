package woojin.projects.youtubeapplication

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class ExoPlayerMotionLayout
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attributeSet, defStyleAttr) {

    var targetView: View? = null

    //GestureDetector 생성 -> SimpleOnGestureListener() , OnGestureListener를 사용 안한 이유
    //모두 override를 해야하기에
    private val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return targetView?.containTouchArea(e1.x.toInt(), e1.y.toInt()) ?: false
            }

        })
    }

    //OnInterceptTouchEvent를 통해 dispatchTouchEvent로 ViewGroup을 하향으로 내려가 notify를 하므로
    // 중간에 가로채어 true일 경우 그 ViewGroup단에서 touchEvent를 가져간다
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            return gestureDetector.onTouchEvent(event)
        } ?: return false

    }

    private fun View.containTouchArea(x: Int, y: Int): Boolean {
        Log.e("testt", "${x}, ${y}")
        return (x in this.left..this.right && y in this.top..this.bottom)
    }
}
