package com.example.aoe2assistant.presentation.individualItems

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.R

@RequiresApi(Build.VERSION_CODES.Q)
class MedievalFont() {

    private val fontProvider = GoogleFont.Provider(
        "com.google.android.gms.fonts",
        "com.google.android.gms",
        R.array.com_google_android_gms_fonts_certs// Font certificate (system-provided)
    )


    private val medievalFont2 = GoogleFont("MedievalSharp")
    private val medievalFont = GoogleFont("Almendra")

    // Use GoogleFont provider to load the custom font
    val fontFamily = FontFamily(
        Font(medievalFont, fontProvider),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.W400),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.W700),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.Medium),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.Bold),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
        Font(medievalFont, fontProvider = fontProvider, weight = FontWeight.Black)
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
val medievalFont = MedievalFont()

@Composable
fun TextMedievalFont(){
    val image = ImageBitmap.imageResource(
        R.drawable.matchup_selection_background
    )
    val imageBrush = remember(image) {
        ShaderBrush(
            shader = ImageShader(
                image = image
            )
        )
    }

    Box(modifier = Modifier.background(brush = imageBrush)
        .size(150.dp,50.dp)
        .border(4.dp, Color.Blue))
}
