package com.hoangt3k56.dropbox.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.adapter.MyHomeAdapter;
import com.hoangt3k56.dropbox.fragment.HomeFragment;
import com.hoangt3k56.dropbox.fragment.SettingFragment;
import com.hoangt3k56.dropbox.fragment.SyncFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        token=getIntent().getStringExtra("TOKEN");

        viewPager2=findViewById(R.id.viewpager2);
        Bundle bundle=new Bundle();
        bundle.putString("TOKEN",token);

        SettingFragment settingFragment=new SettingFragment();
        settingFragment.setArguments(bundle);

        HomeFragment homeFragment=new HomeFragment();
        homeFragment.setArguments(bundle);

        SyncFragment syncFragment=new SyncFragment();

        MyHomeAdapter adapter=new MyHomeAdapter(this,homeFragment,syncFragment,settingFragment);
        viewPager2.setAdapter(adapter);

        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home : viewPager2.setCurrentItem(0); break;
                    case R.id.sync : viewPager2.setCurrentItem(1); break;
                    case R.id.setting: viewPager2.setCurrentItem(2); break;
                }

                return true;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0 : bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true); break;
                    case 1 : bottomNavigationView.getMenu().findItem(R.id.sync).setChecked(true); break;
                    case 2 : bottomNavigationView.getMenu().findItem(R.id.setting).setChecked(true);; break;
                }
                super.onPageSelected(position);
            }
        });

    }

}