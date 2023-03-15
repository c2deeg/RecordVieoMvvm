package com.app.recordvideo.models

data class UploadFileResponse(
    val `data`: Data,
    val errors: Errors,
    val responseMessage: Any,
    val returnMessage: List<Any>,
    val returnStatus: Boolean,
    val statusCode: Int
)