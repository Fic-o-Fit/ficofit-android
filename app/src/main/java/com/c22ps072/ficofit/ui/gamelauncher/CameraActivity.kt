package com.c22ps072.ficofit.ui.gamelauncher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.data.classifier.CalisthenicsClassifier
import com.c22ps072.ficofit.data.classifier.PoseEstimator
import com.c22ps072.ficofit.data.source.model.BodyPart
import com.c22ps072.ficofit.data.source.model.Pose
import com.c22ps072.ficofit.databinding.ActivityCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    var history = mutableListOf<Int>()
    var numFrameRequirement: Int = 5
    var downDone: Boolean = false
    var counter: Int = 0

    private lateinit var surfaceView: SurfaceView
    private lateinit var tvCounter: TextView
    private lateinit var tvTimer: TextView
    private var cameraSource: CameraSource? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        CameraSelector.DEFAULT_FRONT_CAMERA

    }

    override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun cameraClassification() {
        if (allPermissionsGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {

                        }

                        override fun onDetectedInfo(
                            poseIsCorrect: Boolean,
                            pose: Pose
                        ) {
                            when(EXTRA_CLASSIFICATION){
                                "sit" -> {
                                    tvCounter.text =getString(R.string.push_up, counter.toString())
                                    if (poseIsCorrect && bodyIsVisible(pose)){
                                        var shoulderY = 0
                                        shoulderY = if(pose.keyPoints[BodyPart.NOSE.position].coordinate.x < pose.keyPoints[BodyPart.RIGHT_ANKLE.position].coordinate.x){
                                            (pose.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate.y).toInt()
                                        }else{
                                            (pose.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate.y).toInt()
                                        }

                                        if((history.size == 0) || (shoulderY != history.takeLast(1)[0])){
                                            history.add(shoulderY)
                                            history = history.takeLast(numFrameRequirement).toMutableList()
                                            if(history.size >= numFrameRequirement){
                                                if(isDecreasing(history)){
                                                    downDone = true
                                                }else if(isIncreasing(history)){
                                                    if(downDone){
                                                        counter += 1
                                                        downDone = false
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                "push" -> {
                                    tvCounter.text =getString(R.string.push_up, counter.toString())
                                    if (poseIsCorrect && bodyIsVisible(pose)){
                                        var shoulderY = 0
                                        shoulderY = if(pose.keyPoints[BodyPart.NOSE.position].coordinate.x > pose.keyPoints[BodyPart.RIGHT_KNEE.position].coordinate.x){
                                            (pose.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate.y).toInt()
                                        }else{
                                            (pose.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate.y).toInt()
                                        }

                                        if((history.size == 0) || (shoulderY != history.takeLast(1)[0])){
                                            history.add(shoulderY)
                                            history = history.takeLast(numFrameRequirement).toMutableList()
                                            if(history.size >= numFrameRequirement){
                                                if(isDecreasing(history)){
                                                    downDone = true
                                                }else if(isIncreasing(history)){
                                                    if(downDone){
                                                        counter += 1
                                                        downDone = false
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
//
                        }

                    }).apply {
                        prepareCamera()
                    }

                cameraSource?.setClassifier(CalisthenicsClassifier(this.assets, if (EXTRA_CLASSIFICATION == "sit") "sit" else "push"))
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    fun isIncreasing(arr: List<Int>): Boolean{
        var trueCount = 0
        val threshold = 0.8
        for(i in 1 until arr.size){
            if(arr[i] > arr[i-1]){
                trueCount += 1
            }
        }
        val truePercentage = trueCount.toFloat() / (arr.size-1).toFloat()
        return truePercentage >= threshold
    }

    fun isDecreasing(arr: List<Int>): Boolean{
        var trueCount = 0
        val threshold = 0.8
        for(i in 1 until arr.size){
            if(arr[i] < arr[i-1]){
                trueCount += 1
            }
        }
        val truePercentage = trueCount.toFloat() / (arr.size-1).toFloat()
        return truePercentage >= threshold
    }

    fun bodyIsVisible(pose: Pose): Boolean{
        val importantBodyPart = intArrayOf(5, 7, 9, 11, 13)
        for(i in importantBodyPart){
            if((pose.keyPoints[i].score < 0.2f) && (pose.keyPoints[i+1].score < 0.2f)){
                return false
            }
        }
        return true
    }


    private fun createPoseEstimator() {
        val poseEstimator = PoseEstimator(this.assets)

        cameraSource?.setEstimator(poseEstimator)
    }
    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val EXTRA_CLASSIFICATION = "extra_classification"
    }
}