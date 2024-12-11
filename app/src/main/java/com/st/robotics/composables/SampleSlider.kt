package com.st.robotics.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.st.robotics.ui.theme.OnPrimary

@Composable
fun SegmentedControl() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Lock", "Drive", "Follow me", "Autopilot")

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
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
                    color = if (selectedIndex == index) OnPrimary else Color(0xFF7A7A7A),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Color(0xFFDBDEE2), shape = RoundedCornerShape(size = 6.dp))
                .height(75.dp)
                .background(color = Color(0xFF01142C), shape = RoundedCornerShape(size = 6.dp))
        ) {
            val boxWidth = constraints.maxWidth.toFloat()
            val segmentWidth = boxWidth / options.size

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
                    .width(segmentWidth.dp - 16.dp)
                    .offset(x = (selectedIndex * segmentWidth).dp)
                    .background(Color(0xFF0A357E), shape = RoundedCornerShape(6.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .fillMaxHeight(0.7f)
                        .background(Color(0xFF85CAE7), shape = RoundedCornerShape(25.dp))
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