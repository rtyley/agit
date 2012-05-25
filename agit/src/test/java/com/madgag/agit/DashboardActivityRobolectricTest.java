package com.madgag.agit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import android.widget.Button;

import com.google.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectedTestRunner.class)
public class DashboardActivityRobolectricTest {

    @Inject
    DashboardActivity dashboardActivity;

    @Test
    public void shouldBeCool() {
        dashboardActivity.onCreate(null);
    }
}
