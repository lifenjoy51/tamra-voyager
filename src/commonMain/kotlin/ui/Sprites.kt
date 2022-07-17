package ui

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap


fun Bitmap.getSpriteAnimation(
    size: Int = 8,
    seq: Int = 0,
    col: Int = 2,
): SpriteAnimation {
    return SpriteAnimation(
        spriteMap = this,
        spriteWidth = size,
        spriteHeight = size,
        marginTop = seq * 8,
        columns = col
    )
}