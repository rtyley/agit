/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.name.Names.named;

import com.google.inject.AbstractModule;
import com.google.inject.Key;

import org.junit.Test;

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
