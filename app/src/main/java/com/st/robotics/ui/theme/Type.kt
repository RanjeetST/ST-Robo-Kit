package com.st.robotics.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.st.robotics.R

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val openSansFontFamily = FontFamily(
    Font(R.font.opensans_bold,FontWeight.Bold),
    Font(R.font.opensans_extrabold,FontWeight.ExtraBold),
    Font(R.font.opensans_light,FontWeight.Light),
    Font(R.font.opensans_medium,FontWeight.Medium),
    Font(R.font.opensans_regular,FontWeight.Normal),
    Font(R.font.opensans_semibold,FontWeight.SemiBold)
)