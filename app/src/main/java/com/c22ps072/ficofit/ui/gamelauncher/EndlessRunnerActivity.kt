package com.c22ps072.ficofit.ui.gamelauncher

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import android.os.Bundle
import android.os.Process
import android.os.SystemClock
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.viewModels
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.data.classifier.PoseEstimator
import com.c22ps072.ficofit.data.source.model.BodyPart
import com.c22ps072.ficofit.data.source.model.Pose
import com.c22ps072.ficofit.databinding.ActivityEndlessrunnerBinding
import com.c22ps072.ficofit.endlessrunner.EndlessRunner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndlessRunnerActivity : FragmentActivity(), AndroidFragmentApplication.Callbacks, GameReportDialog.ReportDialogListener {

    private lateinit var binding: ActivityEndlessrunnerBinding
    private val gameViewModel: GameViewModel by viewModels()

    lateinit var endlessRunnerFragment: GameFragment

    private var shoulderHistory = mutableListOf<Int>()
    private var hipHistory = mutableListOf<Int>()
    private var numFramesRequirement:Int = 5
    private var currentFps: Int = 15
    private var lastJumpTime: Long? = null
    private var shoulderY = 0
    private var hipY = 0

    private var totalPoints: Int = 0
    private var currentPoints: Int = 0
    private var highScore: Int = 0

    private lateinit var tvCurrentPoints: TextView
    private lateinit var tvHighScore: TextView
    private lateinit var tvTotalPoints: TextView
    private lateinit var svEndlessRunner: SurfaceView
    private lateinit var flGameContent: FrameLayout

    private var screenHeight : Int = 0
    private var screenWidth : Int = 0

    private var cameraSource: CameraSource? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            }else {
                finish()
            }
        }

    override fun onCreate (savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityEndlessrunnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvCurrentPoints = binding.tvCurrentPoints
        tvHighScore = binding.tvHighScore
        tvTotalPoints = binding.tvTotalPoints
        svEndlessRunner = binding.svEndlessRunner
        flGameContent = binding.gameContent

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        println(screenHeight)
        println(screenWidth)

        val svParams: ViewGroup.LayoutParams = svEndlessRunner.layoutParams
        svParams.width = screenWidth
        svParams.height = screenHeight - (screenWidth * 3 / 5)
        svEndlessRunner.layoutParams = svParams

        val flParams: ViewGroup.LayoutParams = flGameContent.layoutParams
        flParams.width = screenWidth
        flParams.height = screenWidth * 3 / 5
        flGameContent.layoutParams = flParams


        // replace frame layout's content with libgdx endless runner game
        endlessRunnerFragment = GameFragment(screenWidth)
        val trans: FragmentTransaction = supportFragmentManager.beginTransaction()
        trans.replace(flGameContent.id, endlessRunnerFragment)
        trans.commit()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        shoulderHistory = mutableListOf()
        hipHistory = mutableListOf()
        numFramesRequirement = 5
        currentFps = 20
        lastJumpTime = null
        totalPoints = 0

        binding.btnEnd.setOnClickListener {
            lifecycleScope.launch {
                gameViewModel.getUserToken().collect { token ->
                    gameViewModel.getMyScore(token).collect { resultMyScore ->
                        resultMyScore.onSuccess { recentScore ->
                            val score: Int = recentScore.score + totalPoints
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
    }

    private fun showGameReportDialog() {
        val dialogReport = GameReportDialog()
        val args = Bundle()

        args.putString(GameReportDialog.TEXT_POINT, totalPoints.toString())
        dialogReport.arguments = args
        dialogReport.show(supportFragmentManager, GameReportDialog::class.java.simpleName)
    }

    // Create a Class that extends AndroidFragmentApplication which is the Fragment implementation for libGDX.
    class GameFragment(screenWidth: Int) : AndroidFragmentApplication()
    {
        val game = EndlessRunner(screenWidth)

        // Add the initializeForView() code in the Fragment's onCreateView method.
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val config = AndroidApplicationConfiguration()

            return initializeForView(game, config)
        }

    }

    override fun exit() {}

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.close()
        cameraSource = null
    }

    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateScores(){
        totalPoints = endlessRunnerFragment.game.totalPoints
        currentPoints = endlessRunnerFragment.game.currentPoints
        highScore = endlessRunnerFragment.game.highScore
    }

    private fun bodyIsVisible(pose: Pose): Boolean{
        val importantBodyparts = intArrayOf(5, 11)  // shoulder and hip
        for(i in importantBodyparts){
            if((pose.keyPoints[i].score < 0.25f) || (pose.keyPoints[i+1].score < 0.25f)){
                return false
            }
        }
        return true
    }

    private fun isJumping(shoulderArr: List<Int>, hipArr: List<Int>): Boolean{

        val ratio = 3.0f

        val startTorsoLength = -(shoulderArr[0] - hipArr[0])
        val endTorsoLength = -(shoulderArr[shoulderArr.size-1] - hipArr[hipArr.size-1])

        val shoulderDisplacement = -(shoulderArr[shoulderArr.size-1] - shoulderArr[0])

        val shoulderIsIncreasing = isIncreasing(shoulderArr.takeLast(3))

        if ((shoulderDisplacement >= startTorsoLength/ratio) && (shoulderDisplacement >= endTorsoLength/ratio) && shoulderIsIncreasing){
            return true
        }

        return false
    }


    private fun isIncreasing(arr: List<Int>): Boolean{
        var trueCount = 0
        val threshold = 0.8
        for(i in 1 until arr.size){
            if(arr[i] < arr[i-1]){  // movenet y coordinate is reversed (?), the higher point will have smaller y value
                trueCount += 1
            }
        }
        val truePercentage = trueCount.toFloat() / (arr.size-1).toFloat()
        return truePercentage >= threshold
    }

    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(svEndlessRunner, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            currentFps = fps

                        }

                        override fun onDetectedInfo(
                            poseIsCorrect: Boolean,
                            pose: Pose
                        ) {
                            updateScores()
                            this@EndlessRunnerActivity.runOnUiThread {
                                tvCurrentPoints.text = getString(R.string.current_points, currentPoints.toString())
                                tvHighScore.text = getString(R.string.high_score, highScore.toString())
                                tvTotalPoints.text = getString(R.string.total_points, totalPoints.toString())
                            }


                            // ini buat test aja

//                            val wristY = (pose.keypoints[BodyPart.NOSE.position].coordinate.y).toInt()
//                            if(pose.keypoints[BodyPart.NOSE.position].score > 0.2){
//                                hipHistory.add(wristY)
//                            }
//                            hipHistory = hipHistory.takeLast(5).toMutableList()
//                            if(isIncreasing(hipHistory)){
//                                if(lastJumpTime != null){
//                                    if((SystemClock.elapsedRealtime() - lastJumpTime!!) > 1000){ // it will have to wait at least 1 sec
//                                        lastJumpTime = SystemClock.elapsedRealtime()
//                                        endlessRunnerFragment.game.triggerJump()
//                                    }
//                                }else{
//                                    lastJumpTime = SystemClock.elapsedRealtime()
//                                    endlessRunnerFragment.game.triggerJump()
//                                }
//                            }

                            if(bodyIsVisible(pose)){
                                numFramesRequirement = if(currentFps < 5){
                                    5
                                }else{
                                    currentFps
                                }

                                shoulderY = (pose.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate.y).toInt()
                                hipY = (pose.keyPoints[BodyPart.RIGHT_HIP.position].coordinate.y).toInt()

                                shoulderHistory.add(shoulderY)
                                hipHistory.add(hipY)
                                shoulderHistory = shoulderHistory.takeLast(numFramesRequirement).toMutableList()
                                hipHistory = hipHistory.takeLast(numFramesRequirement).toMutableList()

                                if((shoulderHistory.size == hipHistory.size) and (shoulderHistory.size >= numFramesRequirement)){
                                    if(isJumping(shoulderHistory, hipHistory)){
                                        if(lastJumpTime != null){
                                            if((SystemClock.elapsedRealtime() - lastJumpTime!!) > 1000){ // it will have to wait at least 1 sec
                                                lastJumpTime = SystemClock.elapsedRealtime()
                                                endlessRunnerFragment.game.triggerJump()
                                            }
                                        }else{
                                            lastJumpTime = SystemClock.elapsedRealtime()
                                            endlessRunnerFragment.game.triggerJump()
                                        }
                                    }
                                }
                            }
                        }
                    }).apply {
                        prepareCamera()
                    }
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun createPoseEstimator() {
        val poseEstimator = PoseEstimator(this.assets)

        cameraSource?.setEstimator(poseEstimator)
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }

    override fun onButtonCloseListener(dialog: GameReportDialog, calories: Double) {
        dialog.dismiss()
        finish()
    }
}
