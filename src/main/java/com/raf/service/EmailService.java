package com.raf.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

private final JavaMailSender mailSender;

@Value("${app.email.from}")
private String fromEmail;

@Value("${app.frontend.url:http://localhost:3000}")
private String frontendUrl;


public void sendWithdrawalOtpEmail(String toEmail, String otpCode, String firstName, String amount) {
try {
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {
helper.setFrom(fromEmail);
}

helper.setTo(toEmail);
helper.setSubject("Rangira Agro Farming - Withdrawal Verification Code");

String htmlContent = buildWithdrawalOtpEmailTemplate(firstName, otpCode, amount);


String plainText = String.format(
"Hello %s!%n%nYour withdrawal verification code is: %s%n%n" +
"Amount: RWF %s%n%n" +
"This code will expire in 10 minutes.%n%n" +
"If you didn't request this withdrawal, please ignore this email.%n%n" +
"Best regards,%nRangira Agro Farming Team",
firstName, otpCode, amount
);


helper.setText(plainText, htmlContent);


message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("✅ Withdrawal OTP email sent successfully to: {}", toEmail);
} catch (Exception e) {
log.error("❌ Failed to send withdrawal OTP email to: {}", toEmail, e);
throw new RuntimeException("Failed to send withdrawal OTP email: " + e.getMessage(), e);
}
}


public void sendRegistrationOtpEmail(String toEmail, String otpCode, String firstName) {

if (mailSender == null) {
log.error("❌ JavaMailSender is NULL! Email cannot be sent.");
throw new RuntimeException("Email service not configured. JavaMailSender is null.");
}

log.info("Preparing to send registration OTP email to: {}", toEmail);
log.info("From email: {}", fromEmail);

try {
MimeMessage message = mailSender.createMimeMessage();
log.debug("MimeMessage created successfully");
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");


try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {

helper.setFrom(fromEmail);
log.warn("Failed to set sender name, using email only: {}", e.getMessage());
}
helper.setTo(toEmail);
helper.setSubject("Rangira Agro Farming - Email Verification Code");


String htmlContent = buildRegistrationOtpEmailTemplate(firstName, otpCode);


String plainText = String.format(
"Hello %s!%n%nThank you for registering with Rangira Agro Farming. " +
"Your verification code is: %s%n%nThis code will expire in 24 hours.%n%n" +
"If you didn't request this code, please ignore this email.%n%n" +
"Best regards,%nRangira Agro Farming Team",
firstName, otpCode
);


helper.setText(plainText, htmlContent);


message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("✅ Registration OTP email sent successfully to: {}", toEmail);
} catch (jakarta.mail.AuthenticationFailedException e) {
log.error("❌ EMAIL AUTHENTICATION FAILED for: {}", toEmail);
log.error("Check your email credentials in application.properties:");
log.error("  - spring.mail.username");
log.error("  - spring.mail.password (should be App Password, not regular password)");
log.error("Error: {}", e.getMessage());
throw new RuntimeException("Email authentication failed. Check email configuration.", e);
} catch (jakarta.mail.SendFailedException e) {
log.error("❌ EMAIL SEND FAILED for: {}", toEmail);
log.error("Error: {}", e.getMessage());
if (e.getNextException() != null) {
log.error("Next exception: {}", e.getNextException().getMessage());
}
throw new RuntimeException("Failed to send verification email. Check email configuration.", e);
} catch (MessagingException e) {
log.error("❌ EMAIL MESSAGING ERROR for: {}", toEmail);
log.error("Error: {}", e.getMessage());
log.error("Exception type: {}", e.getClass().getName());
throw new RuntimeException("Failed to send verification email: " + e.getMessage(), e);
} catch (Exception e) {
log.error("❌ UNEXPECTED EMAIL ERROR for: {}", toEmail);
log.error("Error: {}", e.getMessage());
log.error("Exception type: {}", e.getClass().getName());
throw new RuntimeException("Unexpected error sending email: " + e.getMessage(), e);
}
}


