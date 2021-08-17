package com.project.moyeomoyeo.DataClass

import java.io.Serializable

class UserAttendData(val jwt : String, val clubIdx : Int, val userIdx : Int, val nickname : String, val profile: String, val isAttended:Int) : Serializable {
}