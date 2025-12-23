package com.raf.service;

import com.raf.entity.Notification;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.enums.NotificationType;
import com.raf.repository.NotificationRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import com.raf.repository.WarehouseAccessRepository;
import com.raf.entity.WarehouseAccess;
import com.raf.enums.UserType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

private final NotificationRepository notificationRepository;
private final UserRepository userRepository;
private final TransactionRepository transactionRepository;
private final WarehouseAccessRepository warehouseAccessRepository;

@PersistenceContext
private EntityManager entityManager;


private List<Long> getAssignedWarehouseIds(Long storekeeperId) {
List<WarehouseAccess> warehouseAccesses = warehouseAccessRepository.findByUserIdAndIsActive(storekeeperId, true);
return warehouseAccesses.stream()
.filter(access -> access.getStatus() == com.raf.enums.WarehouseAccessStatus.APPROVED ||
access.getStatus() == com.raf.enums.WarehouseAccessStatus.ACTIVE)
.map(access -> access.getWarehouse().getId())
.distinct()
.toList();
}


private List<Notification> filterNotificationsByWarehouse(List<Notification> notifications, Long userId) {
if (userId == null) {
return notifications;
}

User user = userRepository.findById(userId).orElse(null);
if (user == null || user.getUserType() != UserType.STOREKEEPER) {
return notifications;
}

List<Long> assignedWarehouseIds = getAssignedWarehouseIds(userId);
if (assignedWarehouseIds.isEmpty()) {
log.info("Storekeeper {} has no assigned warehouses, returning empty notification list", userId);
return List.of();
}

log.info("Filtering notifications for storekeeper {} by {} assigned warehouses", userId, assignedWarehouseIds.size());
return notifications.stream()
.filter(notification -> {

if (notification.getTransaction() != null &&
notification.getTransaction().getInventory() != null &&
notification.getTransaction().getInventory().getWarehouse() != null) {
Long warehouseId = notification.getTransaction().getInventory().getWarehouse().getId();
return assignedWarehouseIds.contains(warehouseId);
}


if (notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_CREATED ||
notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_UPDATED ||
notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_DELETED ||
notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_ACCESS_SUBMITTED ||
notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_ACCESS_APPROVED ||
notification.getType() == com.raf.enums.NotificationType.WAREHOUSE_ACCESS_REJECTED ||
notification.getType() == com.raf.enums.NotificationType.SHIPMENT_REQUEST) {

Long warehouseId = extractWarehouseIdFromUrl(notification.getActionUrl());
if (warehouseId != null) {
return assignedWarehouseIds.contains(warehouseId);
}

return true;
}


if (notification.getType() == com.raf.enums.NotificationType.INVENTORY_CREATED ||
notification.getType() == com.raf.enums.NotificationType.INVENTORY_UPDATED ||
notification.getType() == com.raf.enums.NotificationType.INVENTORY_DELETED) {

Long warehouseId = extractWarehouseIdFromUrl(notification.getActionUrl());
if (warehouseId != null) {
return assignedWarehouseIds.contains(warehouseId);
}

return true;
}


if (notification.getType() == com.raf.enums.NotificationType.ORDER_SHIPPED ||
notification.getType() == com.raf.enums.NotificationType.ORDER_DELIVERED) {

return true;
}



return true;
})
.toList();
}


