# API Mapping Documentation

This document maps all frontend API calls to their backend controller implementations.

## Frontend API Service
**Location:** `frontend/src/services/dataService.js`

## Backend Controllers
**Location:** `src/main/java/com/raf/controller/`

---

## 1. Authentication APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `authService.login()` | `POST /api/auth/login` | `AuthController.java` | `login()` |
| `authService.register()` | `POST /api/auth/register` | `AuthController.java` | `register()` |
| `authService.verifyOtp()` | `POST /api/auth/verify-otp` | `AuthController.java` | `verifyOtp()` |
| `authService.resendOtp()` | `POST /api/auth/resend-otp` | `AuthController.java` | `resendOtp()` |
| `authService.forgotPassword()` | `POST /api/auth/forgot-password` | `AuthController.java` | `forgotPassword()` |
| `authService.resetPassword()` | `POST /api/auth/reset-password` | `AuthController.java` | `resetPassword()` |
| `authService.send2FA()` | `POST /api/auth/send-2fa` | `AuthController.java` | `send2FACode()` |
| `authService.verify2FA()` | `POST /api/auth/verify-2fa` | `AuthController.java` | `verify2FA()` |

**Controller File:** `src/main/java/com/raf/controller/AuthController.java`

---

## 2. User Management APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getUsers()` | `GET /api/users` or `GET /api/users/paginated` | `UserController.java` | `getAllUsers()` or `getUsersPaginated()` |
| `getUserById()` | `GET /api/users/{id}` | `UserController.java` | `getUserById()` |
| `getUsersByType()` | `GET /api/users/type/{userType}` | `UserController.java` | `getUsersByType()` |
| `createUser()` | `POST /api/users` | `UserController.java` | `createUser()` |
| `createUserByAdmin()` | `POST /api/users/admin/create` | `UserController.java` | `createUserByAdmin()` |
| `updateUser()` | `PUT /api/users/{id}` | `UserController.java` | `updateUser()` |
| `updateUserProfile()` | `PATCH /api/users/{id}/profile` | `UserController.java` | `updateUserProfile()` |
| `deleteUser()` | `DELETE /api/users/{id}` | `UserController.java` | `deleteUser()` |
| `uploadProfilePicture()` | `POST /api/users/{id}/profile-picture` | `UserController.java` | `uploadProfilePicture()` |
| `deleteProfilePicture()` | `DELETE /api/users/{id}/profile-picture` | `UserController.java` | `deleteProfilePicture()` |
| `updateNotificationPreference()` | `PATCH /api/users/me/notification-preference` | `UserController.java` | `updateNotificationPreference()` |
| `updateTwoFactorPreference()` | `PATCH /api/users/me/two-factor-preference` | `UserController.java` | `updateTwoFactorPreference()` |

**Controller File:** `src/main/java/com/raf/controller/UserController.java`

---

## 3. Warehouse APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getWarehouses()` | `GET /api/warehouses` | `WarehouseController.java` | `getAllWarehouses()` |
| `getWarehouseById()` | `GET /api/warehouses/{id}` | `WarehouseController.java` | `getWarehouseById()` |
| `createWarehouse()` | `POST /api/warehouses` | `WarehouseController.java` | `createWarehouse()` |
| `createWarehouseWithCropTypes()` | `POST /api/warehouses/with-crop-types` | `WarehouseController.java` | `createWarehouseWithCropTypes()` |
| `updateWarehouse()` | `PUT /api/warehouses/{id}` | `WarehouseController.java` | `updateWarehouse()` |
| `deleteWarehouse()` | `DELETE /api/warehouses/{id}` | `WarehouseController.java` | `deleteWarehouse()` |

**Controller File:** `src/main/java/com/raf/controller/WarehouseController.java`

---

## 4. Inventory APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getInventories()` | `GET /api/inventories` | `InventoryController.java` | `getAllInventories()` |
| `getAvailableInventories()` | `GET /api/inventories/available` | `InventoryController.java` | `getAvailableInventories()` |
| `getInventoryById()` | `GET /api/inventories/{id}` | `InventoryController.java` | `getInventoryById()` |
| `createInventory()` | `POST /api/inventories` | `InventoryController.java` | `createInventory()` |
| `updateInventory()` | `PUT /api/inventories/{id}` | `InventoryController.java` | `updateInventory()` |
| `deleteInventory()` | `DELETE /api/inventories/{id}` | `InventoryController.java` | `deleteInventory()` |
| `updateInventoryByStorekeeper()` | `PATCH /api/inventories/{id}/storekeeper` | `InventoryController.java` | `updateInventoryByStorekeeper()` |
| `createInventoryByStorekeeper()` | `POST /api/inventories/storekeeper/create` | `InventoryController.java` | `createInventoryByStorekeeper()` |
| `deleteInventoryByStorekeeper()` | `DELETE /api/inventories/{id}/storekeeper` | `InventoryController.java` | `deleteInventoryByStorekeeper()` |

