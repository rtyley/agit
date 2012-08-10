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

package com.madgag.agit.guice;

import static com.google.common.base.Preconditions.checkState;
import static com.google.inject.name.Names.named;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.OutOfScopeException;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jgit.lib.Repository;


public class RepositoryScope extends ScopeBase {

    private final static Key<File> gitdirKey = Key.get(File.class, named("gitdir"));

    public static Module module() {
        return new AbstractModule() {
            public void configure() {
                RepositoryScope scope = new RepositoryScope();

                bindScope(RepositoryScoped.class, scope);

                bind(RepositoryScope.class).toInstance(scope);

                bind(gitdirKey).toProvider(RepositoryScope.<File>seededKeyProvider()).in(RepositoryScoped.class);
            }
        };
    }

    private final ThreadLocal<File> currentRepoGitdir = new ThreadLocal<File>();

    private final Map<File, Map<Key<?>, Object>> repoScopeMaps = new MapMaker().makeComputingMap(new Function<File,
            Map<Key<?>, Object>>() {
        public Map<Key<?>, Object> apply(File gitdir) {
            ConcurrentMap<Key<?>, Object> repoScopeMap = new MapMaker().makeMap();
            repoScopeMap.put(gitdirKey, gitdir);
            return repoScopeMap;
        }
    });

    public void doWith(Repository repository, Runnable runnable) {
        doWith(repository.getDirectory(), runnable);
    }

    public void doWith(File gitdir, Runnable runnable) {
        enterWithRepoGitdir(gitdir);
        try {
            runnable.run();
        } finally {
            exit();
        }
    }

    public void enterWithRepoGitdir(File gitdir) {
        checkState(currentRepoGitdir.get() == null, "A scoping block is already in progress");
        currentRepoGitdir.set(gitdir);
    }

    public void exit() {
        checkState(currentRepoGitdir.get() != null, "No scoping block in progress");
        currentRepoGitdir.remove();
    }

    @Override
    protected <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        File gitdir = currentRepoGitdir.get();
        if (gitdir == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return repoScopeMaps.get(gitdir);
    }
}