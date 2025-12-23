package com.raf.service;

import com.raf.entity.Transaction;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClearanceDocumentService {


public byte[] generateClearancePdf(Transaction transaction) {
log.info("Generating clearance PDF for transaction {}", transaction.getTransactionCode());

try {
String htmlContent = generateClearanceHtml(transaction);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
HtmlConverter.convertToPdf(htmlContent, outputStream);
byte[] pdfBytes = outputStream.toByteArray();
log.info("✅ Successfully generated clearance PDF ({} bytes) for transaction {}", pdfBytes.length, transaction.getTransactionCode());
return pdfBytes;
} catch (Exception e) {
log.error("Failed to generate clearance PDF for transaction {}: {}", transaction.getTransactionCode(), e.getMessage(), e);
throw new RuntimeException("Failed to generate clearance PDF", e);
}
}


private String generateClearanceHtml(Transaction transaction) {
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' HH:mm");
String currentDate = LocalDateTime.now().format(formatter);

return String.format("""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
body { font-family: 'Arial', sans-serif; margin: 0; padding: 20px; background-color: #ffffff; }
.clearance-container { max-width: 800px; margin: 0 auto; border: 3px solid #a3b104; padding: 30px; background: #ffffff; }
.header { text-align: center; border-bottom: 3px solid #a3b104; padding-bottom: 20px; margin-bottom: 30px; }
.header h1 { color: #a3b104; margin: 0; font-size: 32px; font-weight: 700; text-transform: lowercase; letter-spacing: 3px; }
.header h2 { color: #2d5016; margin: 10px 0 0 0; font-size: 20px; font-weight: 600; }
.clearance-number { background: #f0f0f0; padding: 15px; margin: 20px 0; border-left: 5px solid #a3b104; }
.clearance-number strong { color: #2d5016; font-size: 18px; }
.section { margin: 25px 0; padding: 20px; background: #f9fafb; border-radius: 8px; }
.section h3 { color: #2d5016; margin-top: 0; border-bottom: 2px solid #a3b104; padding-bottom: 10px; }
.info-row { display: flex; justify-content: space-between; margin: 12px 0; padding: 8px 0; border-bottom: 1px solid #e0e0e0; }
.info-label { font-weight: 600; color: #495057; }
.info-value { color: #2d5016; font-weight: 500; }
.warning-box { background: #fff3cd; border: 2px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 8px; }
.warning-box strong { color: #856404; display: block; margin-bottom: 10px; font-size: 16px; }
.signature-section { margin-top: 40px; padding-top: 20px; border-top: 2px solid #a3b104; }
.signature-line { margin-top: 60px; border-top: 2px solid #333; width: 300px; }
.footer { margin-top: 30px; text-align: center; color: #6c757d; font-size: 12px; padding-top: 20px; border-top: 1px solid #e0e0e0; }
</style>
</head>
<body>
<div class="clearance-container">
<div class="header">
<h1>rangira</h1>
<h2>SHIPMENT CLEARANCE DOCUMENT</h2>
</div>

<div class="clearance-number">
<strong>Clearance Code: %s</strong><br>
<span style="color: #666; font-size: 14px;">Generated on: %s</span>
</div>

<div class="section">
<h3>Buyer Information</h3>
<div class="info-row">
<span class="info-label">Name:</span>
<span class="info-value">%s %s</span>
</div>
<div class="info-row">
<span class="info-label">Email:</span>
<span class="info-value">%s</span>
</div>
<div class="info-row">
<span class="info-label">Phone:</span>
<span class="info-value">%s</span>
</div>
</div>

<div class="section">
<h3>Shipment Details</h3>
<div class="info-row">
<span class="info-label">Transaction Code:</span>
<span class="info-value">%s</span>
</div>
<div class="info-row">
<span class="info-label">Product:</span>
<span class="info-value">%s</span>
</div>
<div class="info-row">
<span class="info-label">Quantity:</span>
<span class="info-value">%s %s</span>
</div>
<div class="info-row">
<span class="info-label">Unit Price:</span>
<span class="info-value">RWF %s</span>
</div>
<div class="info-row">
<span class="info-label">Total Amount:</span>
<span class="info-value">RWF %s</span>
</div>
<div class="info-row">
<span class="info-label">Seller:</span>
<span class="info-value">%s %s</span>
</div>
<div class="info-row">
<span class="info-label">Warehouse:</span>
<span class="info-value">%s</span>
</div>
</div>

<div class="warning-box">
<strong>⚠️ IMPORTANT - SHOW THIS TO THE DRIVER</strong>
<p style="margin: 10px 0; color: #856404;">
This document confirms that the shipment belongs to the buyer listed above.
Please present this clearance document to the delivery driver to verify your identity
and confirm receipt of the shipment.
</p>
<p style="margin: 10px 0; color: #856404;">
<strong>Clearance Code:</strong> %s<br>
<strong>Transaction Code:</strong> %s
</p>
</div>

<div class="signature-section">
<p style="color: #666; font-size: 14px;">
<strong>Authorized by:</strong> Rangira Agro Farming Platform<br>
<strong>Date:</strong> %s
</p>
<div class="signature-line"></div>
<p style="color: #666; font-size: 12px; margin-top: 5px;">Buyer Signature (Upon Receipt)</p>
</div>

<div class="footer">
<p><strong>Rangira Agro Farming Platform</strong></p>
<p>This is an official clearance document. Keep this document safe.</p>
<p>© 2024 Rangira Agro Farming. All rights reserved.</p>
</div>
</div>
</body>
</html>
""",
"CLR-" + transaction.getTransactionCode(),
currentDate,
transaction.getBuyer().getFirstName(),
transaction.getBuyer().getLastName(),
transaction.getBuyer().getEmail(),
transaction.getBuyer().getPhoneNumber(),
transaction.getTransactionCode(),
transaction.getInventory().getCropType().getCropName(),
transaction.getQuantityKg(),
transaction.getInventory().getCropType().getMeasurementUnit() != null ? transaction.getInventory().getCropType().getMeasurementUnit() : "KG",
transaction.getUnitPrice().toPlainString(),
transaction.getTotalAmount().toPlainString(),
transaction.getSeller().getFirstName(),
transaction.getSeller().getLastName(),
transaction.getInventory().getWarehouse() != null ? transaction.getInventory().getWarehouse().getWarehouseName() : "N/A",
"CLR-" + transaction.getTransactionCode(),
transaction.getTransactionCode(),
currentDate
);
}
}

