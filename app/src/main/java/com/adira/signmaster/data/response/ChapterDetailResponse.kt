package com.adira.signmaster.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ChapterDetailResponse(
    val error: Boolean,
    val message: String,
    val data: ChapterDetail
)

data class ChapterDetail(
    val chapterId: String,
    val chapterImageUrl: String,
    val materials: List<MaterialDetail>
)

@Parcelize
data class MaterialDetail(
    val id: Int,
    val title: String,
    val content: List<String>,
    val visual_content_url: String
): Parcelable