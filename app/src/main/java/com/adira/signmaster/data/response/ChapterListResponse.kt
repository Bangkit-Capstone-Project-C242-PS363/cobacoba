package com.adira.signmaster.data.response

data class ChapterListResponse(
	val error: Boolean,
	val message: String,
	val data: List<Chapter>
)

data class Chapter(
	val id: Int,
	val title: String,
	val icon_url: String,
	val locked: Boolean
)




