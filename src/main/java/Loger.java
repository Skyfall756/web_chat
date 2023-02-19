import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Loger {

    private static Loger INSTANCE = null;
    private static File logFile = new File("log.txt");

    private static SimpleDateFormat sdt = new SimpleDateFormat("yy.MM.dd hh:mm:ss");

    private Loger() throws IOException {
        if(!logFile.exists()) logFile.createNewFile();
    }

    public static Loger getLoger() throws IOException {
        if (INSTANCE == null) {
            synchronized (Loger.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Loger();
                }
            }
        }
        return INSTANCE;
    }

    public void log (String msg) {
        try (FileWriter fileWriter = new FileWriter(logFile, true)) {
            fileWriter.write("[" + sdt.format(new Date()) + "]" + msg + "\n");
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


}
