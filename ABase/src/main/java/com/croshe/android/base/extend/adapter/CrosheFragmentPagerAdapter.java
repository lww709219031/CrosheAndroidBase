package com.croshe.android.base.extend.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Janesen on 15/12/5.
 */
public class CrosheFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles=null;
    private List<Fragment> fragments;



    public CrosheFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.titles=titles;
        this.fragments=fragments;

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(titles!=null){
            return titles.get(position);
        }
        return super.getPageTitle(position);
    }
}
