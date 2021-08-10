package com.project.moyeomoyeo.DataClass

import java.sql.Timestamp

data class PostingPreviewData(val postIdx: Int,
                              val userIdx: Int,
                              val clubIdx: Int,
                              val content: String,
                              val createdAt: Timestamp)
