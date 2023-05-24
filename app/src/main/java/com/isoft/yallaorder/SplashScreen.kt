package com.isoft.yallaorder

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.gson.Gson
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.data.models.User
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.bold
import com.isoft.yallaorder.ui.theme.extraBold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(
    googleLoginAuth: GoogleSignInClient
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    var visibility by remember { mutableStateOf(false)}
    var showHomeScreen by remember { mutableStateOf(false)}
    val startForResult = rememberGetContentActivityResult(){
        visibility = true
    }
    val localContext = LocalContext.current
    val userStore = UserStore(LocalContext.current)
    val userStoreState = userStore.getUser.collectAsState(initial = null)
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SplashBackground)
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 32.dp, end = 16.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.make_order),
                color = Color.White,
                fontFamily = extraBold,
                textAlign = TextAlign.End,
                fontSize = 25.sp
            )
            Image(
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp)
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally),
                painter = rememberAsyncImagePainter(
                    R.drawable.loading, imageLoader
                ),
                contentDescription = "loading"
            )

            Text(
                modifier = Modifier
                    .padding(top = 32.dp, end = 32.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color.Black,
                            fontFamily = extraBold,
                            fontSize = 35.sp
                        )
                    ) {
                        append(stringResource(id = R.string.save))
                    }
                    append("  ")
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontFamily = extraBold,
                            fontSize = 35.sp
                        )
                    ) {
                        append(stringResource(id = R.string.your_time))
                    }
                    append("\n\n")
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontFamily = extraBold,
                            fontSize = 35.sp
                        )
                    ) {
                        append(stringResource(id = R.string.order_with_us))
                    }
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black
                ),
                onClick = {
                    if(userStoreState.value==null) {
                        startForResult.launch(googleLoginAuth.signInIntent)
                    }else{
                        showHomeScreen = true
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.lets_go),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = extraBold,
                    fontSize = 22.sp
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(Alignment.Bottom)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp),
                text = stringResource(id = R.string.version)+" "+BuildConfig.VERSION_NAME,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = bold
            )
        }
        AnimatedVisibility(
            visible = visibility,
            enter = slideIn(
                initialOffset = {
                    IntRect(offset = IntOffset.Zero, size = it).bottomLeft
                }
            ),
            exit = slideOut(
                targetOffset = {
                    IntRect(offset = IntOffset(0,it.height), size = it).topLeft
                }
            )

        ) {
            BottomPopupProfile(user = startForResult.user,userStore)
        }
        if(showHomeScreen) {
           LaunchedEffect(key1 = "updateWorkInfoUUID"){
               userStore.saveWorkerInfoUUID(Utils.fireWorker(localContext.applicationContext))
           }
           HomeScreen()
        }

        BackHandler(enabled = true) {
            if(visibility) {
                visibility = false
            }else{
                (localContext as MainActivity).finish()
            }
        }
    }
}

class GetContentActivityResult(
    private val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val user: User
) {
    fun launch(intent: Intent) {
        launcher.launch(intent)
    }
}

@Composable
fun rememberGetContentActivityResult(onUserProfileLoaded:()->Unit): GetContentActivityResult {
    var context = LocalContext.current
    var user by remember { mutableStateOf<User?>(
        User(
        "","","",""
    )
    ) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult(), onResult = { result->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (result.data != null) {
                val resultData = GoogleSignIn.getSignedInAccountFromIntent(intent).result
                CoroutineScope(Dispatchers.IO).launch {
                    user = User(
                        resultData.displayName!!,
                        resultData.givenName!!,
                        resultData.email!!,
                        resultData.photoUrl.toString(),
                        accessToken = Utils.getToken(
                            context,
                            resultData.account!!,
                            Constants.GET_TOKEN_SCOPE
                        ),
                        accountName = resultData!!.account!!.name,
                        accountType = resultData.account!!.type
                    )
                    onUserProfileLoaded.invoke()
                }
            }
        }
    })

    return remember(launcher, user) {
        GetContentActivityResult(launcher, user!!)
    }
}



@Composable
@Preview
fun SplashScreenPreview(){

}