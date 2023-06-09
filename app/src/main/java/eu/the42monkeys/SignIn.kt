package eu.the42monkeys

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.FragmentSignInBinding
import java.nio.charset.Charset

class SignIn : Fragment() {

    private var _binding: FragmentSignInBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener { button ->
            button.isEnabled = false
            // TODO: wait spinner?
            val email = binding.signInEmailEditText.text
            val password = binding.signInPasswordEditText.text

            val requestBody = "{\"user\":{\"email\":\"$email\",\"password\":\"$password\"}}"

            Fuel.post("${BuildConfig.BACKEND_URL}/users/sign_in.json")
                .header("Content-Type" to "application/json")
                .body(requestBody, Charset.forName("UTF-8"))
                .response { _, response, result ->
                    when (result) {
                        is Result.Success -> {
                            val bearerToken = response["Authorization"].first()
                            SharedPrefsHelper.saveJwtToken(
                                requireActivity().applicationContext,
                                bearerToken
                            )
                            (activity as MainActivity).sendDeviceToken()
                            findNavController().navigate(R.id.action_SignIn_to_ResolutionsList)
                        }

                        is Result.Failure -> {
                            Toast.makeText(
                                requireActivity(),
                                "Wrong username or password!",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                    button.isEnabled = true
                }
        }

        binding.goToSignUpButton.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_SignIn_to_SignUp)
        }

        binding.goToResetPassword.setOnClickListener{ _ ->
            findNavController().navigate(R.id.action_SignIn_to_ResetPassword)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
