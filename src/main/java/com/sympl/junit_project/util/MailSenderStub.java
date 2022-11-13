package com.sympl.junit_project.util;


import org.springframework.stereotype.Component;

@Component
public class MailSenderStub implements MailSender{
    @Override
    public boolean send() {
        return true;
    }
}
