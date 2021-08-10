package com.project.moyeomoyeo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.moyeomoyeo.DataClass.UserData

class ManageMemberViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle,
userData: UserData, clubIdx: Int) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    var userData = userData
    var clubIdx = clubIdx

    fun setBundle(fragment: Fragment): Fragment{
        val bundle = Bundle()
        bundle.putSerializable("userData",userData)
        bundle.putInt("clubIdx", clubIdx)
        fragment.setArguments(bundle)

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