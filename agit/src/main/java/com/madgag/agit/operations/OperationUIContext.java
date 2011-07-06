package com.madgag.agit.operations;

import com.google.inject.Provider;
import com.madgag.agit.blockingprompt.BlockingPromptService;

public class OperationUIContext {

    private final ProgressListener<Progress> progressListener;
    private final Provider<? extends BlockingPromptService> blockingPromptServiceProvider;

    public OperationUIContext(
            ProgressListener<Progress> progressListener,
            Provider<? extends BlockingPromptService> blockingPromptServiceProvider
    ) {
        this.progressListener = progressListener;
        this.blockingPromptServiceProvider = blockingPromptServiceProvider;
    }


    public BlockingPromptService getBlockingPromptServiceProvider() {
        return blockingPromptServiceProvider.get();
    }

    public ProgressListener<Progress> getProgressListener() {
        return progressListener;
    }
}
