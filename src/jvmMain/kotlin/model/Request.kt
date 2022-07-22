package model

data class DioRequest(
    val data: Any?,
    val methed: String,
    val queryParams: Map<String, Any>,
    val url: String,
    val statusCode: Int,
    val body: Any,
    val headers: Map<String, Any>,
    val responseHeaders: Map<String, Any>,
    var timesatamp: Int,
    var projectName:String
)