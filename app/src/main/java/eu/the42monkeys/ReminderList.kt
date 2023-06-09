package eu.the42monkeys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import eu.the42monkeys.databinding.FragmentRemindersListBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ReminderList : Fragment() {

    private var _binding: FragmentRemindersListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RemindersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resolutionId = requireArguments().getInt("resolution_id")

        val jwtToken = SharedPrefsHelper.getJwtToken(requireActivity().applicationContext)
        if (jwtToken != null) {
            Fuel.get("${BuildConfig.BACKEND_URL}/resolutions/$resolutionId/reminders.json")
                .header("Authorization", jwtToken)
                .responseObject(eu.the42monkeys.model.Reminder.Deserializer()) { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                            val reminders = result.component1()
                            adapter.setData(reminders!!)

                        }

                        is Result.Failure -> {
                            Toast.makeText(
                                activity,
                                "error from the server",
                                Toast.LENGTH_SHORT
                            ).show()
                            SharedPrefsHelper.saveJwtToken(
                                requireActivity().applicationContext,
                                null
                            )
                            findNavController().navigate(R.id.action_RemindersList_to_SignIn)
                        }
                    }
                }
        }

        _binding = FragmentRemindersListBinding.inflate(inflater, container, false)
        recyclerView = _binding!!.resolutionsList
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RemindersAdapter()
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

    class RemindersAdapter : RecyclerView.Adapter<RemindersAdapter.ViewHolder>() {
        private var navController: NavController? = null
        private var dataList: Array<eu.the42monkeys.model.Reminder> = emptyArray()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.reminder_item, parent, false)
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

        fun setData(dataList: Array<eu.the42monkeys.model.Reminder>) {
            this.dataList = dataList
            notifyDataSetChanged()
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: eu.the42monkeys.model.Reminder) {
                itemView.setOnClickListener { _ ->
                    val bundle = Bundle()
                    bundle.putString("body", item.body)
                    bundle.putString("temper", item.temper)
                    navController!!.navigate(R.id.action_RemindersList_to_Reminder, bundle)
                }

                val reminderBodyText = itemView.findViewById<TextView>(R.id.reminder_item_body)
                val reminderCreatedAtText =
                    itemView.findViewById<TextView>(R.id.reminder_item_created_at)

                reminderBodyText.text = item.body
                reminderCreatedAtText.text = item.created_at_formatted
            }
        }
    }
}
