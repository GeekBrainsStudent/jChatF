import chat.MyServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) {

        // Инициализируем logger из библиотеки log4j2
        Logger logger = (Logger) LogManager.getLogger();

        int port = DEFAULT_PORT;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        try {
            // передаем logger объекту MyServer
            new MyServer(port, logger).start();
        } catch (IOException e) {
            // Во всех ошибках, заменяем sout на logger.error()
            logger.error("Ошибка. " + e);
            System.exit(1);
        }
    }
}