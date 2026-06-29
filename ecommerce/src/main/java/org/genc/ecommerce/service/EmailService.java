package org.genc.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    // inject dependency if it is available otherwise don't throw an error.
    @Autowired(required = false)
    // JavaMailSender -> Bean
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@ecommerce.com}")
    private String fromEmail;

    public void sendPaymentConfirmation(String toEmail, Long orderId, String amount) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Skipping email for Order ID: " + orderId);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(" Payment Success - ShopHub E-Commerce");
            message.setText("Dear Valued Customer,\n\n" +
                    "Thank you for your purchase!\n\n" +
                    "============================================\n" +
                    "ORDER CONFIRMATION\n" +
                    "============================================\n" +
                    "Order ID: #" + orderId + "\n" +
                    "Amount: ₹" + amount + "\n" +
                    "Payment Status: COMPLETED ✓\n" +
                    "============================================\n\n" +
                    "Your order is being prepared for shipment. We'll send you a shipping confirmation once it's on the way.\n\n" +
                    "Need help? Contact our support team at support@shophub.com\n\n" +
                    "Best regards,\n" +
                    "ShopHub Team\n");

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail);
            e.printStackTrace();
        }
    }
}