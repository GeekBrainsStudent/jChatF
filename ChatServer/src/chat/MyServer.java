package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;
import chat.handler.ClientHandler;
import clientserver.Command;
import clientserver.CommandType;
import clientserver.commands.MessageInfoCommandData;
import clientserver.commands.PublicMessageCommandData;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();
    // Объявляем объект Logger
    private final Logger logger;

    public MyServer(int port, Logger logger) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
//        получаем logger
        this.logger = logger;
    }


    public void start() throws IOException {
        authService.start();
//        Записываем в лог событие запуска
        logger.info("Сервер запущен!");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            logger.error("Ошибка создания нового подключения");
        } finally {
            authService.close();
            serverSocket.close();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
//        Записываем в лог, что сервер перешел в режим ожидания
        logger.info("Ожидание пользователя...");
        Socket clientSocket = serverSocket.accept();
//        клиент подключился
        logger.info("Клиент подключился!");
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isUsernameBusy(String clientUsername) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(clientUsername)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
//        Добавляем в лог событие присоединения клиента к чату
        logger.info(clientHandler.getUsername() + " присоединился к чату");
    }

    private List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
//        Добавляем в лог событие выхода клиента из чата
        logger.info(clientHandler.getUsername() + " покинул чат");
    }

    public synchronized void broadcastMessage(ClientHandler sender, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(command);
        }
//        Запись в лог, если клиент отправил общее сообщение
        if(command.getType() == CommandType.INFO_MESSAGE && ((MessageInfoCommandData) command.getData()).getSender() != null) {
            logger.info(sender.getUsername() + " отправил общее сообщение: " +
                    ((MessageInfoCommandData) command.getData()).getMessage());
        }
    }

    public synchronized void sendPrivateMessage(String recipient, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
                break;
            }
        }
//        Запись в лог, если клиент отправил приватное сообщение
        logger.info(((MessageInfoCommandData) command.getData()).getSender() + " отправил сообщение " + recipient + ": " +
                    ((MessageInfoCommandData) command.getData()).getMessage());
    }
}