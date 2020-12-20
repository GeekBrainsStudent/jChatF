package chat.auth;

import chat.User;
import chat.database.DataBase;

import java.util.List;

public class BaseAuthService implements AuthService {

    /*private static final List<User> clients = List.of(
            new User("user1", "1111", "Борис_Николаевич"),
            new User("user2", "2222", "Мартин_Некотов"),
            new User("user3", "3333", "Гендальф_Серый")
    );*/

    private DataBase db;

    @Override
    public void start() {
        db = new DataBase();
        if(db.connect()) {
            System.out.println("Ошибка соединения с базой данных");
            return;
        }
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        return db.isValidUser(login, password);
    }

    @Override
    public void close() {
        db.disconnect();
        System.out.println("Сервис аутентификации завершен");

    }
}
