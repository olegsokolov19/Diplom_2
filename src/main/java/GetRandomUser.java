import org.apache.commons.lang3.RandomStringUtils;

public class GetRandomUser {

    public static User getRandomUser() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(14);
        String name = RandomStringUtils.randomAlphabetic(7);

        return new User(email, password, name);

    }
}
