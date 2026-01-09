package me.verschuls.mkapi;

import lombok.Getter;

/**
 * Entry point for accessing the MoggedKits API.
 */
public class MoggedKitsAPI {

    private static MKAPIProvider api;

    /**
     * Gets the API provider instance.
     * @return the API provider
     * @throws IllegalStateException if MoggedKits is not loaded yet
     */
    public static MKAPIProvider getAPI() {
        if (api == null) throw new IllegalStateException("API not initialized");
        return api;
    }

    public static void set(MKAPIProvider api_) {
        if (MoggedKitsAPI.api != null) throw new IllegalStateException("API already set");
        api = api_;
    }
}
