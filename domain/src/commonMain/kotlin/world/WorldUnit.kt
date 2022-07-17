package tamra.world


import me.lifenjoy51.tamra.common.Degree
import me.lifenjoy51.tamra.common.Vector
import tamra.common.GameUnit
import tamra.common.LocationXY
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

data class PlayerFleet(
    override var location: LocationXY,
    override var velocity: Double = 0.0,
    override val map: WorldMap,
    private var shipAngle: Degree = Degree(0.0),
    private val sail: Sail = Sail(),
    private var anchor: Boolean = false,
// 정박/반개/전개
// 풍향?
// 돛 각도.

) : GameUnit() {

    // 외부적인 요인은 받아야 한다.
    // 풍향, 풍속, 가장 중요하다.
    // 해류, 속도. 이것도 후순위로 미룰 수 있다.
    // 파도는 특수 이벤트 처리...
    fun move(wind: Vector, current: Vector) {

        val originVelocity = velocity

        val tailWind = sail.getTailWindSailing(shipAngle, wind) // 돛의 방향에 따라 바람의 힘을 받는 정도가 달라짐.
        val headWind = sail.getHeadWindSailing(shipAngle, wind) // 역풍일 때.
        val wind = tailWind + headWind
        val v = Vector.from(shipAngle) // 선체의 진행방향.
        val r = v.between(wind) // 선체와 바람 사이 각도.
        val p = cos(r.radian) * wind.scala() // 바람의 힘 중 선체와 같은 방향으로 주어지는 힘.
        val c = v * p// 선체의 방향에 바람으로부터 받는 힘을 곱한다. 해류의 영향도 더함.

        val t = c + current

        println("$t / $c / $shipAngle / $sail / $tailWind")

        if (!this.moved(t.x, t.y)) {
            velocity = originVelocity * 0.5
        } else {
            velocity = c.scala()
        }
    }

    fun angle() = shipAngle

    fun turnShip(d: Double) {
        val sum = shipAngle + Degree(d)
        val m = sum.degree.mod(360.0)
        shipAngle = Degree(m)
    }

    fun controlSail(diff: Double) {
        sail.controlSail(diff)
    }

    fun toggleSail() {
        sail.toggleSail()
    }

    fun toggleAnchor() {
        anchor = !anchor
    }
}

data class Sail(
    var sailState: SailState = SailState.FURLING,
    var sailAngle: Degree = Degree(0.0),
) {
    // 순풍일 때.
    fun getTailWindSailing(shipAngle: Degree, wind: Vector): Vector {
        // 바람과 돛의 각도 차이를 구해야한다...
        val v = Vector.from(shipAngle + sailAngle)    // 돛의 절대방향을 구한다.
        val between = v.between(wind)  // 돛과 바람의 각도차이.

        // -45~45 -> 역풍으로 계산.
        // 45 ~ 135, -45 ~ -135, 225 ~ 315 -> 순풍으로 계산.
        // 360-45 = 315. 180+45 = 225
        // .
        val c = cos(between.radian)
        val w = c.pow(2)
        println()
        println("tail : $w ${between.toDegree()}")
        // 영향을 안받으면 0, 최대로 받으면 1.
        //println("$betweenR ${shipAngle + sailAngle} / ${betweenR / (PI * 2) * 360} / $w")
        // 이건 순풍일 때.
        // 역풍일 때 계산은?
        return wind * w
    }

    fun getHeadWindSailing(shipAngle: Degree, wind: Vector): Vector {
        // 바람과 돛의 각도 차이를 구해야한다...
        val v = Vector.from(shipAngle + sailAngle)    // 돛의 절대방향을 구한다.
        val between = v.between(wind)  // 돛과 바람의 각도차이.
        val weight = sin(between.radian) / 4

        println("head : $weight ${between.toDegree()}")

        return v * weight * wind.scala()

    }

    fun controlSail(diff: Double) {
        val sum = sailAngle + Degree(diff)
        val minMax = sum.degree.coerceIn(-45.0, 45.0)
        sailAngle = Degree(minMax)
    }

    fun toggleSail() {
        sailState = when (sailState) {
            SailState.UNFURLING -> SailState.FURLING
            SailState.FURLING -> SailState.UNFURLING
        }
    }

    enum class SailState {
        /**
         * 접힌 돛을 펴다.
         */
        UNFURLING,

        /**
         * 돛을 접다.
         */
        FURLING,
    }
}