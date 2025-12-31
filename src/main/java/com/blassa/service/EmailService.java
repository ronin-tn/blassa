package com.blassa.service;

import com.blassa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

        private final JavaMailSender mailSender;
        private final UserRepository userRepository;

        @Value("${spring.mail.username}")
        private String fromEmail;

        @Value("${app.base-url:http://localhost:8088}")
        private String baseUrl;

        @Value("${app.frontend-url:http://localhost:3000}")
        private String frontendUrl;

        public void sendVerificationEmail(String toEmail, String token) {
                String verifyUrl = baseUrl + "/verify/email?token=" + token;
                String subject = "Bienvenue sur Blassa - Vérifiez votre email";
                String htmlContent = buildEmailTemplate(
                                "Bienvenue sur Blassa !",
                                "Merci de vous être inscrit sur Blassa, la plateforme de covoiturage en Tunisie.",
                                "Pour activer votre compte et commencer à voyager, veuillez vérifier votre adresse email en cliquant sur le bouton ci-dessous.",
                                "Vérifier mon email",
                                verifyUrl,
                                "Ce lien expire dans 24 heures. Si vous n'avez pas créé de compte sur Blassa, vous pouvez ignorer cet email.");
                sendHtmlEmail(toEmail, subject, htmlContent);
        }

        public void sendForgotPasswordEmail(String toEmail) {
                var user = userRepository.findByEmail(toEmail)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Asna3 reset token u sajlou
                String token = java.util.UUID.randomUUID().toString();
                user.setResetToken(token);
                userRepository.save(user);

                String resetUrl = frontendUrl + "/reset-password?token=" + token;
                String subject = "Blassa - Réinitialisation de votre mot de passe";
                String htmlContent = buildEmailTemplate(
                                "Réinitialisation du mot de passe",
                                "Vous avez demandé la réinitialisation de votre mot de passe.",
                                "Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe. Si vous n'avez pas fait cette demande, ignorez simplement cet email.",
                                "Réinitialiser mon mot de passe",
                                resetUrl,
                                "Ce lien expire dans 1 heure. Pour des raisons de sécurité, ne partagez jamais ce lien.");
                sendHtmlEmail(toEmail, subject, htmlContent);
        }

        public void sendNewPassengerEmail(String driverEmail, String passengerName, String rideDetails) {
                String dashboardUrl = frontendUrl + "/dashboard/rides";
                String subject = "Blassa - Nouvelle demande de réservation";
                String htmlContent = buildEmailTemplate(
                                "Nouvelle demande de réservation !",
                                passengerName + " souhaite rejoindre votre trajet.",
                                "Détails du trajet : " + rideDetails
                                                + ". Connectez-vous à votre tableau de bord pour accepter ou refuser cette demande.",
                                "Voir la demande",
                                dashboardUrl,
                                "Vous recevez cet email car vous avez publié un trajet sur Blassa.");
                sendHtmlEmail(driverEmail, subject, htmlContent);
        }

        public void sendBookingAcceptedEmail(String passengerEmail, String rideDetails, String rideId) {
                String rideUrl = frontendUrl + "/rides/" + rideId;
                String subject = "Blassa - Réservation confirmée !";
                String htmlContent = buildEmailTemplate(
                                "Réservation acceptée !",
                                "Bonne nouvelle ! Votre réservation a été confirmée.",
                                "Le conducteur a accepté votre demande pour le trajet " + rideDetails
                                                + ". Vous recevrez les coordonnées du conducteur 30 minutes avant le départ.",
                                "Voir mon trajet",
                                rideUrl,
                                "Soyez à l'heure et bon voyage !");
                sendHtmlEmail(passengerEmail, subject, htmlContent);
        }

        public void sendBookingRejectedEmail(String passengerEmail, String rideDetails) {
                String searchUrl = frontendUrl + "/search";
                String subject = "Blassa - Réservation refusée";
                String htmlContent = buildEmailTemplate(
                                "Réservation refusée",
                                "Malheureusement, le conducteur a refusé votre demande de réservation.",
                                "Votre demande pour le trajet " + rideDetails
                                                + " a été refusée. Ne vous inquiétez pas, vous pouvez rechercher d'autres trajets disponibles.",
                                "Rechercher un trajet",
                                searchUrl,
                                "Nous espérons que vous trouverez un autre trajet qui vous convient.");
                sendHtmlEmail(passengerEmail, subject, htmlContent);
        }

        public void sendBookingCancelledByPassengerEmail(String driverEmail, String passengerName, String rideDetails,
                        String rideId) {
                String rideUrl = frontendUrl + "/rides/" + rideId;
                String subject = "Blassa - Réservation annulée";
                String htmlContent = buildEmailTemplate(
                                "Réservation annulée",
                                passengerName + " a annulé sa réservation.",
                                "La réservation pour le trajet " + rideDetails
                                                + " a été annulée. Les places sont à nouveau disponibles pour d'autres passagers.",
                                "Voir mon trajet",
                                rideUrl,
                                "Vous recevez cet email car vous êtes le conducteur de ce trajet.");
                sendHtmlEmail(driverEmail, subject, htmlContent);
        }

        public void sendRideCancelledEmail(String passengerEmail, String rideDetails) {
                String searchUrl = frontendUrl + "/search";
                String subject = "Blassa - Trajet annulé";
                String htmlContent = buildEmailTemplate(
                                "Trajet annulé !",
                                "Le conducteur a annulé le trajet auquel vous étiez inscrit.",
                                "Malheureusement, le trajet " + rideDetails
                                                + " a été annulé par le conducteur. Nous vous invitons à rechercher un autre trajet.",
                                "Rechercher un trajet",
                                searchUrl,
                                "Nous sommes désolés pour ce désagrément.");
                sendHtmlEmail(passengerEmail, subject, htmlContent);
        }

        public void sendRideStartedEmail(String passengerEmail, String rideDetails, String driverName,
                        String driverPhone) {
                String subject = "Blassa - Trajet commencé";
                String htmlContent = buildEmailTemplate(
                                "Votre trajet a commencé !",
                                "Le conducteur " + driverName + " a démarré le trajet " + rideDetails + ".",
                                "Soyez prêt ! Contact du conducteur : " + driverPhone
                                                + ". Bon voyage et respectez les règles de covoiturage.",
                                "Contacter le conducteur",
                                "tel:" + driverPhone,
                                "Profitez bien de votre trajet !");
                sendHtmlEmail(passengerEmail, subject, htmlContent);
        }

        public void sendRideCompletedEmail(String passengerEmail, String rideDetails, String rideId) {
                String reviewUrl = frontendUrl + "/dashboard/reviews";
                String subject = "Blassa - Trajet terminé";
                String htmlContent = buildEmailTemplate(
                                "Trajet terminé !",
                                "Merci d'avoir voyagé avec Blassa !",
                                "Le trajet " + rideDetails
                                                + " est maintenant terminé. Nous espérons que vous avez passé un bon voyage. N'hésitez pas à laisser un avis sur votre expérience.",
                                "Laisser un avis",
                                reviewUrl,
                                "Votre avis nous aide à améliorer la qualité du service.");
                sendHtmlEmail(passengerEmail, subject, htmlContent);
        }

        private void sendHtmlEmail(String to, String subject, String htmlContent) {
                try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                        helper.setFrom(fromEmail);
                        helper.setTo(to);
                        helper.setSubject(subject);
                        helper.setText(htmlContent, true);
                        mailSender.send(message);
                } catch (MessagingException e) {
                        throw new RuntimeException("Failed to send email", e);
                }
        }

        private String buildEmailTemplate(String title, String greeting, String message, String buttonText,
                        String buttonUrl, String footer) {
                String html = "<!DOCTYPE html><html lang=\"fr\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>"
                                + title + "</title></head>";
                html += "<body style=\"margin: 0; padding: 0; font-family: Segoe UI, Tahoma, Geneva, Verdana, sans-serif; background-color: #f8fafc;\">";
                html += "<table role=\"presentation\" style=\"width: 100%; max-width: 600px; margin: 0 auto; padding: 40px 20px;\"><tr><td>";
                html += "<table role=\"presentation\" style=\"width: 100%; margin-bottom: 32px;\"><tr><td style=\"text-align: center;\">";
                html += "<img src=\"https://res.cloudinary.com/dmxkyk3xc/image/upload/v1765520634/LOGO_dlklvo.png\" alt=\"Blassa\" width=\"80\" height=\"80\" style=\"display: block; margin: 0 auto;\">";
                html += "</td></tr></table>";
                html += "<table role=\"presentation\" style=\"width: 100%; background-color: white; border-radius: 20px; box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06); overflow: hidden;\">";
                html += "<tr><td style=\"height: 4px; background: linear-gradient(90deg, #0A8F8F 0%, #FF9A3E 100%);\"></td></tr>";
                html += "<tr><td style=\"padding: 40px 32px;\">";
                html += "<h1 style=\"margin: 0 0 16px 0; font-size: 24px; font-weight: 700; color: #1e293b;\">" + title
                                + "</h1>";
                html += "<p style=\"margin: 0 0 12px 0; font-size: 16px; line-height: 1.6; color: #64748b;\">"
                                + greeting
                                + "</p>";
                html += "<p style=\"margin: 0 0 32px 0; font-size: 16px; line-height: 1.6; color: #64748b;\">" + message
                                + "</p>";
                html += "<table role=\"presentation\" style=\"width: 100%;\"><tr><td style=\"text-align: center;\">";
                html += "<a href=\"" + buttonUrl
                                + "\" style=\"display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #0A8F8F 0%, #006B8F 100%); color: white; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 12px; box-shadow: 0 4px 12px rgba(10, 143, 143, 0.3);\">"
                                + buttonText + "</a>";
                html += "</td></tr></table>";
                html += "<p style=\"margin: 24px 0 0 0; font-size: 13px; color: #94a3b8; text-align: center;\">Si le bouton ne fonctionne pas, copiez ce lien dans votre navigateur :<br>";
                html += "<a href=\"" + buttonUrl + "\" style=\"color: #0A8F8F; word-break: break-all;\">" + buttonUrl
                                + "</a></p>";
                html += "</td></tr>";
                html += "<tr><td style=\"padding: 24px 32px; background-color: #f8fafc; border-top: 1px solid #e2e8f0;\">";
                html += "<p style=\"margin: 0; font-size: 13px; color: #64748b; text-align: center;\">" + footer
                                + "</p>";
                html += "</td></tr></table>";
                html += "<table role=\"presentation\" style=\"width: 100%; margin-top: 32px;\"><tr><td style=\"text-align: center;\">";
                html += "<p style=\"margin: 0 0 8px 0; font-size: 14px; color: #64748b;\">Voyagez ensemble avec Blassa</p>";
                html += "<p style=\"margin: 0; font-size: 12px; color: #94a3b8;\">© 2025 Blassa. Tous droits reserves.</p>";
                html += "</td></tr></table></td></tr></table></body></html>";
                return html;
        }
}
