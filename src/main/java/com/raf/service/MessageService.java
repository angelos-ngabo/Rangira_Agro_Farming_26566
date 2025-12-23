package com.raf.service;

import com.raf.dto.MessageRequest;
import com.raf.entity.Inventory;
import com.raf.entity.Message;
import com.raf.entity.User;
import com.raf.enums.UserType;
import com.raf.exception.ResourceNotFoundException;
import com.raf.exception.UnauthorizedException;
import com.raf.repository.InventoryRepository;
import com.raf.repository.MessageRepository;
import com.raf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

private final MessageRepository messageRepository;
private final UserRepository userRepository;
private final InventoryRepository inventoryRepository;


public Message sendMessage(MessageRequest request, Long senderId) {
log.info("Sending message from user {} to user {}", senderId, request.getReceiverId());

User sender = userRepository.findById(senderId)
.orElseThrow(() -> new ResourceNotFoundException("Sender not found with ID: " + senderId));

User receiver = userRepository.findById(request.getReceiverId())
.orElseThrow(() -> new ResourceNotFoundException("Receiver not found with ID: " + request.getReceiverId()));


if (sender.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin users cannot send messages");
}

if (receiver.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Cannot send messages to admin users");
}




boolean isValidPair =
(sender.getUserType() == UserType.BUYER && receiver.getUserType() == UserType.FARMER) ||
(sender.getUserType() == UserType.FARMER && receiver.getUserType() == UserType.BUYER) ||
(sender.getUserType() == UserType.STOREKEEPER && receiver.getUserType() == UserType.FARMER) ||
(sender.getUserType() == UserType.FARMER && receiver.getUserType() == UserType.STOREKEEPER);

log.debug("Message validation - Sender: {}, Receiver: {}, Valid: {}",
sender.getUserType(), receiver.getUserType(), isValidPair);

if (!isValidPair) {
log.warn("Invalid message pair - Sender: {}, Receiver: {}", sender.getUserType(), receiver.getUserType());
throw new UnauthorizedException("Messages can only be sent between Buyers and Farmers, or Storekeepers and Farmers");
}


Inventory relatedInventory = null;
if (request.getRelatedInventoryId() != null) {
relatedInventory = inventoryRepository.findById(request.getRelatedInventoryId())
.orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + request.getRelatedInventoryId()));
}


String messageCode = "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

Message message = new Message();
message.setMessageCode(messageCode);
message.setSender(sender);
message.setReceiver(receiver);
message.setRelatedInventory(relatedInventory);
message.setSubject(request.getSubject());
message.setContent(request.getContent());
message.setIsRead(false);
message.setRepliedToMessageId(request.getRepliedToMessageId());

return messageRepository.save(message);
}


public List<Message> getConversation(Long userId1, Long userId2, Long currentUserId) {
User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin users cannot access messages");
}


if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
throw new UnauthorizedException("You can only access your own conversations");
}

User user1 = userRepository.findById(userId1)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId1));

User user2 = userRepository.findById(userId2)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId2));

return messageRepository.findConversationBetweenUsers(user1, user2);
}


public Page<Message> getMessagesForUser(Long userId, Long currentUserId, Pageable pageable) {
User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin users cannot access messages");
}


if (!currentUserId.equals(userId)) {
throw new UnauthorizedException("You can only access your own messages");
}

User user = userRepository.findById(userId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

return messageRepository.findMessagesForUser(user, pageable);
}


public List<Message> getUnreadMessages(Long userId, Long currentUserId) {
User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin users cannot access messages");
}


if (!currentUserId.equals(userId)) {
throw new UnauthorizedException("You can only access your own messages");
}

User user = userRepository.findById(userId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

return messageRepository.findUnreadMessagesForUser(user);
}


public Message markAsRead(Long messageId, Long currentUserId) {
Message message = messageRepository.findById(messageId)
.orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));


if (!message.getReceiver().getId().equals(currentUserId)) {
throw new UnauthorizedException("You can only mark your own received messages as read");
}

message.setIsRead(true);
message.setReadAt(LocalDateTime.now());

return messageRepository.save(message);
}


public Long getUnreadMessageCount(Long userId, Long currentUserId) {
User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.ADMIN) {
return 0L;
}


if (!currentUserId.equals(userId)) {
throw new UnauthorizedException("You can only access your own message count");
}

User user = userRepository.findById(userId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

return messageRepository.countUnreadMessagesForUser(user);
}


public Message getMessageById(Long messageId, Long currentUserId) {
Message message = messageRepository.findById(messageId)
.orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

User currentUser = userRepository.findById(currentUserId)
.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUserId));


if (currentUser.getUserType() == UserType.ADMIN) {
throw new UnauthorizedException("Admin users cannot access messages");
}


if (!message.getSender().getId().equals(currentUserId) &&
!message.getReceiver().getId().equals(currentUserId)) {
throw new UnauthorizedException("You can only access your own messages");
}

return message;
}
}

