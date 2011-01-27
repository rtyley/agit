package com.madgag.gravatar.android;


public interface ImageDownloader<K,ImageType> {
	ImageType get(K key);
}
