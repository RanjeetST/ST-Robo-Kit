package com.st.robotics.utilities

import android.content.Context
import android.util.Log
import com.google.android.gms.common.moduleinstall.InstallStatusListener
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_CANCELED
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_COMPLETED
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate.InstallState.STATE_FAILED
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class QrCodeService @Inject constructor(
    @ApplicationContext private val context : Context
)  {
    private suspend fun GmsBarcodeScanner.scan(): String? {
        return suspendCancellableCoroutine { continuation ->
            startScan()
                .addOnSuccessListener { result ->
                    Log.e(TAG, "Scan success")

                    val scanResult = runCatching {
                        result.displayValue ?: return@runCatching null
                    }.getOrNull()
                    continuation.resume(scanResult)
                }
                .addOnCanceledListener {
                    Log.e(TAG, "Scan canceled")

                    continuation.resume(null)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to start scan")

                    continuation.resume(null)
                }
        }
    }

    private suspend fun checkModuleAvailability(module: GmsBarcodeScanner): Boolean {
        return suspendCancellableCoroutine { continuation ->
            ModuleInstall.getClient(context)
                .areModulesAvailable(module)
                .addOnSuccessListener {
                    if (it.areModulesAvailable()) {
                        continuation.resume(value = true)
                    } else {
                        continuation.resume(value = false)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(value = false)
                }
        }
    }

    private suspend fun installModule(module: GmsBarcodeScanner): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val moduleInstallClient = ModuleInstall.getClient(context)

            moduleInstallClient
                .installModules(
                    ModuleInstallRequest.newBuilder()
                        .addApi(module)
                        .setListener(
                            object : InstallStatusListener {
                                override fun onInstallStatusUpdated(update: ModuleInstallStatusUpdate) {
                                    update.progressInfo?.let {
                                        val progress =
                                            (it.bytesDownloaded * 100 / it.totalBytesToDownload).toInt()
                                        Log.d(TAG, "Install Module Progress: $progress")
                                    }

                                    if (isTerminateState(update.installState)) {
                                        if (update.installState == STATE_COMPLETED) {
                                            continuation.resume(value = true)
                                        } else {
                                            continuation.resume(value = false)
                                        }

                                        moduleInstallClient.unregisterListener(this)
                                    }
                                }

                                private fun isTerminateState(@ModuleInstallStatusUpdate.InstallState state: Int): Boolean {
                                    return state == STATE_CANCELED || state == STATE_COMPLETED || state == STATE_FAILED
                                }
                            }
                        )
                        .build()
                )
                .addOnSuccessListener {
                    if (it.areModulesAlreadyInstalled()) {
                        continuation.resume(value = true)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(value = false)
                }
        }
    }

      suspend fun scan(): String? {
        val barcodeScannerModule = GmsBarcodeScanning.getClient(
            context,
            GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build()
        )

        val moduleIsAvailable = checkModuleAvailability(module = barcodeScannerModule)

        if (moduleIsAvailable.not()) {
            installModule(module = barcodeScannerModule)
        }

        return barcodeScannerModule.scan()
    }

    companion object {
        private const val TAG = "QrCodeServiceImpl"
    }
}