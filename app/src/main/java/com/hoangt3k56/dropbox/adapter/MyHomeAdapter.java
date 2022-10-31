package com.hoangt3k56.dropbox.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class MyHomeAdapter extends FragmentStateAdapter {
    Fragment fragment1,fragment2,fragment3;
    public MyHomeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MyHomeAdapter(@NonNull FragmentActivity fragmentActivity, Fragment fragment1, Fragment fragment2, Fragment fragment3) {
        super(fragmentActivity);
        this.fragment1 = fragment1;
        this.fragment2 = fragment2;
        this.fragment3 = fragment3;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:return fragment1;

            case 1:return fragment2;

            case 2:return fragment3;

            default:return fragment1;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
