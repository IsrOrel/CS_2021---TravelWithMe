import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Attraction_Data
import com.example.travelwithme.R

class Attraction_Adapter(private val attractions: List<Attraction_Data>) :
    RecyclerView.Adapter<Attraction_Adapter.AttractionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attractions, parent, false) // Correct layout inflated here
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.bind(attraction)
        holder.itemView.setOnClickListener {
            
        }
    }

    override fun getItemCount(): Int {
        return attractions.size
    }

    inner class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.attractionsIcon)
        private val titleTextView: TextView = itemView.findViewById(R.id.attractionstitle)
        private val placeTextView: TextView = itemView.findViewById(R.id.attractionsplace)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.attractionsdesc)

        fun bind(attraction: Attraction_Data) {
            iconImageView.setImageResource(attraction.image)
            titleTextView.text = attraction.title
            placeTextView.text = attraction.place
            descriptionTextView.text = attraction.description
        }
    }
}
