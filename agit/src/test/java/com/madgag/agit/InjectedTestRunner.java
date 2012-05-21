package com.madgag.agit;

import static roboguice.RoboGuice.getInjector;
import android.app.Application;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.junit.runners.model.InitializationError;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.RoboInjector;

public class InjectedTestRunner extends RobolectricTestRunner {

    public InjectedTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void prepareTest(Object test) {
        Application application = Robolectric.application;

        getInjector(new RoboActivity()).injectMembers(test);
    }
}
