package com.isoft.yallaorder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.util.SparseArray
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.gson.Gson
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.data.local.OrderRoomDatabase
import com.isoft.yallaorder.data.models.*
import com.isoft.yallaorder.data.netwrok.ApiClient
import com.isoft.yallaorder.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

@Composable
fun HomeScreen(){
    val userStore =
        UserStore(LocalContext.current)
    val apiClient = ApiClient()
    val orderDao = OrderRoomDatabase.getInstance(LocalContext.current.applicationContext).orderDao()
    val user = userStore.getUser.collectAsState(initial = null).value
    val isOrderConfirmedState = userStore.getIsOrderConfirmed().collectAsState(initial = false)
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

//   val workManager =  WorkManager
//        .getInstance(LocalContext.current.applicationContext)
//
//    val workInfos =
//        workManager.getWorkInfosForUniqueWorkLiveData(Constants.GET_DATA_FROM_GOOGLE_SHEETS_WORKER)
//            .observeAsState()
//            .value
//
//    val getSheetDataInfo = remember(key1 = workInfos) {
//        workInfos?.find { it.id == workerInfoUUID }
//    }

    var getSheetDataState by remember {
        mutableStateOf(GoogleSheetStatusWithMenu(""))
    }

    var currentStatus:String? by remember {
        mutableStateOf(null)
    }

    var isShowBottomPopupProfile by remember {
        mutableStateOf(false)
    }
    var isShowBottomPopupRestaurants by remember {
        mutableStateOf(false)
    }
    var isShowBottomPopupSpinWheel by remember {
        mutableStateOf(false)
    }
    var cartNumber by remember {
        mutableStateOf(0)
    }

    var order:Order? by remember {
        mutableStateOf(null)
    }

    var isShowBottomPopupCart by remember {
        mutableStateOf(false)
    }

    val isOrderConfirmed  by remember {
        isOrderConfirmedState
    }

    var isShowBottomPopupMyOrders by remember {
        mutableStateOf(false)
    }

    val textState = remember { mutableStateOf(TextFieldValue("")) }


    val localContext = LocalContext.current

    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        // we will receive data updates in onReceive method.
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra(Constants.GET_GOOGLE_SHEET_MESSAGE)
            // on below line we are updating the data in our text view.
            val getSheetDataStateData = Gson().fromJson(message,GoogleSheetStatusWithMenu::class.java)
            if(currentStatus == null || currentStatus!=getSheetDataStateData.status ||
                (getSheetDataState.selectedRestaurant!=null &&
                        getSheetDataStateData.selectedRestaurant!=null &&
                        getSheetDataStateData.selectedRestaurant.id !=getSheetDataState.selectedRestaurant!!.id)
            ){
                currentStatus = getSheetDataStateData.status
                if(currentStatus!=Constants.STATUS_PENDING ||
                    (getSheetDataState.selectedRestaurant!=null &&
                            getSheetDataStateData.selectedRestaurant!=null &&
                            getSheetDataStateData.selectedRestaurant.id !=getSheetDataState.selectedRestaurant!!.id)){
                    cartNumber = 0
                    order = null
                }
                if(currentStatus==Constants.STATUS_DELIVERED){
                    CoroutineScope(Dispatchers.IO).launch {
                        userStore.saveIsOrderConfirmed(false)
                        orderDao.updateAllOrdersToStatus(Constants.STATUS_DELIVERED)
                    }
                }
                getSheetDataState = getSheetDataState.apply {
                    menuList=null
                }
                getSheetDataState = getSheetDataStateData
            }
        }
    }
    LocalBroadcastManager.getInstance(localContext.applicationContext).registerReceiver(
        broadcastReceiver, IntentFilter(Constants.GET_GOOGLE_SHEET_DATA_LOCAL_BROADCAST)
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeColor)
    ) {
        val (
            profileImage,
            welcomeText,
            userName,
            mobileNumber,
            todayText,
            restaurantName,
            topColorBg,
            topImageBg,
            actionsList,
            selectFromMenuText,
            divider,
            menuList,
            orderStatusImage,
            orderStatusBg,
            cartImage,
            cartNumberText
        ) = createRefs()
        val (
            orderStatusText,
            orderFrom,
            searchBar
        ) = createRefs()


        Box(
            modifier = Modifier
                .constrainAs(topColorBg) {
                    top.linkTo(parent.top)
                    bottom.linkTo(orderFrom.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clip(RoundedCornerShape(0.dp,0.dp,15.dp,15.dp))
                .background(SplashBackground),
        )

        Image(
            modifier = Modifier
                .constrainAs(topImageBg) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .alpha(0.7f),
            painter = painterResource(id = R.drawable.food_bg),
            contentDescription = "topImageBg",
            contentScale = ContentScale.Fit
        )

        Image(
            modifier = Modifier
                .constrainAs(profileImage) {
                    top.linkTo(parent.top, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                }
                .width(70.dp)
                .height(70.dp)
                .clip(CircleShape)
                .clickable {
                    if (!isShowBottomPopupCart &&
                        !isShowBottomPopupMyOrders &&
                        !isShowBottomPopupSpinWheel &&
                        !isShowBottomPopupRestaurants
                    ) {
                        isShowBottomPopupProfile = true
                    }
                },
            painter = rememberAsyncImagePainter(user?.photoUrl),
            contentDescription = "userImage",
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .constrainAs(welcomeText) {
                    top.linkTo(profileImage.top,5.dp)
                    end.linkTo(profileImage.start,10.dp)
                },
            text = stringResource(id = R.string.welcome),
            fontSize = 18.sp,
            fontFamily = bold,
            color = Color.Black
        )
        Text(
            modifier = Modifier
                .constrainAs(userName) {
                    top.linkTo(profileImage.top,5.dp)
                    end.linkTo(welcomeText.start,5.dp)
                },
            text = if(user?.givenName!=null) user.givenName else "",
            fontSize = 19.sp,
            fontFamily = extraBold,
            color = Color.Black
        )
        Text(
            modifier = Modifier
                .constrainAs(mobileNumber) {
                    top.linkTo(userName.bottom,5.dp)
                    end.linkTo(profileImage.start,10.dp)
                },
            text = if(user?.mobileNumber!=null) user.mobileNumber!!
                    else "",
            fontFamily = extraBold,
            color = Color.Black
        )

        Text(
            modifier = Modifier
                .constrainAs(todayText) {
                    top.linkTo(profileImage.bottom,20.dp)
                    end.linkTo(parent.end,20.dp)
                },
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontFamily = bold,
                        fontSize = 20.sp
                    )
                ){
                    append(stringResource(id = R.string.today) )
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.Black,
                        fontFamily = extraBold,
                        fontSize = 22.sp
                    )
                ){
                    val dateFormat = java.text.SimpleDateFormat(
                        "EEEE", Locale("ar")
                    )
                    append(" "+dateFormat.format(Date()))
                }
            }
        )
        Text(
            modifier = Modifier
                .constrainAs(orderFrom) {
                    top.linkTo(todayText.bottom, 10.dp)
                    end.linkTo(parent.end, 20.dp)
                }
                .padding(bottom = 15.dp),
            text = stringResource(id = R.string.order_from),
            fontSize = 20.sp,
            fontFamily = extraBold
        )

        if(getSheetDataState.selectedRestaurant!=null) {
            Text(
                modifier = Modifier
                    .constrainAs(restaurantName) {
                        top.linkTo(orderFrom.top)
                        bottom.linkTo(orderFrom.bottom)
                        end.linkTo(orderFrom.start, 5.dp)
                    }
                    .padding(bottom = 15.dp),
                text = getSheetDataState.selectedRestaurant!!.name,
                fontSize = 20.sp,
                fontFamily = extraBold
            )
        }

        val actions = listOf(
            Action(
                name = stringResource(id = R.string.my_orders),
                image = painterResource(id = R.drawable.my_orders_bg)
            ),
            Action(
                name = stringResource(id = R.string.restaurants),
                image = painterResource(id = R.drawable.restaurants_bg)
            ),
            Action(
                name = stringResource(id = R.string.select_for_me),
                image = painterResource(id = R.drawable.fortune_wheel_bg)
            )
        )
        LazyRow(
            modifier = Modifier
            .constrainAs(actionsList) {
                top.linkTo(topColorBg.bottom,if(isOrderConfirmed) 0.dp else 20.dp)
                end.linkTo(parent.end)
                start.linkTo(parent.start,10.dp)
            }
        ) {
            items(actions){
                ActionItem(action = it,isOrderConfirmed){name->
                    when (name) {
                        localContext.getString(R.string.restaurants) -> {
                            if(!isShowBottomPopupProfile &&
                                !isShowBottomPopupMyOrders &&
                                !isShowBottomPopupSpinWheel &&
                                !isShowBottomPopupCart
                            ) {
                                isShowBottomPopupRestaurants = true
                            }
                        }
                        localContext.getString(R.string.select_for_me) -> {
                            if(!isShowBottomPopupProfile &&
                                !isShowBottomPopupMyOrders &&
                                !isShowBottomPopupCart &&
                                !isShowBottomPopupRestaurants
                            ) {
                                isShowBottomPopupSpinWheel = true
                            }
                        }
                        localContext.getString(R.string.my_orders) -> {
                            if(!isShowBottomPopupProfile &&
                                !isShowBottomPopupCart &&
                                !isShowBottomPopupSpinWheel &&
                                !isShowBottomPopupRestaurants
                            ) {
                                isShowBottomPopupMyOrders = true
                            }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp))
            }
        }
        if(getSheetDataState.status == Constants.STATUS_PENDING && getSheetDataState.menuList!=null && !isOrderConfirmed) {
            Text(
                modifier = Modifier
                    .constrainAs(selectFromMenuText) {
                        top.linkTo(actionsList.bottom,10.dp)
                        end.linkTo(parent.end,10.dp)
                    },
                text = stringResource(id = R.string.select_from_menu),
                color = Color.Black,
                fontFamily = extraBold
            )
            Divider(
                modifier = Modifier
                    .constrainAs(divider) {
                        top.linkTo(selectFromMenuText.bottom,3.dp)
                        end.linkTo(selectFromMenuText.end)
                        start.linkTo(selectFromMenuText.start)
                        width = Dimension.fillToConstraints
                    },
                color = SplashBackground,
                thickness = 1.dp
            )

            SearchView(
                state = textState,
                modifier = Modifier
                    .constrainAs(searchBar){
                        top.linkTo(divider.bottom,16.dp)
                        start.linkTo(parent.start,10.dp)
                        end.linkTo(parent.end,10.dp)
                        width = Dimension.fillToConstraints
                    }
            )

            LazyColumn(
                modifier = Modifier
                .constrainAs(menuList) {
                    top.linkTo(searchBar.bottom, 16.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }) {
                if(getSheetDataState.menuList!=null) {
                    items(Utils.filterMenuItems(getSheetDataState.menuList!!,textState.value.text),
                    key = {it.id}) {
                        MenuItem(
                            menuItemData = it,
                            {menuItemData->
                                cartNumber++
                                if(order==null){
                                    order = Order(
                                        SparseArray<MenuItemData>().apply {
                                            put(menuItemData.id.toInt(),menuItemData)
                                        },
                                        menuItemData.price
                                    )
                                }else{
                                    if(order!!.menuItems[menuItemData.id.toInt()]!=null){
                                        order!!.menuItems[menuItemData.id.toInt()].count = menuItemData.count
                                        order!!.totalPrice +=
                                            order!!.menuItems[menuItemData.id.toInt()].price
                                    }else{
                                        order!!.menuItems.put(menuItemData.id.toInt(),menuItemData)
                                        order!!.totalPrice +=
                                            menuItemData.price
                                    }
                                }
                            },
                            {menuItemData->
                                cartNumber--
                                if(cartNumber==0){
                                    order = null
                                }else {
                                    order!!.totalPrice -=
                                        order!!.menuItems[menuItemData.id.toInt()].price

                                    if(menuItemData.count==0) {
                                        order!!.menuItems.remove(menuItemData.id.toInt())
                                    }else{
                                        order!!.menuItems[menuItemData.id.toInt()].count =
                                            menuItemData.count
                                    }
                                }
                            }
                        ,cartNumber)
                    }
                }
            }
        }else {
            if (getSheetDataState.status == Constants.STATUS_ORDERED) {
                Image(
                    modifier = Modifier
                        .constrainAs(orderStatusBg) {
                            top.linkTo(orderStatusText.bottom, 10.dp)
                            start.linkTo(parent.start, 10.dp)
                            end.linkTo(parent.end, 10.dp)
                            bottom.linkTo(parent.bottom)
                        },
                    painter = painterResource(id = R.drawable.villa_bg),
                    contentDescription = "villa_bg"
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(orderStatusText){
                        top.linkTo(actionsList.bottom, 16.dp)
                        start.linkTo(parent.start, 10.dp)
                        end.linkTo(parent.end, 10.dp)
                    },
                text = when (getSheetDataState.status) {
                    Constants.STATUS_PENDING -> stringResource(id = R.string.collect_order)
                    Constants.STATUS_ORDERED -> stringResource(id = R.string.wait_for_order)
                    Constants.STATUS_DELIVERED -> stringResource(id = R.string.order_arrived)
                    else -> ""
                },
                color = Color.Black,
                fontFamily = extraBold,
                fontSize = 20.sp
            )

                Image(
                    modifier = Modifier
                        .constrainAs(orderStatusImage) {
                            top.linkTo(orderStatusText.bottom, 10.dp)
                            start.linkTo(parent.start, 10.dp)
                            end.linkTo(parent.end, 10.dp)
                            bottom.linkTo(parent.bottom)
                        }
                        .width(500.dp)
                        .height(500.dp),
                    painter = rememberAsyncImagePainter(
                        when (getSheetDataState.status) {
                            Constants.STATUS_ORDERED -> R.drawable.delivery_man
                            Constants.STATUS_DELIVERED -> R.drawable.order_ready_status
                            Constants.STATUS_PENDING -> R.drawable.order_calling_status
                            else -> R.drawable.loading
                        },
                        imageLoader
                    ),
                    contentDescription = "order_status"
                )
        }

        if(cartNumber>0){
            IconButton(
                modifier = Modifier
                    .constrainAs(cartImage) {
                        bottom.linkTo(parent.bottom, 20.dp)
                        start.linkTo(parent.start, 20.dp)
                    }
                    .background(color = SplashBackground, shape = CircleShape),
                onClick = {
                    if(!isShowBottomPopupProfile &&
                        !isShowBottomPopupMyOrders &&
                        !isShowBottomPopupSpinWheel &&
                        !isShowBottomPopupRestaurants
                    ) {
                        isShowBottomPopupCart = true
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "cartImage",
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier
                    .constrainAs(cartNumberText) {
                        top.linkTo(cartImage.top)
                        bottom.linkTo(cartImage.top)
                        start.linkTo(cartImage.start)
                        end.linkTo(cartImage.end, 35.dp)
                    }
                    .drawBehind {
                        drawCircle(
                            color = Orange,
                            radius = 35f
                        )
                    }
                    .padding(5.dp),
                text = cartNumber.toString(),
                color = Color.Black,
                fontFamily = bold,
                fontSize = 12.sp
            )

        }

        AnimatedVisibility(
            visible = isShowBottomPopupProfile,
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
            BottomPopupProfile(user!!,UserStore(LocalContext.current))
        }

        AnimatedVisibility(
            visible = isShowBottomPopupRestaurants,
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
            BottomPopupRestaurants(getSheetDataState.allRestaurants)
        }

        AnimatedVisibility(
            visible = isShowBottomPopupCart,
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
            BottomPopupCart(order = order!!){
                isShowBottomPopupCart = false
                cartNumber = 0
                CoroutineScope(Dispatchers.IO).launch {
                    userStore.saveIsOrderConfirmed(true)
                    val googleSheetPostDataAndOrderTables = Utils.convertOrderToGoogleSheetPostDataAndOrderTables(order!!,user!!,getSheetDataState.selectedRestaurant!!)

                    try {
                        apiClient.api.submitSheet(Constants.ORDERS_RANGE_VALUE,Constants.RAW_VALUE,Constants.INSERT_ROWS_VALUE,Constants.BEARER_VALUE+" "+user!!.accessToken!!,
                            googleSheetPostDataAndOrderTables.googleSheetPostData)
                        orderDao.addOrders(googleSheetPostDataAndOrderTables.orderTables)
                    }catch (e:Exception){
                        if(e is HttpException) {
                            if (e.code() == Constants.UN_AUTHORIZED_CODE) {
                                userStore.saveAccessToken(
                                    Utils.getToken(
                                        localContext.applicationContext,
                                        userStore.getAccessToken(),
                                        userStore.getAccountName(),
                                        userStore.getAccountType(),
                                        Constants.GET_TOKEN_SCOPE
                                    )
                                )
                                apiClient.api.submitSheet(
                                    Constants.ORDERS_RANGE_VALUE,
                                    Constants.RAW_VALUE,
                                    Constants.INSERT_ROWS_VALUE,
                                    Constants.BEARER_VALUE + " " + user!!.accessToken!!,
                                    googleSheetPostDataAndOrderTables.googleSheetPostData
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isShowBottomPopupSpinWheel,
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
            BottomPopupSpinWheel(getSheetDataState.allRestaurants)
        }

        AnimatedVisibility(
            visible = isShowBottomPopupMyOrders,
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
            BottomPopupMyOrders()
        }

        BackHandler(enabled = true) {
            if(isShowBottomPopupProfile) {
                isShowBottomPopupProfile = false
            }else if(isShowBottomPopupRestaurants){
                isShowBottomPopupRestaurants = false
            }else if(isShowBottomPopupCart){
                isShowBottomPopupCart = false
            }else if(isShowBottomPopupSpinWheel){
                isShowBottomPopupSpinWheel = false
            }else if(isShowBottomPopupMyOrders){
                isShowBottomPopupMyOrders = false
            }else{
                (localContext as MainActivity).finish()
            }
        }
    }

}

@Composable
fun MenuItem(menuItemData: MenuItemData,onPlusClick:(menuItemData:MenuItemData)->Unit,
             onMinusClick:(menuItemData:MenuItemData)->Unit,inCartNumber:Int){

    var counter by remember {
        mutableStateOf(menuItemData.count.toString())
    }

    if(inCartNumber==0){
        counter="0"
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (
            name,
            image,
            plus,
            minus,
            number,
            priceText,
            priceEgp,
            divider
        ) = createRefs()
        Image(
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top, 10.dp)
                    end.linkTo(parent.end)
                }
                .clip(RoundedCornerShape(8.dp))
                .width(80.dp)
                .height(70.dp),
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
            fontFamily = extraBold,
            fontSize = 11.sp,
            color = Color.Black
        )
        IconButton(
            modifier = Modifier
                .constrainAs(plus) {
                    bottom.linkTo(image.bottom)
                    end.linkTo(image.start, 8.dp)
                }
                .clip(CircleShape)
                .background(SplashBackground)
                .width(35.dp)
                .height(35.dp),
            onClick = {
                counter = counter.toInt().plus(1).toString()
                //priceValue = (counter.toInt() * menuItemData.price).toString()
                onPlusClick.invoke(menuItemData.apply {
                    count = counter.toInt()
                })
                      },
        ) {
            Icon(imageVector = Icons.Default.Add,
                contentDescription = "add")
        }

        Text(
            modifier = Modifier
                .constrainAs(number){
                    end.linkTo(plus.start,25.dp)
                    top.linkTo(plus.top)
                    bottom.linkTo(plus.bottom)
                },
            text = counter,
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = extraBold
        )
        IconButton(
            modifier = Modifier
                .constrainAs(minus) {
                    bottom.linkTo(image.bottom)
                    end.linkTo(number.start, 25.dp)
                }
                .clip(CircleShape)
                .background(if (counter == "0") SplashBackgroundAlpha else SplashBackground)
                .width(35.dp)
                .height(35.dp),
            onClick = {
                if(counter.toInt()>0) {
                    counter = counter
                        .toInt()
                        .minus(1)
                        .toString()
                    onMinusClick.invoke(menuItemData.apply {
                        count = counter.toInt()
                    })
//                    if(counter.toInt()>0) {
//                        priceValue = (counter.toInt() * menuItemData.price).toString()
//                    }
                }
            },
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_remove),
                contentDescription = "remove")
        }

        Text(
            modifier = Modifier
                .constrainAs(priceText){
                    start.linkTo(priceEgp.end)
                    top.linkTo(plus.top)
                    bottom.linkTo(plus.bottom)
                },
            text = menuItemData.price.toString(),
            fontFamily = extraBold,
            fontSize = 20.sp
        )

        Text(
            modifier = Modifier
                .constrainAs(priceEgp){
                    start.linkTo(parent.start)
                    top.linkTo(priceText.top)
                    bottom.linkTo(priceText.bottom)
                },
            text = stringResource(id = R.string.egp),
            fontFamily = extraBold,
            fontSize = 12.sp
        )
        Divider(modifier = Modifier
            .constrainAs(divider){
                top.linkTo(plus.bottom,10.dp)
                start.linkTo(parent.start,20.dp)
                end.linkTo(parent.end,20.dp)
                width = Dimension.fillToConstraints
            },
            color = SplashBackground)
    }
}
@Composable
@Preview
fun MenuItemPreview(){
//    MenuItem(menuItemData = MenuItemData(
//        "",
//        "ساندوتش بطاطس على بابا غنوج",
//        12.5f
//    ))
}

@Composable
fun ActionItem(
    action:Action,
    isOrderConfirmed:Boolean,
    onCLick:(name:String)->Unit,
){
  ConstraintLayout(modifier = Modifier
      .clickable {
          onCLick.invoke(action.name)
      }
      .padding(
          top = if (isOrderConfirmed) 16.dp else 0.dp,
          start = if (isOrderConfirmed && action.name == stringResource(id = R.string.my_orders)) 10.dp else 0.dp
      )
  ) {
      val (
          name,
          image,
          backgroundBox,
          myOrdersNumberText
      ) = createRefs()

      Box(modifier = Modifier
          .background(SplashBackground, RoundedCornerShape(8.dp))
          .constrainAs(backgroundBox) {
              top.linkTo(image.top)
              bottom.linkTo(image.bottom)
              start.linkTo(parent.start, 5.dp)
              end.linkTo(parent.end)
              width = Dimension.fillToConstraints
              height = Dimension.fillToConstraints
          }
      )

      Text(modifier = Modifier
          .constrainAs(name){
              start.linkTo(parent.start,10.dp)
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
          },
          text = action.name,
          fontFamily = extraBold,
          fontSize = 18.sp
      )
      Image(
          modifier = Modifier
              .constrainAs(image) {
                  start.linkTo(name.end, 10.dp)
                  end.linkTo(parent.end, 10.dp)
                  top.linkTo(parent.top)
              }
              .width(50.dp)
              .height(50.dp),
          painter = action.image,
          contentDescription = "actionImage"
      )

      if(isOrderConfirmed && action.name == stringResource(id = R.string.my_orders)){
          Text(
              modifier = Modifier
                  .constrainAs(myOrdersNumberText) {
                      top.linkTo(backgroundBox.top)
                      bottom.linkTo(backgroundBox.top)
                      start.linkTo(backgroundBox.start)
                      end.linkTo(backgroundBox.start)
                  }
                  .drawBehind {
                      drawCircle(
                          color = Orange,
                          radius = 35f
                      )
                  }
                  .padding(5.dp),
              text = "1",
              color = Color.Black,
              fontFamily = bold,
              fontSize = 12.sp
          )
      }
  }
}

@Composable
@Preview
fun ActionItemPreview(){
    ActionItem(action = Action("طلباتى", painterResource(id = R.drawable.my_orders_bg)),true){

    }
}

@Composable
@Preview
fun HomeScreenPreview(){
    HomeScreen()
}

