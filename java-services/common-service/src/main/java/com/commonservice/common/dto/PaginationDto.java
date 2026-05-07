package com.commonservice.common.dto;

public record PaginationDto(
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
