package com.isoft.yallaorder

import android.os.Build
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.util.size
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.isoft.yallaorder.data.local.OrderRoomDatabase
import com.isoft.yallaorder.data.models.MenuItemData
import com.isoft.yallaorder.data.models.Order
import com.isoft.yallaorder.ui.theme.*
import java.util.*

@Composable
fun BottomPopupMyOrders(){
    var ordersList:List<Order>? by remember {
        mutableStateOf(null)
    }
    val orderDao = OrderRoomDatabase.getInstance(LocalContext.current.applicationContext).orderDao()
    LaunchedEffect(key1 = "orders") {
        val orders = arrayListOf<Order>()
        val ordersNo = orderDao.getOrdersNo()
        for (orderNo in ordersNo) {
            orders.add(Utils.convertOrderTablesToOrder(orderDao.getOrders(orderNo)))
        }
        ordersList = orders
    }
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(TransparentGray)
        .wrapContentHeight(align = Alignment.Bottom),
    ) {
        val (
            backgroundBox,
            orders,
            noOrdersText
        ) = createRefs()

        Box(modifier = Modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .constrainAs(backgroundBox) {
                if(ordersList!=null && ordersList!!.isNotEmpty()) {
                    top.linkTo(orders.top)
                    bottom.linkTo(orders.bottom)
                    start.linkTo(orders.start, 5.dp)
                    end.linkTo(orders.end, 5.dp)
                }else{
                    top.linkTo(noOrdersText.top)
                    bottom.linkTo(noOrdersText.bottom)
                    start.linkTo(noOrdersText.start, 5.dp)
                    end.linkTo(noOrdersText.end, 5.dp)
                }
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )

        if (ordersList != null && ordersList!!.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .constrainAs(orders) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .heightIn(max = 400.dp)
                    .fillMaxWidth()
            ) {
                items(ordersList!!) {order->
                    OrderItem(order = order)
                }
            }
        }else{
            Text(
                modifier = Modifier
                    .constrainAs(noOrdersText){
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 100.dp, bottom = 100.dp),
                text = stringResource(id = R.string.no_orders_found),
                fontFamily = bold,
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

    }

}

@Composable
fun OrderItem(order: Order){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
    ) {
        val (
            backgroundBox,
            pendingImage,
            restaurantName,
            date,
            totalPriceText,
            egpText,
            menuItemsList,
            divider
        ) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(backgroundBox) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(restaurantName.top)
                    bottom.linkTo(totalPriceText.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .background(SplashBackgroundAlpha, RoundedCornerShape(topStart = 30f, topEnd = 30f))
        )
        Text(
            modifier = Modifier
                .constrainAs(restaurantName) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 10.dp, end = 20.dp),
            text = order.restaurantName,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = extraBold
        )

        Text(
            modifier = Modifier
                .constrainAs(date) {
                    top.linkTo(restaurantName.bottom, 10.dp)
                    end.linkTo(parent.end)
                }
                .padding(end = 20.dp),
            text = order.date,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = extraBold
        )
        if(order.status == Constants.STATUS_PENDING){
            Image(
                modifier = Modifier
                    .constrainAs(pendingImage) {
                        top.linkTo(parent.top, 10.dp)
                        end.linkTo(restaurantName.start, 20.dp)
                    }
                    .width(20.dp)
                    .height(20.dp),
                painter = rememberAsyncImagePainter(
                    R.drawable.pending,
                    imageLoader
                ),
                contentDescription = "pending"
            )
        }
        Text(
            modifier = Modifier
                .constrainAs(egpText) {
                    bottom.linkTo(totalPriceText.bottom)
                    start.linkTo(parent.start)
                }
                .padding(bottom = 9.dp, start = 20.dp),
            text = stringResource(id = R.string.egp),
            fontSize = 12.sp,
            color = Color.Black,
            fontFamily = extraBold
        )

        Text(
            modifier = Modifier
                .constrainAs(totalPriceText) {
                    top.linkTo(restaurantName.bottom, 12.dp)
                    start.linkTo(egpText.end, 3.dp)
                }
                .padding(bottom = 8.dp),
            text = order.totalPrice.toString(),
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = extraBold
        )
        Column(
            modifier = Modifier
                .constrainAs(menuItemsList){
                    top.linkTo(backgroundBox.bottom,10.dp)
                }
        ) {
            (0 until order.menuItems.size).forEach { index->
                MenuItem(menuItemData = order.menuItems.valueAt(index))
                if(index!=order.menuItems.size-1){
                    Spacer(
                        modifier = Modifier
                            .height(20.dp)
                    )
                }
            }
        }
        Divider(
            modifier = Modifier
                .constrainAs(divider){
                    top.linkTo(menuItemsList.bottom,8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            color = SplashBackground
        )
    }
}

@Composable
fun MenuItem(menuItemData: MenuItemData){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val (
            markImage,
            name,
            egpText,
            count,
            totalPrice
        ) = createRefs()

        Image(
            modifier = Modifier
                .constrainAs(markImage) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .width(30.dp)
                .height(30.dp),
            painter = painterResource(id = R.drawable.mark),
            contentDescription = "mark"
        )

        Text(
            modifier = Modifier
                .constrainAs(name){
                    end.linkTo(markImage.start,10.dp)
                    top.linkTo(markImage.top)
                },
            text = menuItemData.name,
            color = Color.Black,
            fontFamily = bold,
            fontSize = 13.sp
        )
        Text(
            modifier = Modifier
                .constrainAs(count){
                    end.linkTo(name.end,8.dp)
                    top.linkTo(name.bottom,2.dp)
                },
            text = menuItemData.price.toString() +" x "+menuItemData.count.toString(),
            color = Color.Black,
            fontFamily = bold,
            fontSize = 13.sp
        )

        Text(
            modifier = Modifier
                .constrainAs(egpText){
                    top.linkTo(markImage.top,7.dp)
                    start.linkTo(parent.start)
                },
            text = stringResource(id = R.string.egp),
            fontSize = 12.sp,
            color = Color.Black,
            fontFamily = extraBold
        )

        Text(
            modifier = Modifier
                .constrainAs(totalPrice){
                    top.linkTo(markImage.top,5.dp)
                    start.linkTo(egpText.end,5.dp)
                },
            text = (menuItemData.price * menuItemData.count).toString(),
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = extraBold
        )
    }
}