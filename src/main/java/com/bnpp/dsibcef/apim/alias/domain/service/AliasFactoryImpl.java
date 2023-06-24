package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AliasFactoryImpl implements AliasFactory {

    @Override
    public Alias createAlias(String aliasId, String userId, String partnerId) {
        return new Alias(getNormalizedAliasId(aliasId), getNormalizedUserId(userId), getNormalizedPartnerId(partnerId));
    }
    @Override
    public Alias createAlias(String userId, String partnerId) {
        return new Alias(getNewAliasId(), getNormalizedUserId(userId), getNormalizedPartnerId(partnerId));
    }


    @Override
    public String getNewAliasId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public String getNormalizedUserId(String userId) {
        return userId.trim().toLowerCase().replaceAll("^0*", "");
    }

    @Override
    public String getNormalizedPartnerId(String partnerId) {
        return partnerId.trim().toLowerCase();
    }

    @Override
    public String getNormalizedAliasId(String aliasId) {
        return aliasId.trim().toLowerCase();
    }

}
