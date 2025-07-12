package merail.tools.words

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import merail.tools.database.IDatabaseRepository
import merail.tools.words.ui.theme.WordsDatabaseHandlerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseRepository: IDatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            WordsDatabaseHandlerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        EncryptWordsDBButton()

                        EncryptWordsIdsDBButton()

                        EncryptRandomedWordsIdsDBButton()

                        CheckEncryptedRandomedWordsIdsDBButton()
                    }
                }
            }
        }
    }

    @Composable
    private fun EncryptWordsDBButton() {
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    databaseRepository.encryptWordsDB()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(12.dp),
        ) {
            Text(
                text = "Encrypt Words DB",
            )
        }
    }

    @Composable
    private fun EncryptWordsIdsDBButton() {
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    databaseRepository.encryptWordsIdsDB()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(12.dp),
        ) {
            Text(
                text = "Encrypt Words Ids DB",
            )
        }
    }

    @Composable
    private fun EncryptRandomedWordsIdsDBButton() {
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    databaseRepository.encryptRandomedWordsIdsDB()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(12.dp),
        ) {
            Text(
                text = "Encrypt Randomed Words Ids DB",
            )
        }
    }

    @Composable
    private fun CheckEncryptedRandomedWordsIdsDBButton() {
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    databaseRepository.checkEncryptedRandomedWordsIdsDB()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(12.dp),
        ) {
            Text(
                text = "Check Encrypted Randomed Words Ids DB",
            )
        }
    }
}