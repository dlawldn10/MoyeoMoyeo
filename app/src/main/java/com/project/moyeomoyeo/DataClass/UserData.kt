package com.project.moyeomoyeo.DataClass

import java.io.Serializable

class UserData(val jwt: String, val userIdx : Int, val member: String) : Serializable {
}