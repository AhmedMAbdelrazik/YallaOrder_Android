package com.isoft.yallaorder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.isoft.yallaorder.data.models.MenuItemData
import com.isoft.yallaorder.data.models.Order
import com.isoft.yallaorder.ui.theme.SplashBackground
import com.isoft.yallaorder.ui.theme.TransparentGray
import com.isoft.yallaorder.ui.theme.bold
import com.isoft.yallaorder.ui.theme.extraBold

@Composable
fun BottomPopupCart(order: Order,onClick:()->Unit){
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(TransparentGray)
        .wrapContentHeight(align = Alignment.Bottom)
    ){
        val (
            orderItemsList,
            backgroundBox,
            totalPriceText,
            totalPriceValue,
            currencyText,
            deliveryServiceText,
            confirmButton,
            divider
        ) = createRefs()

        Box(modifier = Modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .constrainAs(backgroundBox) {
                top.linkTo(orderItemsList.top)
                bottom.linkTo(confirmButton.bottom)
                start.linkTo(parent.start, 10.dp)
                end.linkTo(parent.end, 10.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
        LazyColumn(modifier = Modifier
            .constrainAs(orderItemsList) {
                top.linkTo(parent.top)
                start.linkTo(backgroundBox.start, 16.dp)
                end.linkTo(backgroundBox.end, 16.dp)
                width = Dimension.fillToConstraints
            }.heightIn(max = 300.dp)
        ){
            items(order.menuItems.size()){
                OrderItem(menuItemData =order.menuItems.valueAt(it))
            }
        }
        Divider(
            modifier = Modifier
                .constrainAs(divider) {
                    top.linkTo(orderItemsList.bottom)
                    start.linkTo(backgroundBox.start, 16.dp)
                }
                .width(80.dp),
            color = SplashBackground
        )
        Text(
            modifier = Modifier
                .constrainAs(totalPriceText) {
                    top.linkTo(divider.bottom,20.dp)
                    end.linkTo(backgroundBox.end, 16.dp)
                },
            text = stringResource(id = R.string.total_price),
            color = Color.Black,
            fontFamily = extraBold,
            fontSize = 15.sp
        )
        Text(
            modifier = Modifier
                .constrainAs(totalPriceValue) {
                    top.linkTo(divider.bottom)
                    end.linkTo(divider.end, 10.dp)
                },
            text = order.totalPrice.toString(),
            color = Color.Black,
            fontFamily = extraBold,
            fontSize = 15.sp
        )

        Text(
            modifier = Modifier
                .constrainAs(currencyText) {
                    top.linkTo(totalPriceValue.top)
                    bottom.linkTo(totalPriceValue.bottom)
                    end.linkTo(totalPriceValue.start, 3.dp)
                },
            text = " "+stringResource(id = R.string.egp),
            color = Color.Black,
            fontFamily = extraBold,
            fontSize = 12.sp
        )

        Text(
            modifier = Modifier
                .constrainAs(deliveryServiceText) {
                    top.linkTo(totalPriceValue.bottom)
                    end.linkTo(totalPriceValue.end)
                },
            text = stringResource(id = R.string.plus_delivery_service),
            color = Color.Black,
            fontFamily = bold,
            fontSize = 10.sp
        )
        Button(
            modifier = Modifier
                .constrainAs(confirmButton){
                    top.linkTo(deliveryServiceText.bottom)
                    start.linkTo(currencyText.start,20.dp)
                    end.linkTo(totalPriceText.end,20.dp)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black
            ),
            onClick = onClick,
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = stringResource(id = R.string.confirm),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = extraBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun OrderItem(menuItemData: MenuItemData){
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
    ){
        val (
            image,
            name,
            count,
            priceText,
            currencyText
        ) = createRefs()
        Image(
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top, 20.dp)
                    end.linkTo(parent.end)
                }
                .width(40.dp)
                .height(30.dp),
            painter = rememberAsyncImagePainter(
                menuItemData.imageUrl,
                placeholder = painterResource(id = R.drawable.placeholder_image)
            ),
            contentDescription = "menuItemImage",
            contentScale = ContentScale.FillHeight
        )
        Text(
            modifier = Modifier
                .constrainAs(name) {
                    top.linkTo(image.top)
                    end.linkTo(image.start,8.dp)
                },
            text = menuItemData.name,
            fontFamily = bold,
            fontSize = 11.sp,
            color = Color.Black
        )

        Text(
            modifier = Modifier
                .constrainAs(count) {
                    top.linkTo(name.bottom)
                    end.linkTo(image.start,10.dp)
                },
            text = menuItemData.count.toString()+" x "+menuItemData.price.toString(),
            fontFamily = bold,
            fontSize = 11.sp,
            color = Color.Black
        )

        Text(
            modifier = Modifier
                .constrainAs(priceText) {
                    top.linkTo(image.top)
                    bottom.linkTo(image.bottom)
                    start.linkTo(currencyText.end,3.dp)
                },
            text = (menuItemData.price * menuItemData.count).toString(),
            fontFamily = bold,
            fontSize = 11.sp,
            color = Color.Black
        )
        Text(
            modifier = Modifier
                .constrainAs(currencyText) {
                    top.linkTo(priceText.top)
                    bottom.linkTo(priceText.bottom)
                    start.linkTo(parent.start,16.dp)
                },
            text = " "+stringResource(id = R.string.egp),
            fontFamily = bold,
            fontSize = 11.sp,
            color = Color.Black
        )
    }
}