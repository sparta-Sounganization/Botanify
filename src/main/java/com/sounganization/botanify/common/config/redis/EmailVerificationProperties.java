package com.sounganization.botanify.common.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailVerificationProperties {
    private final long verificationTtl;
    private final int maxAttempts;
    private final long attemptsTtl;
}
