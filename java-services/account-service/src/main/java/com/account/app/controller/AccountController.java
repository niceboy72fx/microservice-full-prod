package com.account.app.controller;

import com.account.app.command.account.command.CreateAccountCommand;
import com.account.app.command.account.command.EkycAccountCommand;
import com.account.app.command.account.command.GetAccountDetailCommand;
import com.account.app.command.account.command.ListAccountsCommand;
import com.account.app.command.account.command.SoftDeleteAccountCommand;
import com.account.app.command.account.mapper.AccountCommandMapper;
import com.account.app.command.CommandBus;
import com.account.app.common.response.ApiResponse;
import com.account.app.dto.request.account.CreateAccountRequest;
import com.account.app.dto.request.account.EkycRequest;
import com.account.app.dto.response.account.AccountDetailResponse;
import com.account.app.dto.response.account.PagedAccountsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final CommandBus commandBus;
    private final AccountCommandMapper accountCommandMapper;

    public AccountController(CommandBus commandBus, AccountCommandMapper accountCommandMapper) {
        this.commandBus = commandBus;
        this.accountCommandMapper = accountCommandMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountDetailResponse>> create(@RequestBody CreateAccountRequest request) {
        CreateAccountCommand command = accountCommandMapper.toCreateCommand(request);
        AccountDetailResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> detail(@PathVariable String accountId) {
        GetAccountDetailCommand command = accountCommandMapper.toDetailCommand(accountId);
        AccountDetailResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedAccountsResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ekycStatus,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ListAccountsCommand command = accountCommandMapper.toListCommand(keyword, status, ekycStatus, page, size);
        PagedAccountsResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> softDelete(@PathVariable String accountId) {
        SoftDeleteAccountCommand command = accountCommandMapper.toSoftDeleteCommand(accountId);
        commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{accountId}/ekyc")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> ekyc(@PathVariable String accountId, @RequestBody EkycRequest request) {
        EkycAccountCommand command = accountCommandMapper.toEkycCommand(accountId, request);
        AccountDetailResponse response = commandBus.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
