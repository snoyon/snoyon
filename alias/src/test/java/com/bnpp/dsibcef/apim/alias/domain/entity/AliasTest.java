package com.bnpp.dsibcef.apim.alias.domain.entity;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AliasTest {
    @Test
    void testConstruction() {
        Alias alias=new Alias("123456","1234","12");
        assertEquals("123456", alias.getAlias());
        assertEquals("1234", alias.getUserId());
        assertEquals("12", alias.getPartnerId());
    }


}