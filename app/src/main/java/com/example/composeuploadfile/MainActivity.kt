package com.example.composeuploadfile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.composeuploadfile.ui.theme.ComposeUploadFileTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeUploadFileTheme {
                Scaffold { innerPadding ->
                    UploadFile(innerPadding)
                }
            }
        }
    }
}

//const val filename = "never_gonna_give_you_up.mp4"
const val filename = "android_bot.jpg"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UploadFile(padding: PaddingValues) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val file = File(context.cacheDir, filename)
    var response by remember { mutableStateOf("") }
    val files by getFiles().collectAsState(emptyList())

    LaunchedEffect(Unit) {
        file.outputStream().use { writer ->
            context.assets.open(filename).use { input ->
                input.copyTo(writer)
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        stickyHeader {
            Text("Upload File")
            Button(onClick = {
                scope.launch {
                    response = uploadFile(file)
                    Log.d("UploadFile", response)
                }
            }) { Text("Upload") }
            if (response.isNotEmpty())
                Card(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .padding(top = 20.dp)
                ) {
                    Text(
                        response,
                        modifier = Modifier.padding(10.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
        }
        items(files) {
            Card(Modifier.padding(20.dp)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(it.name, fontFamily = FontFamily.Monospace)
                    Text(it.url, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
