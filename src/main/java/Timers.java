import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class Timers {

    private static int convertToMs = 1000;
    private static HashMap<String, Long> startTimes = new HashMap<>();
    private static CSVWriter writer;

    private static void writeHeader() {
        try {
            File file = new File("izvajanje-" + new Date() + ".csv");
            FileWriter outputFile = new FileWriter(file);
            writer = new CSVWriter(outputFile);
            String[] header = {"Ime Operacije", "Vrednost", "Enota"};
            writer.writeNext(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Timers() {
        writeHeader();
    }

    public String start(String nameOfTimerAndOperation) {
        startTimes.put(nameOfTimerAndOperation, System.nanoTime());
        return nameOfTimerAndOperation;
    }

    public void stop(String nameOfTimerAndOperation) {
        Long startTime = startTimes.get(nameOfTimerAndOperation);
        startTimes.remove(nameOfTimerAndOperation);
        long elapsed = (System.nanoTime() - startTime) / convertToMs;
        addDataToCSV(nameOfTimerAndOperation, String.valueOf(elapsed), "ms");
    }

    public static void addDataToCSV(String nameOfDataEntry, String dataEntryValue, String unit) {
        String[] data1 = {nameOfDataEntry, dataEntryValue, unit};
        writer.writeNext(data1);
    }

    public static void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
