package com.example.starca.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.starca.fragments.ConversationsFragment
import com.example.starca.fragments.ListingsChildFragment

class ProfileViewPager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    //TODO: Create the rented chil fragment and replace with convo fragment here
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ListingsChildFragment()
            1 -> return ConversationsFragment()
        }
        return ListingsChildFragment()
    }
}