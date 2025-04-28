package com.st.robotics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.st.robotics.composables.Controller
import com.st.robotics.composables.copyPdfFromAssets
import com.st.robotics.ui.theme.STRoboKitTheme
import com.st.robotics.utilities.SessionManager
import com.st.robotics.views.BleDeviceListV2
import com.st.robotics.views.DebugConsole
import com.st.robotics.views.DeviceDetailV2
import com.st.robotics.views.FotaScreen
import com.st.robotics.views.HomeScreenV2
import com.st.robotics.views.PlotChartV2
import com.st.robotics.views.ProfileScreen
import com.st.robotics.views.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyPdfFromAssets(this, "um.pdf")
        enableEdgeToEdge()
        SessionManager.setSplashShown(false)
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen(){

    //To navigate between activities
    val navController = rememberNavController()
    val startDestination = if (SessionManager.isSplashShown()) "home" else "splash_screen"

    STRoboKitTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ){innerPadding ->
            NavHost(navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)) {

                composable(
                    route = "splash_screen"
                ){
                    SplashScreen(navController = navController)
                }

                composable(
                    route = "home"
                ){
                    HomeScreenV2(navController = navController)
                }

                composable(
                    route="device_list"
                ){
                    BleDeviceListV2(viewModel = hiltViewModel(),navController = navController)
                }

                composable(
                    route = "detail/{deviceId}",
                    arguments = listOf(navArgument("deviceId"){type = NavType.StringType})
                ){backStackEntry ->
                   backStackEntry.arguments?.getString("deviceId")?.let{deviceId ->
                       DeviceDetailV2 (
                           viewModel = hiltViewModel(),
                           navController = navController,
                           deviceId = deviceId
                       )
                   }
                }

                composable(
                    route = "feature/{deviceId}/debugConsole",
                    arguments = listOf(
                        navArgument("deviceId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        DebugConsole(
                            viewModel = hiltViewModel(),
                            nodeId = deviceId,
                            navController = navController
                        )
                    }
                }

                composable(
                    route = "feature/{deviceId}/controller/{batteryPercentage}",
                    arguments = listOf(
                        navArgument("deviceId") { type = NavType.StringType },
                        navArgument("batteryPercentage") { type = NavType.FloatType }
                    )
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        backStackEntry.arguments?.getInt("batteryPercentage")
                            ?.let { batteryPercentage ->
                                Controller(
                                    viewModel = hiltViewModel(),
                                    nodeId = deviceId,
                                    navController = navController,
                                    batteryVoltage = batteryPercentage
                                )
                            }
                    }
                }

                composable(
                    route = "feature/{deviceId}/plot",
                    arguments = listOf(
                        navArgument("deviceId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        PlotChartV2(
                            viewModel = hiltViewModel(),
                            deviceId = deviceId,
                            navController = navController
                        )
                    }
                }

                composable(
                    route = "feature/{deviceId}/fota",
                    arguments = listOf(
                        navArgument("deviceId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        FotaScreen(
                            viewModel = hiltViewModel(),
                            deviceId = deviceId,
                            navController = navController
                        )
                    }
                }

                composable(
                    route = "profile"
                ) {
                    ProfileScreen(viewModel = hiltViewModel(),navController = navController)
                }
            }
        }
    }
}