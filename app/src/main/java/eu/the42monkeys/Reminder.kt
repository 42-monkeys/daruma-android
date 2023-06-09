package eu.the42monkeys

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import eu.the42monkeys.databinding.FragmentReminderBinding
import java.util.Locale


class Reminder : Fragment() {

    private var _binding: FragmentReminderBinding? = null

    private var textToSpeech: TextToSpeech? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        textToSpeech = TextToSpeech(activity) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data")
                }
                when (ResolutionViewModel.TemperType.valueOf(
                    requireArguments().getString("temper")!!.uppercase()
                )) {
                    ResolutionViewModel.TemperType.AUTHORITARIAN -> {
                        textToSpeech!!.setSpeechRate(0.8f)
                        textToSpeech!!.setPitch(0.1f)
                    }
                    ResolutionViewModel.TemperType.SARCASTIC -> {
                        textToSpeech!!.setSpeechRate(1.8f)
                        textToSpeech!!.setPitch(2.5f)
                    }
                    ResolutionViewModel.TemperType.MOTIVATIONAL -> {
                        textToSpeech!!.setSpeechRate(1f)
                        textToSpeech!!.setPitch(1f)
                    }
                    else -> {}
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }


        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reminderBody.text = requireArguments().getString("body")

        binding.darumaImage.setOnClickListener { _ ->
            textToSpeech?.speak(binding.reminderBody.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        binding.darumaImage.startAnimation( AnimationUtils.loadAnimation(context, R.anim.pulse))

        when (ResolutionViewModel.TemperType.valueOf(
            requireArguments().getString("temper")!!.uppercase()
        )) {
            ResolutionViewModel.TemperType.AUTHORITARIAN -> binding.darumaImage.setImageResource(R.drawable.daruma_black)
            ResolutionViewModel.TemperType.SARCASTIC -> binding.darumaImage.setImageResource(R.drawable.daruma_green)
            ResolutionViewModel.TemperType.MOTIVATIONAL -> binding.darumaImage.setImageResource(R.drawable.daruma_gold)
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (textToSpeech != null) {
            textToSpeech!!.stop();
            textToSpeech!!.shutdown();
        }

        _binding = null
    }
}
