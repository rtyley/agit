package com.madgag.agit.blockingprompt;

import android.os.Handler;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.guice.RepositoryScoped;
import org.connectbot.service.PromptHelper;


/*
- ALL GATs for a single Repo should share the same BlockingPromptService, which blocks on showing a prompt until the current prompt has returned.
- When the handler receives a prompt request, it needs to know if an RMA is currently displaying. If it is, then it can poke the RMA to display, otherwise show the notification.
- When an RMA starts, it needs to be able to get a reference to the current data required by the BlockingPromptService, e.g. the ResponseInterface
- When an RMA ends, it needs to be able to tell someone that it's died. (because the handler thing needs to be able to route display requests properly)
*/
@RepositoryScoped
public class PromptHumper {
    private final PromptHelper promptHelper;
    private final PromptUIProvider statusBarUIProvider;
    private PromptUIProvider activityUIProvider;
    private String TAG = "PromptHumper";

    @Inject
    public PromptHumper(Handler uiThreadHandler, @Named("status-bar") PromptUIProvider statusBarUIProvider) {
        Log.d(TAG,"uiThreadHandler="+uiThreadHandler);
        this.statusBarUIProvider = statusBarUIProvider;
        promptHelper = new PromptHelper(uiThreadHandler, new Runnable() {
            public void run() {
                uiThreadBroadcastOfPrompt();
            }
        });
    }

    private void uiThreadBroadcastOfPrompt() {
        PromptUIProvider activeUI = activeUI();
        Log.d(TAG, "Broadcasting to activeUI="+activeUI);
        if (activeUI!=statusBarUIProvider) {
            statusBarUIProvider.clearPrompt();
        }
        activeUI.acceptPrompt(promptHelper);
    }

    private PromptUIProvider activeUI() {
        return activityUIProvider==null?statusBarUIProvider:activityUIProvider;
    }

    public void setActivityUIProvider(PromptUIProvider activityUIProvider) {
        this.activityUIProvider = activityUIProvider;
        displayPromptIfNecessary();
    }

    public void clearActivityUIProvider(PromptUIProvider providerToClear) {
        if (activityUIProvider==providerToClear) {
            activityUIProvider = null;
            displayPromptIfNecessary();
        }
    }

    private void displayPromptIfNecessary() {
        if (promptHelper.hasPrompt()) {
            uiThreadBroadcastOfPrompt();
        }
    }

    public BlockingPromptService getBlockingPromptService() {
        return promptHelper;
    }
}
