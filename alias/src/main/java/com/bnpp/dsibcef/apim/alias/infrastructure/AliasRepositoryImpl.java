package com.bnpp.dsibcef.apim.alias.infrastructure;

import com.bnpp.dsibcef.apim.alias.domain.entity.Alias;
import com.bnpp.dsibcef.apim.alias.domain.service.AliasFactory;
import com.bnpp.dsibcef.apim.alias.domain.service.AliasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
@Repository
public class AliasRepositoryImpl implements AliasRepository {
    private DataSource dataSource;
    private AliasFactory aliasFactory;
    private String INSERT_ALIAS = "insert into alias_partner(alias,user_id,partner_id) values(?,?,?)";
    private String SELECT_ALIAS_FROM_PARTNER_ID_AND_USER_ID = "select alias,user_id,partner_id from alias_partner where partner_id=? and user_id=?";

    private String SELECT_ALIAS_ALIAS_ID = "select alias,user_id,partner_id from alias_partner where alias=?";
    @Autowired
    public AliasRepositoryImpl(DataSource dataSource, AliasFactory aliasFactory) {
        this.dataSource = dataSource;
        this.aliasFactory = aliasFactory;
    }


    @Override
    public Optional<Alias> getAliasFromPartnerAndUserIdWhenExists(String partnerId, String userId) {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            return findAliasFromPartnerAndUserId(partnerId, userId, conn);
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }
    }

    @Override
    public Alias getOrCreateAliasFromPartnerAndUserId(String partnerId, String userId) {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            Optional<Alias> alias = findAliasFromPartnerAndUserIdBeforeCreate(partnerId, userId, conn);
            if (alias.isPresent()) {
                return alias.get();
            } else {
                return createAliasWithDuplicationChecking(partnerId, userId, conn);
            }
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }
    }

    @Override
    public Optional<Alias> getAliasFromAliasId(String aliasId) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(SELECT_ALIAS_ALIAS_ID);
        ) {
            ps.setString(1, aliasFactory.getNormalizedAliasId(aliasId));
            try (ResultSet rs = ps.executeQuery()) {
                return getAliasFromResultSet(rs);
            }
        } catch (SQLException se) {
            throw new DatabaseException(se);
        }

    }

    protected Optional<Alias> findAliasFromPartnerAndUserIdBeforeCreate(String partnerId, String userId, Connection conn) throws SQLException {
        return findAliasFromPartnerAndUserId(partnerId, userId, conn);
    }

    private Optional<Alias> findAliasFromPartnerAndUserId(String partnerId, String userId, Connection conn) throws SQLException {
        try (
                PreparedStatement ps = conn.prepareStatement(SELECT_ALIAS_FROM_PARTNER_ID_AND_USER_ID);
        ) {
            ps.setString(1, aliasFactory.getNormalizedPartnerId(partnerId));
            ps.setString(2, aliasFactory.getNormalizedUserId(userId));
            try (ResultSet rs = ps.executeQuery()) {
                return getAliasFromResultSet(rs);
            }
        }
    }

    private Alias createAliasWithDuplicationChecking(String partnerId, String userId, Connection conn) throws SQLException {
        try {
            return createAliasFromPartnerAndUserId(partnerId, userId, conn);
        } catch (SQLIntegrityConstraintViolationException se) {
            return findAliasFromPartnerAndUserIdWhenDuplicationDetected(partnerId, userId, conn);
        }
    }

    private Alias findAliasFromPartnerAndUserIdWhenDuplicationDetected(String partnerId, String userId, Connection conn) throws SQLException {
        Optional<Alias> alias = findAliasFromPartnerAndUserId(partnerId, userId, conn);
        if (alias.isPresent())
            return alias.get();
        else
            throw new RuntimeException("No alias found while duplication detected !");
    }






    private Alias createAliasFromPartnerAndUserId(String partnerId, String userId, Connection conn) throws SQLException {
        try (
                PreparedStatement ps = conn.prepareStatement(INSERT_ALIAS);
        ) {
            Alias alias = aliasFactory.createAlias(userId, partnerId);
            ps.setString(1, alias.getAlias());
            ps.setString(2, alias.getUserId());
            ps.setString(3, alias.getPartnerId());
            ps.executeUpdate();
            return alias;
        }
    }




    private Optional<Alias> getAliasFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Alias alias = aliasFactory.createAlias(rs.getString(1), rs.getString(2), rs.getString(3));
            return Optional.of(alias);
        } else {
            return Optional.empty();
        }
    }
}
