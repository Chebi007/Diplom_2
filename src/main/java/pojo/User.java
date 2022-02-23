package pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import static data.UserDataGenerator.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    public String email;
    public String password;
    public String name;

    public User (String email) {
        this.email = email;
    }

    public User (String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User (String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandom() {
        return new User(generateEmail(), generatePassword(), generateName());
    }

    public static User getRandomWithEmail() {
        return new User(generateEmail());
    }
}