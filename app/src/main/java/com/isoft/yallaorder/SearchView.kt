package com.isoft.yallaorder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.extraBold

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchView(state: MutableState<TextFieldValue>,modifier: Modifier) {
    BasicTextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = modifier
            .border(1.dp, SplashBackground, RoundedCornerShape(5.dp)),
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp, fontFamily = extraBold, textAlign = TextAlign.End),
    ){
        TextFieldDefaults.TextFieldDecorationBox(
            value = state.value.text,
            innerTextField = it,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = MutableInteractionSource(),
            placeholder = {
                Text(
                    text = stringResource(
                        id = R.string.search
                    ),
                    modifier=Modifier.fillMaxWidth(),
                    color = Color.LightGray,
                    fontFamily = extraBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                )
            },
            contentPadding = PaddingValues(0.dp),
            leadingIcon = {
                if (state.value != TextFieldValue("")) {
                    IconButton(
                        onClick = {
                            state.value =
                                TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp)
                        )
                    }
                }
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = SplashBackground,
            leadingIconColor = SplashBackground,
            trailingIconColor = SplashBackground,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
        )
    }

//    TextField(
//        value = state.value,
//        onValueChange = { value ->
//            state.value = value
//        },
//        modifier = modifier
//            .border(1.dp, SplashBackground, RoundedCornerShape(5.dp)),
//        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
//        placeholder = {
//            Text(
//                stringResource(id = R.string.search),
//                color = Color.LightGray,
//                fontFamily = extraBold,
//                fontSize = 14.sp
//            )},
//        leadingIcon = {
//            Icon(
//                Icons.Default.Search,
//                contentDescription = "Search Icon",
//                modifier = Modifier
//                    .padding(15.dp)
//                    .size(24.dp)
//            )
//        },
//        trailingIcon = {
//            if (state.value != TextFieldValue("")) {
//                IconButton(
//                    onClick = {
//                        state.value =
//                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
//                    }
//                ) {
//                    Icon(
//                        Icons.Default.Close,
//                        contentDescription = "",
//                        modifier = Modifier
//                            .padding(15.dp)
//                            .size(24.dp)
//                    )
//                }
//            }
//        },
//        singleLine = true,
//        colors = TextFieldDefaults.textFieldColors(
//            textColor = Color.Black,
//            cursorColor = SplashBackground,
//            leadingIconColor = SplashBackground,
//            trailingIconColor = SplashBackground,
//            backgroundColor = Color.Transparent,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//            disabledIndicatorColor = Color.Transparent
//        )
//    )
}