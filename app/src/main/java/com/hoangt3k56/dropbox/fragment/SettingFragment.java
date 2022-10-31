package com.hoangt3k56.dropbox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hoangt3k56.dropbox.listener.Listener;
import com.hoangt3k56.dropbox.R;

public class SettingFragment extends Fragment {

    String token;
    NavSettingFragment navSettingFragment;
    ProfileFragment profileFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting,container,false);
        token=getArguments().getString("TOKEN");
        navSettingFragment=new NavSettingFragment(new Listener() {
            @Override
            public void listener() {
                addFragment(profileFragment);
            }
        });
        profileFragment=new ProfileFragment(token,new Listener() {
            @Override
            public void listener() {
                removeFragment(profileFragment);
            }
        });
        addFragment(navSettingFragment);
        return view;
    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainContent, fragment,null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }



}