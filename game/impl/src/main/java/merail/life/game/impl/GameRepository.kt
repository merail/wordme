package merail.life.game.impl

import kotlinx.coroutines.flow.MutableStateFlow
import merail.life.domain.KeyCellModel
import merail.life.domain.WordModel
import merail.life.game.api.IGameRepository
import javax.inject.Inject

class GameRepository @Inject constructor() : IGameRepository {
    private val keyForms = MutableStateFlow<List<List<KeyCellModel>>?>(null)

    private val dayWord = MutableStateFlow<WordModel?>(null)

    override fun setKeyForms(keyForms: List<List<KeyCellModel>>) {
        this.keyForms.value = keyForms
    }

    override fun getKeyForms() = keyForms

    override fun setDayWord(dayWord: WordModel?) {
        this.dayWord.value = dayWord
    }

    override fun getDayWord() = dayWord
}