package com.st.robotics.views

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.st.robotics.R
import com.st.robotics.models.app.DataLog
import com.st.robotics.models.dataset.Status
import com.st.robotics.ui.theme.GreyColor
import com.st.robotics.ui.theme.PrimaryColor
import com.st.robotics.viewModels.DatalogViewModel

@Composable
fun DatalogScreen(
    viewModel: DatalogViewModel,
    projectId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    var selectedFolderUri by remember { mutableStateOf<Uri?>(null) }
    var folderContents by remember { mutableStateOf<List<DocumentFile>>(emptyList()) }

    val sdDataLogs by viewModel.sdDataLogs.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
//    val ftpDataLogs by viewModel.ftpDataLogs.collectAsStateWithLifecycle(
//        initialValue = emptyList()
//    )
//    val dataLogs by remember {
//        derivedStateOf {
//            sdDataLogs + ftpDataLogs
//        }
//    }
    val uploadedDatalog by viewModel.uploadedDatalog.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            if(uri != null){
                viewModel.setSdUri(uri)
            }
            // Persist permission to access this folder across reboots
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedFolderUri = it

            // Load folder contents
            val documentFile = DocumentFile.fromTreeUri(context, it)
            documentFile?.let { folder ->
                folderContents = folder.listFiles().toList()
            }
        }
    }
    //TODO : CONVERT FILES IN DATALOG FORMAT AND POST IT TO THE RESPECTIVE API
//    val datalogWithLabel by remember(key1 = dataLogs, key2 = uploadedDatalogSet) {
//        derivedStateOf {
//            dataLogs.map { dataLog ->
//                dataLog to uploadedDatalogSet.find { it.name == dataLog.name }?.status
//            }
//        }
//    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Upload Folder",
                    style = MaterialTheme.typography.h6
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { folderLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select Folder")
                    }

                }

                // Show selected folder path
                selectedFolderUri?.let { uri ->
                    Text(
                        text = "Selected: ${uri.path?.split(":")?.lastOrNull() ?: "Unknown"}",
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .background(
                                color = PrimaryColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                }

                // Folder contents section
                if (selectedFolderUri != null) {
                    Text(
                        text = "Folder Contents:",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    if (folderContents.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    color = GreyColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("This folder is empty")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    color = GreyColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp)
                        ) {
                            itemsIndexed(folderContents) { _, file ->

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(modifier: Modifier,dataLog: DataLog,status: Status,file: DocumentFile) {
    val lastModified = remember {
        val date = java.util.Date(file.lastModified())
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        formatter.format(date)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = GreyColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable{

            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon for file type
        Icon(
            painter = if (file.isDirectory)
                painterResource(id = R.drawable.baseline_folder_24)
            else
                painterResource(id = R.drawable.baseline_insert_drive_file_24),
            contentDescription = if (file.isDirectory) "Folder" else "File",
            tint = if (file.isDirectory) PrimaryColor else GreyColor,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        // File details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = file.name ?: "Unknown",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = lastModified,
                style = MaterialTheme.typography.caption,
                color = GreyColor
            )
        }

        // File size (for non-directories)
        if (!file.isDirectory) {
            Text(
                text = formatFileSize(file.length()),
                style = MaterialTheme.typography.caption,
                color = GreyColor
            )
        }
    }
}

fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${(size / 1024f).toInt()} KB"
        size < 1024 * 1024 * 1024 -> "${(size / (1024f * 1024f)).toInt()} MB"
        else -> "${(size / (1024f * 1024f * 1024f)).toInt()} GB"
    }
}