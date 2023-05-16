package com.isoft.yallaorder.data.models

data class ValueRange(
    val majorDimension: String,
    val range: String,
    val values: List<List<String>>
)