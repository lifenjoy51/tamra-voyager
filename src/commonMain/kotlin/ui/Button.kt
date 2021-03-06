package ui

import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.uiObservable
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.paint.ColorPaint
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.vector.*


inline fun Container.tamraButton(
    width: Double = 60.0,
    height: Double = 30.0,
    text: String = "Button",
    textSize: Double = 16.0,
    px: Int = defaultMargin,
    py: Int = defaultMargin,
    ax: Int = 0,    // 기본 간격으로 부터 x 위치 조정.
    ay: Int = 0,    // 기본 간격으로 부터 y 위치 조정.
    vc: Container? = null,  // 컨테이너 기준 수직 중앙정렬.
    block: @ViewDslMarker TamraButton.() -> Unit = {},
): TamraButton = TamraButton(width = width, height = height, text = text, textSize = textSize).apply {
    positionX(px + ax)
    positionY(py + ay)
    if (vc != null) {
        alignY(vc, 0.5, true)
    }
    alpha = 1.0
    onOut { this.alpha = 1.0 }
    onOver { this.alpha = 0.7 }
}.addTo(this).apply(block)

open class TamraButton(
    width: Double = 80.0,
    height: Double = 40.0,
    text: String = "Button",
    textSize: Double = 20.0,
) : UIButton(width, height, text), ViewLeaf {

    var textSize by uiObservable(textSize) { updateText() }
    var textColor by uiObservable(Colors.WHITE) { updateText() }

    init {
        updateText()
    }

    private fun updateText() {
        textView.font = TamraFont.get()
        textView.textSize = textSize
        textView.color = textColor
        // text 변경 시 root가 바뀌어서 원치않게 동작..
        textView.centerOn(root)
        //textView.setTextBounds(Rectangle(0.0, 0.0, width, height))
        textView.setText(text)
    }
}

private fun buildDefaultButton(index: Int): Bitmap {
    return NativeImage(64, 64).context2d {
        val gradient = when (index) {
            0 -> createLinearGradient(0, 0, 0, 64).addColorStop(0.0, Colors["#336699"])
                .addColorStop(1.0, Colors["#336699"]) // Out
            1 -> createLinearGradient(0, 0, 0, 64).addColorStop(0.0, Colors["#4477aa"])
                .addColorStop(1.0, Colors["#4477aa"]) // Over
            2 -> createLinearGradient(0, 0, 0, 64).addColorStop(0.0, Colors["#225588"])
                .addColorStop(1.0, Colors["#225588"]) // Down
            3 -> createLinearGradient(0, 0, 0, 64).addColorStop(0.0, Colors["#A7A7A7"])
                .addColorStop(1.0, Colors["#A7A7A7"]) // Disabled
            else -> TODO()
        }

        val border = when (index) {
            2 -> com.soywiz.korim.paint.ColorPaint(Colors["#002244"])
            3 -> com.soywiz.korim.paint.ColorPaint(Colors["#002244"])
            else -> com.soywiz.korim.paint.ColorPaint(Colors["#002244"])
        }
        fill(gradient) {
            stroke(border, lineWidth = 4.0) {
                roundRect(4, 4, 64 - 4 * 2, 64 - 4 * 2, 6, 6)
            }
        }
    }
}

val DEFAULT_UI_SKIN_IMG: Bitmap32 by lazy {
    //Bitmap32(64 * 3, 64).context2d {
    NativeImage(256, 512).context2d {
        for (n in 0 until 4) {
            drawImage(buildDefaultButton(n), n * 64, 0)
        }
        for (n in 0 until 5) {
            for (enabled in listOf(false, true)) {
                drawImage(buildDefaultShape(n, enabled), 64.0 * (if (enabled) 0 else 1), 64 + 64.0 * n)
            }
        }
    }.mipmaps(true).toBMP32IfRequired().also { image ->
        //launchImmediately(EmptyCoroutineContext) { image.writeTo("/tmp/file.png".uniVfs, PNG) }
    }
    //PNG.decode(
    //    "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".fromBase64()
    //).toBMP32()
}


private fun buildDefaultShape(index: Int, enabled: Boolean): Bitmap {
    val color = ColorPaint(if (enabled) Colors["#111111"] else Colors["#999999"])
    val lineWidth = 8.0
    return NativeImage(64, 64).context2d {
        when (index) {
            0 -> {
                translate(27, 41)
                rotate(45.degrees)
                stroke(color, lineWidth = lineWidth, lineCap = LineCap.BUTT) {
                    moveTo(-16, 0)
                    lineTo(0, 0)
                    lineTo(0, -32)
                }
            }
            else -> {
                translate(32, 32)
                rotate((45 + 90 * (index - 1)).degrees)
                val offsetX = -8
                val offsetY = -8
                val lineLength = 20
                stroke(color, lineWidth = lineWidth, lineCap = LineCap.SQUARE) {
                    line(offsetX + 0, offsetY + 0, offsetX + 0, offsetY + lineLength)
                    line(offsetX + 0, offsetY + 0, offsetX + lineLength, offsetY + 0)
                }
            }
        }
    }
}