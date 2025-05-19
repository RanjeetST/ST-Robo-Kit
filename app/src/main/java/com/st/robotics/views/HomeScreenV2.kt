package com.st.robotics.views

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.st.robotics.BuildConfig
import com.st.robotics.R
import com.st.robotics.composables.PdfViewer
import com.st.robotics.composables.WebViewScreen
import com.st.robotics.models.DeveloperMode
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.TertiaryColor
import java.io.File

@SuppressLint("ServiceCast")
@Composable
fun HomeScreenV2(navController: NavController){

    Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to PrimaryColor.copy(alpha = 0.9f),
            0.95f to PrimaryColor.copy(alpha = 0.7f),
            1f to PrimaryColor.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val painter = painterResource(id = R.drawable.idle_bot)
    val showDialog = rememberSaveable { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.Center)
                ) {

                    Row {

                        IconButton(
                            onClick = {
                                if (!isNavigating) {
                                    isNavigating = true
                                    navController.navigate("profile") {
                                        launchSingleTop = true
                                    }
                                    isNavigating = false
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = "Profile",
                                tint = PrimaryColor
                            )
                        }
                    }

                    Image(painter = painterResource(id = R.drawable.st_logo_clear), contentDescription = "ST_LOGO", modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center))

                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {showDialog.value = true}) {
                            Icon(painter = painterResource(id = R.drawable.baseline_info_outline_24), contentDescription = "Info", tint = PrimaryColor)
                        }
                    }
                }
            },
            backgroundColor = Color.White,
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Box{
                Image(painter = painter, contentDescription = "car", contentScale = ContentScale.Crop, modifier = Modifier
                    .size(220.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = Color.Gray,
                        spotColor = Color.Gray
                    )
                )
            }

            Spacer(
                modifier = Modifier
                    .height(50.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Surface(modifier = Modifier
                        .clickable {
                            navController.navigate("device_list")
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(stringResource(id = R.string.pair_your_robot),color = OnPrimary, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 12.dp), textAlign = TextAlign.Center)
                    }

            }
        }
        Box(modifier = Modifier
            .background(PrimaryColor)
            .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd) {
            val scrollState = rememberScrollState()
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 10.dp)
                    .scrollable(
                        state = scrollState,
                        orientation = Orientation.Horizontal
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val itemNames =  listOf("Home","Controller","Monitor","Debug")

                itemsIndexed(items = itemNames) { _, item ->

                    if(item == "Home" || item == "Controller" || item == "Monitor" || item == "Debug"){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clickable { }
                                .align(Alignment.Center),

                        ) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(horizontal = 20.dp)
                                .background(Color.Transparent),
                            ){
                                var iconImage : Int = R.drawable.controller

                                when (item) {
                                    "Debug" -> {
                                        iconImage = R.drawable.debug
                                    }
                                    "Home" -> {
                                        iconImage = R.drawable.home
                                    }
                                    "Controller" -> {
                                        iconImage = R.drawable.controller
                                    }
                                    "Monitor" -> {
                                        iconImage = R.drawable.plot
                                    }
                                }
                                Column(
                                    modifier= Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                    ,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Icon(painter = painterResource(iconImage),
                                        contentDescription = null,
                                        tint = if (item == "Home") OnPrimary else OnPrimary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(25.dp)
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = item,
                                        color = OnPrimary,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally) 
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(showDialog.value) {
        InformationDialog(showDialog = showDialog)
    }
}

@Composable
fun InformationDialog(showDialog : MutableState<Boolean>){
    val showHelpDialog = rememberSaveable { mutableStateOf(false) }
    val showWebPage = rememberSaveable { mutableStateOf(WebPageStatus.OFF) }
    val showPdf = rememberSaveable { mutableStateOf(false) }
    val versionCode = BuildConfig.VERSION_CODE
    val versionName = BuildConfig.VERSION_NAME

    var tapCount by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current
    Dialog(onDismissRequest = { showDialog.value = false }){
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(500.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(OnPrimary)
                .border(
                    width = 2.dp,
                    color = TertiaryColor,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(modifier = Modifier.fillMaxWidth(0.25f), onClick = { showDialog.value = false }) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                painter = painterResource(id = R.drawable.baseline_close_24),
                                contentDescription = "close"
                            )
                        }
                    }
                }


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "App Version: $versionName", color = TertiaryColor, fontSize = 18.sp)
                }

                Image(
                    painter = painterResource(id = R.drawable.robokit_icon_foreground),
                    contentDescription = "app icon",
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            Log.d("developer", "Image clicked")
                            if (tapCount < 5) {
                                tapCount++
                                if (tapCount == 5) {
                                    DeveloperMode.isDeveloper = true
                                    Toast
                                        .makeText(
                                            context,
                                            "Developer Mode Activated",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            } else {
                                tapCount = 0
                            }
                        },
                    contentScale = ContentScale.Crop
                )

                Column {
                    Surface(modifier = Modifier
                        .clickable {
//                            showWebPage.value = WebPageStatus.PRIVACY_POLICY
                            showHelpDialog.value = true
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(
                            text = stringResource(id = R.string.privacy_policy),
                            color = OnPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(modifier = Modifier
                        .clickable {
//                            showHelpDialog.value = true
                            showPdf.value = true
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(
                            text = stringResource(id = R.string.help),
                            color = OnPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }
    if (showWebPage.value != WebPageStatus.OFF) {
        var url: String = ""

        url = if (showWebPage.value == WebPageStatus.EXERCISE_RIGHTS) {
            stringResource(id = R.string.exercise_rights_url)
        } else {
            stringResource(id = R.string.privacy_policy_url)
        }
        Dialog(onDismissRequest = { showWebPage.value = WebPageStatus.OFF }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                WebViewScreen(url = url)
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TertiaryColor
                    ),
                    onClick = { showWebPage.value = WebPageStatus.OFF },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(text = stringResource(id = R.string.done), color = PrimaryColor)
                }
            }
        }
    }



    if(showPdf.value){
        val uri = Uri.parse("file:///android_asset/um.pdf")

        Dialog(onDismissRequest = { showPdf.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Surface {
                    val pdfFile = File(context.filesDir,"um.pdf")
                    PdfViewer(pdfFile)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TertiaryColor
                    ),
                    onClick = { showPdf.value = false},
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(stringResource(id = R.string.done), color = PrimaryColor)
                }
            }
        }
    }


    if(showHelpDialog.value){
        HelpDialog(showHelpDialog)
    }
}

enum class WebPageStatus {
    OFF ,
    PRIVACY_POLICY,
    EXERCISE_RIGHTS
}

@Composable
fun HelpDialog(showDialog: MutableState<Boolean>){

    val showWebPage = rememberSaveable { mutableStateOf(WebPageStatus.OFF) }

    Dialog(onDismissRequest = { showDialog.value = false }){
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(OnPrimary)
                .border(
                    width = 2.dp,
                    color = TertiaryColor,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.fillMaxHeight(0.2f), verticalArrangement = Arrangement.Center) {
                    Text(stringResource(id = R.string.select_option), color = PrimaryColor, fontWeight = FontWeight.SemiBold)
                }

                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                    Surface(modifier = Modifier
                        .clickable {
                            showWebPage.value = WebPageStatus.PRIVACY_POLICY
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(
                            text = stringResource(id = R.string.privacy_policy),
                            color = OnPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Surface(modifier = Modifier
                        .clickable {
                            showWebPage.value = WebPageStatus.EXERCISE_RIGHTS
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(
                            text = stringResource(id = R.string.exercise_rights),
                            color = OnPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Surface(modifier = Modifier
                        .clickable {
                            showDialog.value = false
                        }
                        .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                        .fillMaxWidth(0.8f),
                        color = PrimaryColor
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = OnPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }

    if (showWebPage.value != WebPageStatus.OFF) {
        var url: String

        url = if (showWebPage.value == WebPageStatus.EXERCISE_RIGHTS) {
            stringResource(id = R.string.exercise_rights_url)
        } else {
            stringResource(id = R.string.privacy_policy_url)
        }
        Dialog(onDismissRequest = { showWebPage.value = WebPageStatus.OFF }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                WebViewScreen(url = url)
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TertiaryColor
                    ),
                    onClick = { showWebPage.value = WebPageStatus.OFF },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(stringResource(id = R.string.done), color = PrimaryColor)
                }
            }
        }
    }
}