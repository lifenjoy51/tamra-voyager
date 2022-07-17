package ui

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import me.lifenjoy51.tamra.common.Degree
import tamra.common.LocationXY

val mainWidth = 400
val mainHeight = 600
val defaultMargin = 8
val windowWidth = 300

fun LocationXY.toPoint() = Point(this.x, this.y)
fun Degree.toAngle(): Angle = Angle.fromDegrees(degree)