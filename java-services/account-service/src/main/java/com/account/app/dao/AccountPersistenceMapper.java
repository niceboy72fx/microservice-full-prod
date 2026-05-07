package com.account.app.dao;

import com.account.app.bean.account.AccountModel;
import com.account.app.bean.account.AccountEntity;
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
