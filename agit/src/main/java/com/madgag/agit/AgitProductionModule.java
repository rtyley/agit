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

import android.app.PendingIntent;
import android.content.ComponentName;
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
import com.madgag.agit.blockingprompt.*;
import com.madgag.agit.guice.RepositoryScope;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.ssh.AndroidAuthAgentProvider;
import com.madgag.agit.ssh.AndroidSshSessionFactory;
import com.madgag.agit.ssh.CuriousHostKeyRepository;
import com.madgag.agit.ssh.jsch.GUIUserInfo;
import com.madgag.android.lazydrawables.*;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.SshSessionFactory;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ContextScoped;
import roboguice.inject.InjectExtra;

import java.io.File;
import java.io.IOException;

import static com.google.inject.assistedinject.FactoryProvider.newFactory;
import static com.google.inject.name.Names.named;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;
import static java.lang.Thread.currentThread;

public class AgitProductionModule extends AbstractAndroidModule {

	@Override
    protected void configure() {
		install(GUIUserInfo.module());
    }

    @Provides @Named("authAgent")
    ComponentName authAgentComponentName() {
        return null;
    }
}
