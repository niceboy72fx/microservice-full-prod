package com.account.app.dto.response.account;

import java.util.List;

public record PagedAccountsResponse(int page, int size, long total, List<AccountDetailResponse> items) {}