**Controller File:** `src/main/java/com/raf/controller/InventoryController.java`

---

## 5. Inventory Request APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `createInventoryRequest()` | `POST /api/inventory-requests` | `InventoryRequestController.java` | `createInventoryRequest()` |
| `getMyInventoryRequests()` | `GET /api/inventory-requests/my-requests` | `InventoryRequestController.java` | `getMyInventoryRequests()` |
| `getInventoryRequestById()` | `GET /api/inventory-requests/{id}` | `InventoryRequestController.java` | `getInventoryRequestById()` |
| `getPendingInventoryRequests()` | `GET /api/inventory-requests/storekeeper/pending` | `InventoryRequestController.java` | `getPendingInventoryRequests()` |
| `getAllInventoryRequests()` | `GET /api/inventory-requests/storekeeper/all` | `InventoryRequestController.java` | `getAllInventoryRequests()` |
| `respondToInventoryRequest()` | `POST /api/inventory-requests/{id}/respond` | `InventoryRequestController.java` | `respondToInventoryRequest()` |
| `deleteInventoryRequest()` | `DELETE /api/inventory-requests/{id}` | `InventoryRequestController.java` | `deleteInventoryRequest()` |

**Controller File:** `src/main/java/com/raf/controller/InventoryRequestController.java`

---

## 6. Transaction APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getTransactions()` | `GET /api/transactions` or `GET /api/transactions/paginated` | `TransactionController.java` | `getAllTransactions()` or `getTransactionsPaginated()` |
| `getTransactionsByPaymentStatus()` | `GET /api/transactions/payment-status/{paymentStatus}` | `TransactionController.java` | `getTransactionsByPaymentStatus()` |
| `getTransactionById()` | `GET /api/transactions/{id}` | `TransactionController.java` | `getTransactionById()` |
| `getTotalSystemCommission()` | `GET /api/transactions/commission/total` | `TransactionController.java` | `getTotalSystemCommission()` |
| `createTransaction()` | `POST /api/transactions` | `TransactionController.java` | `createTransaction()` |
| `updateTransaction()` | `PUT /api/transactions/{id}` | `TransactionController.java` | `updateTransaction()` |
| `updatePaymentStatus()` | `PATCH /api/transactions/{id}/payment` | `TransactionController.java` | `updatePaymentStatus()` |
| `getSellerTransactions()` | `GET /api/transactions/seller/{sellerId}` | `TransactionController.java` | `getSellerTransactions()` |
| `getMyTransactions()` | `GET /api/transactions/my-transactions` | `TransactionController.java` | `getMyTransactions()` |
| `updateDeliveryStatus()` | `PATCH /api/transactions/{transactionId}/delivery-status` | `TransactionController.java` | `updateDeliveryStatus()` |

**Controller File:** `src/main/java/com/raf/controller/TransactionController.java`

---

## 7. Location APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getLocations()` | `GET /api/locations` | `LocationController.java` | `getAllLocations()` |
| `getLocationsByType()` | `GET /api/locations/type/{type}` | `LocationController.java` | `getLocationsByType()` |
| `getLocationById()` | `GET /api/locations/{id}` | `LocationController.java` | `getLocationById()` |
| `createLocation()` | `POST /api/locations` | `LocationController.java` | `createLocation()` |
| `updateLocation()` | `PUT /api/locations/{id}` | `LocationController.java` | `updateLocation()` |
| `deleteLocation()` | `DELETE /api/locations/{id}` | `LocationController.java` | `deleteLocation()` |
| `getProvinces()` | `GET /api/locations/provinces` | `LocationController.java` | `getProvinces()` |
| `getChildLocations()` | `GET /api/locations/parent/{parentId}/children` | `LocationController.java` | `getChildLocations()` |
| `searchLocations()` | `GET /api/locations/search` | `LocationController.java` | `searchLocations()` |

**Controller File:** `src/main/java/com/raf/controller/LocationController.java`

---

## 8. Crop Type APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getCropTypes()` | `GET /api/crop-types` | `CropTypeController.java` | `getAllCropTypes()` |
| `getCropTypeById()` | `GET /api/crop-types/{id}` | `CropTypeController.java` | `getCropTypeById()` |
| `createCropType()` | `POST /api/crop-types` | `CropTypeController.java` | `createCropType()` |
| `updateCropType()` | `PUT /api/crop-types/{id}` | `CropTypeController.java` | `updateCropType()` |
| `deleteCropType()` | `DELETE /api/crop-types/{id}` | `CropTypeController.java` | `deleteCropType()` |

