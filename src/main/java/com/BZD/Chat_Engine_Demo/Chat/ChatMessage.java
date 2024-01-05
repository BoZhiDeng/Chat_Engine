package com.BZD.Chat_Engine_Demo.Chat;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a chat message in the system. Includes information about
 * the message type, content, sender, and timestamp.
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private String content;

    private String sender;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
