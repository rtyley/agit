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

package com.madgag.agit.guice;

import com.google.common.collect.MapMaker;
import com.google.inject.*;
import com.madgag.agit.operations.*;
import com.madgag.agit.operations.Progress;
import com.madgag.android.blockingprompt.BlockingPromptService;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;


public class OperationScope extends ScopeBase implements Scope {

//    private final ProgressListener<Progress> progressListener;

    private final static Key<GitOperation> GIT_OPERATION_KEY = Key.get(GitOperation.class);

    private final static Key<ProgressListener<Progress>> PROGRESS_LISTENER_KEY = Key.get(new TypeLiteral<ProgressListener<Progress>>() {});

    private final static Key<BlockingPromptService> BLOCKING_PROMPT_SERVICE_KEY = Key.get(BlockingPromptService.class);

	public static Module module() {
		return new AbstractModule() {
			public void configure() {
				OperationScope scope = new OperationScope();

				bindScope(OperationScoped.class, scope);

				bind(OperationScope.class).toInstance(scope);

                bind(GIT_OPERATION_KEY).toProvider(ScopeBase.<GitOperation>seededKeyProvider()).in(OperationScoped.class);
                bind(PROGRESS_LISTENER_KEY).toProvider(ScopeBase.<ProgressListener<Progress>>seededKeyProvider()).in(OperationScoped.class);
                // bind(BLOCKING_PROMPT_SERVICE_KEY).toProvider(ScopeBase.<BlockingPromptService>seededKeyProvider()).in(OperationScoped.class);

                bind(CancellationSignaller.class).to(GitOperation.class);
			}
		};
	}

    private final ThreadLocal<Map<Key<?>, Object>> threadLocalMap = new ThreadLocal<Map<Key<?>, Object>>();

	public void enterWithUIContext(GitOperation gitOperation, OperationUIContext operationUIContext) {
		checkState(threadLocalMap.get() == null, "A scoping block is already in progress");
        Map<Key<?>, Object> map = new MapMaker().makeMap();
        threadLocalMap.set(map);
        map.put(GIT_OPERATION_KEY, gitOperation);
        map.put(BLOCKING_PROMPT_SERVICE_KEY,operationUIContext.getBlockingPromptServiceProvider());
        map.put(PROGRESS_LISTENER_KEY,operationUIContext.getProgressListener());
	}

	public void exit() {
		checkState(threadLocalMap.get() != null, "No scoping block in progress");
		threadLocalMap.remove();
	}

    @Override
	protected <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
		Map<Key<?>, Object> map = threadLocalMap.get();
		if (map == null) {
			throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
		}
		return map;
	}

}