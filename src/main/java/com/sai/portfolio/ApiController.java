package com.sai.portfolio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    record ReviewIn(@NotBlank @Size(max = 60) String name, @NotBlank @Email @Size(max = 120) String email,
                    @Min(1) @Max(5) int rating, @NotBlank @Size(max = 1000) String comment, String website) {}
    record ReviewOut(String name, int rating, String comment, Instant createdAt) {}
    record ContactIn(@NotBlank @Size(max = 60) String name, @NotBlank @Email @Size(max = 120) String email,
                     @NotBlank @Size(max = 2000) String message, String website) {}

    private final ReviewRepository reviews;
    private final ContactMessageRepository contacts;
    private final JavaMailSender mail;
    private final String owner;
    private final String from;

    public ApiController(ReviewRepository reviews, ContactMessageRepository contacts, JavaMailSender mail,
                         @Value("${app.owner-email:}") String owner, @Value("${spring.mail.username:}") String from) {
        this.reviews = reviews; this.contacts = contacts; this.mail = mail; this.owner = owner; this.from = from;
    }

    @GetMapping("/reviews")
    public List<ReviewOut> list() {
        // email is stored but never returned
        return reviews.findAllByOrderByCreatedAtDesc().stream()
                .map(r -> new ReviewOut(r.name, r.rating, r.text, r.createdAt)).toList();
    }

    @PostMapping("/reviews")
    public ResponseEntity<Void> addReview(@Valid @RequestBody ReviewIn in) {
        if (in.website() != null && !in.website().isBlank()) return ResponseEntity.ok().build(); // honeypot
        Review r = new Review();
        r.name = in.name().trim(); r.email = in.email().trim(); r.rating = in.rating(); r.text = in.comment().trim();
        reviews.save(r);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/contact")
    public ResponseEntity<Void> contact(@Valid @RequestBody ContactIn in) {
        if (in.website() != null && !in.website().isBlank()) return ResponseEntity.ok().build(); // honeypot
        ContactMessage m = new ContactMessage();
        m.name = in.name().trim(); m.email = in.email().trim(); m.message = in.message().trim();
        contacts.save(m); // always saved, even if email fails
        if (!owner.isBlank()) {
            try {
                SimpleMailMessage mm = new SimpleMailMessage();
                if (!from.isBlank()) mm.setFrom(from);
                mm.setTo(owner);
                mm.setReplyTo(m.email);
                mm.setSubject("Portfolio message from " + m.name);
                mm.setText(m.message + "\n\nFrom: " + m.name + " <" + m.email + ">");
                mail.send(mm);
            } catch (Exception e) {
                log.warn("Could not send email, message {} is saved in the database", m.id, e);
            }
        }
        return ResponseEntity.status(201).build();
    }
}
