package com.example.myapplication;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;
    public LoginAdapter(FragmentManager fm,Context context,int totalTab){
        super(fm);
        this.context=context;
        this.totalTabs=totalTab;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch(position){
            case 0:LoginTabFragement lg=new LoginTabFragement();
            return lg;

            case 1: SignupTabFragement s=  new SignupTabFragement ();
            return s;
            default:return  null;
        }

    }
}
