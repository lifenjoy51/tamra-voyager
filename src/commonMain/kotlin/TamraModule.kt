import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.SizeInt
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import scene.MainScene
import scene.world.WorldScene
import scene.world.WorldViewModel
import tamra.common.*
import ui.TamraFont
import ui.mainHeight
import ui.mainWidth
import kotlin.reflect.KClass


object TamraModule : Module() {
    //override val mainScene: KClass<out Scene> = MainScene::class
    override val mainScene: KClass<out Scene> = WorldScene::class

    override val size: SizeInt = SizeInt(mainWidth, mainHeight)

    override suspend fun AsyncInjector.configure() {
        // loading..
        loadFont()
        DataLoader().loadGameData()

        // load data.
        val store = loadStore()
        mapInstance(store)

        val viewModelProvider = initViewModels(store)
        mapInstance(viewModelProvider)

        // map scenes
        mapPrototype { MainScene() }
        mapPrototype { WorldScene(get()) }
    }

    private suspend fun loadFont() {
        // https://software.naver.com/software/summary.nhn?softwareId=GWS_003430&categoryId=I0100000#
        val ttf = resourcesVfs["today.ttf"].readTtfFont()
        TamraFont.init(ttf)
    }


    private suspend fun loadStore(): GameStore {
        val savedGameJsonString = resourcesVfs["saved.json"].readString()
        return if (savedGameJsonString.isEmpty()) {
            GameStore(
                fleet = Fleet(
                    ships = mutableListOf(
                        GameData.blueprints.getValue(ShipType.CHOMASUN).build("첫배"),
                        GameData.blueprints.getValue(ShipType.DOTBAE).build("짐배")
                    ),
                    balance = 1000,
                    port = PortId.JEJU,
                    location = Coord(126.545, -33.531).location,
                    //location = TileXY(44,63).toLocationXY(),
                    cargoItems = mutableListOf()
                )
            )
        } else {
            val e: JsonElement = Json.parseToJsonElement(savedGameJsonString)
            Json.decodeFromJsonElement(e)
        }
    }

    private fun initViewModels(store: GameStore): ViewModelProvider {
        return ViewModelProvider(
            WorldViewModel(store),
        )
    }
}