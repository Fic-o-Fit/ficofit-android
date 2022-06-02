package com.c22ps072.ficofit.ui.authentication.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.c22ps072.ficofit.databinding.FragmentDialogProfileBinding
import com.c22ps072.ficofit.ui.home.HomeActivity

class DialogProfileFragment : Fragment() {
    private var _binding: FragmentDialogProfileBinding? = null

    private val binding get() = _binding!!

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
        setObjectAnimator(binding.tvGreetings, 0)
        setObjectAnimator(binding.tvGetToKnow, 1500)
        setObjectAnimator(binding.tvGenderQ, 2000)
        setObjectAnimator(binding.tvHeadingGender, 1500)
        setObjectAnimator(binding.rgGender, 0)

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            var gender: String? = null
            when (checkedId) {
                binding.rbMale.id -> gender = "MALE"
                binding.rbFemale.id -> gender = "FEMALE"
            }

            if (gender != null) {
                initStartDelay = 0
                setObjectAnimator(binding.tvHeight, 0)
                binding.textInputGroup.visibility = View.VISIBLE

                binding.icSend.setOnClickListener {
                    val inputHeight = binding.etInput.text
                    if (inputHeight.isNotEmpty()) {
                        binding.tvHeightAnswer.text = "$inputHeight CM"
                        setObjectAnimator(binding.tvHeightAnswer, 0)
                        binding.etInput.text = null

                        setObjectAnimator(binding.tvWeight, 0)
                        binding.icSend.setOnClickListener {
                            val inputWeight = binding.etInput.text
                            if (inputWeight.isNotEmpty()) {
                                binding.tvWeightAnswer.text = "$inputWeight Kg"
                                setObjectAnimator(binding.tvWeightAnswer, 0)
                                binding.etInput.text = null

                                setObjectAnimator(binding.tvClosing, 0)
                                setObjectAnimator(binding.btnContinue, 1000)

                                binding.btnContinue.setOnClickListener {
                                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                    activity?.finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}