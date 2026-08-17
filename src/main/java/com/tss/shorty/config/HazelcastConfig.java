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

        config.addMapConfig(urlCacheConfig);
        return config;
    }
}