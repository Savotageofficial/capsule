package com.example.capsule.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capsule.R
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

object ImageUtils {

    /**
     * Convert Bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 80): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Convert Base64 string to Bitmap
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Compress image file and convert to Base64 (with rotation fix)
     */
    fun compressImageAndConvertToBase64(
        filePath: String,
        maxWidth: Int = 800,
        maxHeight: Int = 800,
        quality: Int = 80
    ): String? {
        return try {
            // Decode image
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            // Load compressed bitmap
            var bitmap = BitmapFactory.decodeFile(filePath, options)

            // Fix rotation if needed
            bitmap = rotateBitmapIfRequired(bitmap, filePath)

            // Resize if still too large
            if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
                val scale = minOf(
                    maxWidth.toFloat() / bitmap.width,
                    maxHeight.toFloat() / bitmap.height
                )
                val width = (bitmap.width * scale).toInt()
                val height = (bitmap.height * scale).toInt()

                bitmap = bitmap.scale(width, height)
            }

            // Convert to Base64
            bitmapToBase64(bitmap, quality)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calculate sample size for bitmap loading
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Create a file from Uri
     */
    fun createFileFromUri(context: android.content.Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("profile_image", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fix image rotation based on EXIF data
     */
    fun rotateBitmapIfRequired(bitmap: Bitmap, filePath: String): Bitmap {
        return try {
            val exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            if (matrix.isIdentity) bitmap else {
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }
}

/**
 * Composable function for displaying profile image with Base64 support
 */
@Composable
fun ProfileImage(
    base64Image: String?,
    defaultImageRes: Int = R.drawable.patient_profile,
    modifier: Modifier = Modifier.size(120.dp),
    onImageClick: (() -> Unit)? = null
) {
    val bitmap = remember(base64Image) {
        base64Image?.let { ImageUtils.base64ToBitmap(it) }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .then(if (onImageClick != null) Modifier.clickable { onImageClick() } else Modifier)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = defaultImageRes),
                contentDescription = "Default Profile Image",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}