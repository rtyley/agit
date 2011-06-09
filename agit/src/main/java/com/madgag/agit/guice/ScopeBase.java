package com.madgag.agit.guice;

import com.google.inject.Provider;

/**
 * Created by IntelliJ IDEA.
 * User: roberto
 * Date: 09/06/11
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class ScopeBase {
    private static final Provider<Object> SEEDED_KEY_PROVIDER = new Provider<Object>() {
        public Object get() {
            throw new IllegalStateException(
                    "If you got here then it means that"
                            + " your code asked for scoped object which should have been"
                            + " explicitly seeded in this scope by calling"
                            + " SimpleScope.seed(), but was not.");
        }
    };

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
}
