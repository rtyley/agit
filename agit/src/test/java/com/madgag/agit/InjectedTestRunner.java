package com.madgag.agit;

import static roboguice.RoboGuice.getInjector;
import android.app.Application;

import com.madgag.agit.shadow.ShadowSherlockActivity;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.junit.runners.model.InitializationError;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.RoboInjector;

public class InjectedTestRunner extends RobolectricTestRunner {

    public InjectedTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        addClassOrPackageToInstrument("com.actionbarsherlock.app.SherlockActivity");
    }

    @Override
    public void prepareTest(Object test) {
        getInjector(new RoboActivity()).injectMembers(test);
    }

    @Override
    protected void bindShadowClasses() {
        super.bindShadowClasses();
        Robolectric.bindShadowClass(ShadowSherlockActivity.class);
    }
}
