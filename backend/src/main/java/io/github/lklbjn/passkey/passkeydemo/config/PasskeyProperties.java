package io.github.lklbjn.passkey.passkeydemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author lklbjn
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "passkey")
public class PasskeyProperties {
    private String id;
    private String name;
    private Set<String> origins;
}