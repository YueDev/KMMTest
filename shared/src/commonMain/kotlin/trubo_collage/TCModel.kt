package trubo_collage

data class TCBitmap(
    val uuid: String,
    val width: Int,
    val height: Int
)

data class TCRectF(
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float
)


class TCResult {
    private val out: MutableMap<String, TCRectF> = mutableMapOf()

    fun add(uuid: String, tcRectF: TCRectF) {
        out[uuid] = tcRectF
    }

    fun get(uuid: String): TCRectF? {
        return out[uuid]
    }

}


internal data class TCRect(
    var left: Double,
    var top: Double,
    var right: Double,
    var bottom: Double
) {
    fun getRectF() = TCRectF(
        left.toFloat(),
        top.toFloat(),
        (left + right).toFloat(),
        (top + bottom).toFloat()
    )
}

internal data class TCCollageItem(
    var uuid: String?,
    var ratioRect: TCRect
) {
    internal fun getRatioMaxBound(canvasWidth: Double, canvasHeight: Double) =
        canvasWidth * ratioRect.right.coerceAtLeast(ratioRect.bottom * canvasHeight)

    internal fun emptyUUID() = (uuid?.isEmpty() == true)
}


