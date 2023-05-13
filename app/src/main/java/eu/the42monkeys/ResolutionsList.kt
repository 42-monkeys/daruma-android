package eu.the42monkeys

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.FragmentResolutionsListBinding
import eu.the42monkeys.model.Resolution

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ResolutionsList : Fragment() {

    private var _binding: FragmentResolutionsListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResolutionsAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        var jwtToken = SharedPrefsHelper.getJwtToken(requireActivity().applicationContext)
        if (jwtToken != null) {
            Fuel.get("${BuildConfig.BACKEND_URL}/resolutions.json")
                .header("Authorization", jwtToken)
                .responseObject(Resolution.Deserializer()) { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                            val resolutions = result.component1()
                            adapter.setData(resolutions!!)

                        }

                        is Result.Failure -> {
                            val exception = result.getException()
                            Toast.makeText(
                                activity,
                                "${R.string.list_resolution_error_server_call}: $exception",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
        }

        _binding = FragmentResolutionsListBinding.inflate(inflater, container, false)
        recyclerView = _binding!!.resolutionsList
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ResolutionsAdapter()
        recyclerView.adapter = adapter
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ResolutionsAdapter : RecyclerView.Adapter<ResolutionsAdapter.ViewHolder>() {
        private var _binding: FragmentResolutionsListBinding? = null
        private val binding get() = _binding!!

        private var dataList: Array<Resolution> = emptyArray()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.resolution_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        fun setData(dataList: Array<Resolution>) {
            this.dataList = dataList
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Resolution) {
                 val textView = itemView.findViewById<TextView>(R.id.textView)
                 textView.text = item.body
            }
        }
    }
}
