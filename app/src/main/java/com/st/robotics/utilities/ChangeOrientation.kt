package com.st.robotics.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable

@Composable
fun ChangeOrientationToLandscape(
    context: Context
){
    val activity = context as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

@Composable
fun ChangeOrientationToPortrait(
    context: Context
){
    val activity = context as Activity
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}