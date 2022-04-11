package com.template.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.template.R



@Composable
fun StartScreen(
    icon: Int = R.drawable.ic_baseline_temple_hindu_24,
    textLabel: String = "Com.Template"
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Icon(
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp),
                painter = painterResource(id = icon),
                tint = Color.White,
                contentDescription = "Logo"
            )
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
            )
            Text(
                text = textLabel,
                color = Color.White,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
            )

            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
@Preview
fun Loading(){
    StartScreen()
}