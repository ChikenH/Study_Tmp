import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShiftJISReaderTest {
    public static void main(String[] args) {
        try {
            // SJIS
            InputStreamReader isr = new InputStreamReader(
                    new FileInputStream("test-sjis.txt"), "Shift_JIS"
            );

            BufferedReader r = new BufferedReader(isr);
            
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                line = r.readLine();
                System.out.println(line);
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
