package com.bnpp.dsibcef.apim.alias.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.bnpp.dsibcef.apim.alias.Main;
import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import com.bnpp.dsibcef.apim.alias.domain.service.AliasFactoryImpl;
import org.dalesbred.Database;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AliasRepositoryImplTest {
    private DataSource dataSource;
    private AliasFactoryImpl aliasFactory;

    private AliasRepositoryImpl aliasRepository;

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
        aliasRepository= Mockito.spy(new AliasRepositoryImpl(dataSource, aliasFactory));
        // aliasRepository= new AliasRepositoryImpl(dataSource, aliasFactory);
        DatabaseUtils.resetTableAlias(dataSource);
    }





    @Test
    void getOrCreateAliasFromPartnerAndUserId_WhenNotExists() {
        Alias alias=aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        assertEquals(36, alias.getAlias().length());
    }

    @Test
    void getOrCreateAliasFromPartnerAndUserId_WhenExists() {
        aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        Alias alias=aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        assertEquals(36, alias.getAlias().length());
    }

    @Test
    void getOrCreateAliasFromPartnerAndUserId_WhenExistsButNotDetected() throws SQLException {
        aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        doReturn(Optional.empty()).when(aliasRepository).findAliasFromPartnerAndUserIdBeforeCreate(any(),any(),any());

        Alias alias=aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234");
        assertEquals(36, alias.getAlias().length());
    }


    @Test
    void getAliasFromPartnerAndUserIdWhenExists_WhenAliasNotExists() {
        Optional<Alias> alias = aliasRepository.getAliasFromPartnerAndUserIdWhenExists("12345", "1234");
        assertTrue(alias.isEmpty(), "Alias found");
    }



    @Test
    void getAliasFromPartnerAndUserIdWhenExists_WhenAliasExists() {
        Alias alias=aliasFactory.createAlias("1234","12345");
        insertAliasInDatabase(alias);

        Optional<Alias> existingAlias = aliasRepository.getAliasFromPartnerAndUserIdWhenExists("12345", "1234");
        assertTrue(existingAlias.isPresent(), "Alias not found");


    }

    @Test
    void getAliasFromAliasId_WhenAliasNotExists() {
        Alias alias=aliasFactory.createAlias("1234","12345");
        Optional<Alias> emptyAlias=aliasRepository.getAliasFromAliasId(alias.getAlias());
        assertTrue(emptyAlias.isEmpty(), "Alias found");
    }

    @Test
    void getAliasFromAliasId_WhenAliasExists() {
        Alias alias=aliasFactory.createAlias("1234","12345");
        insertAliasInDatabase(alias);
        Optional<Alias> existingAlias=aliasRepository.getAliasFromAliasId(alias.getAlias());
        assertTrue(existingAlias.isPresent(), "Alias not found");
    }

    private void insertAliasInDatabase(Alias alias) {
        Database database=Database.forDataSource(dataSource);
        database.update("insert into alias_partner(alias,user_id,partner_id) values(?,?,?)",alias.getAlias(), alias.getUserId(), alias.getPartnerId());
    }
}