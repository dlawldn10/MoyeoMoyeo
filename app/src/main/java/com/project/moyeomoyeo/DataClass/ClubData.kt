package com.project.moyeomoyeo.DataClass

import java.io.Serializable

data class ClubData(val clubIdx: Int,
                    val sortIdx: Int,
                    val name: String,
                    val description: String,
                    val detailDescription:String,
                    val logoImage: String,
                    val clubImage:String,
                    val areaIdx:Int,
                    val fieldIdx:Int,
                    val userIdx: Int,
                    val memberCount: Int,
                    val isOrganizer:Int ) : Serializable{

//                         서버 -> val PeopleNum: Int가 빠져있음!
}
