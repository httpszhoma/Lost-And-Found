package zhoma.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        long expiresIn

        ) {
}
