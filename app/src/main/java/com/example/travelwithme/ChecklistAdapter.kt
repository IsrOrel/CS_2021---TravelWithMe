import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Data.ChecklistItem
import com.example.travelwithme.databinding.CheckListItemBinding

class ChecklistAdapter(
    private val onItemCheckedChange: (ChecklistItem, Boolean) -> Unit,
    private val onItemDelete: (ChecklistItem) -> Unit
) : ListAdapter<ChecklistItem, ChecklistAdapter.ViewHolder>(ChecklistDiffCallback()) {

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
                updateStrikeThrough(this, item.isChecked)
                setOnCheckedChangeListener { _, isChecked ->
                    onItemCheckedChange(item, isChecked)
                    updateStrikeThrough(this, isChecked)
                }
            }
            binding.deleteButton.setOnClickListener {
                onItemDelete(item)
            }
        }

        private fun updateStrikeThrough(checkBox: CheckBox, isChecked: Boolean) {
            if (isChecked) {
                checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                checkBox.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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