public void sendLoginOtpEmail(String toEmail, String otpCode, String firstName) {

if (mailSender == null) {
log.error("❌ JavaMailSender is NULL! Email cannot be sent.");
throw new RuntimeException("Email service not configured. JavaMailSender is null.");
}

log.info("Preparing to send login OTP email to: {}", toEmail);
log.info("From email: {}", fromEmail);

try {
MimeMessage message = mailSender.createMimeMessage();
log.debug("MimeMessage created successfully");
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");


try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {

helper.setFrom(fromEmail);
log.warn("Failed to set sender name, using email only: {}", e.getMessage());
}
helper.setTo(toEmail);
helper.setSubject("Rangira Agro Farming - Login Verification Code");


String htmlContent = buildLoginOtpEmailTemplate(firstName, otpCode);


String plainText = String.format(
"Hello %s!%n%nYour login verification code is: %s%n%n" +
"This code will expire in 10 minutes.%n%n" +
"If you didn't request this code, please ignore this email.%n%n" +
"Best regards,%nRangira Agro Farming Team",
firstName, otpCode
);


helper.setText(plainText, htmlContent);


message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("✅ Login OTP email sent successfully to: {}", toEmail);
} catch (jakarta.mail.AuthenticationFailedException e) {
log.error("❌ EMAIL AUTHENTICATION FAILED for: {}", toEmail);
log.error("Check your email credentials in application.properties:");
log.error("  - spring.mail.username");
log.error("  - spring.mail.password (should be App Password, not regular password)");
log.error("Error: {}", e.getMessage());
throw new RuntimeException("Email authentication failed. Check email configuration.", e);
} catch (jakarta.mail.SendFailedException e) {
log.error("❌ EMAIL SEND FAILED for: {}", toEmail);
log.error("Error: {}", e.getMessage());
if (e.getNextException() != null) {
log.error("Next exception: {}", e.getNextException().getMessage());
}
throw new RuntimeException("Failed to send login OTP email. Check email configuration.", e);
} catch (MessagingException e) {
log.error("❌ EMAIL MESSAGING ERROR for: {}", toEmail);
log.error("Error: {}", e.getMessage());
log.error("Exception type: {}", e.getClass().getName());
throw new RuntimeException("Failed to send login OTP email: " + e.getMessage(), e);
} catch (Exception e) {
log.error("❌ UNEXPECTED EMAIL ERROR for: {}", toEmail);
log.error("Error: {}", e.getMessage());
log.error("Exception type: {}", e.getClass().getName());
throw new RuntimeException("Unexpected error sending login OTP email: " + e.getMessage(), e);
}
}


@Deprecated
public void sendOtpEmail(String toEmail, String otpCode, String firstName) {

sendRegistrationOtpEmail(toEmail, otpCode, firstName);
}

public void sendPasswordResetEmail(String toEmail, String resetToken, String firstName) {
try {
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");


try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {

helper.setFrom(fromEmail);
log.warn("Failed to set sender name, using email only: {}", e.getMessage());
}
helper.setTo(toEmail);
helper.setSubject("Rangira Agro Farming - Password Reset Request");


String htmlContent = buildPasswordResetEmailTemplate(firstName, resetToken);


String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
String plainText = String.format(
"Hello %s!%n%nYou requested to reset your password.%n%n" +
"Click the following link to reset your password:%n%s%n%n" +
"This link will expire in 24 hours.%n%n" +
"If you didn't request this, please ignore this email.%n%n" +
"Best regards,%nRangira Agro Farming Team",
firstName, resetUrl
);


helper.setText(plainText, htmlContent);


message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("Password reset email sent successfully to: {}", toEmail);
} catch (MessagingException e) {
log.error("Failed to send password reset email to: {}", toEmail, e);
throw new RuntimeException("Failed to send password reset email", e);
}
}


