package com.example.strobokit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.utilities.toPx

@Composable
fun SegmentedControl() {
    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("Lock", "Drive", "Follow me", "Autopilot")

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Mode",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp,color = Color(0xFFDBDEE2), shape = RoundedCornerShape(size = 6.dp))
                .height(75.00002.dp)
                .width(318.dp)
                .background(color = Color(0xFFF7F8FA), shape = RoundedCornerShape(size = 6.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                options.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedIndex = index },

                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight()
                    .width(30.dp)
                    .offset(x = ((selectedIndex * (90.dp + 8.dp))).value.dp)
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .padding(8.dp)
                    ,
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .fillMaxHeight(0.7f)

                        .background(Color(0xFF0047B2), shape = RoundedCornerShape(25.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEachIndexed { index, option ->
                Text(
                    text = option,
                    fontSize = 14.sp,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedIndex == index) Color(0xFF1A1A1A) else Color(0xFF7A7A7A),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSegmentedControl() {
    SegmentedControl()
}