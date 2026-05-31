package com.bnpp.dsibcef.apim.alias.domain.entity;

public class Alias {
    private String alias;
    private String userId;
    private String partnerId;

    public Alias(String alias, String userId, String partnerId) {
        this.alias = alias;
        this.userId = userId;
        this.partnerId = partnerId;
    }

    public String getAlias() {
        return alias;
    }

    public String getUserId() {
        return userId;
    }

    public String getPartnerId() {
        return partnerId;
    }
}
