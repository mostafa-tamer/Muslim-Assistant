package com.example.muslimsAssistant.fragments.reminderFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.muslimsAssistant.database.ReminderItem
import com.example.muslimsAssistant.databinding.ReminderAdapterViewHolderBinding
import com.example.muslimsAssistant.utils.CustomList
import com.example.muslimsAssistant.utils.cancelPendingIntent

class ReminderAdapter(private val list: CustomList<ReminderItem>, private val context: Context) :
    RecyclerView.Adapter<ReminderAdapterViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderAdapterViewHolder {
        return create(parent, list, this, context)
    }

    private fun create(
        parent: ViewGroup,
        list: CustomList<ReminderItem>,
        reminderAdapter: ReminderAdapter,
        context: Context
    ): ReminderAdapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ReminderAdapterViewHolderBinding.inflate(layoutInflater, parent, false)
        return ReminderAdapterViewHolder(binding, list, reminderAdapter, context)
    }

    override fun onBindViewHolder(holder: ReminderAdapterViewHolder, position: Int) {
        holder.bind(list[position])
        holder.eventsHandler(position)
    }
}

class ReminderAdapterViewHolder(
    private val binding: ReminderAdapterViewHolderBinding,
    private val list: CustomList<ReminderItem>,
    private val reminderAdapter: ReminderAdapter,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(property: ReminderItem) {
        binding.reminderItem = property
        binding.executePendingBindings()
    }

    fun eventsHandler(position: Int) {
        var lock = false
        binding.removeButton.setOnClickListener {
            if (position < list.size && !lock) {
                lock = true

                cancelPendingIntent(list[position].id, context)
                println("${list[position]} is canceled")

                list.removeAt(position)
                reminderAdapter.notifyItemRemoved(position)
                reminderAdapter.notifyItemRangeChanged(
                    position,
                    list.size
                )
                list.sizeLiveData.value = list.sizeLiveData.value
            }
        }
    }


}

