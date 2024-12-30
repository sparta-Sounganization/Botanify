package com.sounganization.botanify.common.config.onesignal;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OneSignalClient {

    @Value("${onesignal.app-id}")
    private String appId;

    @Value("${onesignal.rest-api-key}")
    private String restApiKey;

    private final RestTemplate restTemplate;

    private static final String ONESIGNAL_API_URL = "https://onesignal.com/api/v1/notifications";

    public OneSignalClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(String title, String message, String userId, String platform, Map<String, Object> additionalData) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("app_id", appId);
        requestBody.put("headings", Collections.singletonMap("en", title));
        requestBody.put("contents", Collections.singletonMap("en", message));
        requestBody.put("include_external_user_ids", Collections.singletonList(userId));

        if ("mobile".equals(platform)) {
            requestBody.put("data", additionalData);
            requestBody.put("ios_badgeType", "Increase");
            requestBody.put("ios_badgeCount", 1);
            requestBody.put("android_channel_id", "plant-care-alerts");
        } else {
            requestBody.put("web_push_topic", "plant-care");
            requestBody.put("web_buttons", Collections.singletonList(
                    Map.of("id", "view-plant", "text", "식물 보기", "url", additionalData.get("redirectUrl"))
            ));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + restApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(ONESIGNAL_API_URL, request, String.class);
            log.info("OneSignal notification 전송 성공 - userId: {}, platform: {}", userId, platform);
        } catch (Exception e) {
            log.error("OneSignal notification 전송 실패 - userId: {}, platform: {}, error: {}",
                    userId, platform, e.getMessage());
            throw new CustomException(ExceptionStatus.NOTIFICATION_SEND_FAILED);
        }
    }
}
