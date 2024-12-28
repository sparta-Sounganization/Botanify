package com.sounganization.botanify.domain.chat.components;

import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import com.sounganization.botanify.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatDataCleanupScheduler {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Scheduled(cron = ChatMessageConstants.CLEANUP_CRON)
    @Transactional
    public void cleanupOldMessages() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now()
                    .minusDays(ChatMessageConstants.DEFAULT_RETENTION_DAYS);

            int totalDeleted = 0;
            int batchCount;

            do {
                batchCount = chatMessageRepository
                        .softDeleteMessagesOlderThan(cutoffDate, ChatMessageConstants.BATCH_SIZE);
                totalDeleted += batchCount;

                log.info("채팅 메시지 정리 진행 중: {} 건 처리됨", totalDeleted);
            } while (batchCount > 0);

            // 빈 채팅방 정리
            int emptyRoomsDeleted = cleanupEmptyRooms();

            log.info("채팅 데이터 정리 완료 - 총 삭제된 메시지: {}, 삭제된 빈 채팅방: {}",
                    totalDeleted, emptyRoomsDeleted);

        } catch (Exception e) {
            log.error("채팅 데이터 정리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private int cleanupEmptyRooms() {
        LocalDateTime cutoffDate = LocalDateTime.now()
                .minusDays(ChatMessageConstants.DEFAULT_RETENTION_DAYS);
        return chatRoomRepository.softDeleteEmptyRoomsOlderThan(cutoffDate);
    }
}
