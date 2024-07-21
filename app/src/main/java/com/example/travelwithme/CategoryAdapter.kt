import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Category
import com.example.travelwithme.R

class CategoryAdapter(private var categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var onCategoryClick: ((Category) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }


    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<Category>) {
        Log.d("CategoryAdapter", "Updating categories: ${newCategories.size} items")
        categories = newCategories
        notifyDataSetChanged()
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.categoryIcon)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.categoryDescription)

        fun bind(category: Category) {
            Log.d("CategoryAdapter", "Binding category: ${category.description} with icon ID: ${category.iconResId}")
            iconImageView.setImageResource(category.iconResId)
            descriptionTextView.text = category.description

            itemView.setOnClickListener {
                onCategoryClick?.invoke(category)
            }
        }
    }
}