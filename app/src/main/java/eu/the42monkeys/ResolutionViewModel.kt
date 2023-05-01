package eu.the42monkeys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class ResolutionViewModel : ViewModel() {
    enum class TemperType {
        MOTIVATIONAL, SARCASTIC, AUTHORITARIAN, RANDOM
    }

    enum class CommitmentType {
        LOW, MEDIUM, HIGH
    }

    val mResolutionText = MutableLiveData<String>()
    val mTemper = MutableLiveData<TemperType>()
    var mCommitment = MutableLiveData<CommitmentType>()
    val mDateLimit = MutableLiveData<Date>()
    val mOffer = MutableLiveData<Int>()

    override fun toString(): String {
        return "resolution text: ${mResolutionText.value} | " +
                "temper: ${mTemper.value} | " +
                "commitment: ${mCommitment.value} | " +
                "date limit: ${mDateLimit.value} |" +
                "offer: ${mOffer.value}"
    }
}