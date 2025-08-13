package com.example.aoe2assistant.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.R
import com.example.aoe2assistant.presentation.individualItems.PaperBackground
import com.example.aoe2assistant.presentation.individualItems.WoodenBackground

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DonateScreen(){

    PaperBackground(
        {
            DonateScreenInterior()
        },
        -1,
        Alignment.Center
    )

}

@Composable
fun DonateScreenInterior()
{
    val context = LocalContext.current
    val buyMeACoffeeUrl = "coff.ee/faltaenvidoapps"
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var b_openUrl = remember {
        mutableStateOf(false)
    }

    if (b_openUrl.value)
    {
        OpenUrl(buyMeACoffeeUrl)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = "I’ve been an Age of Empires II player for several years. I love the game and enjoy competing in ranked matches. But as a casual player, I need something to remind me of what I’ve learned—small strategies and tips I’ve discovered myself or picked up from others.\n\nThis app was born from that need. It’s my personal notebook, where I save matchups so that when I’m playing Persians vs. Mongols, I can recall my past experiences.\n\nThis project is driven purely by passion, and I’m sure you’ll have ideas for improvement! Your donations help keep me going. I hope you enjoy the app as much as I do!",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            OutlinedButton(onClick = {
                b_openUrl.value = true
            }) {
                Text("Support me on Buy Me a Coffee")
            }

            OutlinedButton(onClick = {
                clipboardManager.setText(AnnotatedString(buyMeACoffeeUrl))
            }) {
                Text("Copy Link")
            }
        }
    }
}

@Composable
fun OpenUrl(url: String)
{
    // on below line we are creating
    // a variable for a context
    val ctx = LocalContext.current
    val urlIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www." + url)
    )
    ctx.startActivity(urlIntent)
}