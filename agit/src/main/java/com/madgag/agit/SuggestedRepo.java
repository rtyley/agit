package com.madgag.agit;


public class SuggestedRepo {
    private final String name;
    private final String uri;

    public SuggestedRepo(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public CharSequence getURI() {
        return uri;
    }

    public String getName() {
        return name;
    }
}
