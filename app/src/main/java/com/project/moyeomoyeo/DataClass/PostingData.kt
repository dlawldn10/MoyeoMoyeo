package com.project.moyeomoyeo.DataClass

import java.io.Serializable

//프사 추가해야함
data class PostingData(val postIdx: Int,
                              val userIdx: Int,
                              val clubIdx: Int,
                              val nickname: String,
                              val profileImage: String,
                              val content: String,
                              val createdAt: String,
                              val commentsCount: Int) : Serializable
