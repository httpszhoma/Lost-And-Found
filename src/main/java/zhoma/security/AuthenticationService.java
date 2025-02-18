package zhoma.security;


import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import zhoma.dto.request.RegisterUserDto;
import zhoma.email.EmailService;
import zhoma.exceptions.EmailAlreadyExist;
import zhoma.exceptions.EmailNotFoundException;
import zhoma.exceptions.UsernameAlreadyExist;
import zhoma.models.User;
import zhoma.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

public User signup(RegisterUserDto input){
    if (userRepository.findByEmail(input.email()).isPresent()) {
        throw new EmailAlreadyExist("This email already exists: " + input.email() + ". Try another email!");
    }
    if(userRepository.findByUsername(input.username()).isPresent()){
        throw new UsernameAlreadyExist("This username already exists: " + input.username() + ". Try another username!");
    }

    zhoma.models.User user = new zhoma.models.User(input.firstname(),input.lastname(),input.username(), input.email(), passwordEncoder.encode(input.password()));
    user.setVerificationCode(generateVerificationCode());
    user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
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
            throw new EmailNotFoundException("This email = "+ user.getEmail()+ "  doesn't exist !" );
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        return String.valueOf(code);
    }


}
