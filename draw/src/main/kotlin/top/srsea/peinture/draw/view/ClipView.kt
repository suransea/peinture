package top.srsea.peinture.draw.view

import android.content.Context
import android.graphics.*
import android.widget.FrameLayout

class ClipView(context: Context) : FrameLayout(context) {
    var shape = ""
    var radii = floatArrayOf()
    private val rect = RectF()
    private val srcPaint = Paint()
    private val dstPaint = Paint()
    private val path = Path()

    init {
        srcPaint.isAntiAlias = true
        dstPaint.isAntiAlias = true
        dstPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        path.reset()
        when (shape) {
            "oval" -> path.addOval(rect, Path.Direction.CW)
            else -> path.addRoundRect(rect, radii, Path.Direction.CW)
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.apply {
            saveLayer(rect, srcPaint)
            drawPath(path, srcPaint)
            saveLayer(rect, dstPaint)
            super.draw(canvas)
            restore()
        }
    }
}
