package com.st.robotics.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.st.robotics.viewModels.FotaViewModel

@Composable
fun FotaScreen(
    viewModel: FotaViewModel,
    deviceId : String ,
    navController : NavController
){

    LaunchedEffect(Unit) {
        viewModel.getNode(deviceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Text("FOTA Screen",color = Color.White)

    }

//    val pickFileLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.OpenDocument()
//    ) { fileUri ->
//        if (fileUri != null) {
//            viewModel.changeFile(uri = fileUri)
//        }
//    }



}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FwUpgradeScreen(
//    modifier: Modifier = Modifier,
//    state: FwUpdateState,
//    fwLock: Boolean = false,
//    wbOta: Boolean = false,
//    isRebootedYet: Boolean = true,
//    fileSize: Long? = null,
//    errorMessageCode: Int = -1,
//    boardModel: Boards.Model? = null,
//    onChangeFile: () -> Unit = { /** NOOP **/ },
//    onDismissSuccessDialog: () -> Unit = { /** NOOP **/ },
//    onDismissErrorDialog: () -> Unit = { /** NOOP **/ },
//    onStartUploadFile: (WbOTAUtils.WBBoardType?, FirmwareType?, String?, String?) -> Unit = { _, _, _, _ -> /** NOOP **/ }
//) {
//    val supportedBoardModels = listOf(
//
//        //Boards.Model.WB_BOARD,
//        Boards.Model.WB55_NUCLEO_BOARD,
//        Boards.Model.ASTRA1,
//        Boards.Model.PROTEUS,
//        Boards.Model.STDES_CBMLORABLE,
//        Boards.Model.WB5M_DISCOVERY_BOARD,
//        Boards.Model.WB55_USB_DONGLE_BOARD,
//        Boards.Model.WB15_NUCLEO_BOARD,
//        Boards.Model.WB1M_DISCOVERY_BOARD,
//
//        //Boards.Model.WBA_BOARD,
//        Boards.Model.WBA5X_NUCLEO_BOARD,
//        Boards.Model.WBA_DISCOVERY_BOARD,
//
//        Boards.Model.WB0X_NUCLEO_BOARD)
//
//    val supportedBoardModelsWB = listOf(
//
//        Boards.Model.WB55_NUCLEO_BOARD,
//        Boards.Model.ASTRA1,
//        Boards.Model.PROTEUS,
//        Boards.Model.STDES_CBMLORABLE,
//        Boards.Model.WB5M_DISCOVERY_BOARD,
//        Boards.Model.WB55_USB_DONGLE_BOARD,
//        Boards.Model.WB15_NUCLEO_BOARD,
//        Boards.Model.WB1M_DISCOVERY_BOARD)
//
//
//
//
//    fun getDefaultAddress(boardModel: Boards.Model?, radioSelection: Int, selectedBoardIndex: Int): String {
//        val currentBoard = if(boardModel != null && supportedBoardModels.contains(boardModel) && !supportedBoardModelsWB.contains(boardModel)) {
//            boardModel
//        } else {
//            when(selectedBoardIndex) {
//                //2 -> Boards.Model.WBA_BOARD
//                2 -> Boards.Model.WBA_DISCOVERY_BOARD
//                //else -> Boards.Model.WB_BOARD
//                1 -> Boards.Model.WB15_NUCLEO_BOARD
//                else -> Boards.Model.WB55_NUCLEO_BOARD
//            }
//        }
//
//        return when(currentBoard) {
//            //Boards.Model.WB_BOARD -> if(radioSelection == 0) "0x007000" else "0x000000"
//            Boards.Model.WB55_NUCLEO_BOARD,
//            Boards.Model.ASTRA1,
//            Boards.Model.PROTEUS,
//            Boards.Model.STDES_CBMLORABLE,
//            Boards.Model.WB5M_DISCOVERY_BOARD,
//            Boards.Model.WB55_USB_DONGLE_BOARD -> if(radioSelection == 0) "0x007000" else "0x011000"
//
//            Boards.Model.WB15_NUCLEO_BOARD,
//            Boards.Model.WB1M_DISCOVERY_BOARD -> "0x007000"
//
//            Boards.Model.WBA5X_NUCLEO_BOARD,
//            Boards.Model.WBA_DISCOVERY_BOARD,-> if(radioSelection == 0) "0x080000" else "0x0F6000"
//            Boards.Model.WB0X_NUCLEO_BOARD -> if(radioSelection == 0) "0x3F800" else "0x07E000"
//            else -> "" //shouldn't happen
//        }
//    }
//
//    var radioSelection by remember { mutableStateOf(value = 0) }
//    var selectedBoardIndex by remember { mutableStateOf(value = 0) }
//
//    var address by remember { mutableStateOf(value = getDefaultAddress(boardModel, radioSelection, 0)) }
//    val selectFileText = stringResource(id = R.string.st_extConfig_fwUpgrade_selectFile)
//    var nbSectorsToErase by remember { mutableStateOf(value = selectFileText) }
//
//    var lastFileSize: Long? = null // this allows to update the number of sectors to erase each time the file changes while still being able to change it manually (textfield)
//    var isUploadStarted by remember { mutableStateOf(value = false) }
//
//    val haptic = LocalHapticFeedback.current
//
//    fun getCurrentBoardType(): WbOTAUtils.WBBoardType {
//        //return if(boardModel == null || !supportedBoardModels.contains(boardModel) || boardModel == Boards.Model.WB_BOARD) {
//
//        val isWB = (boardModel == Boards.Model.WB55_NUCLEO_BOARD) ||
//                (boardModel == Boards.Model.ASTRA1) ||
//                (boardModel == Boards.Model.PROTEUS) ||
//                (boardModel == Boards.Model.STDES_CBMLORABLE) ||
//                (boardModel == Boards.Model.WB5M_DISCOVERY_BOARD) ||
//                (boardModel == Boards.Model.WB55_USB_DONGLE_BOARD) ||
//                (boardModel == Boards.Model.WB15_NUCLEO_BOARD) ||
//                (boardModel == Boards.Model.WB1M_DISCOVERY_BOARD)
//
//        return if(boardModel == null || !supportedBoardModels.contains(boardModel) || isWB) {
//            when(selectedBoardIndex) {
//                0 -> WbOTAUtils.WBBoardType.WB5xOrWB3x
//                1 -> WbOTAUtils.WBBoardType.WB1x
//                else -> WbOTAUtils.WBBoardType.WBA
//            }
//        } else {
//            when(boardModel) {
//                //Boards.Model.WBA_BOARD -> WbOTAUtils.WBBoardType.WBA
//                Boards.Model.WBA5X_NUCLEO_BOARD,
//                Boards.Model.WBA_DISCOVERY_BOARD -> WbOTAUtils.WBBoardType.WBA
//
//                Boards.Model.WB0X_NUCLEO_BOARD -> WbOTAUtils.WBBoardType.WB09
//                else -> WbOTAUtils.WBBoardType.WBA
//            }
//        }
//    }
//
//    fun getCurrentFwType(): FirmwareType {
//        return if (radioSelection == 0) {
//            FirmwareType.BOARD_FW
//        } else {
//            FirmwareType.BLE_FW
//        }
//    }
//
//    Column(modifier = modifier.fillMaxWidth()) {
//        if (state.downloadFinished.not()) {
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        12.dp
//                    )
//            )
//        }
//
//        Surface(modifier = Modifier.fillMaxWidth()) {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                androidx.compose.material3.Text(
//                    color = Color.Gray,
//                    style = MaterialTheme.typography.bodyLarge,
//                    text = stringResource(
//                        id = R.string.st_extConfig_fwUpgrade_nameLabel,
//                        state.boardInfo?.boardName ?: ""
//                    )
//                )
//                androidx.compose.material3.Text(
//                    color = Grey6,
//                    style = MaterialTheme.typography.bodyLarge,
//                    text = stringResource(
//                        id = R.string.st_extConfig_fwUpgrade_versionLabel,
//                        state.boardInfo?.toString() ?: ""
//                    )
//                )
//                androidx.compose.material3.Text(
//                    color = Grey6,
//                    style = MaterialTheme.typography.bodyLarge,
//                    text = stringResource(
//                        id = R.string.st_extConfig_fwUpgrade_mcuLabel,
//                        state.boardInfo?.mcuType ?: ""
//                    )
//                )
//
//                if (wbOta) {
//                    if(fileSize != null && fileSize != lastFileSize && !isUploadStarted) {
//                        lastFileSize = fileSize
//                        nbSectorsToErase = WbOTAUtils.getNumberOfSectorsToDelete(
//                            getCurrentBoardType(),
//                            getCurrentFwType(),
//                            fileSize
//                        ).toString()
//                    }
//                    Column(modifier = Modifier.fillMaxWidth()) {
//
//                        val isNotSupportedBoard = (boardModel == null || !supportedBoardModels.contains(boardModel))
//                        //val isWb = boardModel == Boards.Model.WB_BOARD
//                        val isWb = (boardModel == Boards.Model.WB55_NUCLEO_BOARD) ||
//                                (boardModel == Boards.Model.ASTRA1) ||
//                                (boardModel == Boards.Model.PROTEUS) ||
//                                (boardModel == Boards.Model.STDES_CBMLORABLE) ||
//                                (boardModel == Boards.Model.WB5M_DISCOVERY_BOARD) ||
//                                (boardModel == Boards.Model.WB55_USB_DONGLE_BOARD) ||
//                                (boardModel == Boards.Model.WB15_NUCLEO_BOARD) ||
//                                (boardModel == Boards.Model.WB1M_DISCOVERY_BOARD)
//
//                        val isWBA = (boardModel == Boards.Model.WBA5X_NUCLEO_BOARD) ||
//                                (boardModel == Boards.Model.WBA_DISCOVERY_BOARD)
//
//                        if(isNotSupportedBoard || isWb) {
//                            BoardDropdown(selectedIndex = selectedBoardIndex, wbOnly = isWb,boardModel = boardModel) { boardIndex ->
//                                selectedBoardIndex = boardIndex
//                                address = getDefaultAddress(boardModel, radioSelection, selectedBoardIndex)
//                            }
//                        }
//                        val radioOptions = mutableListOf(
//                            stringResource(id = R.string.st_extConfig_fwUpgrade_otaOpt1),
//                            stringResource(id = if(selectedBoardIndex == 2 || isWBA || boardModel == Boards.Model.WB0X_NUCLEO_BOARD) R.string.st_extConfig_fwUpgrade_otaOpt2bis else R.string.st_extConfig_fwUpgrade_otaOpt2)
//                        )
//
//                        radioOptions.forEachIndexed { index, text ->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .selectable(selected = (index == radioSelection),
//                                        onClick = {
//                                            radioSelection = index
//                                            address = getDefaultAddress(
//                                                boardModel,
//                                                index,
//                                                selectedBoardIndex
//                                            )
//                                        }
//                                    )
//                                    .padding(horizontal = LocalDimensions.current.paddingNormal),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                RadioButton(
//                                    selected = (index == radioSelection),
//                                    onClick = {
//                                        radioSelection = index
//                                        address = getDefaultAddress(boardModel, index, selectedBoardIndex)
//                                    }
//                                )
//                                androidx.compose.material3.Text(
//                                    color = Grey6,
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    text = text,
//                                    modifier = Modifier.padding(start = LocalDimensions.current.paddingNormal)
//                                )
//                            }
//                        }
//                        TextField(
//                            value = address,
//                            onValueChange = { address = it },
//                            label = { androidx.compose.material3.Text(text = stringResource(id = R.string.st_extConfig_fwUpgrade_address)) },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(height = 60.dp)
//                                .wrapContentHeight(),
//                            colors = ExposedDropdownMenuDefaults.textFieldColors()
//                        )
//                        //if(!(boardModel == Boards.Model.WB_BOARD && isRebootedYet)) {
//                        if(!(isWb && isRebootedYet)) {
//                            TextField(
//                                value = nbSectorsToErase,
//                                onValueChange = { nbSectorsToErase = it },
//                                label = { androidx.compose.material3.Text(text = stringResource(id = R.string.st_extConfig_fwUpgrade_nbSectors)) },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(height = 60.dp)
//                                    .wrapContentHeight(),
//                                colors = ExposedDropdownMenuDefaults.textFieldColors()
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.width(width = LocalDimensions.current.paddingNormal))
//
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(all = LocalDimensions.current.paddingNormal),
//            shape = androidx.compose.material3.Shapes.small,
//            shadowElevation = LocalDimensions.current.elevationNormal,
//            onClick = { if (fwLock.not()) onChangeFile() }
//        ) {
//            Column(modifier = Modifier.padding(all = LocalDimensions.current.paddingNormal)) {
//                androidx.compose.material3.Text(
//                    color = Grey6,
//                    style = MaterialTheme.typography.bodyLarge,
//                    text = stringResource(id = R.string.st_extConfig_fwUpgrade_selectedLabel)
//                )
//                Text(
//                    color = Grey6,
//                    style = MaterialTheme.typography.bodyLarge,
//                    text = state.fwName
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.width(width = LocalDimensions.current.paddingNormal))
//
//        val errorMessage = when(errorMessageCode) {
//            0 ->  stringResource(id = R.string.st_extConfig_fwUpgrade_errorMessage0)
//            1 ->  stringResource(id = R.string.st_extConfig_fwUpgrade_errorMessage1)
//            2 -> stringResource(id = R.string.st_extConfig_fwUpgrade_errorMessage2)
//            3 -> stringResource(id = R.string.st_extConfig_fwUpgrade_errorMessage3)
//            4 -> stringResource(id = R.string.st_extConfig_fwUpgrade_errorMessage4)
//            else -> ""
//        }
//
//        if(errorMessage.isNotBlank()) {
//            androidx.compose.material3.Text(
//                text = errorMessage,
//                color = ErrorText,
//                modifier = Modifier.padding(10.dp)
//            )
//        }
//
//        Row(modifier = Modifier.fillMaxWidth()) {
//            Spacer(modifier = Modifier.weight(weight = 1f))
//
//            BlueMsButton(
//                enabled = state.downloadFinished && state.error == null,
//                text = stringResource(id = R.string.st_extConfig_fwUpgrade_upgradeBtn),
//                onClick = {
//                    isUploadStarted = true
//
//                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//                    if (wbOta) {
//                        val boardType = getCurrentBoardType()
//                        val firmwareType = getCurrentFwType()
//
//                        onStartUploadFile(boardType, firmwareType, address, nbSectorsToErase)
//                    } else {
//                        onStartUploadFile(null, null, null, null)
//                    }
//                },
//                iconPainter = painterResource(id = R.drawable.ic_file_upload)
//            )
//        }
//    }
//
//    if (state.isComplete) {
//        FwUpgradeSuccessDialog(
//            onPositiveButtonPressed = onDismissSuccessDialog,
//            seconds = state.duration
//        )
//    }
//
//    if (state.isInProgress) {
//        //FwUpdateProgressDialog(progress = state.progress)
//        BlueMSDialogCircularProgressIndicator(percentage = state.progress, message = stringResource(id = R.string.st_extConfig_fwUpgrade_title), colorFill = null)
//    }
//
//    if (state.error != null) {
//        FwUpgradeErrorDialog(
//            fwUploadError = state.error,
//            onPositiveButtonPressed = onDismissErrorDialog
//        )
//    }
//}