package com.especial_topics_1.restaurant.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailtRemetente;

    public void sendVerificationCode(String to, String code) {



        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailtRemetente);
        message.setTo(to);
        message.setSubject("Olá! Seu código de Verificação - Restaurante");
        message.setText("Olá! Seu código de verificação é: " + code +
                "\nEle expira em 5 minutos.");
        mailSender.send(message);
    }


}
