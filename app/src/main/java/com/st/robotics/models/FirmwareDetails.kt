package com.st.robotics.models

import javax.inject.Singleton

//STATE TO HANDLE DEVELOPER MODE USER CAN TURN THIS ON BY CLICKING ON APP ICON IMAGE IN INFO TAB ON HOME SCREEN
@Singleton
object FirmwareDetails {
    var firmwareVersion: String? = ""
}