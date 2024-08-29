package com.example.strobokit.composables


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.sceneview.Scene
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode

@Composable
fun CarModel(){
    Box(modifier = Modifier.fillMaxWidth().size(300.dp)) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val environmentLoader = rememberEnvironmentLoader(engine)

        val cameraNode = rememberCameraNode(engine).apply {
            position = Position(z = 4.0f)
        }
        val centerNode = rememberNode(engine)
            .addChildNode(cameraNode)

        val modelNode = rememberNode {
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = "models/car_project_edited.glb"
                ),
                scaleToUnits = 3f
            )
        }

        Scene(
            modifier = Modifier.fillMaxWidth(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            isOpaque = false,
            mainLightNode = rememberMainLightNode(engine){
                intensity = 100_000.0f
            },
            childNodes = listOf(centerNode,modelNode),
//            environment = environmentLoader.createEnvironment(),
            onFrame = {
                cameraNode.lookAt(centerNode, upDirection = Direction(y = 0.5f))
            }
        )
    }
}