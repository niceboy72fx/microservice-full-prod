package com.account.app.persistence.repository.account;

import com.account.app.domain.model.account.AccountModel;
import com.account.app.persistence.entity.account.AccountEntity;
import com.account.app.persistence.mapper.account.AccountPersistenceMapper;
import com.account.app.repository.account.AccountRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAccountRepository implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AccountPersistenceMapper accountPersistenceMapper;

    public JdbcAccountRepository(JdbcTemplate jdbcTemplate, AccountPersistenceMapper accountPersistenceMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountPersistenceMapper = accountPersistenceMapper;
    }

    @Override
    public String create(String userId, String gender, String bankName, String accountNumber, String accountName) {
        String profileId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        jdbcTemplate.update("INSERT INTO user_profile (id, user_id, status, gender, create_at, update_at) VALUES (?, ?, 'ACTIVE', ?, SYSTIMESTAMP, SYSTIMESTAMP)", profileId, userId, gender);
        jdbcTemplate.update("INSERT INTO ekyc (user_id, status, selfie_url, create_at, update_at) VALUES (?, 'PENDING', NULL, SYSTIMESTAMP, SYSTIMESTAMP)", userId);
        jdbcTemplate.update("INSERT INTO bank_account (user_id, bank_name, account_number, account_name, create_at, update_at) VALUES (?, ?, ?, ?, SYSTIMESTAMP, SYSTIMESTAMP)", userId, bankName, accountNumber, accountName);
        return profileId;
    }

    @Override
    public boolean existsByUserId(String userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM user_profile WHERE user_id = ? AND status <> 'DELETED'", Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public Optional<AccountModel> findById(String id) {
        List<AccountModel> rows = jdbcTemplate.query(baseSelect() + " WHERE u.id = ? AND u.status <> 'DELETED'", (rs, rowNum) -> accountPersistenceMapper.toModel(new AccountEntity(rs.getString("id"), rs.getString("user_id"), rs.getString("gender"), rs.getString("status"), rs.getString("ekyc_status"), rs.getString("selfie_url"), rs.getString("bank_name"), rs.getString("account_number"), rs.getString("account_name"))), id);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    @Override
    public List<AccountModel> list(String keyword, String status, String ekycStatus, int page, int size) {
        StringBuilder sql = new StringBuilder(baseSelect()).append(" WHERE u.status <> 'DELETED'");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, ekycStatus);
        sql.append(" ORDER BY u.create_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * size);
        params.add(size);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> accountPersistenceMapper.toModel(new AccountEntity(rs.getString("id"), rs.getString("user_id"), rs.getString("gender"), rs.getString("status"), rs.getString("ekyc_status"), rs.getString("selfie_url"), rs.getString("bank_name"), rs.getString("account_number"), rs.getString("account_name"))), params.toArray());
    }

    @Override
    public long count(String keyword, String status, String ekycStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM user_profile u LEFT JOIN ekyc e ON e.user_id = u.user_id LEFT JOIN bank_account b ON b.user_id = u.user_id WHERE u.status <> 'DELETED'");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, status, ekycStatus);
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count == null ? 0 : count;
    }

    @Override
    public void softDelete(String id) {
        jdbcTemplate.update("UPDATE user_profile SET status = 'DELETED', update_at = SYSTIMESTAMP WHERE id = ? AND status <> 'DELETED'", id);
    }

    @Override
    public boolean updateEkyc(String userId, String status, String selfieUrl) {
        int rows = jdbcTemplate.update("UPDATE ekyc SET status = ?, selfie_url = ?, update_at = SYSTIMESTAMP WHERE user_id = ?", status, selfieUrl, userId);
        return rows > 0;
    }

    private void appendFilters(StringBuilder sql, List<Object> params, String keyword, String status, String ekycStatus) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(u.user_id) LIKE ? OR LOWER(b.account_name) LIKE ? OR LOWER(b.account_number) LIKE ?)");
            String value = "%" + keyword.trim().toLowerCase() + "%";
            params.add(value);
            params.add(value);
            params.add(value);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND u.status = ?");
            params.add(status.trim().toUpperCase());
        }
        if (ekycStatus != null && !ekycStatus.isBlank()) {
            sql.append(" AND e.status = ?");
            params.add(ekycStatus.trim().toUpperCase());
        }
    }

    private String baseSelect() {
        return "SELECT u.id, u.user_id, u.gender, u.status, NVL(e.status, 'PENDING') AS ekyc_status, e.selfie_url, b.bank_name, b.account_number, b.account_name FROM user_profile u LEFT JOIN ekyc e ON e.user_id = u.user_id LEFT JOIN bank_account b ON b.user_id = u.user_id";
    }
}
