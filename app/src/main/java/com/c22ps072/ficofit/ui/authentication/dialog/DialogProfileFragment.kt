package com.c22ps072.ficofit.ui.authentication.dialog

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.fragment.app.Fragment
import com.c22ps072.ficofit.databinding.FragmentDialogProfileBinding

class DialogProfileFragment : Fragment() {
    private var _binding: FragmentDialogProfileBinding? = null

    private val binding get() = _binding!!

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

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}