package com.statusappbysyahrul.app.data.response

import com.google.gson.annotations.SerializedName

data class SuccessResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
