package data;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class UserDataGenerator {
    static Faker faker = new Faker(new Locale("en_EN"));

    public static String generateEmail() {
        return faker.internet().emailAddress();
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String generateName() {
        return faker.name().firstName();
    }
}