package com.example.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.designsystem.R

val notoSansFont = FontFamily(
    Font(R.font.noto_sans_bold, weight = FontWeight.Bold),
    Font(R.font.noto_sans_medium, weight = FontWeight.Medium),
    Font(R.font.noto_sans_bold, weight = FontWeight.Normal)
)

val Typography = Typography(
    titleMedium = setTextStyle(24.sp, FontWeight(700), 28.sp),
    bodyLarge = setTextStyle(12.sp, FontWeight(400), 16.sp),
    bodySmall = setTextStyle(8.sp, FontWeight(700), 12.sp)
)

private fun setTextStyle(
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Black
) = TextStyle(
    color = color,
    fontFamily = notoSansFont,
    fontSize = fontSize,
    fontWeight = fontWeight,
    lineHeight = lineHeight
)