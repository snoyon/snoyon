package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;

import java.util.Optional;

public interface AliasService {

    Optional<Alias> getAliasFromPartnerAndUserIdWhenExists(String partnerId, String userId);

    Alias getOrCreateAliasFromPartnerAndUserId(String partnerId, String userId);
    Optional<Alias> getAliasFromAliasId(String aliasId);
}
