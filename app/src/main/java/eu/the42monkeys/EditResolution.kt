package eu.the42monkeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import eu.the42monkeys.databinding.FragmentEditResolutionBinding
import eu.the42monkeys.model.ResolutionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class EditResolution : Fragment() {

    private var _binding: FragmentEditResolutionBinding? = null
    private val vm: ResolutionViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentEditResolutionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resolutionTextInput.doOnTextChanged { text, _, _, _ ->
            vm.mResolutionText.value = text.toString()
        }

        binding.dateLimitPicker.minDate = Calendar.getInstance().timeInMillis

        binding.radioGoldDaruma.setOnCheckedChangeListener { _, _ ->
            resetTemperRadios()
            binding.radioGoldDaruma.setBackgroundResource(R.drawable.daruma_gold)
            vm.mTemper.value = ResolutionViewModel.TemperType.MOTIVATIONAL
        }
        binding.radioBlackDaruma.setOnCheckedChangeListener { _, _ ->
            resetTemperRadios()
            binding.radioBlackDaruma.setBackgroundResource(R.drawable.daruma_black)
            vm.mTemper.value = ResolutionViewModel.TemperType.AUTHORITARIAN
        }
        binding.radioGreenDaruma.setOnCheckedChangeListener { _, _ ->
            resetTemperRadios()
            binding.radioGreenDaruma.setBackgroundResource(R.drawable.daruma_green)
            vm.mTemper.value = ResolutionViewModel.TemperType.SARCASTIC
        }
        binding.radioRedDaruma.setOnCheckedChangeListener { _, _ ->
            resetTemperRadios()
            binding.radioRedDaruma.setBackgroundResource(R.drawable.daruma_red)
            vm.mTemper.value = ResolutionViewModel.TemperType.RANDOM
        }

        binding.commitmentRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            resetCommitmentRadios()
            when (checkedId) {
                binding.radioLowCommitment.id -> {
                    binding.radioLowCommitment.setBackgroundResource(R.drawable.bell_selected_1)
                    vm.mCommitment.value = ResolutionViewModel.CommitmentType.LOW
                }

                binding.radioMediumCommitment.id -> {
                    binding.radioMediumCommitment.setBackgroundResource(R.drawable.bell_selected_2)
                    vm.mCommitment.value = ResolutionViewModel.CommitmentType.MODERATE
                }

                binding.radioHighCommitment.id -> {
                    binding.radioHighCommitment.setBackgroundResource(R.drawable.bell_selected_3)
                    vm.mCommitment.value = ResolutionViewModel.CommitmentType.HIGH
                }
            }
        }

        binding.dateLimitPicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            vm.mDateLimit.value = calendar
        }

        binding.saveButton.setOnClickListener { button ->
            button.isEnabled = false
            // validate
            val isValid = validate()
            if (!isValid) {
                button.isEnabled = true
                return@setOnClickListener
            }
            val activity = (activity as MainActivity)
            activity.pendingResolution = vm

            activity.saveResolution()
            button.isEnabled = true
        }
    }

    private fun validate(): Boolean {
        if (vm.mResolutionText.value == null || (vm.mResolutionText.value != null && vm.mResolutionText.value!!.isEmpty())) {
            Toast.makeText(
                activity,
                R.string.edit_resolution_resolution_text_error,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (vm.mTemper.value == null) {
            Toast.makeText(activity, R.string.edit_resolution_temper_error, Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (vm.mCommitment.value == null) {
            Toast.makeText(activity, R.string.edit_resolution_commitment_error, Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (vm.mDateLimit.value == null) {
            Toast.makeText(activity, R.string.edit_resolution_date_limit_error, Toast.LENGTH_SHORT)
                .show()
            return false
        }

        return true
    }

    private fun resetTemperRadios() {
        binding.radioGoldDaruma.setBackgroundResource(R.drawable.daruma_gold_new)
        binding.radioBlackDaruma.setBackgroundResource(R.drawable.daruma_black_new)
        binding.radioGreenDaruma.setBackgroundResource(R.drawable.daruma_green_new)
        binding.radioRedDaruma.setBackgroundResource(R.drawable.daruma_red_new)
        binding.radioGoldDaruma.isChecked = false
        binding.radioBlackDaruma.isChecked = false
        binding.radioGreenDaruma.isChecked = false
        binding.radioRedDaruma.isChecked = false
    }

    private fun resetCommitmentRadios() {
        binding.radioLowCommitment.setBackgroundResource(R.drawable.bell_1)
        binding.radioMediumCommitment.setBackgroundResource(R.drawable.bell_2)
        binding.radioHighCommitment.setBackgroundResource(R.drawable.bell_3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}