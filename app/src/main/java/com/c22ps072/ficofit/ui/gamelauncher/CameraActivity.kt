package com.c22ps072.ficofit.ui.gamelauncher

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.data.classifier.CalisthenicsClassifier
import com.c22ps072.ficofit.data.classifier.PoseEstimator
import com.c22ps072.ficofit.data.source.model.BodyPart
import com.c22ps072.ficofit.data.source.model.Pose
import com.c22ps072.ficofit.databinding.ActivityCameraBinding
import com.c22ps072.ficofit.service.TimeService
import com.c22ps072.ficofit.utils.UnderDevelopmentDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import kotlin.math.roundToInt

@AndroidEntryPoint
class CameraActivity : AppCompatActivity(), DialogSetting.DialogSettingListener, GameReportDialog.ReportDialogListener {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var type: String
    var points: Int = 0
    var history = mutableListOf<Int>()
    var numFrameRequirement: Int = 5
    var downDone: Boolean = false
    var counter: Int = 0

    private lateinit var surfaceView: SurfaceView
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var cameraSource: CameraSource? = null
    private val gameViewModel: GameViewModel by viewModels()

    private val mFragmentManager = supportFragmentManager

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

        surfaceView = binding.surfaceView

        type = intent.getStringExtra(EXTRA_CLASSIFICATION).toString()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        history = mutableListOf<Int>()
        numFrameRequirement = 5
        downDone = false
        counter = 0

        serviceIntent = Intent(applicationContext, TimeService::class.java)
        registerReceiver(updateTime, IntentFilter(TimeService.TIMER_UPDATED))
        startStopTimer()
        binding.btnEnd.setOnClickListener {
            points = ((counter / time) * 100).roundToInt()
            lifecycleScope.launch {
                gameViewModel.getUserToken().collect { token ->
                    Log.e("Camera", "Timer : $time, Counter: $counter")
                    gameViewModel.getMyScore(token).collect { resultMyScore ->
                        resultMyScore.onSuccess { recentScore ->
                            val score: Int = recentScore.score + points
                            gameViewModel.postSubmitScore(token, score).collect { result ->
                                result.onSuccess {
                                    Log.e("Camera", it.status)

                                    // show report dialog
                                    showGameReportDialog()
                                }
                                result.onFailure {
                                    Log.e("Camera", it.message.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.ivSetting.setOnClickListener {
            val mSettingDialogFragment = DialogSetting()
            mSettingDialogFragment.show(mFragmentManager, DialogSetting::class.java.simpleName)
        }


    }

    private fun showGameReportDialog() {
        startStopTimer()

        val dialogReport = GameReportDialog()
        val args = Bundle()

        args.putString(GameReportDialog.TEXT_POINT, points.toString())
        dialogReport.arguments = args
        dialogReport.show(mFragmentManager, GameReportDialog::class.java.simpleName)
    }

    override fun onStart() {
        super.onStart()
        cameraClassification()
    }

    public override fun onResume() {
        super.onResume()
        cameraSource?.resume()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.close()
        cameraSource = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
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
                            when(type){
                                "sit" -> {
                                    this@CameraActivity.runOnUiThread {
                                        binding.tvCounter.text = getString(R.string.sit_up, counter.toString())
                                    }

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
                                    this@CameraActivity.runOnUiThread {
                                        binding.tvCounter.text = getString(R.string.push_up, counter.toString())
                                    }
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

                cameraSource?.setClassifier(CalisthenicsClassifier(this.assets, if (type == "sit") "sit" else "push"))
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


    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimeService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        timerStarted = false
    }
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimeService.TIME_EXTRA, 0.0)
            binding.tvTimer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(minutes, seconds)
    }

    private fun makeTimeString( min: Int, sec: Int): String = String.format("%02d:%02d", min, sec)

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
        const val EXTRA_CLASSIFICATION = "extra_classification"
    }

    private fun showUnderDevelopmentDialog() {
        val mDialogUnderDevelopment = UnderDevelopmentDialog()
        mDialogUnderDevelopment.show(mFragmentManager, UnderDevelopmentDialog::class.java.simpleName)
    }

    override fun onDialogSwitchClick(dialog: DialogFragment) {
        showUnderDevelopmentDialog()
    }

    override fun onDialogModeClick(dialog: DialogFragment) {
        showUnderDevelopmentDialog()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onButtonCloseListener(dialog: GameReportDialog) {
        dialog.dismiss()
        finish()
    }
}