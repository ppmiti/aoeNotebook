package com.example.aoe2assistant.presentation.individualItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
fun WoodenBackground(content: @Composable () -> Unit, height: Int, alignment: Alignment){
    Box(
        modifier = Modifier.fillMaxWidth().height(height.dp),
        contentAlignment = alignment
    ) {
        Image(
            painter = painterResource(id = R.drawable.buttons_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth()
        )

        // displaying the top layer
        content()

    }
}