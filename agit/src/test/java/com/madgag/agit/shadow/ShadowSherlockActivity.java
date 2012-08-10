/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.shadow;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

/**
 * Copied from the Apache-2.0 licensed sample at https://github.com/passy/absshadow-sample
 */
@Implements(SherlockActivity.class)
public class ShadowSherlockActivity extends ShadowActivity {
	
	@Implementation
	public ActionBar getSupportActionBar() {
		return new ActionBar() {
			
			@Override
			public void show() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTitle(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setTitle(CharSequence arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSubtitle(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSubtitle(CharSequence arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSelectedNavigationItem(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setNavigationMode(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setLogo(Drawable arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setLogo(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setListNavigationCallbacks(SpinnerAdapter arg0,
					OnNavigationListener arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setIcon(Drawable arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setIcon(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayUseLogoEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayShowTitleEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayShowHomeEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayShowCustomEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayOptions(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayOptions(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisplayHomeAsUpEnabled(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCustomView(View arg0, LayoutParams arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCustomView(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCustomView(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBackgroundDrawable(Drawable arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void selectTab(Tab arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeTabAt(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeTab(Tab arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeOnMenuVisibilityListener(OnMenuVisibilityListener arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeAllTabs() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Tab newTab() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isShowing() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void hide() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public CharSequence getTitle() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getTabCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Tab getTabAt(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public CharSequence getSubtitle() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Tab getSelectedTab() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getSelectedNavigationIndex() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getNavigationMode() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getNavigationItemCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getHeight() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getDisplayOptions() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public View getCustomView() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addTab(Tab arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addTab(Tab arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addTab(Tab arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addTab(Tab arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addOnMenuVisibilityListener(OnMenuVisibilityListener arg0) {
				// TODO Auto-generated method stub
				
			}
		};
	}
		
	@Implementation
	public void setContentView(int layoutResId) {
		super.setContentView(layoutResId);
	}
}