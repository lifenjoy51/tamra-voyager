import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.json.Json
import tamra.common.*
import tamra.event.*
import tamra.port.market.Market
import tamra.port.market.MarketProduct
import tamra.port.market.MarketProductState
import tamra.port.shipyard.ShipBlueprint
import tamra.port.shipyard.Shipyard

class DataLoader {

    suspend fun loadGameData() {
        val dataJsonString = resourcesVfs["data.json"].readString()
        val data: Map<String, MutableList<Map<String, String>>> =
            Json.parse(dataJsonString) as Map<String, MutableList<Map<String, String>>>
        val products = loadProducts(data)
        val shipBlueprints = loadShipBlueprints(data)
        val ports = loadPorts(data, products, shipBlueprints)
        val conditions = loadConditions(data)
        val contents = loadContents(data)
        val events = loadEvents(data, conditions, contents)
        GameData.init(
            products = products,
            ports = ports,
            shipBlueprints = shipBlueprints,
            conditions = conditions,
            events = events
        )
    }

    private fun loadProducts(data: Map<String, MutableList<Map<String, String>>>): Map<ProductId, Product> {
        return data.getValue("products").associate { productData ->
            val productId = ProductId.valueOf(productData.getValue("productId"))
            val productType = ProductType.valueOf(productData.getValue("productType"))
            val productName = productData.getValue("name")
            val price = productData.getValue("price").toInt()

            productId to Product(
                id = productId,
                type = productType,
                name = productName,
                price = price
            )
        }
    }

    private fun loadShipBlueprints(data: Map<String, MutableList<Map<String, String>>>): Map<ShipType, ShipBlueprint> {
        return data.getValue("ships").associate { shipData ->
            val shipType = ShipType.valueOf(shipData.getValue("shipType"))
            val imgName = shipData.getValue("imgName")
            val shipTypeName = shipData.getValue("typeName")
            val cargoSize = shipData.getValue("cargoSize").toInt()
            val speed = shipData.getValue("speed").toInt()
            val price = shipData.getValue("price").toInt()

            shipType to ShipBlueprint(
                shipType, shipTypeName, imgName, cargoSize, speed, price
            )
        }
    }

    private fun loadPorts(
        data: Map<String, MutableList<Map<String, String>>>,
        allProducts: Map<ProductId, Product>,
        allShips: Map<ShipType, ShipBlueprint>,
    ): Map<PortId, Port> {
        return data.getValue("ports").associate { portData ->
            val portId = portData.getValue("id")
            val portName = portData.getValue("name")

            val products = data.getValue("ports_products")
                .filter { it["portId"] == portId }.associate {
                    val productId = ProductId.valueOf(it.getValue("productId"))
                    val marketSize = it.getValue("marketSize").toString().toInt()
                    val supplyAndDemand = it.getValue("supplyAndDemand").toString().toInt()

                    productId to MarketProduct(
                        id = productId,
                        state = MarketProductState(
                            marketSize = marketSize,
                            marketStock = marketSize,
                            supplyAndDemand = supplyAndDemand
                        )
                    )
                }
            val portMarket = Market(
                PortId.valueOf(portId),
                products
            )

            val shipsOnSale = data.getValue("ports_ships")
                .filter { it["portId"] == portId }.map {
                    val shipType = ShipType.valueOf(it.getValue("shipType"))
                    allShips.getValue(shipType)
                }
            val portShipYard = Shipyard(
                PortId.valueOf(portId),
                shipsOnSale
            )

            PortId.valueOf(portId) to Port(
                PortId.valueOf(portId),
                portName,
                portMarket,
                portShipYard
            )
        }
    }

    private fun loadContents(data: Map<String, MutableList<Map<String, String>>>): Map<String, List<EventContent>> {
        return data.getValue("eventContents").map { contentData ->
            val type = ContentType.valueOf(contentData.getValue("type"))
            val eventId = contentData.getValue("eventId")
            val position = contentData["position"]?.let { ContentPosition.valueOf(it) } ?: ContentPosition.C
            val speaker = contentData["speaker"] ?: ""
            val lines = contentData.getValue("lines")
            when (type) {
                ContentType.N -> Narration(
                    eventId = eventId,
                    position = position,
                    lines = lines
                )
                ContentType.C -> Conversation(
                    eventId = eventId,
                    position = position,
                    speaker = speaker,
                    lines = lines
                )
            }.apply { Unit }
        }.groupBy { it.eventId }
    }

    private fun loadEvents(data: Map<String, MutableList<Map<String, String>>>, conditions: Map<String, EventCondition>, contents: Map<String, List<EventContent>>): Map<String, GameEvent> {
        return data.getValue("events").associate { eventData ->
            val eventId = eventData.getValue("id")
            val location = EventLocation.valueOf(eventData.getValue("location"))
            val conditionId = eventData.getValue("conditionId")

            eventId to GameEvent(
                id = eventId,
                location = location,
                condition = conditions.getValue(conditionId),
                contents = contents.getValue(eventId)
            )
        }
    }

    private fun loadConditions(data: Map<String, MutableList<Map<String, String>>>): Map<String, EventCondition> {
        return data.getValue("eventConditions").associate { conditionData ->
            val id = conditionData.getValue("id")
            val x = conditionData.getValue("x").let(Subject.Companion::parse)
            val op = Op.valueOf(conditionData.getValue("op"))
            val y = conditionData.getValue("y").let(Subject.Companion::parse)
            id to EventCondition(
                id = id,
                x = x,
                op = op,
                y = y
            )
        }
    }
}