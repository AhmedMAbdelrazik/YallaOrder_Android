package com.isoft.yallaorder

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.commandiron.spin_wheel_compose.SpinWheel
import com.commandiron.spin_wheel_compose.SpinWheelDefaults
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState
import com.isoft.yallaorder.data.models.Restaurant
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.TransparentGray
import com.isoft.yallaorder.ui.theme.bold
import com.isoft.yallaorder.ui.theme.extraBold
import kotlinx.coroutines.launch

@Composable
fun BottomPopupSpinWheel(restaurants:List<Restaurant>?){

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(TransparentGray)
            .wrapContentHeight(align = Alignment.Bottom)
    ) {

        val (
            backgroundBox,
            confusedText,
            spinWheel,
            okWillOrderFromText,
            restaurantNameText,
            pressHereButton
        ) = createRefs()

        Box(modifier = Modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .constrainAs(backgroundBox) {
                top.linkTo(confusedText.top)
                bottom.linkTo(pressHereButton.bottom)
                start.linkTo(parent.start, 5.dp)
                end.linkTo(parent.end, 5.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
        Text(
            modifier = Modifier
                .constrainAs(confusedText) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 16.dp,end = 16.dp),
            text = stringResource(id = R.string.confused_press_here),
            color = SplashBackground,
            fontFamily = bold,
            fontSize = 16.sp
        )

        val textList by remember {
            mutableStateOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8")
            )
        }
        val state = rememberSpinWheelState()
        val scope = rememberCoroutineScope()

        SpinWheel(
            modifier = Modifier
                .constrainAs(spinWheel){
                    top.linkTo(confusedText.bottom,16.dp)
                    start.linkTo(parent.start,16.dp)
                    end.linkTo(parent.end,16.dp)
                },
            state = state,
            colors = SpinWheelDefaults.spinWheelColors(
                frameColor = SplashBackground,
                selectorColor = Color.White,
                dividerColor = SplashBackground,
                pieColors = listOf(
                    Color(0xFFCBCDF7),
                    Color(0xFF9FA2F0),
                    Color(0xFF8185EC),
                    Color(0xFF6469E7),
                    Color(0xFFBCBEF5),
                    Color(0xFF7377E9),
                    Color(0xFF9094EE),
                    Color(0xFFAEB0F2)
                )
            )
        ) {pieIndex ->
            Text(
                text = textList[pieIndex],
                fontSize = 14.sp,
                fontFamily = extraBold
            )
        }
        var selectedRestaurantName by remember {
            mutableStateOf("")
        }
        if(selectedRestaurantName.isNotEmpty()){
            Text(
                modifier = Modifier
                    .constrainAs(okWillOrderFromText) {
                        top.linkTo(spinWheel.bottom,16.dp)
                        end.linkTo(parent.end, 5.dp)
                        start.linkTo(parent.start,5.dp)
                    },
                text = stringResource(id = R.string.ok_will_order),
                color = SplashBackground,
                fontFamily = bold,
                fontSize = 16.sp
            )

            Text(
                modifier = Modifier
                    .constrainAs(restaurantNameText) {
                        top.linkTo(okWillOrderFromText.bottom,16.dp)
                        end.linkTo(parent.end, 5.dp)
                        start.linkTo(parent.start,5.dp)
                    },
                text = selectedRestaurantName,
                color = SplashBackground,
                fontFamily = bold,
                fontSize = 16.sp
            )
        }
        val bottomBarrier = createBottomBarrier(spinWheel, restaurantNameText)

        Button(
            modifier = Modifier
                .constrainAs(pressHereButton){
                    top.linkTo(bottomBarrier,16.dp)
                    start.linkTo(spinWheel.start)
                    end.linkTo(spinWheel.end)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = SplashBackground
            ),
            onClick = {
                scope.launch { state.animate {
                    if(restaurants!=null){
                        val randomIndex = (restaurants.indices).random()
                        selectedRestaurantName = restaurants[randomIndex].name
                    }
                }}
            },
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = stringResource(id = R.string.press_here),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontFamily = extraBold,
                fontSize = 16.sp
            )
        }
    }

}