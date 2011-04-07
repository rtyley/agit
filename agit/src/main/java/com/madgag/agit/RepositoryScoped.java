package com.madgag.agit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

@Target({ TYPE, METHOD }) @Retention(RUNTIME) @ScopeAnnotation
public @interface RepositoryScoped {}
