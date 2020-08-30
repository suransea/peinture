package top.srsea.peinture.draw.view

import android.content.Context
import android.graphics.*
import android.widget.FrameLayout

class CardView(context: Context) : FrameLayout(context) {
    var radius = 0
    private val rect = RectF()
    private val srcPaint = Paint()
    private val dstPaint = Paint()

    init {
        srcPaint.isAntiAlias = true
        dstPaint.isAntiAlias = true
        dstPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
    }

    override fun draw(canvas: Canvas?) {
        canvas?.apply {
            saveLayer(rect, srcPaint)
            drawRoundRect(rect, radius.toFloat(), radius.toFloat(), srcPaint)
            saveLayer(rect, dstPaint)
            super.draw(canvas)
            restore()
        }
    }
}
