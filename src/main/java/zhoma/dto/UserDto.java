package zhoma.dto;


import java.util.List;

public record UserDto(Long id,
                      String username,
                      String firstname,
                      String lastname,
                      String email,
                      String imageUrl,
                      String roles ) {
}
