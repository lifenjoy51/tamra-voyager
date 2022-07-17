package me.lifenjoy51.tamra.common

import me.lifenjoy51.tamra.toRad
import kotlin.math.*

data class Vector(
    val x: Double,
    val y: Double,
) {
    companion object {
        fun from(degree: Degree = Degree(0.0), weight: Double = 1.0): Vector {
            val r = degree.toRadian().radian
            val x = cos(r) * weight
            val y = sin(r) * weight
            return Vector(x, y)
        }
    }

    operator fun plus(o: Vector) = Vector(x + o.x, y + o.y)

    operator fun minus(o: Vector) = Vector(x - o.x, y - o.y)

    operator fun times(o: Double) = Vector(x * o, y * o)

    fun scala() = sqrt(x * x + y * y)

    fun inverse() = Vector(-x, -y)

    fun turn(angleD: Double): Vector {
        val c = cos(angleD.toRad())
        val s = sin(angleD.toRad())
        return Vector(
            x = x * c - y * s,
            y = x * s + y * c
        )
    }

    // return radian.
    // https://www.omnicalculator.com/math/angle-between-two-vectors
    fun between(o: Vector): Radian {
        // 1) (xa * xb + ya * yb)
        val a = x * o.x + y * o.y
        // 2) (√(xa2 + ya2) * √(xb2 + yb2))
        val b = scala() * o.scala()
        // max 1
        val c = (a / b).coerceIn(-1.0, 1.0).let {
            if (it.isNaN()) 0.0 else it
        }
        //println("$a $b $c $this $o")
        //angle = arccos[ 1) / 2) ]
        return Radian(acos(c))
    }
}


// 0 ~ 2PI
data class Radian(
    val radian: Double,
) {
    fun toDegree(): Degree = Degree(radian * 180 / PI)
}

// 0~360도.
data class Degree(
    val degree: Double,
) {
    fun toRadian(): Radian = Radian(degree * PI / 180)
    operator fun plus(o: Degree) = Degree((degree + o.degree)
        .mod(360.0))
}