**Controller File:** `src/main/java/com/raf/controller/CropTypeController.java`

---

## 9. Rating APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getRatings()` | `GET /api/ratings` or `GET /api/ratings/paginated` | `RatingController.java` | `getAllRatings()` or `getRatingsPaginated()` |
| `getRatingById()` | `GET /api/ratings/{id}` | `RatingController.java` | `getRatingById()` |
| `createRating()` | `POST /api/ratings` | `RatingController.java` | `createRating()` |
| `updateRating()` | `PUT /api/ratings/{id}` | `RatingController.java` | `updateRating()` |
| `deleteRating()` | `DELETE /api/ratings/{id}` | `RatingController.java` | `deleteRating()` |
| `getRatingsByTransaction()` | `GET /api/ratings/transaction/{transactionId}` | `RatingController.java` | `getRatingsByTransaction()` |

**Controller File:** `src/main/java/com/raf/controller/RatingController.java`

---

## 10. Warehouse Access APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getWarehouseAccesses()` | `GET /api/warehouse-accesses` | `WarehouseAccessController.java` | `getAllWarehouseAccesses()` |
| `getWarehouseAccessById()` | `GET /api/warehouse-accesses/{id}` | `WarehouseAccessController.java` | `getWarehouseAccessById()` |
| `createWarehouseAccess()` | `POST /api/warehouse-accesses` | `WarehouseAccessController.java` | `createWarehouseAccess()` |
| `updateWarehouseAccess()` | `PUT /api/warehouse-accesses/{id}` | `WarehouseAccessController.java` | `updateWarehouseAccess()` |
| `updateWarehouseAccessStatus()` | `PATCH /api/warehouse-accesses/{id}/status` | `WarehouseAccessController.java` | `updateWarehouseAccessStatus()` |
| `deleteWarehouseAccess()` | `DELETE /api/warehouse-accesses/{id}` | `WarehouseAccessController.java` | `deleteWarehouseAccess()` |
| `checkStorekeeperAssignment()` | `GET /api/warehouse-accesses/assign-storekeeper/check` | `WarehouseAccessController.java` | `checkStorekeeperAssignment()` |
| `assignStorekeeperToWarehouse()` | `POST /api/warehouse-accesses/assign-storekeeper` | `WarehouseAccessController.java` | `assignStorekeeperToWarehouse()` |
| `uploadWarehouseAccessImage()` | `POST /api/warehouse-accesses/upload-image` | `WarehouseAccessController.java` | `uploadWarehouseAccessImage()` |

**Controller File:** `src/main/java/com/raf/controller/WarehouseAccessController.java`

---

## 11. Message APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `sendMessage()` | `POST /api/messages` | `MessageController.java` | `sendMessage()` |
| `getConversation()` | `GET /api/messages/conversation` | `MessageController.java` | `getConversation()` |
| `getMessagesForUser()` | `GET /api/messages/user/{userId}` | `MessageController.java` | `getMessagesForUser()` |
| `getUnreadMessages()` | `GET /api/messages/user/{userId}/unread` | `MessageController.java` | `getUnreadMessages()` |
| `getUnreadMessageCount()` | `GET /api/messages/user/{userId}/unread-count` | `MessageController.java` | `getUnreadMessageCount()` |
| `getMessageById()` | `GET /api/messages/{messageId}` | `MessageController.java` | `getMessageById()` |
| `markMessageAsRead()` | `PATCH /api/messages/{messageId}/read` | `MessageController.java` | `markMessageAsRead()` |

**Controller File:** `src/main/java/com/raf/controller/MessageController.java`

---

## 12. Notification APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getNotifications()` | `GET /api/notifications` | `NotificationController.java` | `getNotifications()` |
| `getUnreadNotifications()` | `GET /api/notifications/unread` | `NotificationController.java` | `getUnreadNotifications()` |
| `getUnreadNotificationCount()` | `GET /api/notifications/unread/count` | `NotificationController.java` | `getUnreadNotificationCount()` |
| `markNotificationAsRead()` | `PATCH /api/notifications/{id}/read` | `NotificationController.java` | `markNotificationAsRead()` |
| `markAllNotificationsAsRead()` | `PATCH /api/notifications/read-all` | `NotificationController.java` | `markAllNotificationsAsRead()` |

**Controller File:** `src/main/java/com/raf/controller/NotificationController.java`

---

