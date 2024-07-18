
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.travelwithme.Data.Attraction_Data
import com.example.travelwithme.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttractionDialog(private val context: Context) {

    fun show(attraction: Attraction_Data) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.confirm_attraction_dialog, null)
        val alertDialog = AlertDialog.Builder(context).setView(dialogView).create()

        val titleTextView: TextView = dialogView.findViewById(R.id.dialogTitle)
        val descriptionTextView: TextView = dialogView.findViewById(R.id.dialogDescription)
        val placeTextView: TextView = dialogView.findViewById(R.id.dialogPlace)
        val dateButton: Button = dialogView.findViewById(R.id.btnSelectDate)
        val timeButton: Button = dialogView.findViewById(R.id.btnSelectTime)
        val dateTextView: TextView = dialogView.findViewById(R.id.tvSelectedDate)
        val timeTextView: TextView = dialogView.findViewById(R.id.tvSelectedTime)
        val confirmButton: Button = dialogView.findViewById(R.id.btnConfirm)

        titleTextView.text = attraction.title
        descriptionTextView.text = attraction.description
        placeTextView.text = attraction.place

        dateButton.setOnClickListener {
            showDatePicker(dateTextView)
        }

        timeButton.setOnClickListener {
            showTimePicker(timeTextView)
        }

        confirmButton.setOnClickListener {
            // Handle confirmation (e.g., save to database)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showDatePicker(dateTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(selectedDate.time)
        }, year, month, day).show()
    }

    private fun showTimePicker(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeTextView.text = selectedTime
        }, hour, minute, true).show()
    }
}