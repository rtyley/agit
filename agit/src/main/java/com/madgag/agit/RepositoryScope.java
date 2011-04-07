package com.madgag.agit;

import static com.google.common.base.Preconditions.checkState;
import static com.google.inject.name.Names.named;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.eclipse.jgit.lib.Repository;


public class RepositoryScope implements Scope {

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

    private final Map<File,Map<Key<?>, Object>> repoScopeMaps = new MapMaker().makeComputingMap(new Function<File,Map<Key<?>, Object>>() {
        public Map<Key<?>, Object> apply(File gitdir) {
            ConcurrentMap<Key<?>,Object> repoScopeMap = new MapMaker().makeMap();
            repoScopeMap.put(gitdirKey, gitdir);
            return repoScopeMap;
        }
    });

    public void doWith(Repository repository, Runnable runnable) {
        enterWithRepoGitdir(repository.getDirectory());
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

	public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
		return new Provider<T>() {
			public T get() {
				Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
                
				@SuppressWarnings("unchecked")
				T current = (T) scopedObjects.get(key);
				if (current == null && !scopedObjects.containsKey(key)) {
					current = unscoped.get();
					scopedObjects.put(key, current);
				}
				return current;
			}
		};
	}

	private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
		File gitdir = currentRepoGitdir.get();
		if (gitdir == null) {
			throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
		}
		return repoScopeMaps.get(gitdir);
	}

	/**
	 * Returns a provider that always throws exception complaining that the
	 * object in question must be seeded before it can be injected.
	 * 
	 * @return typed provider
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> Provider<T> seededKeyProvider() {
		return (Provider<T>) SEEDED_KEY_PROVIDER;
	}

    private static final Provider<Object> SEEDED_KEY_PROVIDER = new Provider<Object>() {
		public Object get() {
			throw new IllegalStateException(
					"If you got here then it means that"
							+ " your code asked for scoped object which should have been"
							+ " explicitly seeded in this scope by calling"
							+ " SimpleScope.seed(), but was not.");
		}
	};
}