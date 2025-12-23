package com.raf.service;

import com.raf.entity.NewsletterSubscription;
import com.raf.exception.DuplicateResourceException;
import com.raf.repository.NewsletterSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {

private final NewsletterSubscriptionRepository newsletterRepository;
private final EmailService emailService;

@Transactional
public NewsletterSubscription subscribe(String email) {

if (newsletterRepository.existsByEmailAndIsActiveTrue(email)) {
throw new DuplicateResourceException("Email is already subscribed to the newsletter");
}


NewsletterSubscription existing = newsletterRepository.findByEmail(email).orElse(null);
if (existing != null) {
existing.setIsActive(true);
existing.setUnsubscribedAt(null);
existing = newsletterRepository.save(existing);
log.info("Resubscribed email to newsletter: {}", email);
} else {

existing = NewsletterSubscription.builder()
.email(email)
.isActive(true)
.build();
existing = newsletterRepository.save(existing);
log.info("New newsletter subscription: {}", email);
}


try {
emailService.sendNewsletterSubscriptionConfirmation(email);
log.info("Newsletter subscription confirmation email sent to: {}", email);
} catch (Exception e) {
log.error("Failed to send newsletter subscription confirmation email to: {}", email, e);

}

return existing;
}

@Transactional
public void unsubscribe(String email) {
NewsletterSubscription subscription = newsletterRepository.findByEmail(email)
.orElseThrow(() -> new RuntimeException("Email not found in newsletter subscriptions"));

subscription.setIsActive(false);
subscription.setUnsubscribedAt(java.time.LocalDateTime.now());
newsletterRepository.save(subscription);

log.info("Unsubscribed email from newsletter: {}", email);
}
}

