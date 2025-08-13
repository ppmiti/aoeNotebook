package com.example.aoe2assistant.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.aoe2assistant.LocalOrchestrator
import com.example.aoe2assistant.LocalVoice
import com.example.aoe2assistant.R
import com.example.aoe2assistant.data.WhenToSpeak
import com.example.aoe2assistant.presentation.individualItems.MedievalFont
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun buttonsTopBar(
    navController: NavHostController,
    title: String
) {
    val localOrch = LocalOrchestrator.current
    val voiceObj = LocalVoice.current
    val voiceCoroutineScope = rememberCoroutineScope()

    val medievalFont = MedievalFont()

    var returning by remember{ mutableStateOf(false) }

    LaunchedEffect(key1 = returning){
        voiceCoroutineScope.launch {
            if (localOrch.settings.speak == WhenToSpeak.ONBOTH || localOrch.settings.speak == WhenToSpeak.ONBENEFITCHANGE){
                voiceObj.stopPlayback()
            }
        }
        returning = false
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentAlignment = Alignment.Center
    )
    {
        Image(
            painter = painterResource(id = R.drawable.buttons_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth()
        )
        TopAppBar(
            backgroundColor = Color.Transparent,
            contentColor = Color(0xFF930800),
            title = {
                Box(modifier = Modifier.fillMaxHeight().width(250.dp).padding(8.dp),
                    contentAlignment = Alignment.Center)
                {
                    Image(
                        painter = painterResource(id = R.drawable.paper_background),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        title,
                        fontFamily = medievalFont.fontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

        },
            navigationIcon = {
                if (currentRoute(navController) == ScreenLinks.MatchUpInfo.route) {
                    IconButton(onClick = {
                        navController.navigate(ScreenLinks.MatchUp.route);
                        returning = true
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            },
            actions = {
                if (currentRoute(navController) != ScreenLinks.MatchUpInfo.route) {
                    IconButton(onClick = {
                        navController.navigate(ScreenLinks.User.route); returning = true
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(ScreenLinks.User.iconResId!!),
                            contentDescription = "User Settings",
                            modifier = Modifier.fillMaxSize(0.85f),
                            tint = Color(168,18,18)
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}