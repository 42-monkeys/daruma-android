package eu.the42monkeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.the42monkeys.databinding.FragmentReminderBinding

class Reminder : Fragment() {

    private var _binding: FragmentReminderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reminderBody.text = requireArguments().getString("body")

        when (ResolutionViewModel.TemperType.valueOf(requireArguments().getString("temper")!!.uppercase())) {
            ResolutionViewModel.TemperType.AUTHORITARIAN -> binding.darumaImage.setImageResource(R.drawable.daruma_black)
            ResolutionViewModel.TemperType.SARCASTIC -> binding.darumaImage.setImageResource(R.drawable.daruma_green)
            ResolutionViewModel.TemperType.MOTIVATIONAL -> binding.darumaImage.setImageResource(R.drawable.daruma_gold)
            else -> {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
