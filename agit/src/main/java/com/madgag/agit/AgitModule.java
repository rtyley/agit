package com.madgag.agit;

import static com.google.inject.assistedinject.FactoryProvider.newFactory;
import static com.google.inject.name.Names.named;

import java.io.File;
import java.io.IOException;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.SshSessionFactory;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.inject.InjectExtra;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.ssh.AndroidAuthAgentProvider;
import com.madgag.agit.ssh.AndroidSshSessionFactory;
import com.madgag.android.lazydrawables.BitmapFileStore;
import com.madgag.android.lazydrawables.ImageProcessor;
import com.madgag.android.lazydrawables.ImageResourceDownloader;
import com.madgag.android.lazydrawables.ImageResourceStore;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.lazydrawables.ScaledBitmapDrawableGenerator;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class AgitModule extends AbstractAndroidModule {

	@Override
    protected void configure() {
		install(RepoOpScope.module());
		bind(File.class).annotatedWith(Names.named("gitdir")).toProvider(RepoGitDirProvider.class);
    	bind(ImageSession.class).toProvider(ImageSessionProvider.class);
    	bind(Repository.class).toProvider(RepositoryProvider.class);
    	bind(Ref.class).annotatedWith(named("branch")).toProvider(BranchRefProvider.class);
    	bind(AndroidAuthAgent.class).toProvider(AndroidAuthAgentProvider.class);
    	bind(GitAsyncTaskFactory.class).toProvider(newFactory(GitAsyncTaskFactory.class, GitAsyncTask.class));
    	bind(SshSessionFactory.class).to(AndroidSshSessionFactory.class);
    	bind(TransportFactory.class);
    	bind(BlockingPromptService.class).to(PromptHelper.class).in(RepoOpScoped.class);
    	bind(PromptHelper.class).in(RepoOpScoped.class);
    }
	
	@ContextScoped
    public static class BranchRefProvider implements Provider<Ref> {
		@Inject @Named("repository-from-context") Repository repository;
		@InjectExtra(value="branch",optional=true) String branchName;
		
		public Ref get() {
			try {
				if (branchName!=null)
					return repository.getRef(branchName);
			} catch (IOException e) {
				Log.e("BRP", "Couldn't get branch ref", e);
			} 
			return null;
		}
	}
	
	@ContextScoped
    public static class ImageSessionProvider implements Provider<ImageSession<String, Bitmap>> {

        @Inject Resources resources;

        public ImageSession<String, Bitmap> get() {
        	Log.i("BRP", "ImageSessionProvider INVOKED");
    		ImageProcessor<Bitmap> imageProcessor = new ScaledBitmapDrawableGenerator(34, resources);
    		ImageResourceDownloader<String, Bitmap> downloader = new GravatarBitmapDownloader();
    		File file = new File(Environment.getExternalStorageDirectory(),"boho");
    		ImageResourceStore<String, Bitmap> imageResourceStore = new BitmapFileStore<String>(file);
    		return new ImageSession<String, Bitmap>(imageProcessor, downloader, imageResourceStore, resources.getDrawable(R.drawable.loading_34_centred));
        }

	}
}
