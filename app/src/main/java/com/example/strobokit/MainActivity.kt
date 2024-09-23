package com.example.strobokit

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.strobokit.composables.Controller
import com.example.strobokit.ui.theme.STRoboKitTheme
import com.example.strobokit.utilities.SessionManager
import com.example.strobokit.views.BleDeviceList
import com.example.strobokit.views.DebugConsole
import com.example.strobokit.views.DeviceDetail
import com.example.strobokit.views.FeatureDetail
import com.example.strobokit.views.HomeScreen
import com.example.strobokit.views.PlotChart
import com.example.strobokit.views.PlotChartNew
import com.example.strobokit.views.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Reset the splash screen flag on app start
        SessionManager.setSplashShown(false)
        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen(){
    //to navigate between activities
    val navController = rememberNavController()
    val startDestination = if (SessionManager.isSplashShown()) "home" else "splash_screen"

    STRoboKitTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ){innerPadding ->
            NavHost(navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)) {

                composable(route = "splash_screen"){
                    SplashScreen(navController = navController)
                }

                composable(route = "home"){
                    HomeScreen(navController = navController)
                }

                composable(route="device_list"){
                    BleDeviceList(viewModel = hiltViewModel(),navController = navController)
                }

                composable(
                    route = "detail/{deviceId}",
                    arguments = listOf(navArgument("deviceId"){type = NavType.StringType})
                ){backStackEntry ->
                   backStackEntry.arguments?.getString("deviceId")?.let{deviceId ->
                       DeviceDetail(
                           viewModel = hiltViewModel(),
                           navController = navController,
                           deviceId = deviceId
                       )
                   }
                }

                composable(
                    route = "feature/{deviceId}/{featureName}",
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType },
                        navArgument("featureName") { type = NavType.StringType })
                ){
                        backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        backStackEntry.arguments?.getString("featureName")?.let { featureName ->
                            FeatureDetail(
                                viewModel = hiltViewModel(),
                                navController = navController,
                                deviceId = deviceId,
                                featureName = featureName
                            )
                        }
                    }
                }

                composable(
                    route = "feature/{deviceId}/debugConsole",
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
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
                    route = "feature/{deviceId}/controller",
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        Controller(
                            viewModel = hiltViewModel(),
                            nodeId = deviceId,
                            navController = navController
                        )
                    }
                }

                composable(
                    route = "feature/{deviceId}/plot",
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                        PlotChartNew(
                            viewModel = hiltViewModel(),
                            deviceId = deviceId,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}