private void embedLogoImage(MimeMessageHelper helper) throws MessagingException {




log.debug("Logo will be embedded in email HTML for profile picture display");
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
* Build registration OTP email template
*/
private String buildRegistrationOtpEmailTemplate(String firstName, String otpCode) {
String template = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.otp-box { background: #f9f9f9; border: 2px solid #116530; padding: 20px; text-align: center; margin: 20px 0; border-radius: 6px; }
.otp-code { font-size: 32px; font-weight: bold; color: #116530; letter-spacing: 6px; font-family: monospace; }
.warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 15px 0; }
.warning-box p { margin: 5px 0; color: #856404; font-size: 13px; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Email Verification</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<p>Thank you for registering. Verify your email with this code:</p>
<div class="otp-box">
<div class="otp-code">%s</div>
</div>
<div class="warning-box">
<p><strong>Important:</strong> Code expires in 24 hours.</p>
</div>
<p>Thank you for joining Rangira Agro Farming!</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""";
return String.format(template, firstName, otpCode);
}

/**
* Build login OTP email template
*/
private String buildLoginOtpEmailTemplate(String firstName, String otpCode) {
String template = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.otp-box { background: #f9f9f9; border: 2px solid #116530; padding: 20px; text-align: center; margin: 20px 0; border-radius: 6px; }
.otp-code { font-size: 32px; font-weight: bold; color: #116530; letter-spacing: 6px; font-family: monospace; }
.warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 15px 0; }
.warning-box p { margin: 5px 0; color: #856404; font-size: 13px; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Login Verification</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<p>Use this code to complete your login:</p>
<div class="otp-box">
<div class="otp-code">%s</div>
</div>
<div class="warning-box">
<p><strong>Security:</strong> Code expires in 15 minutes. Never share this code.</p>
</div>
<p>Enter this code in the login form to complete authentication.</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""";
return String.format(template, firstName, otpCode);
}

private String buildPasswordResetEmailTemplate(String firstName, String resetToken) {
String resetLink = frontendUrl + "/reset-password/" + resetToken;
String template = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.button { display: inline-block; background: #116530; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: 600; }
.link-text { word-break: break-all; color: #116530; font-size: 12px; background-color: #f9f9f9; padding: 10px; border-radius: 5px; margin: 15px 0; }
.warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 15px 0; }
.warning-box p { margin: 5px 0; color: #856404; font-size: 13px; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Password Reset</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<p>We received a password reset request for your account.</p>
<p>Click the button below to reset your password:</p>
<div style="text-align: center;">
<a href="%s" class="button">Reset Password</a>
</div>
<p style="font-size: 13px; color: #666;">Or copy this link: %s</p>
<div class="warning-box">
<p><strong>Security:</strong> Link expires in 1 hour. If you didn't request this, ignore this email.</p>
</div>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""";
return String.format(template, firstName, resetLink, resetLink);
}

/**
* Build withdrawal OTP email template
*/
private String buildWithdrawalOtpEmailTemplate(String firstName, String otpCode, String amount) {
String template = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.info-box { background: #e8f5e9; border-left: 4px solid #10b981; padding: 15px; margin: 15px 0; }
.info-box p { margin: 5px 0; color: #155724; font-size: 14px; }
.amount { font-size: 20px; font-weight: 700; color: #10b981; }
.otp-box { background: #f9f9f9; border: 2px solid #116530; padding: 20px; text-align: center; margin: 20px 0; border-radius: 6px; }
.otp-code { font-size: 32px; font-weight: bold; color: #116530; letter-spacing: 6px; font-family: monospace; }
.warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 12px; margin: 15px 0; }
.warning-box p { margin: 5px 0; color: #856404; font-size: 13px; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Withdrawal Verification</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<p>Verify your withdrawal with this code:</p>
<div class="info-box">
<p><strong>Amount:</strong> <span class="amount">RWF %s</span></p>
</div>
<div class="otp-box">
<div class="otp-code">%s</div>
</div>
<div class="warning-box">
<p><strong>Security:</strong> Code expires in 15 minutes. Never share this code.</p>
</div>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""";
return String.format(template, firstName, amount, otpCode);
}

/**
* Send HTML email (generic method)
* Prevents email clipping by adding proper headers and structure
*/
public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
try {
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {
helper.setFrom(fromEmail);
}

helper.setTo(toEmail);
helper.setSubject(subject);

String plainText = htmlContent
.replaceAll("<[^>]+>", "")
.replaceAll("&nbsp;", " ")
.replaceAll("&amp;", "&")
.replaceAll("&lt;", "<")
.replaceAll("&gt;", ">")
.replaceAll("&quot;", "\"")
.replaceAll("&#39;", "'")
.replaceAll("\\s+", " ")
.trim();

helper.setText(plainText, htmlContent);

message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("HTML email sent successfully to: {}", toEmail);
} catch (MessagingException e) {
log.error("Failed to send HTML email to: {}", toEmail, e);
throw new RuntimeException("Failed to send email", e);
}
}


public void sendHtmlEmailWithAttachment(String toEmail, String subject, String htmlContent,
byte[] pdfAttachment, String attachmentFileName) {
try {
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

try {
helper.setFrom(fromEmail, "Rangira Agro Farming");
} catch (UnsupportedEncodingException e) {
helper.setFrom(fromEmail);
}

helper.setTo(toEmail);
helper.setSubject(subject);


String plainText = htmlContent
.replaceAll("<[^>]+>", "")
.replaceAll("&nbsp;", " ")
.replaceAll("&amp;", "&")
.replaceAll("&lt;", "<")
.replaceAll("&gt;", ">")
.replaceAll("&quot;", "\"")
.replaceAll("&#39;", "'")
.replaceAll("\\s+", " ")
.trim();


helper.setText(plainText, htmlContent);


if (pdfAttachment != null && pdfAttachment.length > 0) {
helper.addAttachment(attachmentFileName,
new org.springframework.core.io.ByteArrayResource(pdfAttachment), "application/pdf");
log.info("Added PDF attachment: {} ({} bytes)", attachmentFileName, pdfAttachment.length);
}


message.setHeader("Precedence", "bulk");
message.setHeader("X-Auto-Response-Suppress", "All");
message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");
message.setHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click");
message.setHeader("X-Priority", "1");
message.setHeader("Importance", "high");

mailSender.send(message);
log.info("HTML email with PDF attachment sent successfully to: {}", toEmail);
} catch (MessagingException e) {
log.error("Failed to send HTML email with attachment to: {}", toEmail, e);
throw new RuntimeException("Failed to send email with attachment", e);
}
}


public void sendPurchaseConfirmationToFarmer(com.raf.entity.Transaction transaction) {
String farmerName = transaction.getSeller().getFirstName() + " " + transaction.getSeller().getLastName();
String buyerName = transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName();
String cropName = transaction.getInventory().getCropType().getCropName();

String htmlContent = String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.success-box { background-color: #d4edda; border-left: 4px solid #28a745; padding: 15px; margin: 15px 0; }
.success-box strong { color: #155724; font-size: 16px; }
.details { background-color: #f8f9fa; padding: 20px; border-radius: 6px; margin: 15px 0; border: 1px solid #e9ecef; }
.details h3 { color: #116530; margin-top: 0; margin-bottom: 15px; font-size: 18px; }
.details p { margin: 8px 0; color: #495057; font-size: 14px; }
.details strong { color: #212529; font-weight: 600; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Product Purchase Confirmation</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<div class="success-box">
<strong>Your product has been successfully purchased!</strong>
</div>
<p>Your product has been purchased by <strong>%s</strong>.</p>
<div class="details">
<h3>Transaction Details</h3>
<p><strong>Transaction Code:</strong> %s</p>
<p><strong>Product:</strong> %s</p>
<p><strong>Quantity:</strong> %s kg</p>
<p><strong>Unit Price:</strong> RWF %s</p>
<p><strong>Total Amount:</strong> RWF %s</p>
<p><strong>Commission (5%%):</strong> RWF %s</p>
<p><strong>Net Amount (credited to wallet):</strong> RWF %s</p>
</div>
<p><strong>Important:</strong> Amount credited to your wallet. You can withdraw it from your dashboard.</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""",
farmerName,
buyerName,
transaction.getTransactionCode(),
cropName,
transaction.getQuantityKg(),
transaction.getUnitPrice().toPlainString(),
transaction.getTotalAmount().toPlainString(),
transaction.getCommission().toPlainString(),
transaction.getNetAmount().toPlainString());

sendHtmlEmail(transaction.getSeller().getEmail(),
"Product Purchase Confirmation - " + transaction.getTransactionCode(),
htmlContent);
}


public void sendPurchaseConfirmationToBuyer(com.raf.entity.Transaction transaction) {
String buyerName = transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName();
String cropName = transaction.getInventory().getCropType().getCropName();
String farmerName = transaction.getSeller().getFirstName() + " " + transaction.getSeller().getLastName();

String htmlContent = String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 10px; overflow: hidden; }
.header { background: linear-gradient(135deg, #116530 0%%, #2ea359 100%%); color: #ffffff; padding: 30px 20px; text-align: center; }
.header h1 { margin: 0; font-size: 28px; font-weight: 700; text-transform: lowercase; letter-spacing: 2px; }
.header p { margin: 10px 0 0 0; font-size: 16px; opacity: 0.95; }
.content { padding: 40px 30px; color: #333333; }
.content h2 { color: #116530; margin-top: 0; font-size: 24px; font-weight: 600; }
.content p { color: #555555; font-size: 16px; margin: 15px 0; line-height: 1.8; }
.success-box { background-color: #d4edda; border-left: 5px solid #28a745; padding: 20px; margin: 25px 0; border-radius: 5px; }
.success-box strong { color: #155724; font-size: 18px; display: block; }
.details { background-color: #f8f9fa; padding: 25px; border-radius: 8px; margin: 25px 0; border: 1px solid #e9ecef; }
.details h3 { color: #116530; margin-top: 0; margin-bottom: 20px; font-size: 20px; font-weight: 600; }
.details p { margin: 12px 0; color: #495057; font-size: 15px; }
.details strong { color: #212529; font-weight: 600; min-width: 160px; display: inline-block; }
.footer { text-align: center; margin-top: 30px; padding-top: 25px; border-top: 2px solid #e9ecef; color: #6c757d; font-size: 13px; }
.footer p { margin: 5px 0; color: #6c757d; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Purchase Successful</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<div class="success-box">
<strong>Your purchase was successful. Please await delivery.</strong>
</div>
<p>Thank you for your purchase! Payment processed successfully.</p>
<div class="details">
<h3>Order Details</h3>
<p><strong>Transaction Code:</strong> %s</p>
<p><strong>Product:</strong> %s</p>
<p><strong>Quantity:</strong> %s kg</p>
<p><strong>Unit Price:</strong> RWF %s</p>
<p><strong>Total Amount Paid:</strong> RWF %s</p>
<p><strong>Farmer:</strong> %s</p>
</div>
<p><strong>Next Steps:</strong> Your order will be shipped to your registered address. You will receive a notification once shipment is initiated.</p>
<p>An invoice has been sent to this email. You can also view receipts from your dashboard.</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""",
buyerName,
transaction.getTransactionCode(),
cropName,
transaction.getQuantityKg(),
transaction.getUnitPrice().toPlainString(),
transaction.getTotalAmount().toPlainString(),
farmerName);

sendHtmlEmail(transaction.getBuyer().getEmail(),
"Purchase Successful - " + transaction.getTransactionCode(),
htmlContent);
}


public void sendShipmentNotificationToBuyer(com.raf.entity.Transaction transaction, byte[] clearancePdf) {
String buyerName = transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName();
String cropName = transaction.getInventory().getCropType().getCropName();

String htmlContent = String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 10px; overflow: hidden; }
.header { background: linear-gradient(135deg, #116530 0%%, #2ea359 100%%); color: #ffffff; padding: 30px 20px; text-align: center; }
.header h1 { margin: 0; font-size: 28px; font-weight: 700; text-transform: lowercase; letter-spacing: 2px; }
.header p { margin: 10px 0 0 0; font-size: 16px; opacity: 0.95; }
.content { padding: 40px 30px; color: #333333; }
.content h2 { color: #116530; margin-top: 0; font-size: 24px; font-weight: 600; }
.content p { color: #555555; font-size: 16px; margin: 15px 0; line-height: 1.8; }
.success-box { background-color: #d1ecf1; border-left: 5px solid #0dcaf0; padding: 20px; margin: 25px 0; border-radius: 5px; }
.success-box strong { color: #0c5460; font-size: 18px; display: block; }
.details { background-color: #f8f9fa; padding: 25px; border-radius: 8px; margin: 25px 0; border: 1px solid #e9ecef; }
.details h3 { color: #116530; margin-top: 0; margin-bottom: 20px; font-size: 20px; font-weight: 600; }
.details p { margin: 12px 0; color: #495057; font-size: 15px; }
.details strong { color: #212529; font-weight: 600; min-width: 160px; display: inline-block; }
.footer { text-align: center; margin-top: 30px; padding-top: 25px; border-top: 2px solid #e9ecef; color: #6c757d; font-size: 13px; }
.footer p { margin: 5px 0; color: #6c757d; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Order Shipped</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<div class="success-box">
<strong>Your order has been shipped and is on the way!</strong>
</div>
<p>Your order has been shipped to your registered address.</p>
<div class="details">
<h3>Order Details</h3>
<p><strong>Transaction Code:</strong> %s</p>
<p><strong>Product:</strong> %s</p>
<p><strong>Quantity:</strong> %s kg</p>
<p><strong>Total Amount:</strong> RWF %s</p>
</div>
<p><strong>Tracking:</strong> Track order status in your dashboard.</p>
<div class="warning-box">
<strong>IMPORTANT: Clearance Document Attached</strong>
<p>A Shipment Clearance Document (PDF) is attached. Print or save it and show it to the delivery driver when your shipment arrives.</p>
<p><strong>Clearance Code:</strong> CLR-%s<br>
<strong>Transaction Code:</strong> %s</p>
</div>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""",
buyerName,
transaction.getTransactionCode(),
cropName,
transaction.getQuantityKg(),
transaction.getTotalAmount().toPlainString(),
transaction.getTransactionCode(),
transaction.getTransactionCode());

String subject = "Order Shipped - " + transaction.getTransactionCode() + " (Clearance Document Attached)";
String attachmentFileName = "Clearance-" + transaction.getTransactionCode() + ".pdf";

if (clearancePdf != null && clearancePdf.length > 0) {
sendHtmlEmailWithAttachment(
transaction.getBuyer().getEmail(),
subject,
htmlContent,
clearancePdf,
attachmentFileName);
log.info("✅ Shipment notification email with PDF clearance sent to buyer: {}",
transaction.getBuyer().getEmail());
} else {

log.warn("⚠️  PDF clearance not generated, sending email without attachment");
sendHtmlEmail(transaction.getBuyer().getEmail(), subject, htmlContent);
}
}


public void sendDeliveryReminderToStorekeeper(
com.raf.entity.Transaction transaction,
com.raf.entity.User storekeeper,
com.raf.entity.StorageWarehouse warehouse) {
String storekeeperName = storekeeper.getFirstName() + " " + storekeeper.getLastName();
String buyerName = transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName();
String cropName = transaction.getInventory().getCropType().getCropName();


String buyerAddress = "Registered address";
try {
com.raf.entity.Location buyerLocation = transaction.getBuyer().getLocation();
if (buyerLocation != null) {

java.util.List<String> addressParts = new java.util.ArrayList<>();
com.raf.entity.Location current = buyerLocation;


if (current.getName() != null && !current.getName().trim().isEmpty()) {
addressParts.add(current.getName());
}


int depth = 0;
while (current != null && current.getParent() != null && depth < 5) {
current = current.getParent();
if (current.getName() != null && !current.getName().trim().isEmpty()) {
addressParts.add(current.getName());
}
depth++;
}


java.util.Collections.reverse(addressParts);

if (!addressParts.isEmpty()) {
buyerAddress = String.join(", ", addressParts);
}
}
} catch (Exception e) {
log.warn("Failed to build buyer address from location: {}", e.getMessage());
buyerAddress = "Registered address";
}

String htmlContent = String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border-radius: 10px; overflow: hidden; }
.header { background: linear-gradient(135deg, #116530 0%%, #2ea359 100%%); color: #ffffff; padding: 30px 20px; text-align: center; }
.header h1 { margin: 0; font-size: 28px; font-weight: 700; text-transform: lowercase; letter-spacing: 2px; }
.header p { margin: 10px 0 0 0; font-size: 16px; opacity: 0.95; }
.content { padding: 40px 30px; color: #333333; }
.content h2 { color: #116530; margin-top: 0; font-size: 24px; font-weight: 600; }
.content p { color: #555555; font-size: 16px; margin: 15px 0; line-height: 1.8; }
.action-box { background-color: #fff3cd; border-left: 5px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 5px; }
.action-box strong { color: #856404; font-size: 18px; display: block; margin-bottom: 10px; }
.details { background-color: #f8f9fa; padding: 25px; border-radius: 8px; margin: 25px 0; border: 1px solid #e9ecef; }
.details h3 { color: #116530; margin-top: 0; margin-bottom: 20px; font-size: 20px; font-weight: 600; }
.details p { margin: 12px 0; color: #495057; font-size: 15px; }
.details strong { color: #212529; font-weight: 600; min-width: 160px; display: inline-block; }
.button { display: inline-block; padding: 12px 30px; background-color: #116530; color: #ffffff; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: 600; }
.footer { text-align: center; margin-top: 30px; padding-top: 25px; border-top: 2px solid #e9ecef; color: #6c757d; font-size: 13px; }
.footer p { margin: 5px 0; color: #6c757d; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Delivery Reminder</p>
</div>
<div class="content">
<h2>Hello %s!</h2>
<div class="action-box">
<strong>ACTION REQUIRED: Process Delivery</strong>
<p>A new order has been paid and requires immediate shipment processing.</p>
</div>
<p>Please process the delivery for the following order:</p>
<div class="details">
<h3>Order Details</h3>
<p><strong>Transaction Code:</strong> %s</p>
<p><strong>Product:</strong> %s</p>
<p><strong>Quantity:</strong> %s kg</p>
<p><strong>Buyer:</strong> %s</p>
<p><strong>Delivery Address:</strong> %s</p>
<p><strong>Warehouse:</strong> %s</p>
<p><strong>Total Amount:</strong> RWF %s</p>
</div>
<p><strong>Next Steps:</strong></p>
<ol>
<li>Confirm item pickup from warehouse</li>
<li>Initiate shipment to buyer's address</li>
<li>Update shipment status in the system</li>
</ol>
<p style="text-align: center;">
<a href="%s/shipments" class="button">View Shipment Page</a>
</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""",
storekeeperName,
transaction.getTransactionCode(),
cropName,
transaction.getQuantityKg(),
buyerName,
buyerAddress,
warehouse.getWarehouseName(),
transaction.getTotalAmount().toPlainString(),
frontendUrl);

sendHtmlEmail(storekeeper.getEmail(),
"Delivery Reminder - Process Shipment for " + transaction.getTransactionCode(),
htmlContent);
log.info("✅ Delivery reminder email sent to storekeeper: {}", storekeeper.getEmail());
}


public void sendNewsletterSubscriptionConfirmation(String email) {
String htmlContent = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.success-box { background-color: #d4edda; border-left: 4px solid #28a745; padding: 15px; margin: 15px 0; }
.success-box strong { color: #155724; font-size: 16px; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>Newsletter Subscription</p>
</div>
<div class="content">
<h2>Welcome to Our Newsletter!</h2>
<div class="success-box">
<strong>You have successfully subscribed to our newsletter!</strong>
</div>
<p>Thank you for subscribing to Rangira Agro Farming newsletter.</p>
<p>You will now receive:</p>
<ul>
<li>Latest agricultural insights and market trends</li>
<li>Platform updates and new features</li>
<li>Tips for farmers and buyers</li>
<li>Special offers and promotions</li>
</ul>
<p>We promise to keep you informed with valuable content.</p>
</div>
<div class="footer">
<p>Automated email. Do not reply.</p>
</div>
</div>
</body>
</html>
""";

sendHtmlEmail(email, "Welcome to Rangira Agro Farming Newsletter!", htmlContent);
log.info("✅ Newsletter subscription confirmation email sent to: {}", email);
}


public void sendContactFormEmail(String name, String email, String subject, String message) {
String htmlContent = String.format(
"""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style>
body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f5f5f5; }
.container { max-width: 600px; margin: 0 auto; background: #ffffff; padding: 0; border-radius: 8px; overflow: hidden; }
.header { background: #116530; color: #ffffff; padding: 20px; text-align: center; }
.header h1 { margin: 0; font-size: 24px; font-weight: 700; }
.content { padding: 30px; }
.content h2 { color: #116530; margin-top: 0; font-size: 20px; }
.content p { color: #555; font-size: 14px; margin: 12px 0; }
.info-box { background-color: #f8f9fa; padding: 15px; margin: 15px 0; border-left: 4px solid #116530; }
.info-box p { margin: 5px 0; color: #495057; }
.info-box strong { color: #116530; }
.message-box { background-color: #f8f9fa; padding: 15px; margin: 15px 0; border: 1px solid #e9ecef; }
.message-box p { margin: 0; color: #495057; white-space: pre-wrap; }
.footer { text-align: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e9ecef; color: #6c757d; font-size: 12px; }
</style>
</head>
<body>
<div class="container">
<div class="header">
<h1>Rangira Agro Farming</h1>
<p>New Contact Form Submission</p>
</div>
<div class="content">
<h2>New Contact Form Message</h2>
<div class="info-box">
<p><strong>From:</strong> %s</p>
<p><strong>Email:</strong> %s</p>
<p><strong>Subject:</strong> %s</p>
</div>
<div class="message-box">
<p><strong>Message:</strong></p>
<p>%s</p>
</div>
<p>Please respond to this inquiry at your earliest convenience.</p>
</div>
<div class="footer">
<p>Automated email from contact form.</p>
</div>
</div>
</body>
</html>
""",
name, email, subject, message);


sendHtmlEmail(fromEmail, "New Contact Form Submission: " + subject, htmlContent);
log.info("✅ Contact form email sent to system email: {}", fromEmail);
}
}
