package com.project.moyeomoyeo.DataClass

import java.io.Serializable

data class ClubPreviewData(val clubIdx: Int,
                           val name: String,
                           val description: String,
                           val logoImage: String,
                           val isLike:Int ) : Serializable{

}
