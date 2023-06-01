package org.solvo.server.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

interface FileManager {
    suspend fun read(outputStream: OutputStream, path: String)
    suspend fun write(inputStream: InputStream, path: String)
    suspend fun delete(path: String)
}

class FileManagerImpl(): FileManager {
    override suspend fun read(outputStream: OutputStream, path: String) {
        withContext(Dispatchers.IO) {
            Files.copy(Paths.get(path), outputStream)
        }
    }

    override suspend fun write(inputStream: InputStream, path: String) {
        withContext(Dispatchers.IO) {
            val pathObj = Paths.get(path)
            Files.createDirectories(pathObj.parent)
            Files.copy(inputStream, pathObj)
        }
    }

    override suspend fun delete(path: String) {
        withContext(Dispatchers.IO) {
            Files.delete(Paths.get(path))
        }
    }
}