package com.velichkomarija.recognizesymbolapp.utils

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Classifier(activity: Activity) {

    companion object {
        const val logTag: String = "Classifier"
        const val MODEL_NAME: String = "symbols.tflite"
        const val BATCH_SIZE: Short = 1
        const val IMG_HEIGHT: Int = 28
        const val IMG_WIDTH: Int = 28
        const val NUM_CHANNEL: Short = 1
        const val NUM_CLASSES: Int = 12
    }

    private val options: Interpreter.Options = Interpreter.Options()
    private val mImagePixels = IntArray(IMG_HEIGHT * IMG_WIDTH)
    private val mResult = Array(1) { FloatArray(NUM_CLASSES) }

    private var mInterpreter: Interpreter
    private var mImageData: ByteBuffer

    init {
        mInterpreter = Interpreter(loadModelFile(activity), options)
        mImageData = ByteBuffer.allocateDirect(
            4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL
        )
        mImageData.order(ByteOrder.nativeOrder())
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        val startTime = SystemClock.uptimeMillis()
        mInterpreter.run(mImageData, mResult)
        val endTime = SystemClock.uptimeMillis()
        val timeCost = endTime - startTime

        Log.d(
            logTag, "classify(): result = " + Arrays.toString(mResult[0])
                    + ", timeCost = " + timeCost
        )

        return Result(mResult[0], timeCost)
    }

    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = activity.assets.openFd(MODEL_NAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel

        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (mImageData == null) {
            return
        }

        mImageData.rewind()

        bitmap.getPixels(
            mImagePixels,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )

        var pixel = 0

        for (i in IMG_WIDTH downTo 1 step 1) {

            for (j in IMG_HEIGHT downTo 1 step 1) {

                val value: Int = mImagePixels[pixel++]
                mImageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int): Float {
        return (0 + ((color shr 16 and 0xFF) * 0.299f +
                (color shr 8 and 0xFF) * 0.587f +
                (color and 0xFF) * 0.114f)) / 255.0f
    }

}