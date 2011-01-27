package com.madgag.gravatar.android;


public interface ImageStore<K,ImageType> {
	
	boolean containsKey(Object key); 
    
	ImageType get(K key);
  
	ImageType put(K key, ImageType value);
	
}
