package eu.the42monkeys.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Resolution(
    var id: Int,
    var body: String,
    var time_limit: String,
    var commitment: String,
    var temper: String,
    var offer: Int,
    var created_at: String,
    var updated_at: String,
    var url: String
) {

    class Deserializer : ResponseDeserializable<Array<Resolution>> {
        override fun deserialize(content: String): Array<Resolution> =
            Gson().fromJson(content, Array<Resolution>::class.java)
    }
}