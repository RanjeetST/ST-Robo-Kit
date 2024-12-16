package com.st.robotics.composables

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url : String) {
    //GIVES WEB VIEW FOR EXERCISE RIGHTS AND PRIVACY POLICY POLICY
    AndroidView(
        factory = { context ->
             return@AndroidView WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(false)
            }
        },
        update = {
            it.loadUrl(url)
        },
        modifier = Modifier.fillMaxSize()
    )
}
