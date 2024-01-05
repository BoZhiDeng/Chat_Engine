package com.BZD.Chat_Engine_Demo.Chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.BZD.Chat_Engine_Demo.Login_System.User;
import com.BZD.Chat_Engine_Demo.Login_System.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import java.util.*;

/**
 * Controller for chat functionalities, handling web requests for chat operations,
 * managing chat messages, and chat history.
 */
@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Value("${chat.history.max-messages}")
    private int maxMessages;

    /**
     * Serves the chat page and loads the chat history for the user.
     */
    @GetMapping("/chat")
    public String getChatPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userRepository.findByEmail(currentPrincipalName);
        if (user != null) {
            model.addAttribute("UserName", user.getUsername());
            model.addAttribute("Email", user.getEmail());
        }
        PageRequest pageRequest = PageRequest.of(0, maxMessages, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<ChatMessage> chatHistory = chatMessageRepository.findAll(pageRequest).getContent();
        model.addAttribute("chatHistory", chatHistory);

        return "chat";
    }

    /**
     * Handles sending chat messages to the public chat topic.
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    /**
     * Handles the addition of a user to the chat and notifies other users.
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Optional.ofNullable(headerAccessor.getSessionAttributes())
                .ifPresent(attributes -> attributes.put("username", chatMessage.getSender()));
        return chatMessage;
    }

    /**
     * Loads the chat history for the user's session.
     */
    @MessageMapping("/chat.loadHistory")
    @SendTo("/topic/history")
    public List<ChatMessage> loadChatHistory(SimpMessageHeaderAccessor headerAccessor) {
        Optional<Map<String, Object>> sessionAttributes = Optional.ofNullable(headerAccessor.getSessionAttributes());
        return sessionAttributes.map(attrs -> (String) attrs.get("username"))
                .map(username -> {
                    PageRequest pageRequest = PageRequest.of(0, maxMessages, Sort.by(Sort.Direction.ASC, "timestamp"));
                    return chatMessageRepository.findAll(pageRequest).getContent();
                }).orElse(Collections.emptyList());
    }
}
