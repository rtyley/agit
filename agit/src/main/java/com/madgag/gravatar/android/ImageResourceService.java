package com.madgag.gravatar.android;

public class ImageResourceService<K, ImageResourceType> {
	ImageDownloader<K, ImageResourceType> downloader;
	ImageStore<K, ImageResourceType> diskImageStore;
}
