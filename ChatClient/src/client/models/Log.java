package client.models;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Log {

    private final String PATH = "ChatClient/src/client/models/log";
    private File file;

    public Log() {
        file = new File(PATH);
        if(!file.exists())
            System.out.println("Не найден файл журнала");
    }

    public void write(String message) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            writer.write(message);

        } catch (IOException e) {
            System.out.println("Ошибка записи в файл журнала");
            e.printStackTrace();
        }
    }

    public ArrayList<String> readHistory() {
        final int limit = 100;
        var res = new ArrayList<String>(100);

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

            var src = (ArrayList<String>) reader.lines().collect(Collectors.toList());
            int size = src.size();
            int start = size > limit ? size - limit + 1 : 0;
            res.addAll((int) start, src);
            return res;

        } catch (IOException e) {
            System.out.println("Ошибка чтения с журнала");
            e.printStackTrace();
            return null;
        }
    }
}
