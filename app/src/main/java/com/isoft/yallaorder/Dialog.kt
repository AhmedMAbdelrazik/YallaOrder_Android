package com.isoft.yallaorder

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.Dimension
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.isoft.yallaorder.ui.theme.*

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

@Composable
fun LoadingDialog(){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Dialog(
        onDismissRequest = {  },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment= Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .background(White, shape = RoundedCornerShape(8.dp))
        ) {
            Image(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                painter = rememberAsyncImagePainter(
                    R.drawable.loading, imageLoader
                ),
                contentDescription = "loading"
            )
        }
    }
}

@Composable
fun CustomAlertDialog(messageRes:Int,onConfirmClicked:()->Unit,onDismissClicked:()->Unit){
    Dialog(
        onDismissRequest = {  },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment= Alignment.Center,
            modifier = Modifier
                .width(300.dp)
                .background(LightGray, shape = RoundedCornerShape(8.dp))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.delete_order_image),
                    contentDescription = "deleteOrderImage",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),

                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = messageRes),
                    fontFamily = bold,
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SplashBackground
                    ),
                    onClick = onDismissClicked,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.think),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = extraBold,
                        fontSize = 16.sp
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    ),
                    onClick = onConfirmClicked,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_order),
                        color = White,
                        textAlign = TextAlign.Center,
                        fontFamily = extraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
        }
        }
    }
}


@Composable
fun ErrorDialog(messageRes:Int,onConfirmClicked:()->Unit){
    Dialog(
        onDismissRequest = {  },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment= Alignment.Center,
            modifier = Modifier
                .width(300.dp)
                .background(LightGray, shape = RoundedCornerShape(8.dp))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.error_image),
                    contentDescription = "errorImage",
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),

                    )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = messageRes),
                    fontFamily = bold,
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SplashBackground
                    ),
                    onClick = onConfirmClicked,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.ok),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = extraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}