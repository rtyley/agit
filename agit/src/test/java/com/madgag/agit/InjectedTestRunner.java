package com.madgag.agit;

import android.app.Application;
import com.google.inject.Injector;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import roboguice.RoboGuice;
import roboguice.inject.ContextScope;
import roboguice.inject.RoboInjector;

public class InjectedTestRunner extends RobolectricTestRunner {

    public InjectedTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override public void prepareTest(Object test) {
        Application application = Robolectric.application;

        //This project's application does not extend GuiceInjectableApplication therefore we need to enter the ContextScope manually.

        RoboInjector injector = RoboGuice.getInjector(application);
//        ContextScope scope = injector.getInstance(ContextScope.class);
//        scope.enter(application);
        injector.injectMembers(test);
//        scope.exit(application);
    }
}
