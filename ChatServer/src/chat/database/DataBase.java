package chat.database;

import java.sql.*;

import chat.User;
import chat.database.SchemeDB;
import chat.database.SchemeDB.UsersTable;

public class DataBase {

    public final String JDBC_CLASS = "org.sqlite.JDBC";
    public final String DRIVER_PATH = "jdbc:sqlite:ChatServer/src/chat/database/main.db";

    private Connection connect;

    public boolean connect() {
        try {
            Class.forName(JDBC_CLASS);
            connect = DriverManager.getConnection(DRIVER_PATH);
            return true;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Ошибка соединения с базой");
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        if(connect != null)
            try {
                connect.close();
                System.out.println("База данных закрыта");
            } catch (SQLException e) {
                System.out.println("Ошибка закрытия базы");
                e.printStackTrace();
            }
    }

    public String isValidUser(String login, String pass) {
        String query = "SELECT * FROM " + UsersTable.NAME +
                " WHERE " + UsersTable.Cols.LOGIN + " = '" + login + "' AND " + UsersTable.Cols.PASS + " = '" + pass + "'";

        try(Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            if(rs.next()) {
                return rs.getString(UsersTable.Cols.NAME);
            }
            return null;

        } catch(NullPointerException | SQLException e) {
            System.out.println("Ошибка запроса");
            e.printStackTrace();
            return null;
        }
    }
}
