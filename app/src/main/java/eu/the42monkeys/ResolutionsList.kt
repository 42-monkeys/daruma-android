package eu.the42monkeys

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.FragmentResolutionsListBinding
import eu.the42monkeys.databinding.ResolutionItemBinding
import eu.the42monkeys.model.Resolution

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

        val jwtToken = SharedPrefsHelper.getJwtToken(requireActivity().applicationContext)
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
                            SharedPrefsHelper.saveJwtToken(
                                requireActivity().applicationContext,
                                null
                            )
                            findNavController().navigate(R.id.action_ResolutionsList_to_SignIn)
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

        binding.addResolution.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_ResolutionsList_to_EditResolution)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ResolutionsAdapter : RecyclerView.Adapter<ResolutionsAdapter.ViewHolder>() {
        private var dataList: Array<Resolution> = emptyArray()
        private var navController: NavController? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.resolution_item, parent, false)

            navController = Navigation.findNavController(parent)
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
                itemView.setOnClickListener { _ ->
                    val bundle = Bundle()
                    bundle.putInt("resolution_id", item.id)
                    navController!!.navigate(R.id.action_ResolutionsList_to_RemindersList, bundle)
                }
                val resolutionText = itemView.findViewById<TextView>(R.id.resolution_text)
                val darumaImage = itemView.findViewById<ImageView>(R.id.daruma_item_image)
                val commitmentImage = itemView.findViewById<ImageView>(R.id.commitment_image)
                val timeLimitText = itemView.findViewById<TextView>(R.id.time_limit_text)
                val offerText = itemView.findViewById<TextView>(R.id.offer_text)

                resolutionText.text = item.body
                timeLimitText.text = item.time_limit
                offerText.text = item.offer.toString()

                when (ResolutionViewModel.TemperType.valueOf(item.temper.uppercase())) {
                    ResolutionViewModel.TemperType.AUTHORITARIAN -> darumaImage.setImageResource(R.drawable.daruma_black)
                    ResolutionViewModel.TemperType.SARCASTIC -> darumaImage.setImageResource(R.drawable.daruma_green)
                    ResolutionViewModel.TemperType.MOTIVATIONAL -> darumaImage.setImageResource(R.drawable.daruma_gold)
                    else -> {}
                }

                when (ResolutionViewModel.CommitmentType.valueOf(item.commitment.uppercase())) {
                    ResolutionViewModel.CommitmentType.MODERATE -> commitmentImage.setImageResource(R.drawable.bell_selected_2)
                    ResolutionViewModel.CommitmentType.HIGH -> commitmentImage.setImageResource(R.drawable.bell_selected_3)
                    else -> {}
                }
            }
        }
    }
}
