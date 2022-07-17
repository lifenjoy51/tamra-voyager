package scene.world

import me.lifenjoy51.tamra.common.Degree
import me.lifenjoy51.tamra.common.Vector
import tamra.common.GameStore
import tamra.common.toWorldTileXY
import tamra.world.PlayerFleet
import tamra.world.WorldMap
import ui.LiveData

class WorldViewModel(
    private val store: GameStore,
) {
    val playerFleet: LiveData<PlayerFleet> = LiveData(null)
    val turnDegree = 15.0

    private fun letFleet(block: (PlayerFleet)->Unit){
        playerFleet.value?.let {
            block(it)
            onMoveFleet(it)
        }
    }

    private fun onMoveFleet(fleet: PlayerFleet) {
        playerFleet(fleet)
        val worldMap = fleet.map
        val txy = fleet.location.toWorldTileXY()

        // save current location.
        store.fleet.location = fleet.location
    }

    fun initPlayerFleet(worldMap: WorldMap) {
        val fleet = PlayerFleet(location = store.fleet.location, map = worldMap)
        playerFleet(fleet)
        onMoveFleet(fleet)
    }

    fun turnLeft() {
        letFleet {
            it.turnShip(-turnDegree)
        }
    }

    fun turnRight() {
        letFleet {
            it.turnShip(turnDegree)
        }
    }

    fun toggleAnchor() {
        letFleet {
            it.toggleAnchor()
        }
    }

    fun move() {
        letFleet {
            it.move(
                // 각도는 향하는 방향.
                // 남향 기준. 동쪽이 +x축. 남쪽이 +y축.
                // 45도면 동남쪽으로 부는 바람.
                wind = Vector.from(Degree(-45.0),  0.1),
                current = Vector.from(Degree(0.0),  0.0),
            )
        }
    }

    fun sailLeft() {
        letFleet {
            it.controlSail(-turnDegree)
        }
    }

    fun sailRight() {
        letFleet {
            it.controlSail(turnDegree)
        }
    }

    fun toggleSail() {
        letFleet {
            it.toggleSail()
        }
    }

}