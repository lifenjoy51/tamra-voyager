package tamra.common

import kotlinx.serialization.Serializable

@Serializable
data class GameStore(
    val fleet: Fleet,
    val doneEvents: MutableList<String> = mutableListOf(),  // 완료한 이벤트 아이디. TODO 완료시 추가.
) {
    fun port(): Port? = fleet.port?.let { GameData.ports[it] }
}