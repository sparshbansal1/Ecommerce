package org.genc.ecommerce.service;

import org.genc.ecommerce.model.Payment;
import org.genc.ecommerce.model.PaymentStatus;
import org.genc.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    public Payment processPayment(Payment payment) {

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDate.now());

        Payment savedPayment = paymentRepository.save(payment);

        // 3. Trigger SMTP Email
        if(payment.getCustomerEmail() != null && !payment.getCustomerEmail().isEmpty()) {
            emailService.sendPaymentConfirmation(
                    payment.getCustomerEmail(),
                    payment.getOrderId(),
                    payment.getAmount().toString()
            );
        }

        return savedPayment;
    }
}