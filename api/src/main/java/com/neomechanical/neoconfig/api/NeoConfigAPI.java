package com.neomechanical.neoconfig.api;

public class NeoConfigAPI {
    private static NeoConfigProvider provider;

    public static void setProvider(NeoConfigProvider neoConfigProvider) {
        provider = neoConfigProvider;
    }

    public static NeoConfigProvider getProvider() {
        return provider;
    }
}