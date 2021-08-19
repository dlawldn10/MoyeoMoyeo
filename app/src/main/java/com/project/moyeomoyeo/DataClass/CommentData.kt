package com.project.moyeomoyeo.DataClass


//프사 추가해야함
data class CommentData(val commentIdx: Int,
                       val content: String,
                       val userIdx: Int,
                       val nickname: String,
                       val profileImage: String,
                       val postIdx: Int,
                       val parentCommentIdx: Int,
                       val seq: Int,
                       val createdAt: String,
                       val status: Int)