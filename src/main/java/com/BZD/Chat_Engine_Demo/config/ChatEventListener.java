package com.BZD.Chat_Engine_Demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.BZD.Chat_Engine_Demo.Chat.ChatMessage;
import com.BZD.Chat_Engine_Demo.Chat.MessageType;
import java.util.Optional;
import java.util.Map;

/**
 * Listener for chat events such as disconnection, and responsible for handling
 * session-related events within the chat application.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    /**
     * Listener for chat events such as disconnection, and responsible for handling
     * session-related events within the chat application.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Optional<Map<String, Object>> sessionAttributes = Optional.ofNullable(headerAccessor.getSessionAttributes());
        sessionAttributes.map(attrs -> (String) attrs.get("username"))
                .ifPresent(username -> {
                    log.info("User disconnected: {}", username);
                    var chatMessage = ChatMessage.builder()
                            .type(MessageType.LEAVE)
                            .sender(username)
                            .build();
                    messagingTemplate.convertAndSend("/topic/public", chatMessage);
                });
    }
}
