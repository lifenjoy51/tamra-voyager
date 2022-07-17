package tamra.world

import tamra.common.*

class WorldMap(
    val portPositions: Map<TileXY, PortId?> = mapOf(),
    val landingPositions: Map<TileXY, LandingId?> = mapOf(),
    override val tiles: Map<TileXY, TileId> = mapOf(),
    override val collisions: Map<TileId, List<PointArea>> = mapOf(),
) : GameMap() {
    override val onWater: Boolean = true
    override fun getTileXY(locationXY: LocationXY): TileXY {
        return locationXY.toWorldTileXY()
    }
}