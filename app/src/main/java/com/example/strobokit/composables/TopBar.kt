package com.example.strobokit.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.STRoboKitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        colors = topAppBarColors(
            containerColor = PrimaryColor,
            scrolledContainerColor = PrimaryColor,
            titleContentColor = OnPrimary,
            actionIconContentColor = OnPrimary
        ),
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                onBack?.let{
                    IconButton(onClick = it) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = title,modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.weight(1f))
            }
        },

    )
}

/** ----------------------- PREVIEW --------------------------------------- **/

@Preview(showBackground = true)
@Composable
private fun StTopBarPreview() {
    STRoboKitTheme {
        StTopBar(
            title = "TITLE"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StTopBarWithBackPreview() {
    /**NOOP**/
    STRoboKitTheme {
        StTopBar(
            title = "TITLE",
            onBack = { /**NOOP**/ }
        )
    }
}