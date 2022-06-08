package com.c22ps072.ficofit.ui.authentication.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.c22ps072.ficofit.R
import com.c22ps072.ficofit.databinding.FragmentSignInBinding
import com.c22ps072.ficofit.ui.home.HomeActivity
import kotlinx.coroutines.Job
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.c22ps072.ficofit.ui.authentication.AuthenticationViewModel
import com.c22ps072.ficofit.ui.home.HomeActivity.Companion.EXTRA_TOKEN
import com.c22ps072.ficofit.utils.Helpers.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!


    private var signInJob: Job = Job()
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var emptyField = false

            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                binding.etEmail.requestFocus()
                emptyField = true
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                emptyField = true
            }

            if (!emptyField) {
                signInAction(email, password)
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInAction(email: String, password: String){
        setLoading(true)
        lifecycleScope.launchWhenResumed {
            if (signInJob.isActive) signInJob.cancel()

            signInJob  = launch {
                viewModel.postUserSignIn(email, password).collect { result ->
                    result.onSuccess { credentials ->
                        credentials.apply {
                            Log.d("SIGN IN", "token : $token")
                            Log.d("SIGN IN", "refreshToken : $refreshToken")
                            viewModel.saveEmailUser(email)
                            token.let { token->
                                viewModel.saveUserToken(token)
                            }
                            refreshToken.let { refreshToken ->
                                viewModel.saveUserRefreshToken(refreshToken)
                            }
                            Intent(requireContext(), HomeActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN,token)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                        MotionToast.createToast(requireActivity(),
                            "Sign In Successful",
                            "You'll be redirected to Home Page",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.nunito)
                        )
                    }

                    result.onFailure {
                        MotionToast.createToast(requireActivity(),
                            "Sign In Failed",
                            "Wrong email or password",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireActivity(), R.font.nunito)
                        )
                        setLoading(false)
                        binding.etPassword.text = null
                    }
                }
            }
        }

    }

    private fun setLoading(state: Boolean){
        binding.apply {
            etEmail.isEnabled = !state
            etPassword.isEnabled = !state
            btnSignIn.isEnabled = !state

            if (state) {
                viewLoading.isVisible(true)

            }else {
                viewLoading.isVisible(false)
            }
        }
    }

    companion object {

    }
}