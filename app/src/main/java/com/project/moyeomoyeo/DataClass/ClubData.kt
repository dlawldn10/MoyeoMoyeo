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
                    val isLike: Int,
                    val isMember: Int,
                    val isOrganizer:Int) : Serializable{

                    }
