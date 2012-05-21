/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import static android.os.Looper.getMainLooper;
import static com.google.inject.assistedinject.FactoryProvider.newFactory;
import static com.google.inject.name.Names.named;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoPendingIntent;
import static java.lang.Thread.currentThread;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import com.madgag.agit.git.AgitTransportConfig;
import com.madgag.agit.git.model.RDTBranch;
import com.madgag.agit.git.model.RDTRemote;
import com.madgag.agit.git.model.RDTTag;
import com.madgag.agit.git.model.RepoDomainType;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.prompts.StatusBarPromptUI;
import com.madgag.agit.ssh.AndroidAuthAgentProvider;
import com.madgag.agit.ssh.AndroidSshSessionFactory;
import com.madgag.agit.ssh.CuriousHostKeyRepository;
import com.madgag.agit.ssh.jsch.GUIUserInfo;
import com.madgag.agit.sync.SyncCampaign;
import com.madgag.agit.sync.SyncCampaignFactory;
import com.madgag.android.blockingprompt.PromptUI;
import com.madgag.android.blockingprompt.PromptUIRegistry;
import com.madgag.android.lazydrawables.BitmapFileStore;
import com.madgag.android.lazydrawables.ImageProcessor;
import com.madgag.android.lazydrawables.ImageResourceDownloader;
import com.madgag.android.lazydrawables.ImageResourceStore;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.lazydrawables.ScaledBitmapDrawableGenerator;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshSessionFactory;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.inject.InjectExtra;

public class AgitModule extends AbstractAndroidModule {

    private static final String TAG = "AgitMod";

    @Override
    protected void configure() {
        install(RepositoryScope.module());
        install(OperationScope.module());
        bind(UserInfo.class).to(GUIUserInfo.class);
        bind(ImageSession.class).toProvider(ImageSessionProvider.class);

        bind(Repository.class).toProvider(RepositoryProvider.class);
        bind(Ref.class).annotatedWith(named("branch")).toProvider(BranchRefProvider.class);
        bind(AndroidAuthAgent.class).toProvider(AndroidAuthAgentProvider.class);
        bind(GitAsyncTaskFactory.class).toProvider(newFactory(GitAsyncTaskFactory.class, GitAsyncTask.class));

        bind(SyncCampaignFactory.class).toProvider(newFactory(SyncCampaignFactory.class, SyncCampaign.class));

        bind(TransportConfigCallback.class).to(AgitTransportConfig.class);
        bind(CredentialsProvider.class).to(GUICredentialsProvider.class);
        bind(SshSessionFactory.class).to(AndroidSshSessionFactory.class);
        bind(PromptUIRegistry.class);

        bind(HostKeyRepository.class).to(CuriousHostKeyRepository.class);
        bind(PromptUI.class).annotatedWith(named("status-bar")).to(StatusBarPromptUI.class);

        bind(RepoDomainType.class).annotatedWith(named("branch")).to(RDTBranch.class);
        bind(RepoDomainType.class).annotatedWith(named("remote")).to(RDTRemote.class);
        bind(RepoDomainType.class).annotatedWith(named("tag")).to(RDTTag.class);

        bind(CommitViewHolderFactory.class).toProvider(newFactory(CommitViewHolderFactory.class,
                CommitViewHolder.class));
        bind(BranchViewHolderFactory.class).toProvider(newFactory(BranchViewHolderFactory.class,
                BranchViewHolder.class));
    }

    @Provides
    @Singleton
    @Named("uiThread")
    Handler createHandler() {
        Looper mainLooper = getMainLooper();
        Log.d(TAG, "About to create handler, my thread is " + currentThread() + " - looper thread=" + mainLooper
                .getThread());
        return new Handler(mainLooper);
    }

    @Provides
    @RepositoryScoped
    PendingIntent createRepoManagementPendingIntent(Context context, @Named("gitdir") File gitdir) {
        return manageRepoPendingIntent(gitdir, context);
    }

    @Provides
    @RepositoryScoped
    Git gitApi(Repository repository) {
        return Git.wrap(repository);
    }

    @ContextScoped
    public static class BranchRefProvider implements Provider<Ref> {
        @Inject
        Repository repository;
        @InjectExtra(value = "branch", optional = true)
        String branchName;

        public Ref get() {
            try {
                if (branchName != null)
                    return repository.getRef(branchName);
            } catch (IOException e) {
                Log.e("BRP", "Couldn't get branch ref", e);
            }
            return null;
        }
    }

    @ContextScoped
    public static class ImageSessionProvider implements Provider<ImageSession<String, Bitmap>> {

        @Inject
        Resources resources;

        public ImageSession<String, Bitmap> get() {
            Log.i("BRP", "ImageSessionProvider INVOKED");
            ImageProcessor<Bitmap> imageProcessor = new ScaledBitmapDrawableGenerator(34, resources);
            ImageResourceDownloader<String, Bitmap> downloader = new GravatarBitmapDownloader();
            File file = new File(Environment.getExternalStorageDirectory(), "gravatars");
            ImageResourceStore<String, Bitmap> imageResourceStore = new BitmapFileStore<String>(file);
            return new ImageSession<String, Bitmap>(imageProcessor, downloader, imageResourceStore, resources.getDrawable(R.drawable.loading_34_centred));
        }

    }
}
