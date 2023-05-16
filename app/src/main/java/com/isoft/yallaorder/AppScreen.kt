package com.isoft.yallaorder

import androidx.compose.ui.graphics.painter.Painter

sealed class AppScreen(
    val title:String,
    val route:String,
    val image:Painter
)
