package com.isoft.yallaorder

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.data.models.User
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.TransparentGray
import com.isoft.yallaorder.ui.theme.bold
import com.isoft.yallaorder.ui.theme.extraBold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomPopupProfile(user: User,userStore: UserStore){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    val context = LocalContext.current
    var showHomeScreen by remember {
        mutableStateOf(false)
    }
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(TransparentGray)
        .wrapContentHeight(align = Alignment.Bottom),
    ) {
        val (
            profileImage,
            backgroundBox,
            emailImage,
            emailText,
            nameImage,
            nameText,
            enterMobileText,
            countryCodeButton,
            mobileNumberInput,
            mobileImage,
            mobileNumberValue,
            doneButton
        ) = createRefs()

        var mobileNumberText by remember { mutableStateOf("") }

        val painter = rememberAsyncImagePainter(user.photoUrl)
        Box(modifier = Modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .constrainAs(backgroundBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 5.dp)
                end.linkTo(parent.end, 5.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
        Image(
            modifier = Modifier
                .constrainAs(profileImage) {
                    top.linkTo(backgroundBox.top)
                    bottom.linkTo(backgroundBox.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(100.dp)
                .height(100.dp)
                .clip(CircleShape),
            alignment = Alignment.Center,
            painter = painter,
            contentDescription = "userProfileImage",
            contentScale = ContentScale.Crop
        )

        Image(
            modifier=Modifier
            .constrainAs(emailImage){
                top.linkTo(profileImage.bottom,20.dp)
                start.linkTo(parent.start,20.dp)
            },
            painter = painterResource(id = R.drawable.ic_email),
            contentDescription = "emailImage"
        )
        
        Text(
            modifier = Modifier
                .constrainAs(emailText){
                     top.linkTo(emailImage.top)
                     bottom.linkTo(emailImage.bottom)
                     start.linkTo(emailImage.end,10.dp)
                },
            text = user.email,
            color = SplashBackground,
            fontFamily = bold,
            fontSize = 16.sp
        )

        Image(
            modifier=Modifier
                .constrainAs(nameImage){
                    top.linkTo(emailImage.bottom,10.dp)
                    start.linkTo(parent.start,20.dp)
                },
            painter = painterResource(id = R.drawable.ic_name),
            contentDescription = "nameImage"
        )

        Text(
            modifier = Modifier
                .constrainAs(nameText){
                    top.linkTo(nameImage.top)
                    bottom.linkTo(nameImage.bottom)
                    start.linkTo(nameImage.end,10.dp)
                },
            text = user.fullName,
            color = SplashBackground,
            fontFamily = bold,
            fontSize = 16.sp
        )
        if(user.mobileNumber==null){
        Text(
            modifier = Modifier
                .constrainAs(enterMobileText){
                    top.linkTo(nameImage.bottom,40.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = stringResource(id = R.string.enter_mobile_number),
            color = SplashBackground,
            fontFamily = extraBold,
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier
                .constrainAs(countryCodeButton) {
                    top.linkTo(enterMobileText.bottom, 5.dp)
                    start.linkTo(parent.start, 20.dp)
                }
                .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp))
                .padding(start = 15.dp, top = 11.dp, bottom = 11.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .width(30.dp)
                    .height(20.dp),
                painter = painterResource(id = R.drawable.ic_egypt_flag),
                contentDescription = "countryCodeImage"
            )

            Text(
                modifier = Modifier
                    .padding(start = 5.dp),
                text = "+2",
                color = Color.Black,
                fontFamily = extraBold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
        BasicTextField(
            value = mobileNumberText,
            onValueChange ={
                if(it.length < 12) {
                    mobileNumberText = it
                }
            },
            modifier = Modifier
                .constrainAs(mobileNumberInput) {
                    start.linkTo(countryCodeButton.end, 10.dp)
                    end.linkTo(parent.end, 20.dp)
                    bottom.linkTo(countryCodeButton.bottom)
                    top.linkTo(countryCodeButton.top)
                    width = Dimension.fillToConstraints
                }
                .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(4.dp)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                ){
            TextFieldDefaults.TextFieldDecorationBox(
                value = mobileNumberText,
                innerTextField = it,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = MutableInteractionSource(),
                placeholder = {
                    Text(
                        text = stringResource(
                            id = R.string.enter_mobile_number_here
                        ),
                        color = Color.LightGray,
                        fontFamily = extraBold,
                        fontSize = 14.sp
                    )
                },
                contentPadding = PaddingValues(10.dp)
            )
        }

        Image(
            modifier = Modifier
                .constrainAs(doneButton) {
                    top.linkTo(countryCodeButton.bottom, 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(50.dp)
                .height(50.dp)
                .clickable {
                    if (mobileNumberText.isEmpty()) {
                        Toast
                            .makeText(
                                context,
                                R.string.please_enter_mobile_number,
                                Toast.LENGTH_LONG
                            )
                            .show()
                    } else if (mobileNumberText.length != 11) {
                        Toast
                            .makeText(
                                context,
                                R.string.please_enter_valid_mobile_number,
                                Toast.LENGTH_LONG
                            )
                            .show()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            userStore.saveUser(user.apply {
                                mobileNumber = mobileNumberText
                                Log.i("access_token",accessToken!!)
                            })
                            showHomeScreen = true
                        }
                    }
                },
            painter = rememberAsyncImagePainter(
                R.drawable.done,
                imageLoader
            ),
            contentDescription = "doneImage"
        )
    }else{
        Image(
            modifier = Modifier
                .constrainAs(mobileImage){
                    start.linkTo(parent.start,20.dp)
                    top.linkTo(nameImage.bottom,10.dp)
                    bottom.linkTo(parent.bottom,20.dp)
                },
            painter = painterResource(id = R.drawable.ic_mobile),
            contentDescription = "mobileImage"
        )
        Text(
            modifier = Modifier
                .constrainAs(mobileNumberValue){
                start.linkTo(mobileImage.end,10.dp)
                top.linkTo(mobileImage.top)
                bottom.linkTo(mobileImage.bottom)
            },
            text = user.mobileNumber!!,
            color = SplashBackground,
            fontFamily = bold,
            fontSize = 16.sp
        )
    }
        if(showHomeScreen){
            LaunchedEffect(key1 = "updateWorkInfoUUID"){
                userStore.saveWorkerInfoUUID(Utils.fireWorker(context.applicationContext))
            }
            HomeScreen()
        }

    }
}


@Composable
@Preview(showBackground = true)
fun BottomPopupProfilePreview(){
    BottomPopupProfile(
        user = User("Ahmed Khedr",
        "Ahmed",
        "ahmedkhaderfcih@gmail.com",
        ""),
        UserStore(LocalContext.current)
    )
}