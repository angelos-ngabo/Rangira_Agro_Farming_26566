package com.raf.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
@Order(100)
public class NotificationConstraintFixer implements CommandLineRunner {

private final JdbcTemplate jdbcTemplate;

@Override
public void run(String... args) {
fixNotificationTypeConstraint();
}

private void fixNotificationTypeConstraint() {
try {
log.info("Checking and fixing notification_type_check constraint...");


try {
jdbcTemplate.execute("ALTER TABLE notification DROP CONSTRAINT IF EXISTS notification_type_check");
log.info("Dropped existing notification_type_check constraint");
} catch (Exception e) {
log.debug("Constraint may not exist yet: {}", e.getMessage());
}


String constraintSql = """
ALTER TABLE notification
ADD CONSTRAINT notification_type_check
CHECK (type IN (
'SHIPMENT_REQUEST',
'PAYMENT_RECEIVED',
'ORDER_CONFIRMED',
'ORDER_SHIPPED',
'ORDER_DELIVERED',
'ENQUIRY_RECEIVED',
'ENQUIRY_ACCEPTED',
'ENQUIRY_REJECTED',
'WAREHOUSE_ACCESS_SUBMITTED',
'WAREHOUSE_ACCESS_APPROVED',
'WAREHOUSE_ACCESS_REJECTED',
'SYSTEM_ALERT',
'USER_CREATED',
'USER_UPDATED',
'USER_DELETED',
'WAREHOUSE_CREATED',
'WAREHOUSE_UPDATED',
'WAREHOUSE_DELETED',
'INVENTORY_CREATED',
'INVENTORY_UPDATED',
'INVENTORY_DELETED',
'CROP_TYPE_CREATED',
'CROP_TYPE_UPDATED',
'CROP_TYPE_DELETED'
))
""";

jdbcTemplate.execute(constraintSql);
log.info("Successfully updated notification_type_check constraint with all notification types");

} catch (Exception e) {

if (e.getMessage() != null && e.getMessage().contains("already exists")) {
log.debug("Constraint already exists, skipping: {}", e.getMessage());
} else {
log.warn("Failed to update notification_type_check constraint: {}. You may need to run the SQL script manually.", e.getMessage());
}
}
}
}

