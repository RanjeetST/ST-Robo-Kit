package com.st.robotics.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay

//FUNCTION TO HANDLE ORIENTATION OF THE PAGE
fun ChangeOrientationToLandscape(
    context: Context
){
    val activity = context as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

}

fun ChangeOrientationToPortrait(
    context: Context
){
    val activity = context as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}