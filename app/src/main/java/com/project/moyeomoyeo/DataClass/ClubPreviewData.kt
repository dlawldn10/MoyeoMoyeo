package com.project.moyeomoyeo.DataClass

import android.provider.ContactsContract
import java.io.Serializable

data class ClubPreviewData(val clubIdx: Int,
                           val name: String?,
                           val description: String?,
                           val logoImage: String?,
                           val isLike:Int,
                           val nickname: String,
                           val profileImage: String,
                           val fieldIdx: Int) : Serializable{

}
