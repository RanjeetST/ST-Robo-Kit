package com.example.strobokit.views

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.strobokit.ui.theme.ErrorColor
import com.example.strobokit.ui.theme.GreyColor
import com.example.strobokit.ui.theme.OnPrimary
import com.example.strobokit.ui.theme.PrimaryColor
import com.example.strobokit.ui.theme.TertiaryColor
import com.example.strobokit.viewModels.DebugConsoleMsg
import com.example.strobokit.viewModels.DebugConsoleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DebugConsole(
    viewModel: DebugConsoleViewModel,
    nodeId : String
){
    val debugMessages by viewModel.debugMessages.collectAsStateWithLifecycle()
    var queryState by rememberSaveable { mutableStateOf(value = "") }
    val scrollState = rememberScrollState()
    var autoScroll by rememberSaveable { mutableStateOf(value = true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = debugMessages.size) {
        coroutineScope.launch {
            if(autoScroll){
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }
    
    val annotedString : AnnotatedString  = buildAnnotatedString {
        debugMessages.forEach{
            when(it){
                is DebugConsoleMsg.DebugConsoleCommand -> {
                    withStyle(style = SpanStyle(TertiaryColor)){
                        append("[${it.time}>] ${it.command}")
                        append("\n")
                    }
                }

                is DebugConsoleMsg.DebugConsoleResponse -> {
                    withStyle(style = SpanStyle(if(it.response.isError) ErrorColor else GreyColor )){
                        if(it.time != null) {
                            append("[${it.time}<]")
                        }
                        append(it.response.payload)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            value = queryState,
            label = { Text(text = "Send Debug Message")},
            onValueChange = {queryState = it},
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.sendDebugMessage(nodeId = nodeId,msg = queryState)
                    queryState = ""
                    keyboardController?.hide()
                }) {
                    Icon(
                        tint = PrimaryColor,
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                }
            }
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .verticalScroll(state = scrollState),
            text = annotedString
        )
    }
}

@Composable
@Preview
fun DebugConsolePreview(){
    var queryState by rememberSaveable { mutableStateOf(value = "") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            value = queryState,
            label = { Text(text = "Send Debug Message")},
            onValueChange = {queryState = it},
            trailingIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        tint = PrimaryColor,
                        imageVector = Icons.Default.Send,
                        contentDescription = null
                    )
                }
            }
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            text = "HELLo"
        )
    }
}
