package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AliasServiceImpl implements AliasService {

    private AliasRepository aliasRepository;
    @Autowired
    public AliasServiceImpl(AliasRepository aliasRepository) {
        this.aliasRepository=aliasRepository;
    }
    @Override
    public Optional<Alias> getAliasFromPartnerAndUserIdWhenExists(String partnerId, String userId) {
        return aliasRepository.getAliasFromPartnerAndUserIdWhenExists(partnerId, userId);
    }

    @Override
    public Alias getOrCreateAliasFromPartnerAndUserId(String partnerId, String userId) {
        return aliasRepository.getOrCreateAliasFromPartnerAndUserId(partnerId, userId);
    }

    @Override
    public Optional<Alias> getAliasFromAliasId(String aliasId) {
        return aliasRepository.getAliasFromAliasId(aliasId);
    }
}
