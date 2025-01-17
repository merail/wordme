package merail.life.word.store.impl.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import merail.life.word.store.impl.KeyCells
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

internal class KeyCellsSerializer @Inject constructor() : Serializer<KeyCells> {
    override val defaultValue: KeyCells = KeyCells.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): KeyCells =
        try {
            KeyCells.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: KeyCells, output: OutputStream) {
        t.writeTo(output)
    }
}
