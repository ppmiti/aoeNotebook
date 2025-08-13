package com.example.aoe2assistant.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aoe2assistant.data.UserClass

@Composable
fun SignScreen(userclass: UserClass) : UserClass {

    var ingameName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = ingameName, onValueChange = {
                ingameName = it
            }, label = {Text("Enter AOE2 nickname")})
            Spacer(Modifier.size(8.dp))
            Button(onClick = { /*TODO*/ }) {
                Text("Start assistant as ${ingameName}")
            }
        }
    }


    //var newUser: UserClass = userclass.copy(inGameName = ingameName);
    return userclass.copy(inGameName = ingameName)
}

@Preview(showBackground = true)
@Composable
fun singInScreenPreview(){
    SignScreen(UserClass(""))
}