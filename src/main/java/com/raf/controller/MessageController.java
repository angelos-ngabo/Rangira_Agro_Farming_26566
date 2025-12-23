package com.raf.controller;

import com.raf.dto.MessageRequest;
import com.raf.entity.Message;
import com.raf.entity.User;
import com.raf.service.MessageService;
import com.raf.service.UserService;
import com.raf.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Messaging APIs for Buyers and Farmers (Admin cannot access)")
public class MessageController {

private final MessageService messageService;
private final UserService userService;
private final JwtUtil jwtUtil;

@PostMapping
@Operation(summary = "Send a message", description = "Send a message from Buyer to Farmer or vice versa. Admin cannot send messages.")
public ResponseEntity<Message> sendMessage(@Valid @RequestBody MessageRequest request, HttpServletRequest httpRequest) {
Long senderId = getCurrentUserId(httpRequest);
Message message = messageService.sendMessage(request, senderId);
return new ResponseEntity<>(message, HttpStatus.CREATED);
}

@GetMapping("/conversation")
@Operation(summary = "Get conversation between two users", description = "Get all messages in a conversation between two users. Admin cannot access.")
public ResponseEntity<List<Message>> getConversation(
@RequestParam Long userId1,
@RequestParam Long userId2,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
List<Message> messages = messageService.getConversation(userId1, userId2, currentUserId);
return ResponseEntity.ok(messages);
}

@GetMapping("/user/{userId}")
@Operation(summary = "Get all messages for a user", description = "Get all messages (sent and received) for a user. Admin cannot access.")
public ResponseEntity<Page<Message>> getMessagesForUser(
@PathVariable Long userId,
@PageableDefault(size = 20) Pageable pageable,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
Page<Message> messages = messageService.getMessagesForUser(userId, currentUserId, pageable);
return ResponseEntity.ok(messages);
}

@GetMapping("/user/{userId}/unread")
@Operation(summary = "Get unread messages for a user", description = "Get all unread messages for a user. Admin cannot access.")
public ResponseEntity<List<Message>> getUnreadMessages(
@PathVariable Long userId,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
List<Message> messages = messageService.getUnreadMessages(userId, currentUserId);
return ResponseEntity.ok(messages);
}

@GetMapping("/user/{userId}/unread-count")
@Operation(summary = "Get unread message count", description = "Get count of unread messages for a user. Admin cannot access.")
public ResponseEntity<Long> getUnreadMessageCount(
@PathVariable Long userId,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
Long count = messageService.getUnreadMessageCount(userId, currentUserId);
return ResponseEntity.ok(count);
}

@GetMapping("/{messageId}")
@Operation(summary = "Get message by ID", description = "Get a specific message by ID. Admin cannot access.")
public ResponseEntity<Message> getMessageById(
@PathVariable Long messageId,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
Message message = messageService.getMessageById(messageId, currentUserId);
return ResponseEntity.ok(message);
}

@PatchMapping("/{messageId}/read")
@Operation(summary = "Mark message as read", description = "Mark a message as read. Only the receiver can mark it as read.")
public ResponseEntity<Message> markAsRead(
@PathVariable Long messageId,
HttpServletRequest httpRequest) {
Long currentUserId = getCurrentUserId(httpRequest);
Message message = messageService.markAsRead(messageId, currentUserId);
return ResponseEntity.ok(message);
}

private Long getCurrentUserId(HttpServletRequest request) {
String authHeader = request.getHeader("Authorization");
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
throw new RuntimeException("Authorization header missing or invalid");
}

String jwt = authHeader.substring(7);
Long userId = jwtUtil.getUserIdFromToken(jwt);

if (userId == null) {

String email = jwtUtil.extractUsername(jwt);
User user = userService.getUserByEmail(email);
return user.getId();
}

return userId;
}
}

