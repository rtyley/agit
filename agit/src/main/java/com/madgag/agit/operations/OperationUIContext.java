/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.operations;

import com.google.inject.Provider;
import com.madgag.android.blockingprompt.BlockingPromptService;

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
