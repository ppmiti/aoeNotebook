package com.example.aoe2assistant.presentation.individualItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.R


@Composable
fun PaperBackground(content: @Composable () -> Unit, height: Int = -1, alignment: Alignment){

    val modifierBox: Modifier
    val modifierIm: Modifier
    if (height == -1){
        modifierBox = Modifier.fillMaxSize()
        modifierIm = Modifier.fillMaxSize()
    }
    else {
        modifierBox = Modifier.fillMaxWidth().height(height.dp)
        modifierIm = Modifier.fillMaxWidth()
    }

    Box(
        modifier = modifierBox,
        contentAlignment = alignment
    ) {
        Image(
            painter = painterResource(id = R.drawable.paper_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifierIm
        )

        // displaying the top layer
        content()

    }
}