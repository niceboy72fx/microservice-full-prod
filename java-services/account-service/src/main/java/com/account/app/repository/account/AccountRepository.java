package com.account.app.repository.account;

import com.account.app.domain.model.account.AccountModel;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    String create(String userId, String gender, String bankName, String accountNumber, String accountName);
    boolean existsByUserId(String userId);
    Optional<AccountModel> findById(String id);
    List<AccountModel> list(String keyword, String status, String ekycStatus, int page, int size);
    long count(String keyword, String status, String ekycStatus);
    void softDelete(String id);
    boolean updateEkyc(String userId, String status, String selfieUrl);
}
