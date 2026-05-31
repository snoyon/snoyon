package com.bnpp.dsibcef.apim.alias.domain.service;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import com.bnpp.dsibcef.apim.alias.infrastructure.AliasRepositoryImpl;
import com.bnpp.dsibcef.apim.alias.infrastructure.AliasRepositoryImplTest;
import com.bnpp.dsibcef.apim.alias.infrastructure.DatabaseUtils;
import org.dalesbred.Database;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AliasServiceImplTest {
    private DataSource dataSource;
    private AliasFactory aliasFactory;

    private AliasRepository aliasRepository;

    private AliasService aliasService;

    @BeforeAll
    static public void init() {
        DatabaseUtils.createDatabaseSchema();
    }


    @AfterAll
    static public void clean() {
        DatabaseUtils.dropDatabaseSchema();
    }

    @BeforeEach
    private void beforeEach() {
        dataSource = JdbcConnectionPool.create("jdbc:h2:mem:alias", "alias_user", "password");
        aliasFactory=new AliasFactoryImpl();
        aliasRepository=new AliasRepositoryImpl(dataSource, aliasFactory);
        aliasService=new AliasServiceImpl(aliasRepository);
        DatabaseUtils.resetTableAlias(dataSource);
    }

    @Test
    void getOrCreateAliasFromPartnerAndUserId_WhenNotExists() {
        Alias alias=aliasService.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        assertEquals(36, alias.getAlias().length());
    }

    @Test
    void getOrCreateAliasFromPartnerAndUserId_WhenExists() {
        aliasService.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        Alias alias=aliasService.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        assertEquals(36, alias.getAlias().length());
    }


}
