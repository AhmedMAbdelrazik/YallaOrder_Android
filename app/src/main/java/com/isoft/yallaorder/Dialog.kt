package com.isoft.yallaorder

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.bold

@Composable
fun ShowAlertDialog(messageRes:Int,onConfirmClicked:()->Unit,onDismissClicked:()->Unit){
        AlertDialog(onDismissRequest = {}, confirmButton = {
            Button(onClick = onConfirmClicked){
                Text(
                    text = stringResource(id = R.string.yes),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = bold
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismissClicked){
                Text(
                    text = stringResource(id = R.string.no),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = bold
                )
            }
        },
        text = {
            Text(
                text = stringResource(id = messageRes),
                color = SplashBackground,
                fontSize = 16.sp,
                fontFamily = bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        )
}