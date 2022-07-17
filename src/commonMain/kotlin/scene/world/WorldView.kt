package scene.world

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.tiles.tiled.readTiledMap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import scene.FilterParams
import scene.VFilter
import tamra.world.WorldMap
import ui.*

class WorldView(
    private val vm: WorldViewModel,
) {

    suspend fun draw(container: Container) {
        val tiledMap = resourcesVfs["world.tmx"].readTiledMap()
        val worldMap = WorldMap()
        val s = 2.0

        val shipSprite = resourcesVfs["ship.png"].readBitmap()
            .getSpriteAnimation(size = 32, col = 4)

        container.fixedSizeContainer(mainWidth, mainHeight) {
            // pos = tile * size.
            val tileMapView = tiledMapView(tiledMap, false, false) {
                scale = s
            }

            addFilter(VFilter(FilterParams(scaledWidth, scaledHeight)))

            // on update fleet position
            vm.playerFleet.observe { fleet ->
                // 하단 중앙을 기점으로 한다.
                with(tileMapView) {
                    val angle = Angle.HALF + Angle.QUARTER + fleet.angle().toAngle().unaryMinus()
                    val rotatedPoint = (fleet.location.toPoint() - tamra.common.baseCoord.location.toPoint())
                        .rotate(angle)
                        .mul(s)
                    val t = Matrix.Transform(
                        x = mainWidth / 2 - rotatedPoint.x,
                        y = mainHeight / 2 - rotatedPoint.y,
                        scaleX = s,
                        scaleY = s,
                        rotation = angle
                    )
                    setTransform(t)
                }
            }
        }

        container.fixedSizeContainer(mainWidth, mainHeight) {

            sprite(shipSprite) {
                playAnimationLooped(shipSprite, spriteDisplayTime = TimeSpan(500.0))
                center()
                scale = 4.0
                position(mainWidth * 0.5, mainHeight * 0.6)
            }

        }

        // init vm fleet
        vm.initPlayerFleet(worldMap)
    }

}