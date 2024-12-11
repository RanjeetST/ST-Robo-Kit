package com.st.robotics.utilities

object SessionManager {
    private var splashShown: Boolean = false

    fun isSplashShown(): Boolean {
        return splashShown
    }

    fun setSplashShown(value: Boolean) {
        splashShown = value
    }
}
