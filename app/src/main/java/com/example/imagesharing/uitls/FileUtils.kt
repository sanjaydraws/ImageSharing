package com.example.imagesharing.uitls

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.example.imagesharing.R
import java.io.*


/**
 * Create a new file in files directory from provided Uri and return path of newly created file
 * @uri File uri
 * @external create new file in external storage or internal storage
 */
@JvmOverloads
fun Context.getPath(
    uri: Uri?,
    external: Boolean = false
): String {

    if (uri == null)
        return ""

    try {
        // Store to tmp file

        val mFolder = if (external) {
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else
            File("$filesDir/Images")

        if (mFolder != null) {
            if (!mFolder.exists()) {
                mFolder.mkdirs()
            }


            val tmpFile = File(mFolder.absolutePath, getFileName(uri))

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(tmpFile)

                fos.copyInputStreamToFile(contentResolver.openInputStream(uri))
                fos.flush()
                fos.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return tmpFile.path
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }

    return ""
}

/**
 * Get File Name From URI
 */
fun Context.getFileName(uri: Uri): String {
// The query, because it only applies to a single document, returns only
    // one row. There's no need to filter, sort, or select fields,
    // because we want all fields for one document.
    // The query, because it only applies to a single document, returns only
    // one row. There's no need to filter, sort, or select fields,
    // because we want all fields for one document.
    return contentResolver
        .query(uri, null, null, null, null, null).use {
            var name = ""
            try {
                // moveToFirst() returns false if the cursor has 0 rows. Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (it != null && it.moveToFirst()) {

                    // Note it's called "Display Name". This is
                    // provider-specific, and might not necessarily be the file name.
                    name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                    // val sizeIndex: Int = it?.getColumnIndex(OpenableColumns.SIZE)
                    // If the size is unknown, the value stored is null. But because an
                    // int can't be null, the behavior is implementation-specific,
                    // and unpredictable. So as
                    // a rule, check if it's null before assigning to an int. This will
                    // happen often: The storage API allows for remote files, whose
                    // size might not be locally known.
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@use name
        }
}

/**
 * Get file extension from Uri
 */
fun Context.getExtension(uri: Uri): String {
    val cR: ContentResolver = contentResolver
    val mime = MimeTypeMap.getSingleton()
    val extension = mime.getExtensionFromMimeType(cR.getType(uri))
    return extension ?: ""
}

/**
 * Copy File using streams
 */
fun OutputStream.copyInputStreamToFile(inputStream: InputStream?) {
    this.use { fileOut ->
        inputStream?.copyTo(fileOut)
    }

}
fun Context.getUri(file: File?): Uri? {
    return if (file != null)
        FileProvider.getUriForFile(
            this, "com.example.imagesharing.fileprovider", file
        )
    else
        null
}


fun Context.createFile(fileName: String): File {
    val folder = File(filesDir,"images")
    if (folder.exists() != true)
        folder?.mkdir()
    return File(folder?.path, fileName)
}

/**
 * Granting permissions to write and read for available cameras to file provider.
 */
fun Context.applyProviderPermission(intent: Intent, uri: Uri?) {
    val resInfoList: List<ResolveInfo> = packageManager
        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in resInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        grantUriPermission(
            packageName,
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}