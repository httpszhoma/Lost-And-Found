package zhoma.exceptions;

public class UsernameAlreadyExist extends RuntimeException {

    public UsernameAlreadyExist(String message) {
        super(message);
    }
}
