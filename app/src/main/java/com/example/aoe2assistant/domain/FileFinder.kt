package com.example.aoe2assistant.domain

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.R
import java.io.OutputStreamWriter

@Composable
fun CreateNotesFile(onUriSelected: (Uri?) -> Unit) {
    val result = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        result.value = uri
        onUriSelected(uri)
    }

    Column {
        Button(onClick = {
            launcher.launch("notes.txt")
        }) {
            Text(text = LocalOrchestrator.current.recoverText(R.string.selectDocExport))
        }
    /*
        result.value?.let { uri ->
            Text(text = "Document URI: $uri")
        }

     */
    }
}

@Composable
fun SelectNotesFile(onUriSelected: (Uri?) -> Unit) {
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {uri ->
        result.value = uri
        onUriSelected(uri)
    }

    Column {
        Button(onClick = {
            launcher.launch("*/*")
        }) {
            Text(text = LocalOrchestrator.current.recoverText(R.string.selectDocImport))
        }
        /*
        result.value?.let { document ->
            Text(text = "Document Path: "+document.path.toString())
        }

         */
    }
}


fun writeToFile(context: Context, uri: Uri, content: String) {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
        OutputStreamWriter(outputStream).use { writer ->
            writer.write(content)
        }
    }
}
