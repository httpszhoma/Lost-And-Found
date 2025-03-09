package zhoma.dto.request;



public record RegisterUserDto(
        String email,
        String username,
        String firstname,
        String lastname,
        String password

) {

}

