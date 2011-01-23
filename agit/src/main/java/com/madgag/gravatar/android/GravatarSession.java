package com.madgag.gravatar.android;

import static android.graphics.Bitmap.createScaledBitmap;
import static com.madgag.agit.DigestUtils.md5Hex;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class GravatarSession {

	private final static ConcurrentMap<String,String> emailToGravatarIdCache 
		= new MapMaker().makeComputingMap(new Function<String, String>() {
			public String apply(String emailAddress) { return md5Hex(emailAddress.trim().toLowerCase()); }
		});
	
	
	private final ConcurrentMap<String, Drawable> gravatarIdToBitmapCache = new ConcurrentHashMap<String, Drawable>();
	private final GravatarProvider provider;
	private final Resources resources;
	private final int size;

	
	private final Drawable placeholderDrawable;
	private final static String TAG = "GS";


	
	public GravatarSession(GravatarProvider provider, Resources resources, int size) {
		this.provider = provider;
		this.resources = resources;
		this.size = size;
		placeholderDrawable=resources.getDrawable(android.R.drawable.stat_sys_download);
	}
    
    public Drawable getGravatar(String emailAddress) {
    	String gravitarId = emailToGravatarIdCache.get(emailAddress);
    	Drawable scaledGravatar = gravatarIdToBitmapCache.get(gravitarId);
    	if (scaledGravatar==null) {
    		scaledGravatar = scaleAndCache(gravitarId, provider.getGravatar(gravitarId, size));
    	}
    	return scaledGravatar;
    }

	public Drawable getGravatar(String emailAddress, final Runnable runnable) {
		final String gravitarId = emailToGravatarIdCache.get(emailAddress);
    	Drawable scaledGravatar = gravatarIdToBitmapCache.get(gravitarId);
    	if (scaledGravatar != null) {
    		return scaledGravatar;
    	}
    		
		Log.d(TAG,"Session cache MISS : "+emailAddress);
		Bitmap originalBitmap=provider.getGravatar(gravitarId, new GravatarLoadListener() {
			public void onSuccessfulLoad(Bitmap originalBitmap) {
				scaleAndCache(gravitarId, originalBitmap); // this will wipe out the null bitmap previously stored
				runnable.run();
			}
		});
		return scaleAndCache(gravitarId, originalBitmap); // don't ask to fetch this gravitar again during this session.
	}

	private Drawable scaleAndCache(String gravitarId, Bitmap originalBitmap) {
		Drawable scaledGravatar = (originalBitmap==null)?placeholderDrawable:new BitmapDrawable(resources,createScaledBitmap(originalBitmap, size, size, true));
		Log.d(TAG,"Session-caching gravitarId="+gravitarId+" scaledGravatar="+scaledGravatar);
		gravatarIdToBitmapCache.put(gravitarId, scaledGravatar);
		return scaledGravatar;
	}


}
