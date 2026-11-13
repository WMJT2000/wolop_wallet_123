package com.wolop.wallet.ui.dialogs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Movie
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.wolop.wallet.R

/**
 * Dibuja el GIF de carga sin alterar el recurso.
 * Fallback: arco animado si el decodificador Movie no estuviera disponible.
 */
class GifLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val movie: Movie? = runCatching {
        Movie.decodeStream(resources.openRawResource(R.drawable.icon_alert_loading))
    }.getOrNull()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var startTime = 0L
    private val sizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        84f,
        resources.displayMetrics
    ).toInt()

    fun start() {
        startTime = SystemClock.uptimeMillis()
        invalidate()
    }

    fun stop() {
        startTime = 0L
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSize(sizePx, widthMeasureSpec),
            resolveSize(sizePx, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        val current = movie
        if (current != null) {
            val duration = current.duration().takeIf { it > 0 } ?: 1000
            if (startTime > 0L) {
                current.setTime(((SystemClock.uptimeMillis() - startTime) % duration).toInt())
            } else {
                current.setTime(0)
            }
            val scale = measuredWidth.toFloat() / current.width().coerceAtLeast(1)
            canvas.save()
            canvas.translate(
                (measuredWidth - current.width() * scale) / 2f,
                (measuredHeight - current.height() * scale) / 2f
            )
            canvas.scale(scale, scale)
            current.draw(canvas, 0f, 0f, paint)
            canvas.restore()
            if (startTime > 0L) postInvalidateOnAnimation()
            return
        }

        // Fallback: arco giratorio dorado.
        if (startTime == 0L) return
        val radius = measuredWidth / 2f - 8f
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        val sweep = 270f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.color = Color.parseColor("#C0964E")
        paint.strokeCap = Paint.Cap.ROUND

        val angle = ((SystemClock.uptimeMillis() - startTime) / 12f) % 360f
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            angle,
            sweep,
            false,
            paint
        )
        postInvalidateOnAnimation()
    }
}
