package com.st.robotics.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun PdfViewer(pdfFile: File) {
    val context = LocalContext.current
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    DisposableEffect(pdfFile) {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val tempBitmaps = mutableListOf<Bitmap>()

        for (i in 0 until pdfRenderer.pageCount) {
            val page = pdfRenderer.openPage(i)
            val width = context.resources.displayMetrics.densityDpi / 72 * page.width
            val height = context.resources.displayMetrics.densityDpi / 72 * page.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            tempBitmaps.add(bitmap)
            page.close()
        }

        bitmaps = tempBitmaps
        pdfRenderer.close()

        onDispose {
            fileDescriptor.close()
        }
    }

    LazyColumn {
        items(bitmaps.size) { index ->
            bitmaps[index].let {
                Image(bitmap = it.asImageBitmap(), contentDescription = null)
            }
        }
    }
}
fun copyPdfFromAssets(context: Context, fileName: String) {
    val assetManager = context.assets
    val inputStream: InputStream = assetManager.open(fileName)
    val outFile = File(context.filesDir, fileName)
    val outputStream = FileOutputStream(outFile)

    inputStream.copyTo(outputStream)

    inputStream.close()
    outputStream.close()
}