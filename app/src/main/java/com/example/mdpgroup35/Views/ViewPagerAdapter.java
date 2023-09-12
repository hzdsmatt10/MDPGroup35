package com.example.mdpgroup35.Views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mdpgroup35.Fragments.FragmentControl;
import com.example.mdpgroup35.Fragments.FragmentMap;
import com.example.mdpgroup35.Fragments.FragmentMessage;
import com.example.mdpgroup35.Fragments.FragmentRecalibrate;
import com.example.mdpgroup35.Grid.GridMap;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new FragmentMessage();
            case 1:
                return new FragmentMap();
            case 2:
                return new FragmentControl();
            case 3:
                return new FragmentRecalibrate();
            default:
                return new FragmentMessage();
        }
    }

    @Override
    public int getItemCount() { return 4; }

}
