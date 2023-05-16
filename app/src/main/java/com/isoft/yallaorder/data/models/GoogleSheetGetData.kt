package com.isoft.yallaorder.data.models

data class GoogleSheetGetData(
    val spreadsheetId: String,
    val valueRanges: List<ValueRange>
)