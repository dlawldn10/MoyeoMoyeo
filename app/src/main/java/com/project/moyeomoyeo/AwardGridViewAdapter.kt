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

        var badgeList = ArrayList<Int>()

        badgeList.add(R.drawable.img_badge01)
        badgeList.add(R.drawable.img_badge02)
        badgeList.add(R.drawable.img_badge03)
        badgeList.add(R.drawable.img_badge04)
        badgeList.add(R.drawable.img_badge05)
        badgeList.add(R.drawable.img_badge06)
        badgeList.add(R.drawable.img_badge07)
        badgeList.add(R.drawable.img_badge08)
        badgeList.add(R.drawable.img_badge09)
        badgeList.add(R.drawable.img_badge10)
        badgeList.add(R.drawable.img_badge11)
        badgeList.add(R.drawable.img_badge12)

        var badgeList_dec = ArrayList<Int>()

        badgeList_dec.add(R.drawable.img_badge01_dec)
        badgeList_dec.add(R.drawable.img_badge02_dec)
        badgeList_dec.add(R.drawable.img_badge03_dec)
        badgeList_dec.add(R.drawable.img_badge04_dec)
        badgeList_dec.add(R.drawable.img_badge05_dec)
        badgeList_dec.add(R.drawable.img_badge06_dec)
        badgeList_dec.add(R.drawable.img_badge07_dec)
        badgeList_dec.add(R.drawable.img_badge08_dec)
        badgeList_dec.add(R.drawable.img_badge09_dec)
        badgeList_dec.add(R.drawable.img_badge10_dec)
        badgeList_dec.add(R.drawable.img_badge11_dec)
        badgeList_dec.add(R.drawable.img_badge12_dec)


        if(AwardItem.isActive){
            image.setImageResource(badgeList[p0])
        }
        else{
            image.setImageResource(badgeList_dec[p0])
        }

        name.text = AwardItem.name

        return view
    }
}