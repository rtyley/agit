package com.madgag.agit;

import static com.google.inject.assistedinject.FactoryProvider.newFactory;
import static com.google.inject.name.Names.named;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;

import java.io.File;
import java.io.IOException;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import com.google.inject.Provides;
import com.madgag.agit.blockingprompt.*;
import com.madgag.agit.guice.RepositoryScope;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.GitAsyncTaskFactory;
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
		install(RepositoryScope.module());
    	bind(ImageSession.class).toProvider(ImageSessionProvider.class);

    	bind(Repository.class).toProvider(RepositoryProvider.class);
        bind(Handler.class).toProvider(HandlerProvider.class).asEagerSingleton();
    	bind(Ref.class).annotatedWith(named("branch")).toProvider(BranchRefProvider.class);
    	bind(AndroidAuthAgent.class).toProvider(AndroidAuthAgentProvider.class);
    	bind(GitAsyncTaskFactory.class).toProvider(newFactory(GitAsyncTaskFactory.class, GitAsyncTask.class));
    	bind(SshSessionFactory.class).to(AndroidSshSessionFactory.class);
    	bind(TransportFactory.class);
    	bind(PromptHumper.class);
        bind(PromptUIProvider.class).annotatedWith(named("status-bar")).to(StatusBarPromptProvider.class);

        bind(RepoDomainType.class).annotatedWith(named("branch")).to(RDTBranch.class);
        bind(RepoDomainType.class).annotatedWith(named("remote")).to(RDTRemote.class);
        bind(RepoDomainType.class).annotatedWith(named("tag")).to(RDTTag.class);
    }


    public static class HandlerProvider implements Provider<Handler> {
        public Handler get() {
			return new Handler();
		}
    }

    @Provides @RepositoryScoped
    PendingIntent createRepoManagementPendingIntent(Context context, @Named("gitdir") File gitdir) {
        return manageRepoPendingIntent(gitdir, context);
    }

    @Provides @RepositoryScoped
    BlockingPromptService createBlockingPromptService(PromptHumper promptHumper) {
        return promptHumper.getBlockingPromptService();
    }

	@ContextScoped
    public static class BranchRefProvider implements Provider<Ref> {
		@Inject Repository repository;
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
