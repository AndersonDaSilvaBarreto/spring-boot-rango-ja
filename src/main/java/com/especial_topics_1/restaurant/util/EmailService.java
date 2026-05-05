package com.especial_topics_1.restaurant.util;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ResourceLoader resourceLoader;

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${EMAIL_REMETENTE}")
    private String emailtRemetente;

    @Async
    public void sendVerificationCode(String to, String code) {

        try{

            Resource resource = resourceLoader.getResource
                    ("classpath:templates/email-verification.html");

            String htmlTemplate = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            String finalHtml = htmlTemplate.replace("{{CODE}}", code);

            Resend resend = new Resend(resendApiKey);


            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("RangoJa <" + emailtRemetente + ">")
                    .to(to)
                    .subject("Seu código de acesso - RangoJá")
                    .html(finalHtml)
                    .build();

            resend.emails().send(params);
        } catch (Exception e ) {
            System.err.println("Erro ao processar ou enviar e-mail: " + e.getMessage());
            e.printStackTrace();
        }



    }


}
