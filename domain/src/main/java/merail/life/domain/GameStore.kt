package merail.life.domain

import kotlinx.coroutines.flow.MutableStateFlow

object GameStore {
    var keyForms: List<List<KeyCellModel>>? = null
    var dayWord = MutableStateFlow<WordModel?>(null)
}