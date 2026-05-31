package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AliasFactoryImplTest {
    private AliasFactoryImpl aliasFactory;
    @BeforeEach
    void init() {
        aliasFactory=new AliasFactoryImpl();
    }



    @Test
    void getNewAliasId() {
        String aliasId= aliasFactory.getNewAliasId();
        assertEquals(36, aliasId.length());
    }

    @Test
    void getNormalizedUserId() {
        String userId= aliasFactory.getNormalizedUserId("  0001234 ");
        assertEquals("1234", userId);
    }

    @Test
    void getNormalizedPartnerId() {
        String partnerId= aliasFactory.getNormalizedPartnerId("  0001234 ");
        assertEquals("0001234", partnerId);
    }

    @Test
    void createAlias() {
        Alias alias=aliasFactory.createAlias("  0001234 ", "  0001234 ");
        assertEquals("1234", alias.getUserId());
        assertEquals("0001234", alias.getPartnerId());
        assertEquals(36, alias.getAlias().length());
    }
}