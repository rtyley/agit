package com.madgag.agit.guice;

import com.google.common.collect.MapMaker;
import com.google.inject.*;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.blockingprompt.BlockingPromptService;
import com.madgag.agit.operations.OperationUIContext;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;


public class OperationScope extends ScopeBase implements Scope {

//    private final ProgressListener<Progress> progressListener;

    private final static Key<ProgressListener<Progress>> PROGRESS_LISTENER_KEY = Key.get(new TypeLiteral<ProgressListener<Progress>>() {});

    private final static Key<BlockingPromptService> BLOCKING_PROMPT_SERVICE_KEY = Key.get(BlockingPromptService.class);

	public static Module module() {
		return new AbstractModule() {
			public void configure() {
				OperationScope scope = new OperationScope();

				bindScope(OperationScoped.class, scope);

				bind(OperationScope.class).toInstance(scope);

                bind(PROGRESS_LISTENER_KEY).toProvider(ScopeBase.<ProgressListener<Progress>>seededKeyProvider()).in(OperationScoped.class);
                bind(BlockingPromptService.class).toProvider(ScopeBase.<BlockingPromptService>seededKeyProvider()).in(OperationScoped.class);
			}
		};
	}

    private final ThreadLocal<Map<Key<?>, Object>> threadLocalMap = new ThreadLocal<Map<Key<?>, Object>>();

	public void enterWithUIContext(OperationUIContext operationUIContext) {
		checkState(threadLocalMap.get() == null, "A scoping block is already in progress");
        Map<Key<?>, Object> map = new MapMaker().makeMap();
        threadLocalMap.set(map);
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