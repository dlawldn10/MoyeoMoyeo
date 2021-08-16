package com.project.moyeomoyeo.DataClass

import java.io.Serializable

//프사 추가해야함
class MemberData(val userIdx : Int,
                 val nickname: String,
                 val profileImage: String) : Serializable {
}