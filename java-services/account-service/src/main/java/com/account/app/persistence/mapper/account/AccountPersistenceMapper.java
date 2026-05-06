package com.account.app.persistence.mapper.account;

import com.account.app.domain.model.account.AccountModel;
import com.account.app.persistence.entity.account.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public AccountModel toModel(AccountEntity entity) {
        return new AccountModel(
                entity.id(), entity.userId(), entity.gender(), entity.status(), entity.ekycStatus(),
                entity.selfieUrl(), entity.bankName(), entity.accountNumber(), entity.accountName()
        );
    }
}
