package merail.life.game.api

import kotlinx.coroutines.flow.MutableStateFlow
import merail.life.domain.KeyCellModel
import merail.life.domain.WordModel

interface IGameRepository {
    fun setKeyForms(keyForms: List<List<KeyCellModel>>)

    fun getKeyForms(): MutableStateFlow<List<List<KeyCellModel>>?>

    fun setDayWord(dayWord: WordModel?)

    fun getDayWord(): MutableStateFlow<WordModel?>
}