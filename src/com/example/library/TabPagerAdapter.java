package com.example.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.cicp_application.collect_stamp;
import com.example.cicp_application.list_collection;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
	
	public TabPagerAdapter(FragmentManager fm){
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		// TODO Auto-generated method stub
		switch (i){
		case 0:
			return new collect_stamp();
		case 1:
			return new list_collection();
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	

}