## 13. Receipt APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getBuyerReceipts()` | `GET /api/receipts` | `ReceiptController.java` | `getBuyerReceipts()` |
| `getReceiptByTransactionId()` | `GET /api/receipts/{transactionId}` | `ReceiptController.java` | `getReceiptByTransactionId()` |
| `downloadReceipt()` | `GET /api/receipts/{transactionId}/download` | `ReceiptController.java` | `downloadReceipt()` |
| `getReceiptByTransactionIdAdmin()` | `GET /api/receipts/admin/{transactionId}` | `ReceiptController.java` | `getReceiptByTransactionIdAdmin()` |
| `downloadReceiptAdmin()` | `GET /api/receipts/admin/{transactionId}/download` | `ReceiptController.java` | `downloadReceiptAdmin()` |

**Controller File:** `src/main/java/com/raf/controller/ReceiptController.java`

---

## 14. Enquiry APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `createEnquiry()` | `POST /api/enquiries` | `EnquiryController.java` | `createEnquiry()` |
| `getBuyerEnquiries()` | `GET /api/enquiries/buyer` | `EnquiryController.java` | `getBuyerEnquiries()` |
| `getFarmerEnquiries()` | `GET /api/enquiries/farmer` | `EnquiryController.java` | `getFarmerEnquiries()` |
| `respondToEnquiry()` | `POST /api/enquiries/{enquiryId}/respond` | `EnquiryController.java` | `respondToEnquiry()` |
| `getEnquiryById()` | `GET /api/enquiries/{id}` | `EnquiryController.java` | `getEnquiryById()` |

**Controller File:** `src/main/java/com/raf/controller/EnquiryController.java`

---

## 15. Payment APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `processPayment()` | `POST /api/payments` | `PaymentController.java` | `processPayment()` |

**Controller File:** `src/main/java/com/raf/controller/PaymentController.java`

---

## 16. Wallet APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getWallet()` | `GET /api/wallet` | `WalletController.java` | `getWallet()` |
| `requestWithdrawal()` | `POST /api/wallet/withdraw` | `WalletController.java` | `requestWithdrawal()` |
| `verifyWithdrawalOtp()` | `POST /api/wallet/withdraw/{withdrawalId}/verify-otp` | `WalletController.java` | `verifyOtpAndCompleteWithdrawal()` |
| `getWithdrawals()` | `GET /api/wallet/withdrawals` | `WalletController.java` | `getWithdrawals()` |

**Controller File:** `src/main/java/com/raf/controller/WalletController.java`

---

## 17. Search APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `publicSearch()` | `GET /api/public/search` | `PublicSearchController.java` | `search()` |
| `dashboardSearch()` | `GET /api/dashboard/search` | `DashboardSearchController.java` | `search()` |

**Controller Files:**
- `src/main/java/com/raf/controller/PublicSearchController.java`
- `src/main/java/com/raf/controller/DashboardSearchController.java`

---

## 18. Dashboard APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `getDashboardStats()` | `GET /api/dashboard/stats` | `DashboardController.java` (or similar) | `getDashboardStats()` |

**Controller File:** Check for `DashboardController.java` or similar

---

## 19. Newsletter APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `subscribeNewsletter()` | `POST /api/newsletter/subscribe` | `NewsletterController.java` | `subscribe()` |
| `unsubscribeNewsletter()` | `POST /api/newsletter/unsubscribe` | `NewsletterController.java` | `unsubscribe()` |

**Controller File:** `src/main/java/com/raf/controller/NewsletterController.java`

---

## 20. Contact APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| `sendContactMessage()` | `POST /api/contact/send` | `ContactController.java` | `sendMessage()` |

**Controller File:** `src/main/java/com/raf/controller/ContactController.java`

---

## 21. File Upload APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| File uploads are handled through various controllers | `POST /api/files/upload` or similar | `FileController.java` | Various upload methods |

**Controller File:** `src/main/java/com/raf/controller/FileController.java`

---

## 22. Health Check APIs

### Frontend → Backend Mapping

| Frontend Call | Backend Endpoint | Controller | Method |
|--------------|------------------|------------|--------|
| Health check endpoints | `GET /api/health` or similar | `HealthController.java` | Health check methods |

**Controller File:** `src/main/java/com/raf/controller/HealthController.java`

---

## Summary

- **Total Controllers:** 23
- **Frontend API Service:** `frontend/src/services/dataService.js`
- **Backend Base Path:** `/api/*`
- **All controllers are located in:** `src/main/java/com/raf/controller/`

## Notes

1. All API endpoints are prefixed with `/api/`
2. Authentication is handled via JWT tokens in the `Authorization` header
3. Most endpoints support pagination with `page` and `size` query parameters
4. Role-based access control is implemented using `@PreAuthorize` annotations
5. File uploads use `multipart/form-data` content type
6. Error handling is consistent across all controllers using custom exceptions

