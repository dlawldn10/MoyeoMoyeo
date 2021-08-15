package com.project.moyeomoyeo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.moyeomoyeo.DataClass.ClubData
import com.project.moyeomoyeo.DataClass.UserData

class ManageMemberViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle,
userData: UserData, clubData: ClubData) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    var userData = userData
    var clubData = clubData

    private fun setBundle(fragment: Fragment): Fragment{
        val bundle = Bundle()
        bundle.putSerializable("userData",userData)
        bundle.putSerializable("clubData", clubData)
        fragment.arguments = bundle

        return fragment
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{

                setBundle(FragmentMemberAll())

            }
            1->{
                setBundle(FragmentMemberApply())
            }
            else->{
                Fragment()
            }
        }
    }



}