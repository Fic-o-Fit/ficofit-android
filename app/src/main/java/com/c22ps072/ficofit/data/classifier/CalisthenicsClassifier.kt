package com.c22ps072.ficofit.data.classifier

import android.content.res.AssetManager
import com.c22ps072.ficofit.data.source.model.Pose
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class CalisthenicsClassifier(assetManager: AssetManager, calisthenicType: String) {
    private var INTERPRETER: Interpreter
    private var threshold: Float = 0.9f
    private var INPUT: IntArray
    private var OUTPUT: IntArray
    private var MODEL_TYPE:String

    init {
        val tfliteOptions = Interpreter.Options()
        tfliteOptions.setNumThreads(5)
        MODEL_TYPE = calisthenicType
        val modelPath = when (MODEL_TYPE) {
            "push" -> {
                "pushup-model.tflite"
            }
            "sit" -> {
                "situp-model.tflite"
            }
            else -> {
                "pushup-model.tflite"
            }
        }
        INTERPRETER = Interpreter(loadModelFile(assetManager, modelPath), tfliteOptions)
        INPUT = INTERPRETER.getInputTensor(0).shape()
        OUTPUT = INTERPRETER.getOutputTensor(0).shape()
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun poseIsCorrect(pose: Pose): Boolean {
        val inputVector = poseToArray(pose)
        val outputTensor = FloatArray(OUTPUT[1])
        INTERPRETER.run(arrayOf(inputVector), arrayOf(outputTensor))

        return when (MODEL_TYPE) {
            "push" -> {
                (outputTensor[1] >= threshold) || (outputTensor[2] >= threshold)
            }
            "sit" -> {
                (outputTensor[1] >= threshold)
            }
            else -> {
                (outputTensor[1] >= threshold) || (outputTensor[2] >= threshold)
            }
        }

    }

    private fun poseToArray(pose: Pose) : FloatArray{
        val result = FloatArray(INPUT[1])   //51
        for (keypoint in pose.keyPoints) {
            val position = keypoint.bodyPart.position * 3
            if (keypoint.score > 0.2f) {
                result[position] = keypoint.coordinate.y   //the y coordinate is the first element
                result[position+1] = keypoint.coordinate.x
                result[position+2] = keypoint.score
            }else{
                result[position] = 0.0f
                result[position+1] = 0.0f
                result[position+2] = 0.0f
            }
        }
        return result
    }

    fun close() {
        INTERPRETER.close()
    }

}