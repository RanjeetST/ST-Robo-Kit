package com.example.strobokit.composables

import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.TertiaryColor
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
@Preview(showBackground = true, widthDp = 800, heightDp = 400)
fun Controller(){
    val gradientBrush = Brush.radialGradient(
        0.0f to TertiaryColor,
        1f to Color.DarkGray,
        radius = 1400.0f,
        tileMode = TileMode.Repeated
    )

    Row(modifier = Modifier
        .fillMaxSize()
        .background(brush = gradientBrush)) {

        //left column
        Column(modifier = Modifier
            .fillMaxWidth(0.3f)
        ) {
            //Top left status icon row
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()) {
                Text(text = "STATUS ICONS")
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Linear Motion")
                JoyStick()
            }
        }

        //Mid Column
        Column(modifier = Modifier
            .fillMaxWidth(0.5f)) {

            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){

                SafetyButton()
                Text(text = "Disarmed",color = OnPrimary,)
            }
        }

        //Right Column
        Column(modifier = Modifier
            .fillMaxWidth()) {
            //Top Right close button
            Row(modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
                .padding(20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { /* Handle close action */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Button",
                        tint = OnPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(color = OnPrimary, fontSize = 18.sp, text = "Direction Motion")
                Spacer(modifier = Modifier.height(10.dp))
                    DirectionMotion()
            }
        }
    }
}

@Composable
fun SafetyButton() {
    // Remember the state of the switch
    val isChecked = remember { mutableStateOf(false) }

    // Switch component
    Switch(
        checked = isChecked.value,
        onCheckedChange = { isChecked.value = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFF611616),
            checkedTrackColor = Color(0xFF11FF00)
        ),
        modifier = Modifier.padding(16.dp)
    )
}