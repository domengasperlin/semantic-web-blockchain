import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class Timer {

    private static int convertToMs = 1000000;
    private static HashMap<String, Long> startTimes = new HashMap<>();
    private static CSVWriter writer;

    private static Timer timer = null;
    private static String totalTimeOfProgramTimer = null;

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

    private Timer() {
        writeHeader();
    }

    public static Timer getInstance() {
        if (timer == null) {
            timer = new Timer();
            Timer.addDataToCSV("legenda tipi scenarijev","(zacetni - prazna lokalna baza, nova pogodba), (nadaljevanje - obstoječa baza in pametna pogodba le nove migracije), (sodelovanje - obnovitev lokalne baze iz blockchaina, objava svojih dodatnih lokalnih migracij na blockchain )","string");
            totalTimeOfProgramTimer = timer.start("Skupni cas izvajanja programa");
        }
        return timer;
    }

    public String start(String nameOfTimerAndOperation) {
        startTimes.put(nameOfTimerAndOperation, System.nanoTime());
        return nameOfTimerAndOperation;
    }

    public void stop(String nameOfTimerAndOperation) {
        Long startTime = startTimes.get(nameOfTimerAndOperation);
        startTimes.remove(nameOfTimerAndOperation);
        long elapsed = (System.nanoTime() - startTime.longValue()) / convertToMs;
        addDataToCSV(nameOfTimerAndOperation, String.valueOf(elapsed), "ms");
    }

    public static void addDataToCSV(String nameOfDataEntry, String dataEntryValue, String unit) {
        String[] data1 = {nameOfDataEntry, dataEntryValue, unit};
        writer.writeNext(data1);
    }

    public static void finish() {
        timer.stop(totalTimeOfProgramTimer);

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
