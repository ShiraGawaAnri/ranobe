package com.kd.novel.backend.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisClusterFactory {
    //自动注入redis配置属性文件
    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisCluster jedisCluster() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        config.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        config.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis());
        config.setTestOnBorrow(false);
        Set<HostAndPort> nodeSet = new HashSet<>();
        for (String node : properties.getCluster().getNodes()) {
            String[] split = node.split(":");
            nodeSet.add(new HostAndPort(split[0], Integer.valueOf(split[1])));
        }
        JedisCluster jedisCluster = new JedisCluster(nodeSet, 60000, 6000, 5, properties.getPassword(), config);
        return jedisCluster;
    }
}
