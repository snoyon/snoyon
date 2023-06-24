package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;

public interface AliasFactory {
    Alias createAlias(String aliasId, String userId, String partnerId);

    Alias createAlias(String userId, String partnerId);

    String getNewAliasId();

    String getNormalizedUserId(String userId);

    String getNormalizedPartnerId(String partnerId);

    String getNormalizedAliasId(String aliasId);
}
