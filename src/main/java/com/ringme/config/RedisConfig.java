package com.ringme.config;

import io.lettuce.core.resource.ClientResources;
import lombok.Setter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "natcom")
@Setter
public class RedisConfig {
    private RedisProperties redis;
    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("connectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean(name = "connectionFactory")
    @Primary
    public LettuceConnectionFactory connectionFactory(ClientResources clientResources) {
        RedisClusterConfiguration redisClusterConfiguration = getClusterConfig(redis);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(clientResources, redis);
        return new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
    }
    /**
     * redis cluster config
     */
    private RedisClusterConfiguration getClusterConfig(RedisProperties redisProperties) {
        RedisClusterConfiguration config = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
//        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
        return config;
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ClientResources clientResources,
                                                                     RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
                createBuilder(redisProperties.getLettuce().getPool());
        if (redisProperties.getSsl().isEnabled()) {
            builder.useSsl();
        }
        if (redisProperties.getTimeout() != null) {
            builder.commandTimeout(redisProperties.getTimeout());
        }
        if (redisProperties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = redisProperties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(redisProperties.getLettuce().getShutdownTimeout());
            }
        }
        builder.clientResources(clientResources);
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(getPoolConfig(pool));
    }

    private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }

    @Bean(name = "rc")
    public RedisCacheManager cacheRedisManager(@Autowired @Qualifier("connectionFactory") LettuceConnectionFactory connectionFactory) {
        return RedisCacheManager
                .builder(connectionFactory)
                .withCacheConfiguration("rc10m",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                                .entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration("rc60m",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                                .entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration("rc24h",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                                .entryTtl(Duration.ofHours(24)))
                .build();
    }
}
