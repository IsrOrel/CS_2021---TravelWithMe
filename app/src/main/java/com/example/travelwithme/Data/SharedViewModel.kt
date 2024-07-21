import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _cityName = MutableLiveData<String>()
    val cityName: LiveData<String> get() = _cityName

    fun setCityName(city: String) {
        _cityName.value = city
    }
}