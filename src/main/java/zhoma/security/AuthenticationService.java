package zhoma.security;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zhoma.dto.request.LoginUserDto;
import zhoma.dto.request.RegisterUserDto;
import zhoma.dto.request.VerifyUserDto;
import zhoma.models.Role;
import zhoma.services.EmailService;
import zhoma.exceptions.*;
import zhoma.models.User;
import zhoma.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public User signup(RegisterUserDto input) {
        if (userRepository.findByEmail(input.email()).isPresent()) {
            throw new EmailAlreadyExist("This email already exists: " + input.email() + ". Try another email!");
        }
        if (userRepository.findByUsername(input.username()).isPresent()) {
            throw new UsernameAlreadyExist("This username already exists: " + input.username() + ". Try another username!");
        }

        zhoma.models.User user = new zhoma.models.User(input.firstname(), input.lastname(), input.username(), input.email(), passwordEncoder.encode(input.password()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    private void sendVerificationEmail(zhoma.models.User user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new EmailNotFoundException("This email = " + user.getEmail() + "  doesn't exist !");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(() -> new EmailNotFoundException("This email = " + input.email() + " doesn't exists !!!"));

        if (!user.isEnabled()) {
            throw new UserNotVerifiedException();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        return user;

    }
    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.email());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new VerificationCodeExpiredException(input.email());
            }
            if (user.getVerificationCode().equals(input.verificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new EmailNotFoundException(input.email());
        }
    }


    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new EmailNotFoundException(email);
        }
    }

    public void sendForgotPasswordCode(String email){
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new EmailNotFoundException("This email already exists: " + email + ". Try another email!")

            );
                user.setVerificationCode(generateVerificationCode());
                user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
                sendVerificationEmail(user);
                userRepository.save(user);



    }
    public void verifyForgotPasswordCode(String email,String verificationCode){
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EmailNotFoundException("This email already exists: " + email + ". Try another email!")

        );
        if (user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(verificationCode) ||
                user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired verification code.");
        }

        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

    }
    public void resetPassword(String email, String newPassword){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email not found: " + email));

        if (user.getVerificationCode() != null) {
            throw new RuntimeException("Verification code not verified.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }


}
