package com.isoft.yallaorder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.ui.theme.SplashBackground
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(googleSignInClient:GoogleSignInClient){
    val userStore = UserStore(LocalContext.current)
    var isUserLoggedIn:Boolean? by remember{
        mutableStateOf(null)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
    )
    LaunchedEffect(key1 ="checkIfUserLoggedIn") {
        delay(2000)
        isUserLoggedIn = userStore.isUserLoggedIn()
    }
    if(isUserLoggedIn!=null){
        if(isUserLoggedIn!!){
            HomeScreen()
        }else{
            WelcomeScreen(googleLoginAuth = googleSignInClient)
        }
    }
}