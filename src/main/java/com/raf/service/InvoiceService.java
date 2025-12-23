package com.raf.service;

import com.raf.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

private final EmailService emailService;

public void sendInvoiceToBuyer(Transaction transaction) {
log.info("Generating and sending invoice for transaction {}", transaction.getTransactionCode());

String invoiceHtml = generateInvoiceHtml(transaction);
String subject = "Invoice - " + transaction.getTransactionCode();

emailService.sendHtmlEmail(
transaction.getBuyer().getEmail(),
subject,
invoiceHtml);

log.info("Invoice sent to buyer: {}", transaction.getBuyer().getEmail());
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

public String generateInvoiceHtml(Transaction transaction) {
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
String logoBase64 = getLogoBase64();

return String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.invoice-container { max-width: 800px; margin: 0 auto; background: #ffffff; padding: 0; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 10px; overflow: hidden; }
.header { background: linear-gradient(135deg, #116530 0%%, #2ea359 100%%); color: #ffffff; padding: 30px 20px; text-align: center; }
.logo-img { width: 60px; height: 60px; margin-bottom: 10px; display: block; margin: 0 auto 10px; }
.header h1 { margin: 0; font-size: 28px; font-weight: 700; text-transform: lowercase; letter-spacing: 2px; }
.header p { margin: 10px 0 0 0; font-size: 16px; opacity: 0.95; }
.header .invoice-number { margin-top: 15px; font-size: 18px; font-weight: 600; }
.content { padding: 40px 30px; color: #333333; }
.invoice-details { display: flex; justify-content: space-between; margin-bottom: 30px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; }
.invoice-details div { flex: 1; }
.invoice-details strong { color: #116530; font-size: 16px; display: block; margin-bottom: 10px; }
.invoice-details p { color: #495057; margin: 5px 0; font-size: 15px; }
.section { margin-bottom: 30px; }
.section h2 { color: #116530; border-bottom: 3px solid #116530; padding-bottom: 10px; margin-bottom: 20px; font-size: 22px; font-weight: 600; }
table { width: 100%%; border-collapse: collapse; margin: 20px 0; background-color: #ffffff; }
th, td { padding: 15px; text-align: left; border-bottom: 1px solid #e9ecef; }
th { background-color: #116530; color: #ffffff; font-weight: 600; font-size: 15px; }
td { color: #495057; font-size: 15px; }
.total-row { font-weight: bold; font-size: 1.2em; background-color: #f8f9fa; }
.total-row td { color: #116530; font-weight: 700; }
.footer { margin-top: 40px; padding-top: 25px; border-top: 2px solid #e9ecef; color: #6c757d; text-align: center; font-size: 14px; }
.footer p { margin: 8px 0; color: #6c757d; }
</style>
</head>
<body>
<div class="invoice-container">
<div class="header">
<img src="data:image/png;base64,%s" alt="Rangira Logo" class="logo-img" style="width: 80px; height: 80px; margin-bottom: 15px; border-radius: 8px;" />
<h1>rangira</h1>
<p>Agro Farming Platform</p>
<p class="invoice-number">Invoice #%s</p>
</div>

<div class="content">
<div class="invoice-details">
<div>
<strong>Bill To:</strong>
<p>%s %s</p>
<p>%s</p>
<p>%s</p>
</div>
<div>
<strong>Invoice Date:</strong>
<p>%s</p>
<strong>Transaction Code:</strong>
<p>%s</p>
</div>
</div>

<div class="section">
<h2>Product Details</h2>
<table>
<tr>
<th>Description</th>
<th>Quantity</th>
<th>Unit Price</th>
<th>Total</th>
</tr>
<tr>
<td>%s</td>
<td>%s kg</td>
<td>RWF %s</td>
<td>RWF %s</td>
</tr>
</table>
</div>

<div class="section">
<h2>Payment Summary</h2>
<table>
<tr>
<td>Subtotal:</td>
<td style="text-align: right;">RWF %s</td>
</tr>
<tr>
<td>Storage Fee (2%%):</td>
<td style="text-align: right;">RWF %s</td>
</tr>
<tr>
<td>Transaction Fee (1%%):</td>
<td style="text-align: right;">RWF %s</td>
</tr>
<tr class="total-row">
<td>Total Amount:</td>
<td style="text-align: right; color: #116530;">RWF %s</td>
</tr>
</table>
</div>

<div class="footer">
<p><strong>Thank you for your purchase!</strong></p>
<p>This is an automated invoice. Please keep this for your records.</p>
<p>© 2024 Rangira Agro Farming. All rights reserved.</p>
</div>
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
transaction.getBuyer().getPhoneNumber(),
LocalDateTime.now().format(formatter),
transaction.getTransactionCode(),
transaction.getInventory().getCropType().getCropName(),
transaction.getQuantityKg(),
transaction.getUnitPrice().toPlainString(),
transaction.getTotalAmount().toPlainString(),
transaction.getTotalAmount().toPlainString(),
transaction.getStorageFee().toPlainString(),
transaction.getTransactionFee().toPlainString(),
transaction.getTotalAmount().toPlainString());
}

public String generateInvoicePdf(Transaction transaction) {
// TODO: Implement PDF generation using a library like iText or Apache PDFBox
// For now, return HTML that can be converted to PDF
return generateInvoiceHtml(transaction);
}
}
