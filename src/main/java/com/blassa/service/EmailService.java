package com.blassa.service;

import com.blassa.model.entity.User;
import com.blassa.repository.UserRepository;
import com.blassa.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JwtUtils jwtUtils;
    private final JavaMailSender mailSender;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = baseUrl + "/verify/email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Blassa - Verification Email");
        message.setText("Click to verify: " + verifyUrl);
        mailSender.send(message);
    }

    @Transactional
    public void sendForgotPasswordEmail(String to) {
        User user = userRepository.findByEmail(to).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtUtils.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        user.setVerificationToken(token);
        userRepository.save(user);
        String verifyUrl = baseUrl + "/reset/email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Blassa - Forgot Password");
        message.setText("Click to reset password: " + verifyUrl);
        mailSender.send(message);
    }

    @Transactional
    public void sendNewPassengerEmail(String driverEmail, String passengerEmail) {
        User driver = userRepository.findByEmail(driverEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User passenger= userRepository.findByEmail(passengerEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String driverName=driver.getFirstName()+" "+driver.getLastName();
        String passengerName=passenger.getFirstName()+" "+passenger.getLastName();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(driverEmail);
        message.setSubject("Blassa - New Passenger");
        message.setText("You have a new Passenger: " + passengerName);
        mailSender.send(message);
    }
}
