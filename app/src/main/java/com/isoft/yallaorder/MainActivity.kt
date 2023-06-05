package com.isoft.yallaorder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.ui.theme.YallaOrderTheme


class MainActivity : ComponentActivity() {
    var backPressedCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YallaOrderTheme {
               SplashScreen(googleSignInClient = getGoogleLoginAuth())
            }
        }
        onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(backPressedCount==0){
                    Toast.makeText(this@MainActivity,R.string.press_back_again, Toast.LENGTH_LONG).show()
                    backPressedCount++
                }else {
                    finish()
                }
            }
        })
    }
    private fun getGoogleLoginAuth(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.google_cloud_server_client_id))
            .requestId()
            .requestProfile()
            .requestScopes(Scope("https://www.googleapis.com/auth/spreadsheets"),Scope(Scopes.DRIVE_FULL))
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YallaOrderTheme {

    }
}