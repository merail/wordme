package merail.life.game.api

import merail.life.domain.WordModel

interface IGameLoader {

    suspend fun loadInitialData()

    suspend fun loadNewDay(): WordModel?
}
