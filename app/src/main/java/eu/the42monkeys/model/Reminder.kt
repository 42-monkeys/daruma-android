package eu.the42monkeys.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Reminder(
    var id: Int,
    var body: String,
    var created_at: String,
    var updated_at: String,
    var url: String
) {

    class Deserializer : ResponseDeserializable<Array<Reminder>> {
        override fun deserialize(content: String): Array<Reminder> =
            Gson().fromJson(content, Array<Reminder>::class.java)
    }
}