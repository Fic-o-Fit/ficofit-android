package com.c22ps072.ficofit.ui.authentication.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ScrollView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentDialogProfileBinding
import com.c22ps072.ficofit.ui.authentication.AuthenticationViewModel
import com.c22ps072.ficofit.ui.home.HomeActivity
import com.c22ps072.ficofit.utils.Helpers.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class DialogProfileFragment : Fragment() {
    private var _binding: FragmentDialogProfileBinding? = null

    private val binding get() = _binding!!
    private val args: DialogProfileFragmentArgs by navArgs()
    private lateinit var gender: String
    private lateinit var weight: String
    private lateinit var height: String

    private lateinit var simpleName: String

    private var dialogJob: Job = Job()
    private val viewModel: AuthenticationViewModel by viewModels()

    private var initStartDelay: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDialogProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        simpleName = args.name.split(' ')[0]
        setupAnimation()
    }

    private fun setObjectAnimator(target: View, aStartDelay: Long) {
        target.visibility = View.VISIBLE
        initStartDelay += aStartDelay
        val objectAnimator = ObjectAnimator.ofFloat(target, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            play(objectAnimator)
            startDelay = initStartDelay
        }.start()
        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
    }

    @SuppressLint("SetTextI18n")
    private fun setupAnimation() {
        binding.tvGreetings.text = getString(R.string.greetings, simpleName)
        setObjectAnimator(binding.tvGreetings, 0)

        setObjectAnimator(binding.tvGetToKnow, 1500)

        binding.tvGenderQ.text = getString(R.string.gender_q, simpleName)
        setObjectAnimator(binding.tvGenderQ, 2000)

        setObjectAnimator(binding.tvHeadingGender, 1500)
        setObjectAnimator(binding.rgGender, 0)

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbMale.id -> gender = "L"
                binding.rbFemale.id -> gender = "P"
            }

            initStartDelay = 0
            binding.tvHeight.text = getString(R.string.height, simpleName)
            setObjectAnimator(binding.tvHeight, 0)
            binding.textInputGroup.visibility = View.VISIBLE

            binding.icSend.setOnClickListener {
                height = binding.etInput.text.toString().trim()
                if (height.isNotEmpty()) {
                    binding.tvHeightAnswer.text = "$height CM"
                    setObjectAnimator(binding.tvHeightAnswer, 0)
                    binding.etInput.text = null

                    binding.tvWeight.text = getString(R.string.weight, simpleName)
                    setObjectAnimator(binding.tvWeight, 0)
                    binding.icSend.setOnClickListener {
                         weight = binding.etInput.text.toString().trim()
                        if (weight.isNotEmpty()) {
                            binding.tvWeightAnswer.text = "$weight Kg"
                            setObjectAnimator(binding.tvWeightAnswer, 0)
                            binding.etInput.text = null

                            setObjectAnimator(binding.tvClosing, 0)
                            setObjectAnimator(binding.btnContinue, 1000)

                            binding.btnContinue.setOnClickListener {
                                signUpAction()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun signUpAction(){
        setLoading(true)
        lifecycleScope.launchWhenResumed {
            if (dialogJob.isActive) dialogJob.cancel()

            dialogJob = launch {
                viewModel.postUserRegister(
                    args.name,
                    args.email,
                    args.password,
                    gender,
                    weight,
                    height
                ).collect{ result ->

                    setLoading(false)
                    result.onSuccess {
                        MotionToast.createToast(requireActivity(),
                            "Sign Up Successful",
                            "You'll be redirected to Home Page",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.nunito)
                        )

                        signInAction()
                    }
                    result.onFailure {
                        MotionToast.createToast(requireActivity(),
                            "Sign Up Failed",
                            "Please try again",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.nunito)
                        )
                    }
                }
            }
        }
    }

    private fun signInAction() {
        setLoading(true)

        lifecycleScope.launchWhenResumed {
            if (dialogJob.isActive) dialogJob.cancel()

            dialogJob = launch {
                viewModel.postUserSignIn(args.email, args.password).collect{ result ->
                    result.onSuccess { credentials ->
                        credentials.apply {
                            Log.d("SIGN UP", "token : $token")
                            Log.d("SIGN UP", "refreshToken : $refreshToken")
                            token.let { token->
                                viewModel.saveUserToken(token)
                            }
                            refreshToken.let { refreshToken ->
                                viewModel.saveUserRefreshToken(refreshToken)
                            }.also {
                                viewModel.saveEmailUser(args.email)
                                viewModel.saveUserPassword(args.password)
                                Intent(requireContext(), HomeActivity::class.java).also { intent ->
                                    intent.putExtra(HomeActivity.EXTRA_TOKEN,token)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                    result.onFailure {
                        setLoading(false)
                        MotionToast.createToast(requireActivity(),
                            "Sign In Failed",
                            "Please try again",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.nunito)
                        )
                    }
                }
            }
        }
    }


    private fun setLoading(state: Boolean){
        binding.apply {
            scrollView.isEnabled = !state
            if (state) {
                viewLoading.isVisible(true)
            }else {
                viewLoading.isVisible(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}