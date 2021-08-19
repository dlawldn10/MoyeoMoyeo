package com.project.moyeomoyeo.DataClass

import java.io.Serializable

class MyData(
    val userIdx: Int,
    var nickname: String,
    var name : String,
    var phoneNumber: String,
    var profileImage: String,
    var likeSortIdx: Int
    ) : Serializable {
}