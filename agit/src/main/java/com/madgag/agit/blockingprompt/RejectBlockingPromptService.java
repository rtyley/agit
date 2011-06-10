package com.madgag.agit.blockingprompt;


import android.util.Log;
import com.madgag.agit.operations.OpPrompt;

public class RejectBlockingPromptService implements BlockingPromptService {

    private static final String TAG = "RBPS";

    public <T> T request(OpPrompt<T> opPrompt) {
        Log.d(TAG, "Going to reject prompt " + opPrompt);
        throw new RuntimeException("Won't ask the user this: "+opPrompt.getOpNotification().getTickerText());
    }
}
