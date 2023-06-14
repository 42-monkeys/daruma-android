package eu.the42monkeys

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.the42monkeys.databinding.FragmentResetPasswordBinding


class ResetPassword : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resetPasswordWebView.settings.javaScriptEnabled = true
        binding.resetPasswordWebView.loadUrl("${BuildConfig.BACKEND_URL}/users/password/new")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}