import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactStorage(private val context: Context) {
    private val preferences = context.getSharedPreferences("contacts", Context.MODE_PRIVATE)

    fun saveContacts(contacts: List<Contact>) {
        val editor = preferences.edit()
        editor.putString("contacts", Gson().toJson(contacts))
        editor.apply()
    }

    fun getContacts(): List<Contact> {
        val contactsJson = preferences.getString("contacts", "[]")
        return Gson().fromJson(contactsJson, object : TypeToken<List<Contact>>() {}.type)
    }
}