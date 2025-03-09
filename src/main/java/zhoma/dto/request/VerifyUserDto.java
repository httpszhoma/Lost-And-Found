package zhoma.dto.request;

public record VerifyUserDto(
         String email,
         String verificationCode
) {
}
