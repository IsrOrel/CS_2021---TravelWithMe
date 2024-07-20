package com.example.travelwithme

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.ChecklistItem
import com.example.travelwithme.databinding.CheckListItemBinding

class ChecklistAdapter(private val onItemCheckedChange: (ChecklistItem, Boolean) -> Unit) :
    ListAdapter<ChecklistItem, ChecklistAdapter.ViewHolder>(ChecklistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CheckListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: CheckListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChecklistItem) {
            binding.checkBox.apply {
                text = item.text
                isChecked = item.isChecked
                setOnCheckedChangeListener { _, isChecked ->
                    onItemCheckedChange(item, isChecked)
                }
            }
        }
    }

    class ChecklistDiffCallback : DiffUtil.ItemCallback<ChecklistItem>() {
        override fun areItemsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChecklistItem, newItem: ChecklistItem): Boolean {
            return oldItem == newItem
        }
    }
}