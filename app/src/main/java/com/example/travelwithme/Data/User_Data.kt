package com.example.travelwithme.Data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "user_data")
data class User_Data(
    @PrimaryKey
    val email: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "take_off_date")
    val takeOffDate: Date,

    @ColumnInfo(name = "landing_date")
    val landingDate: Date,

    @ColumnInfo(name = "destination")
    val destination: String,

    @ColumnInfo(name = "selected_attractions")
    val selectedAttractions: List<SelectedAttraction>,

    @ColumnInfo(name = "Hotels")
    val hotels: List<Hotels>,

    @ColumnInfo(name = "CheckList")
    val checklist: List<ChecklistItem>


)
data class Hotels(
    val name: String,
    val address: String,
    val CheckinDate: Date,
    val CheckoutDate: Date

)
data class ChecklistItem(
    val id: Long,
    val text: String,
    val isChecked: Boolean
)

data class SelectedAttraction(
    val title: String,
    val plannedDate: Date,
    val plannedTime: String
)