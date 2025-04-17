package com.st.robotics.viewModels

import android.transition.Scene
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.blue_sdk.BlueManager
import com.st.blue_sdk.features.FeatureUpdate
import com.st.blue_sdk.features.extended.scene_description.SceneDescriptionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SceneDescriptorViewModel @Inject constructor(
    private val blueManager: BlueManager
): ViewModel() {
    companion object {
        private val TAG = SceneDescriptorViewModel::class.simpleName
    }

    val featureUpdates: State<SceneDescriptionInfo?>
        get() = _featureUpdates

    private val _featureUpdates = mutableStateOf<SceneDescriptionInfo?>(null)

    private var observeFeatureJob: Job? = null

    fun observeFeature(featureName: String, deviceId: String) {
        observeFeatureJob?.cancel()

        blueManager.nodeFeatures(deviceId).find { it.name == featureName }?.let { feature ->
            observeFeatureJob =
                blueManager.getFeatureUpdates(nodeId = deviceId, features = listOf(feature))
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        Log.d("Scene Data" , it.data.logValue)
                        _featureUpdates.value = (it.data as SceneDescriptionInfo)

                    }.launchIn(viewModelScope)
        }
    }

    fun disconnectFeature(deviceId: String, featureName: String) {
        observeFeatureJob?.cancel()
        _featureUpdates.value = null
        viewModelScope.launch {
            val features = blueManager.nodeFeatures(deviceId).filter { it.name == featureName }
            blueManager.disableFeatures(
                nodeId = deviceId,
                features = features
            )
        }
    }
}