private Long extractWarehouseIdFromUrl(String url) {
if (url == null || url.isEmpty()) {
return null;
}

try {

java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("/(?:warehouse|warehouses)/(\\d+)");
java.util.regex.Matcher matcher1 = pattern1.matcher(url);
if (matcher1.find()) {
return Long.parseLong(matcher1.group(1));
}


java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("[?&]warehouseId=(\\d+)");
java.util.regex.Matcher matcher2 = pattern2.matcher(url);
if (matcher2.find()) {
return Long.parseLong(matcher2.group(1));
}


java.util.regex.Pattern pattern3 = java.util.regex.Pattern.compile("warehouseId=(\\d+)");
java.util.regex.Matcher matcher3 = pattern3.matcher(url);
if (matcher3.find()) {
return Long.parseLong(matcher3.group(1));
}
} catch (Exception e) {
log.debug("Failed to extract warehouse ID from URL: {}", url, e);
}

return null;
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public Notification createNotification(Long userId, String title, String message,
NotificationType type, Long transactionId) {
return createNotification(userId, title, message, type, transactionId, null);
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public Notification createNotification(Long userId, String title, String message,
NotificationType type, Long transactionId, String actionUrl) {
User user = userRepository.findById(userId)
.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


if (user.getNotificationsEnabled() == null || !user.getNotificationsEnabled()) {
log.info("Notifications are disabled for user {}. Skipping notification creation.", userId);
return null;
}

Notification notification = new Notification();
notification.setUser(user);
notification.setTitle(title);
notification.setMessage(message);
notification.setType(type);
notification.setIsRead(false);

if (transactionId != null) {
Transaction transaction = transactionRepository.findById(transactionId)
.orElse(null);
if (transaction != null) {
notification.setTransaction(transaction);
notification.setActionUrl(actionUrl != null ? actionUrl : "/transactions/" + transactionId);
}
} else if (actionUrl != null) {
notification.setActionUrl(actionUrl);
}

Notification saved = notificationRepository.save(notification);
entityManager.flush();
log.info("Notification created for user {}: {}", userId, title);
return saved;
}

@Transactional(readOnly = true)
public List<Notification> getUserNotifications(Long userId) {

List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDescWithUser(userId);


User user = userRepository.findById(userId).orElse(null);
if (user == null) {
return List.of();
}


notifications = filterNotificationsByRole(notifications, user);


if (user.getUserType() == UserType.STOREKEEPER) {
notifications = filterNotificationsByWarehouse(notifications, userId);
}

return notifications;
}


private List<Notification> filterNotificationsByRole(List<Notification> notifications, User user) {
return notifications.stream()
.filter(notification -> {
NotificationType type = notification.getType();
UserType userType = user.getUserType();


if (userType == UserType.BUYER) {
return type == NotificationType.PAYMENT_RECEIVED ||
type == NotificationType.ORDER_SHIPPED ||
type == NotificationType.ORDER_DELIVERED ||
type == NotificationType.ENQUIRY_ACCEPTED ||
type == NotificationType.ENQUIRY_REJECTED ||
type == NotificationType.SHIPMENT_REQUEST;
}


if (userType == UserType.FARMER) {
return type == NotificationType.PAYMENT_RECEIVED ||
type == NotificationType.INVENTORY_CREATED ||
type == NotificationType.INVENTORY_UPDATED ||
type == NotificationType.ENQUIRY_RECEIVED ||
type == NotificationType.WAREHOUSE_ACCESS_APPROVED ||
type == NotificationType.WAREHOUSE_ACCESS_REJECTED ||
type == NotificationType.ORDER_SHIPPED ||
type == NotificationType.ORDER_DELIVERED;
}


if (userType == UserType.STOREKEEPER) {
return type == NotificationType.SHIPMENT_REQUEST ||
type == NotificationType.INVENTORY_CREATED ||
type == NotificationType.INVENTORY_UPDATED ||
type == NotificationType.WAREHOUSE_ACCESS_SUBMITTED ||
type == NotificationType.ORDER_SHIPPED ||
type == NotificationType.ORDER_DELIVERED;
}


if (userType == UserType.ADMIN) {
return type == NotificationType.WAREHOUSE_CREATED ||
type == NotificationType.WAREHOUSE_UPDATED ||
type == NotificationType.WAREHOUSE_DELETED ||
type == NotificationType.WAREHOUSE_ACCESS_SUBMITTED ||
type == NotificationType.INVENTORY_CREATED ||
type == NotificationType.INVENTORY_UPDATED ||
type == NotificationType.INVENTORY_DELETED ||
type == NotificationType.SYSTEM_ALERT ||
type == NotificationType.USER_CREATED;
}


return notification.getUser().getId().equals(user.getId());
})
.toList();
}

@Transactional(readOnly = true)
public List<Notification> getUnreadNotifications(Long userId) {

List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseWithUser(userId);


User user = userRepository.findById(userId).orElse(null);
if (user == null) {
return List.of();
}


notifications = filterNotificationsByRole(notifications, user);


if (user.getUserType() == UserType.STOREKEEPER) {
notifications = filterNotificationsByWarehouse(notifications, userId);
}

return notifications;
}

@Transactional(readOnly = true)
public long getUnreadCount(Long userId) {

User user = userRepository.findById(userId).orElse(null);
if (user != null && user.getUserType() == UserType.STOREKEEPER) {
List<Notification> unreadNotifications = getUnreadNotifications(userId);
return unreadNotifications.size();
}

return notificationRepository.countUnreadByUserId(userId);
}

public void markAsRead(Long notificationId, Long userId) {
Notification notification = notificationRepository.findById(notificationId)
.orElseThrow(() -> new RuntimeException("Notification not found"));

if (!notification.getUser().getId().equals(userId)) {
throw new RuntimeException("Unauthorized to mark this notification as read");
}

notification.setIsRead(true);
notification.setReadAt(LocalDateTime.now());
notificationRepository.save(notification);
entityManager.flush();
}

public void markAllAsRead(Long userId) {
List<Notification> unread = notificationRepository.findByUserIdAndIsRead(userId, false);
for (Notification notification : unread) {
notification.setIsRead(true);
notification.setReadAt(LocalDateTime.now());
}
notificationRepository.saveAll(unread);
entityManager.flush();
}


@Transactional(propagation = Propagation.REQUIRES_NEW)
public void notifyAllAdmins(String title, String message, NotificationType type, String actionUrl) {
try {
List<User> admins = userRepository.findByUserType(com.raf.enums.UserType.ADMIN);
for (User admin : admins) {
try {
createNotification(admin.getId(), title, message, type, null, actionUrl);
log.info("Notified admin {}: {}", admin.getEmail(), title);
} catch (Exception e) {
log.error("Failed to notify admin {}: {}", admin.getEmail(), e.getMessage());

}
}
} catch (Exception e) {
log.error("Failed to notify admins: {}", e.getMessage());

}
}


@Transactional(propagation = Propagation.REQUIRES_NEW)
public void notifyUser(Long userId, String title, String message, NotificationType type, String actionUrl) {
try {
createNotification(userId, title, message, type, null, actionUrl);
} catch (Exception e) {
log.error("Failed to notify user {}: {}", userId, e.getMessage());

}
}
}

