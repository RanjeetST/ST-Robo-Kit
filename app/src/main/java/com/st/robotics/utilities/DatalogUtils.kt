package com.st.robotics.utilities

import kotlinx.coroutines.yield
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Locale

private const val SVI = "System Volume Information"
private const val UUID_START_DELIMITER = "\"uuid\": \""
private const val UUID_END_DELIMITER = "\""
private const val ACQUISITION_INFO_FILE_NAME = "acquisition_info.json"
private const val BUFFER_SIZE = 1024 * 1024

fun String.formatDate(): String {
    val inputSdf = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.ROOT)
    val date = inputSdf.parse(this)
    val sdf = SimpleDateFormat("EEE MMM d yyyy HH:mm:ss", Locale.UK)
    return sdf.format(date)
}

fun String.readUUID(encoding: Charset = StandardCharsets.UTF_8): String =
    Files.readAllLines(Paths.get(this), encoding)
        .joinToString("")
        .substringAfter(UUID_START_DELIMITER)
        .substringBefore(UUID_END_DELIMITER)

suspend fun downloadDirectory(
    isActive: Boolean,
    ftpClient: FTPClient,
    remoteDirPath: String,
    localDirPath: String,
    onUpdateProgress: (Float, String) -> Unit
): Pair<String, List<File>> {
    var uuid = ""

    val ftpFiles: List<FTPFile> =
        ftpClient.listFiles(remoteDirPath).filter { it.isDirectory.not() }

    val localFolder = File(localDirPath)
    if (localFolder.exists().not()) {
        localFolder.mkdirs()
    }

    val filesSize: Long = ftpFiles.sumOf { it.size }
    var totalBytesRead: Long = 0

    val files = ftpFiles.map { file ->

        val savePath = "${localFolder.absolutePath}/${file.name}"
        downloadFTPFile(
            isActive = isActive,
            ftpClient = ftpClient,
            fileName = "${remoteDirPath}/${file.name}",
            savePath = savePath
        ) { bytesRead ->
            totalBytesRead += bytesRead
            onUpdateProgress(totalBytesRead.toFloat() / filesSize, file.name)
        }

        if (file.name.equals(ACQUISITION_INFO_FILE_NAME)) {
            uuid = savePath.readUUID()
        }

        File("$localDirPath/${file.name}")
    }

    onUpdateProgress(1f, "")

    return uuid to files
}

fun readAcquisitionName(
    files: List<File>
): Pair<String, List<File>> {
    val uuid =
        files.find { it.name.equals(ACQUISITION_INFO_FILE_NAME) }?.absolutePath?.readUUID() ?: ""

    return uuid to files
}


@Throws(IOException::class)
suspend fun downloadFTPFile(
    isActive: Boolean,
    ftpClient: FTPClient,
    fileName: String,
    savePath: String,
    updateProgress: (Int) -> Unit
) {
    val localFile = File(savePath)

    val inputStream: InputStream = ftpClient.retrieveFileStream(fileName)
    val outputStream = FileOutputStream(localFile.absoluteFile)

    val buffer = ByteArray(BUFFER_SIZE)
    var bytesRead: Int

    while (inputStream.read(buffer).also { bytesRead = it } != -1 && isActive) {
        outputStream.write(buffer, 0, bytesRead)
        updateProgress(bytesRead)
        yield()
    }

    outputStream.close()
    inputStream.close()

    // TODO remove when fixed on FW
    if (fileName.contains(".json")) {
        val raf = RandomAccessFile(localFile, "rwd")
        raf.seek(localFile.length() - 1)
        val b: Byte = raf.readByte()
        if (b == '\u0000'.toByte()) {
            raf.setLength(localFile.length() - 1)
        }
    }

    ftpClient.completePendingCommand()
}