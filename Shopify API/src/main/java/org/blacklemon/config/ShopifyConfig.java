package org.blacklemon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShopifyConfig {

    @Value("${shopify.pim.storeUrl}")
    private String pimStoreUrl;

    @Value("${shopify.pim.accessToken}")
    private String pimAccessToken;

    @Value("${shopify.receiver.storeUrl}")
    private String receiverStoreUrl;

    @Value("${shopify.receiver.accessToken}")
    private String receiverAccessToken;

    public String getPimStoreUrl() {
        return pimStoreUrl;
    }

    public String getPimAccessToken() {
        return pimAccessToken;
    }

    public String getReceiverStoreUrl() {
        return receiverStoreUrl;
    }

    public String getReceiverAccessToken() {
        return receiverAccessToken;
    }
}