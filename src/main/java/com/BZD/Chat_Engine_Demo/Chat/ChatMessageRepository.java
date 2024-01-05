package com.BZD.Chat_Engine_Demo.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * JPA repository for chat messages, providing an abstract way to access
 * the database and retrieve messages.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByOrderByTimestampDesc(Pageable pageable);
    List<ChatMessage> findAllByOrderByTimestampAsc();
}
