package http

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ImageRepository {

    private val url = "https://gitee.com/qweszxc9160/resources/raw/master/imageUrls.json"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    @Throws(Exception::class)
    suspend fun getImageUrls(): List<String> {
        val imageBean = httpClient.get(url).body<ImageBean>()
        return imageBean.imageUrls.shuffled()
    }

}