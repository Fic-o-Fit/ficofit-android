package com.c22ps072.ficofit.ui.authentication.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            signInAction()
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInAction(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        setLoading(true)
        lifecycleScope.launchWhenResumed {
            if (signInJob.isActive) signInJob.cancel()

            signInJob  = launch {
                viewModel.postUserSignIn(email, password).collect { result ->
                    result.onSuccess { credentials ->
                        credentials.apply {
                            Log.d("SIGN IN", "token : $token")
                            Log.d("SIGN IN", "refreshToken : $refreshToken")
                            token.let { token->
                                viewModel.saveUserToken(token)
                            }
                            refreshToken.let { refreshToken ->
                                viewModel.saveUserRefreshToken(refreshToken)
                            }.also {
                                Intent(requireContext(), HomeActivity::class.java).also { intent ->
                                    intent.putExtra(EXTRA_TOKEN,token)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
                        Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show()
                    }

                    result.onFailure {
                        Toast.makeText(context, "Sign In Failed", Toast.LENGTH_LONG).show()
                        setLoading(false)
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