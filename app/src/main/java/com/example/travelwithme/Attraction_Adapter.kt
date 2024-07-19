import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwithme.Attraction_Data
import com.example.travelwithme.R

class Attraction_Adapter(private var attractions: List<Attraction_Data>) :
    RecyclerView.Adapter<Attraction_Adapter.AttractionViewHolder>() {

    var onImageClick: ((Attraction_Data) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attractions, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.bind(attraction)
    }

    override fun getItemCount(): Int = attractions.size

    fun updateAttractions(newAttractions: List<Attraction_Data>) {
        attractions = newAttractions
        notifyDataSetChanged()
    }

    inner class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.attractionsIcon)
        private val titleTextView: TextView = itemView.findViewById(R.id.attractionstitle)
        private val placeTextView: TextView = itemView.findViewById(R.id.attractionsplace)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.attractionsdesc)

        fun bind(attraction: Attraction_Data) {
            Log.d("AttractionViewHolder", "Binding item: ${attraction.title}")

            iconImageView.setImageResource(attraction.image)
            titleTextView.text = attraction.title
            placeTextView.text = attraction.place
            descriptionTextView.text = attraction.description

            iconImageView.setOnClickListener {
                Log.d("AttractionViewHolder", "Image clicked: ${attraction.title}")
                onImageClick?.invoke(attraction)
            }
        }
    }
}


