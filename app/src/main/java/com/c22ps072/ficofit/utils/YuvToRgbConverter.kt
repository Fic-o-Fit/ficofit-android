/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.c22ps072.ficofit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.renderscript.Type
import java.nio.ByteBuffer

/**
 * Helper class used to convert a [Image] object from
 * [ImageFormat.YUV_420_888] format to an RGB [Bitmap] object, it has equivalent
 * functionality to https://github
 * .com/androidx/androidx/blob/androidx-main/camera/camera-core/src/main/java/androidx/camera/core/ImageYuvToRgbConverter.java
 *
 * NOTE: This has been tested in a limited number of devices and is not
 * considered production-ready code. It was created for illustration purposes,
 * since this is not an efficient camera pipeline due to the multiple copies
 * required to convert each frame. For example, this
 * implementation
 * (https://stackoverflow.com/questions/52726002/camera2-captured-picture-conversion-from-yuv-420-888-to-nv21/52740776#52740776)
 * might have better performance.
 */
class YuvToRgbConverter(context: Context) {
    private val rs = RenderScript.create(context)
    private val scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

    private var pixelCount: Int = -1
    private lateinit var yuvBuffer: ByteBuffer
    private lateinit var inputAllocation: Allocation
    private lateinit var outputAllocation: Allocation

    @Synchronized
    fun yuvToRgb(image: Image, output: Bitmap) {

        // Ensure that the intermediate output byte buffer is allocated
        if (!::yuvBuffer.isInitialized) {
            pixelCount = image.cropRect.width() * image.cropRect.height()
            yuvBuffer = ByteBuffer.allocateDirect(
                pixelCount * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8)
        }

        // Get the YUV data in byte array form
        imageToByteBuffer(image, yuvBuffer)

        // Ensure that the RenderScript inputs and outputs are allocated
        if (!::inputAllocation.isInitialized) {
            inputAllocation = Allocation.createSized(rs, Element.U8(rs), yuvBuffer.array().size)
        }
        if (!::outputAllocation.isInitialized) {
            outputAllocation = Allocation.createFromBitmap(rs, output)
        }

        // Convert YUV to RGB
        inputAllocation.copyFrom(yuvBuffer.array())
        scriptYuvToRgb.setInput(inputAllocation)
        scriptYuvToRgb.forEach(outputAllocation)
        outputAllocation.copyTo(output)
    }

    private fun imageToByteBuffer(image: Image, outputBuffer: ByteBuffer) {
        assert(image.format == ImageFormat.YUV_420_888)

        val imageCrop = image.cropRect
        val imagePlanes = image.planes
        val rowData = ByteArray(imagePlanes.first().rowStride)

        imagePlanes.forEachIndexed { planeIndex, plane ->

            // How many values are read in input for each output value written
            // Only the Y plane has a value for every pixel, U and V have half the resolution i.e.
            //
            // Y Plane            U Plane    V Plane
            // ===============    =======    =======
            // Y Y Y Y Y Y Y Y    U U U U    V V V V
            // Y Y Y Y Y Y Y Y    U U U U    V V V V
            // Y Y Y Y Y Y Y Y    U U U U    V V V V
            // Y Y Y Y Y Y Y Y    U U U U    V V V V
            // Y Y Y Y Y Y Y Y
            // Y Y Y Y Y Y Y Y
            // Y Y Y Y Y Y Y Y
            val outputStride: Int

            // The index in the output buffer the next value will be written at
            // For Y it's zero, for U and V we start at the end of Y and interleave them i.e.
            //
            // First chunk        Second chunk
            // ===============    ===============
            // Y Y Y Y Y Y Y Y    U V U V U V U V
            // Y Y Y Y Y Y Y Y    U V U V U V U V
            // Y Y Y Y Y Y Y Y    U V U V U V U V
            // Y Y Y Y Y Y Y Y    U V U V U V U V
            // Y Y Y Y Y Y Y Y
            // Y Y Y Y Y Y Y Y
            // Y Y Y Y Y Y Y Y
            var outputOffset: Int

            when (planeIndex) {
                0 -> {
                    outputStride = 1
                    outputOffset = 0
                }
                1 -> {
                    outputStride = 2
                    outputOffset = pixelCount + 1
                }
                2 -> {
                    outputStride = 2
                    outputOffset = pixelCount
                }
                else -> {
                    // Image contains more than 3 planes, something strange is going on
                    return@forEachIndexed
                }
            }

            val buffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride

            // We have to divide the width and height by two if it's not the Y plane
            val planeCrop = if (planeIndex == 0) {
                imageCrop
            } else {
                Rect(
                    imageCrop.left / 2,
                    imageCrop.top / 2,
                    imageCrop.right / 2,
                    imageCrop.bottom / 2
                )
            }

            val planeWidth = planeCrop.width()
            val planeHeight = planeCrop.height()

            buffer.position(rowStride * planeCrop.top + pixelStride * planeCrop.left)
            for (row in 0 until planeHeight) {
                val length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    // When there is a single stride value for pixel and output, we can just copy
                    // the entire row in a single step
                    length = planeWidth
                    buffer.get(outputBuffer.array(), outputOffset, length)
                    outputOffset += length
                } else {
                    // When either pixel or output have a stride > 1 we must copy pixel by pixel
                    length = (planeWidth - 1) * pixelStride + 1
                    buffer.get(rowData, 0, length)
                    for (col in 0 until planeWidth) {
                        outputBuffer.array()[outputOffset] = rowData[col * pixelStride]
                        outputOffset += outputStride
                    }
                }

                if (row < planeHeight - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
    }
}
