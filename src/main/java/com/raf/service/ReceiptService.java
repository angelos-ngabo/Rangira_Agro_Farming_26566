package com.raf.service;

import com.raf.entity.Payment;
import com.raf.entity.Transaction;
import com.raf.entity.User;
import com.raf.exception.ResourceNotFoundException;
import com.raf.repository.PaymentRepository;
import com.raf.repository.TransactionRepository;
import com.raf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReceiptService {

private final TransactionRepository transactionRepository;
private final PaymentRepository paymentRepository;
private final UserRepository userRepository;


public List<Transaction> getBuyerReceipts(Long buyerId) {
if (!userRepository.existsById(buyerId)) {
throw new ResourceNotFoundException("Buyer not found with ID: " + buyerId);
}

return transactionRepository.findByBuyerId(buyerId).stream()
.filter(t -> t.getPaymentStatus() == com.raf.enums.PaymentStatus.PAID)
.collect(Collectors.toList());
}


public Transaction getReceiptByTransactionId(Long transactionId, Long buyerId) {
Transaction transaction = transactionRepository.findById(transactionId)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

if (!transaction.getBuyer().getId().equals(buyerId)) {
throw new ResourceNotFoundException("Transaction not found or access denied");
}

if (transaction.getPaymentStatus() != com.raf.enums.PaymentStatus.PAID) {
throw new IllegalArgumentException("Transaction is not paid yet");
}

return transaction;
}


public Transaction getTransactionForAdmin(Long transactionId) {
return transactionRepository.findById(transactionId)
.orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
}


private String getLogoBase64() {
try {

java.io.InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
if (logoStream == null) {
logoStream = getClass().getClassLoader().getResourceAsStream("images/logo.png");
}
if (logoStream == null) {

java.nio.file.Path logoPath = java.nio.file.Paths.get("frontend/public/images/logo.png");
if (java.nio.file.Files.exists(logoPath)) {
byte[] logoBytes = java.nio.file.Files.readAllBytes(logoPath);
return java.util.Base64.getEncoder().encodeToString(logoBytes);
}
}

if (logoStream != null) {
byte[] logoBytes = logoStream.readAllBytes();
logoStream.close();
return java.util.Base64.getEncoder().encodeToString(logoBytes);
}
} catch (Exception e) {
log.warn("Failed to load logo.png file, using fallback SVG: {}", e.getMessage());
}


String svgLogo = """
<svg width="128" height="128" viewBox="0 0 128 128" xmlns="http:
<defs>
<linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
<stop offset="0%" style="stop-color:#116530;stop-opacity:1" />
<stop offset="100%" style="stop-color:#2ea359;stop-opacity:1" />
</linearGradient>
</defs>
<rect width="128" height="128" rx="20" fill="url(#grad)"/>
<g transform="translate(32, 32)">
<path d="M32 16L8 32v32l24-16 24 16V32L32 16z" fill="white"/>
<circle cx="32" cy="48" r="6" fill="#2ea359"/>
</g>
<text x="64" y="100" font-family="Arial, sans-serif" font-size="18" font-weight="700"
fill="white" text-anchor="middle" text-transform="lowercase" letter-spacing="2">rangira</text>
</svg>
""";
return java.util.Base64.getEncoder().encodeToString(svgLogo.getBytes(java.nio.charset.StandardCharsets.UTF_8));
}

/**
* Generate PDF receipt for a transaction
* Returns HTML that can be converted to PDF on the frontend
*/
public String generateReceiptHtml(Transaction transaction) {
Payment payment = paymentRepository.findByTransactionId(transaction.getId())
.stream()
.findFirst()
.orElse(null);

DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' HH:mm");
DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

String paymentDate = payment != null && payment.getPaymentDate() != null
? payment.getPaymentDate().format(formatter)
: transaction.getPaymentDate() != null
? transaction.getPaymentDate().format(formatter)
: "N/A";

String transactionDate = transaction.getTransactionDate() != null
? transaction.getTransactionDate().format(dateFormatter)
: "N/A";

String logoBase64 = getLogoBase64();

return String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Arial', sans-serif; background: #f5f5f5; padding: 20px; }
.receipt-container { max-width: 800px; margin: 0 auto; background: white; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.header { border-bottom: 4px solid #116530; padding-bottom: 20px; margin-bottom: 30px; text-align: center; }
.logo-img { width: 60px; height: 60px; margin-bottom: 10px; }
.header h1 { color: #116530; margin-bottom: 5px; font-size: 32px; text-transform: lowercase; letter-spacing: 1px; }
.header .subtitle { color: #666; font-size: 14px; }
.receipt-number { background: #f0f0f0; padding: 10px; border-radius: 5px; margin-top: 15px; display: inline-block; }
.receipt-number strong { color: #116530; }
.details-section { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 30px; }
.detail-box { background: #f9fafb; padding: 15px; border-radius: 8px; border-left: 4px solid #116530; }
.detail-box h3 { color: #116530; margin-bottom: 10px; font-size: 16px; }
.detail-box p { margin: 5px 0; color: #333; }
.product-section { margin: 30px 0; }
.product-section h2 { color: #116530; border-bottom: 2px solid #116530; padding-bottom: 10px; margin-bottom: 20px; }
table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
th { background: #116530; color: white; padding: 12px; text-align: left; font-weight: 600; }
td { padding: 12px; border-bottom: 1px solid #e0e0e0; }
.total-section { background: #f9fafb; padding: 20px; border-radius: 8px; margin-top: 20px; }
.total-row { display: flex; justify-content: space-between; padding: 8px 0; }
.total-row.final { border-top: 2px solid #116530; margin-top: 10px; padding-top: 15px; font-size: 18px; font-weight: bold; color: #116530; }
.footer { margin-top: 40px; padding-top: 20px; border-top: 2px solid #e0e0e0; text-align: center; color: #666; font-size: 12px; }
.status-badge { display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; }
.status-paid { background: #2ea359; color: white; }
</style>
</head>
<body>
<div class="receipt-container">
<div class="header">
<img src="data:image/png;base64,%s" alt="Rangira Logo" class="logo-img" style="width: 80px; height: 80px; border-radius: 8px;" />
<h1>rangira</h1>
<p class="subtitle">Digital Farm Management & Market Linkage Platform</p>
<div class="receipt-number">
<strong>Receipt #%s</strong>
</div>
</div>

<div class="details-section">
<div class="detail-box">
<h3>Buyer Information</h3>
<p><strong>Name:</strong> %s %s</p>
<p><strong>Email:</strong> %s</p>
<p><strong>Phone:</strong> %s</p>
</div>
<div class="detail-box">
<h3>Seller Information</h3>
<p><strong>Name:</strong> %s %s</p>
<p><strong>Email:</strong> %s</p>
<p><strong>Phone:</strong> %s</p>
</div>
</div>

<div class="product-section">
<h2>Product Details</h2>
<table>
<thead>
<tr>
<th>Description</th>
<th>Quantity</th>
<th>Unit Price</th>
<th>Total</th>
</tr>
</thead>
<tbody>
<tr>
<td>%s</td>
<td>%s kg</td>
<td>RWF %s</td>
<td>RWF %s</td>
</tr>
</tbody>
</table>
</div>

<div class="total-section">
<div class="total-row">
<span>Subtotal:</span>
<span>RWF %s</span>
</div>
<div class="total-row">
<span>Storage Fee:</span>
<span>RWF %s</span>
</div>
<div class="total-row">
<span>Transaction Fee:</span>
<span>RWF %s</span>
</div>
<div class="total-row final">
<span>Total Amount Paid:</span>
<span>RWF %s</span>
</div>
</div>

<div class="details-section" style="margin-top: 30px;">
<div class="detail-box">
<h3>Transaction Details</h3>
<p><strong>Transaction Date:</strong> %s</p>
<p><strong>Payment Date:</strong> %s</p>
<p><strong>Payment Method:</strong> %s</p>
<p><strong>Status:</strong> <span class="status-badge status-paid">PAID</span></p>
</div>
</div>

<div class="footer">
<p>This is an official receipt. Please keep this for your records.</p>
<p>Generated on %s</p>
</div>
</div>
</body>
</html>
""",
logoBase64,
transaction.getTransactionCode(),
transaction.getBuyer().getFirstName(),
transaction.getBuyer().getLastName(),
transaction.getBuyer().getEmail(),
transaction.getBuyer().getPhoneNumber() != null ? transaction.getBuyer().getPhoneNumber() : "N/A",
transaction.getSeller().getFirstName(),
transaction.getSeller().getLastName(),
transaction.getSeller().getEmail(),
transaction.getSeller().getPhoneNumber() != null ? transaction.getSeller().getPhoneNumber() : "N/A",
transaction.getInventory().getCropType().getCropName(),
transaction.getQuantityKg(),
transaction.getUnitPrice(),
transaction.getTotalAmount(),
transaction.getTotalAmount(),
transaction.getStorageFee() != null ? transaction.getStorageFee() : BigDecimal.ZERO,
transaction.getTransactionFee() != null ? transaction.getTransactionFee() : BigDecimal.ZERO,
transaction.getTotalAmount(),
transactionDate,
paymentDate,
payment != null && payment.getPaymentMethod() != null ? payment.getPaymentMethod().toString() : "N/A",
LocalDateTime.now().format(formatter));
}
}
