package com.example.strobokit.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.common.Status
import com.st.blue_sdk.models.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleDeviceListViewModel @Inject constructor(
    private val blueManager: BlueManager
) : ViewModel() {

    val scanBleDevices = MutableStateFlow<List<Node>>(emptyList())
    val isLoading = MutableStateFlow(false)

    private var scanPeripheralJob: Job? = null

    var isRefreshing by mutableStateOf(false)
        private set

    fun onRefresh() {
        viewModelScope.launch {
            isRefreshing = true
            startScan()
            delay(1000L)
            isRefreshing = false
        }
    }

    fun startScan() {
        scanPeripheralJob?.cancel()
        scanPeripheralJob = viewModelScope.launch {
            blueManager.scanNodes().map {
                isLoading.tryEmit(it.status == Status.LOADING)

                it.data ?: emptyList()
            }.collect {
                scanBleDevices.tryEmit(it)
            }
        }
    }
}