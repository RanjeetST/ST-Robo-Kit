package com.st.robotics.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.st.robotics.ui.theme.ErrorColor
import com.st.robotics.R
import com.st.robotics.ui.theme.BrownColor
import com.st.robotics.ui.theme.GreyColor
import com.st.robotics.ui.theme.OnPrimary
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.ui.theme.ST_Magenta
import com.st.robotics.ui.theme.ST_Maroon
import com.st.robotics.ui.theme.SuccessColor
import com.st.robotics.ui.theme.TertiaryColor
import com.st.robotics.viewModels.DebugConsoleMsg
import com.st.robotics.viewModels.DebugConsoleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DebugConsole(
    viewModel: DebugConsoleViewModel,
    nodeId : String,
    navController: NavController
){
    val debugMessages by viewModel.debugMessages.collectAsStateWithLifecycle()
    var queryState by rememberSaveable { mutableStateOf(value = "") }
    val scrollState = rememberScrollState()
    val autoScroll by rememberSaveable { mutableStateOf(value = true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = debugMessages.size) {
        coroutineScope.launch {
            if(autoScroll){
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.receiveDebugMessage(nodeId = nodeId)
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = { navController.popBackStack() }) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            androidx.compose.material3.Text(
                text = stringResource(id = R.string.debug_console),
                fontSize = 20.sp,
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            value = queryState,
            label = { Text(text = stringResource(id = R.string.send_debug_message))},
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

        Row(modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Surface(modifier = Modifier
                .clickable {
                    viewModel.sendDebugMessage(nodeId = nodeId,msg = "info")
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = TertiaryColor
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.info),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                    viewModel.sendDebugMessage(nodeId = nodeId,msg = "help")
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = Color(0xFF04572F)
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.help),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                    viewModel.sendDebugMessage(nodeId = nodeId,msg = "versionBle")
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = ST_Magenta
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.version_ble),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                    viewModel.sendDebugMessage(nodeId = nodeId,msg = "uid")
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = ST_Maroon
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.uid),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }
        }

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
    rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryColor)
        ){
            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material.IconButton(onClick = {}) {
                    androidx.compose.material.Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnPrimary
                    )
                }
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Text(
                        text = stringResource(id = R.string.debug_console),
                        fontSize = 20.sp,
                        color = OnPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            value = queryState,
            label = { Text(text = stringResource(id = R.string.send_debug_message))},
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

        Row(modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Surface(modifier = Modifier
                .clickable {
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = TertiaryColor
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.info),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = Color(0xFF04572F)
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.help),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = ST_Magenta
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.version_ble),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }

            Surface(modifier = Modifier
                .clickable {
                }
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp, 3.dp, 12.dp, 3.dp))
                .weight(1f),
                color = BrownColor
            ) {
                androidx.compose.material3.Text(stringResource(id = R.string.uid),color = OnPrimary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp,vertical = 10.dp), textAlign = TextAlign.Center)
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            text = "HELLO"
        )
    }
}
