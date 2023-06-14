package eu.the42monkeys.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar
import java.util.Date

class ResolutionViewModel : ViewModel() {
    enum class TemperType(val type: String) {
        MOTIVATIONAL("motivational"), SARCASTIC("sarcastic"), AUTHORITARIAN("authoritarian"), RANDOM("random")
    }

    enum class CommitmentType(val type: String) {
        LOW("low"), MODERATE("moderate"), HIGH("high")
    }

    val mResolutionText = MutableLiveData<String>()
    val mTemper = MutableLiveData<TemperType>()
    var mCommitment = MutableLiveData<CommitmentType>()
    val mDateLimit = MutableLiveData<Calendar>()

    override fun toString(): String {
        return "resolution text: ${mResolutionText.value} | " +
                "temper: ${mTemper.value} | " +
                "commitment: ${mCommitment.value} | " +
                "date limit: ${mDateLimit.value}"
    }
}