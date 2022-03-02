package pojo;

public class UserCredentials {
    public String email;
    public String password;

    public UserCredentials (String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCredentials getUserCredentials(User user) {
        return new UserCredentials(user.email, user.password);
    }
}