package com.bnpp.dsibcef.apim.alias.infrastructure;

import com.bnpp.dsibcef.apim.alias.domain.service.AliasFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import javax.sql.DataSource;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class AliasRepositoryImplDatabaseDownTest {
    @Mock
    private DataSource dataSource;
    private AliasFactoryImpl aliasFactory;

    private AliasRepositoryImpl aliasRepository;

    @BeforeEach
    private void beforeEach() throws SQLException {
        when(dataSource.getConnection()).thenThrow(SQLException.class);
        aliasFactory=new AliasFactoryImpl();
        aliasRepository= new AliasRepositoryImpl(dataSource, aliasFactory);
    }
    @Test
    void getAliasFromPartnerAndUserIdWhenExists(){
        assertThrows(DatabaseException.class, ()->aliasRepository.getAliasFromPartnerAndUserIdWhenExists("12345","1234"));
    }

    @Test
    void getOrCreateAliasFromPartnerAndUserId() {
        assertThrows(DatabaseException.class, ()->aliasRepository.getOrCreateAliasFromPartnerAndUserId("12345","1234"));
    }

    @Test
    void getAliasFromAliasId() {
        assertThrows(DatabaseException.class, ()->aliasRepository.getAliasFromAliasId("123"));
    }
}
