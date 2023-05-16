package com.isoft.yallaorder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.data.models.Restaurant
import com.isoft.yallaorder.data.netwrok.ApiClient
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.TransparentGray
import com.isoft.yallaorder.ui.theme.bold
import retrofit2.HttpException

@Composable
fun BottomPopupRestaurants(restaurants:List<Restaurant>?){
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(TransparentGray)
        .wrapContentHeight(align = Alignment.Bottom)
    ){
        val (
            restaurantsList,
            backgroundBox,
        ) = createRefs()

        Box(modifier = Modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .constrainAs(backgroundBox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 5.dp)
                end.linkTo(parent.end, 5.dp)
                width = Dimension.fillToConstraints
            }
            .height(500.dp)
        )
        if(restaurants!=null) {
            LazyColumn(modifier = Modifier
                .constrainAs(restaurantsList) {
                    top.linkTo(backgroundBox.top)
                    bottom.linkTo(backgroundBox.bottom)
                    start.linkTo(backgroundBox.start, 16.dp)
                    end.linkTo(backgroundBox.end, 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }) {
                items(restaurants!!) {
                    RestaurantItem(restaurant = it)
                }
            }
        }
    }
}

@Composable
@Preview
fun BottomPopupRestaurantsPreview(){
    BottomPopupRestaurants(arrayListOf())
}

@Composable
fun RestaurantItem(restaurant: Restaurant){
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (
            image,
            name,
            rate,
            address,
            divider
        ) = createRefs()
        Image(
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top, 10.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                }
                .height(200.dp),
            painter = rememberAsyncImagePainter(
                restaurant.image,
                placeholder = painterResource(id = R.drawable.placeholder_image)
            ),
            contentDescription = "restaurantImage"
        )
        Text(modifier = Modifier
            .constrainAs(name) {
                top.linkTo(image.bottom, 10.dp)
                end.linkTo(parent.end)
            },
        text = restaurant.name,
        fontFamily = bold,
        fontSize = 14.sp,
        color = SplashBackground
        )

        RatingBar(
            modifier = Modifier
                .constrainAs(rate) {
                    top.linkTo(image.bottom, 10.dp)
                    start.linkTo(parent.start)
                },
            rating = restaurant.rate.toFloat(),
            spaceBetween = 1.dp
        )
        Text(modifier = Modifier
            .constrainAs(address) {
                top.linkTo(name.bottom, 10.dp)
                end.linkTo(parent.end)
            },
            text = restaurant.address,
            fontFamily = bold,
            fontSize = 14.sp,
            color = SplashBackground
        )
        Divider(modifier = Modifier
            .constrainAs(divider){
                top.linkTo(address.bottom,10.dp)
                start.linkTo(parent.start,20.dp)
                end.linkTo(parent.end,20.dp)
                width = Dimension.fillToConstraints
            },
            color = SplashBackground
        )
    }
}

@Composable
@Preview
fun RestaurantItem(){
    RestaurantItem(restaurant = Restaurant(
        "1",
        "",
        "مطعم عم بشندى",
        "الهضبة الوسطى المقطم",
        "4.5",
    )
    )
}
