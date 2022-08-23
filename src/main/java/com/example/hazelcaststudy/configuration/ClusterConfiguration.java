package com.example.hazelcaststudy.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "core.cluster")
public class ClusterConfiguration {

    private String apiUrlPrefix;

    private String imgUrlPrefix;

    private String frontUrlPrefix;

    public String getApiUrlPrefix() {
        return apiUrlPrefix;
    }

    public String getImgUrlPrefix() {
        return imgUrlPrefix;
    }

    public String getFrontUrlPrefix() {
        return frontUrlPrefix;
    }

    public void setApiUrlPrefix(String apiUrlPrefix) {
        this.apiUrlPrefix = apiUrlPrefix;
    }

    public void setImgUrlPrefix(String imgUrlPrefix) {
        this.imgUrlPrefix = imgUrlPrefix;
    }

    public void setFrontUrlPrefix(String frontUrlPrefix) {
        this.frontUrlPrefix = frontUrlPrefix;
    }
}
