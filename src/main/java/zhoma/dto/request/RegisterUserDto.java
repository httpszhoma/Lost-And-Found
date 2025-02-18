package zhoma.dto.request;


import lombok.Data;

@Data
public record RegisterUserDto (
 String email,
 String username,
 String firstname,
 String lastname,
 String password

){}

