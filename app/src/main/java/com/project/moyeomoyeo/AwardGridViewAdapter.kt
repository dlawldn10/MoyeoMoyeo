package com.project.moyeomoyeo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.project.moyeomoyeo.DataClass.AwardData

class AwardGridViewAdapter(var context : Context?, var awardList : ArrayList<AwardData>) : BaseAdapter(){
    override fun getCount(): Int {
        return awardList.size
    }

    override fun getItem(p0: Int): Any {
        return awardList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view:View = View.inflate(context, R.layout.item_attend_award, null)

        var image : ImageView = view.findViewById(R.id.award_imageView)
        var name : TextView = view.findViewById(R.id.award_textView)

        var AwardItem : AwardData = awardList.get(p0)

        if(AwardItem.isActive){
            image.setImageResource(R.drawable.btn_modify)
        }
        else{
            image.setImageResource(R.drawable.btn_scrap)
        }

        name.text = AwardItem.name

        return view
    }
}