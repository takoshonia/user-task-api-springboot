package com.tamar.user_task_api.dto.response;

public record AppInfoResponse(
        String applicationTitle,
        int paginationLimit,
        String contactEmail
) {
}
