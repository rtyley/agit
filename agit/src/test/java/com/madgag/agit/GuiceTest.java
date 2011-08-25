package com.madgag.agit;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import org.junit.Test;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.name.Names.named;

public class GuiceTest {

	@Test
	public void shouldBeCool() {
		createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(Integer.class).annotatedWith(named("three")).toInstance(3);
				bind(Integer.class).annotatedWith(named("five")).toInstance(5);
			}
		}).getInstance(Key.get(Integer.class, named("five")));
			//	Key<Integer>.get(Integer.class, named("five")));
	}
}
