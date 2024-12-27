package com.sounganization.botanify.domain.chat.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import com.sounganization.botanify.domain.chat.components.ChatMessageConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column
    private LocalDateTime expirationDate;

    @PrePersist
    public void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusDays(ChatMessageConstants.DEFAULT_RETENTION_DAYS);
    }

    public enum MessageType {
        ENTER, TALK, LEAVE
    }
}
