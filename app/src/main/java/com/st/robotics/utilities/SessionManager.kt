package com.st.robotics.utilities

//TO MANAGER SESSION FOR CUSTOM SPLASH SCREEN
object SessionManager {
    private var splashShown: Boolean = false


    fun isSplashShown(): Boolean {
        return splashShown
    }

    fun setSplashShown(value: Boolean) {
        splashShown = value
    }


}
