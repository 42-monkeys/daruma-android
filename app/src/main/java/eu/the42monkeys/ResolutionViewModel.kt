package eu.the42monkeys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    val mDateLimit = MutableLiveData<Date>()

    override fun toString(): String {
        return "resolution text: ${mResolutionText.value} | " +
                "temper: ${mTemper.value} | " +
                "commitment: ${mCommitment.value} | " +
                "date limit: ${mDateLimit.value}"
    }
}