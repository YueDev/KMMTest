package http

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageBean(
    @SerialName("imageUrls")
    val imageUrls:List<String>
)
