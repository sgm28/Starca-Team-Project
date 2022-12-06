package com.example.starca.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.starca.fragments.ConversationsFragment
import com.example.starca.fragments.ListingsChildFragment
import com.example.starca.fragments.RentedChildFragment

class ProfileViewPager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return RentedChildFragment()
            1 -> return ListingsChildFragment()
        }
        return RentedChildFragment()
    }
}