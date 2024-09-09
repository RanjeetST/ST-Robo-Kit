package com.example.strobokit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.services.debug.DebugMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DebugConsoleViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    private val _debugMessages = MutableStateFlow<List<DebugConsoleMsg>>(emptyList())
    val debugMessages: StateFlow<List<DebugConsoleMsg>> = _debugMessages.asStateFlow()


    fun sendDebugMessage(nodeId : String , msg : String){
        viewModelScope.launch{
            _debugMessages.value = _debugMessages.value + DebugConsoleMsg.DebugConsoleCommand(
                command = msg, time = LocalDateTime.now().format(dateTimeFormatter)
            )

            blueManager.writeDebugMessage(
                nodeId = nodeId,msg = "$msg\n"
            )
        }
    }

    fun receiveDebugMessage(nodeId: String){
        viewModelScope.launch {
            var lastReceivedData = System.currentTimeMillis()
            blueManager.getDebugMessages(nodeId = nodeId)?.collect{
                val currentTime = System.currentTimeMillis()
                if((currentTime - lastReceivedData) > 100){
                    _debugMessages.value += DebugConsoleMsg.DebugConsoleResponse(
                        response = it,time = LocalDateTime.now().format(dateTimeFormatter)
                    )
                }else{
                    _debugMessages.value += DebugConsoleMsg.DebugConsoleResponse(
                        response = it,time = null
                    )
                }
                lastReceivedData = currentTime
            }
        }
    }
}

sealed class DebugConsoleMsg{
    data class DebugConsoleCommand(
        val command: String,
        val time : String
    ) : DebugConsoleMsg()

    data class DebugConsoleResponse(
        val response: DebugMessage,
        val time : String?
    ) : DebugConsoleMsg()
}