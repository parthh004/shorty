package com.tss.shorty.config;

import com.hazelcast.config.*;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfig {
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("shorty-hazelcast-cluster");

        MapConfig urlCacheConfig = new MapConfig("urls");
        urlCacheConfig.setTimeToLiveSeconds(3600);

        urlCacheConfig.setEvictionConfig(new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
        );

        MapConfig pricingCacheConfig = new MapConfig("pricing");
        pricingCacheConfig.setTimeToLiveSeconds(86400); // Keep pricing in RAM for 24 hours

        EvictionConfig evictionConfig = new EvictionConfig();
        evictionConfig.setMaxSizePolicy(MaxSizePolicy.PER_NODE);
        evictionConfig.setSize(100);
        pricingCacheConfig.setEvictionConfig(evictionConfig);

        config.addMapConfig(pricingCacheConfig);

        config.addMapConfig(urlCacheConfig);
        return config;
    }
}