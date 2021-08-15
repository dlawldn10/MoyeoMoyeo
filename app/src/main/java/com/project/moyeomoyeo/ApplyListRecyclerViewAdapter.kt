package com.project.moyeomoyeo

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.moyeomoyeo.DataClass.ApplyData
import com.project.moyeomoyeo.DataClass.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ApplyListRecyclerViewAdapter(private val items : ArrayList<ApplyData>,
                                   val userData:UserData, val rootView: View, val clubIdx: Int)
    : RecyclerView.Adapter<ApplyListRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myGroup = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_apply_member, parent, false)

        return ViewHolder(myGroup)
    }

    var Position = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var Photo = holder.PreviewList.findViewById<ImageView>(R.id.apply_photo_imageView)
        var NickName = holder.PreviewList.findViewById<TextView>(R.id.apply_nickName_TextView)
        var Motive = holder.PreviewList.findViewById<TextView>(R.id.apply_motive_TextView)
        var AcceptBtn = holder.PreviewList.findViewById<ImageButton>(R.id.applyAccept_imageButton)
        var DeleteBtn = holder.PreviewList.findViewById<ImageButton>(R.id.applyDelete_imageButton)

        //프사 uri or url 넣기
        //GroupPhoto <- items[position].ProfilePhoto
        NickName.text = items[position].nickname
        Motive.text = items[position].motive

        AcceptBtn.setOnClickListener {
            FragmentMemberApply().BuildAcceptDialog(NickName.text as String, items[position], rootView, userData, clubIdx)
        }

        DeleteBtn.setOnClickListener {
            FragmentMemberApply().BuildDeleteDialog(NickName.text as String, items[position], rootView, userData, clubIdx)
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(val PreviewList: View) : RecyclerView.ViewHolder(PreviewList)




}