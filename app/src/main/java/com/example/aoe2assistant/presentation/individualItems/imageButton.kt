package com.example.aoe2assistant.presentation.individualItems

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aoe2assistant.R

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MyImageButton(item: String, iconId: Int, fontSize: Int = 24){

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (iconId >= 0 ){
            Icon(
                modifier = Modifier.size(size = 30.dp),
                painter = painterResource(id = iconId),
                contentDescription = "Login Button Icon",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(width = 2.dp))
        }
        Text(
            text = item,
            textAlign = TextAlign.Start,
            color = Color.Black,
            modifier = Modifier.background(Color.Transparent),
            fontFamily = medievalFont.fontFamily,
            fontWeight = FontWeight.Black,
            style = TextStyle(
                fontSize = fontSize.sp, // Set the font size to 24sp
                fontWeight = FontWeight.Black // Optional: Set the font weight to bold
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ButtonCivTile(onClick: () -> Unit,
                  item: String,
                  iconId: Int,
                  hButton: Int = 80,
                  wButton: Int = 300,
                  hoffset: Int = 0,
                  fontSize: Int = 14,
                  padding: Int = 0) {
    Box(
        modifier = Modifier
            .size(wButton.dp,hButton.dp)
            .clickable { onClick() }
            .padding(padding.dp)
            .offset(0.dp, (hoffset).dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.civ_backgroung_plate),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth()
        )
        MyImageButton(item, iconId, fontSize)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ButtonCatTile(onClick: () -> Unit,
                  item: String,
                  thisSelected: Boolean,
                  hButton: Int = 120,
                  wButton: Int = 150,
                  vOffset: Int = 0,
                  startingFontSize: Int = 24,
                  nextMatchPlaque: Boolean = false,
                  enableOnClick: Boolean = true) {
    Box(
        modifier = Modifier
            .size(wButton.dp,hButton.dp)
            .offset(y=vOffset.dp)
            .clickable (enableOnClick, onClick = {onClick()}),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id =
            if (thisSelected)
            {
                R.drawable.cat_background_plaque_selected
            }
            else{
                R.drawable.cat_background_plaque
            }
            ),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth()
        )

        var textToDisplay = item
        var fontSize = startingFontSize

        if (!nextMatchPlaque){

            textToDisplay = if (item.count() > 13){
                item.substring(0,13)
            }
            else{
                item
            }

            fontSize = if (item.count() > 8){
                startingFontSize-6
            } else{
                startingFontSize
            }
        }



        MyImageButton(textToDisplay, -1, fontSize)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ComboBoxTile(onClick: () -> Unit,
                 item: String,
                 iconId: Int,
                 hButton: Int = 80,
                 wButton: Int = 300,
                 fontSize: Int = 14,
                 enabled: Boolean = true) {
        Box(
            modifier = Modifier
                .size(wButton.dp,hButton.dp)
                .clickable { if (enabled) {onClick()} }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cat_background_plaque),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxWidth()
            )
            MyImageButton(item, iconId, fontSize)
        }
}