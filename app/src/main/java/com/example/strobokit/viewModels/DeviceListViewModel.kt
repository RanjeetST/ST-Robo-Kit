package com.example.strobokit.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.common.Status
import com.st.blue_sdk.models.Node
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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