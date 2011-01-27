package com.madgag.gravatar.android;

import java.util.concurrent.ConcurrentMap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class ImageSession<K, ImageResourceType> {

	ImageProcessor<ImageResourceType> imageProcessor;
	ImageDownloader<K, ImageResourceType> downloader;
	ImageStore<K, ImageResourceType> imageResourceStore;
	ConcurrentMap<K, Drawable> memoryImageCache;

	ConcurrentMap<K, AsyncImageDownloaderTask> things = new MapMaker()
			.makeComputingMap(new Function<K, AsyncImageDownloaderTask>() {
				public AsyncImageDownloaderTask apply(K key) {
					AsyncImageDownloaderTask asyncImageDownloaderTask = new AsyncImageDownloaderTask(key);
					asyncImageDownloaderTask.execute();
					return asyncImageDownloaderTask;
				}
			});

	public Drawable get(K key) {
		Drawable memoryCachedDrawable = memoryImageCache.get(key);
		if (memoryCachedDrawable != null) {
			return memoryCachedDrawable;
		}

		ImageResourceType storedResource = imageResourceStore.get(key);
		if (storedResource != null) {
			return convertAndStore(key, storedResource);
		}

		AsyncLoadDrawable asyncLoadDrawable = new AsyncLoadDrawable();

		things.get(key).register(asyncLoadDrawable);
		return asyncLoadDrawable;
	}

	private Drawable convertAndStore(K key, ImageResourceType storedResource) {
		Drawable drawable = imageProcessor.convert(storedResource);
		memoryImageCache.put(key, drawable);
		return drawable;
	}
	
	class AsyncImageDownloaderTask extends AsyncTask<Void, Void, Drawable> {

		private final K key;
		private ConcurrentMap<AsyncLoadDrawable, Boolean> drawablesToUpdate = new MapMaker().weakKeys().makeMap();
		
		public AsyncImageDownloaderTask(K key) {
			this.key = key;
		}

		public void register(AsyncLoadDrawable asyncLoadDrawable) {
			drawablesToUpdate.put(asyncLoadDrawable, Boolean.TRUE);
		}

		@Override
		protected Drawable doInBackground(Void... params) {
			try {
				ImageResourceType imageResource = downloader.get(key);
				return convertAndStore(key, imageResource);
			} finally {
				things.remove(this);
			}
		}
		
		protected void onPostExecute(Drawable result) {
			for (AsyncLoadDrawable asyncLoadDrawable : drawablesToUpdate.keySet()) {
				asyncLoadDrawable.onLoad(result);
			}
		};
		
	}
	
}
