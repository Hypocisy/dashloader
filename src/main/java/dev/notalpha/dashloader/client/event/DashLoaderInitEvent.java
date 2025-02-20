package dev.notalpha.dashloader.client.event;

import dev.notalpha.dashloader.api.DashEntrypoint;
import dev.notalpha.dashloader.api.cache.CacheFactory;
import net.minecraftforge.eventbus.api.Event;

public class DashLoaderInitEvent extends Event {
    private final CacheFactory cacheManagerFactory;

    public DashLoaderInitEvent(CacheFactory cacheManagerFactory) {
        this.cacheManagerFactory = cacheManagerFactory;
    }

    public CacheFactory getCacheManagerFactory() {
        return cacheManagerFactory;
    }
}