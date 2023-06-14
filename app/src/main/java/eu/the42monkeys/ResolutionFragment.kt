package eu.the42monkeys

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.the42monkeys.model.ResolutionViewModel

class ResolutionFragment : Fragment() {

    companion object {
        fun newInstance() = ResolutionFragment()
    }

    private lateinit var viewModel: ResolutionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resolution, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResolutionViewModel::class.java)
        // TODO: Use the ViewModel
    }

}