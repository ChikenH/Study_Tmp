import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReaderWriterTest {
    public static void main(String[] args) {
        try {
            BufferedReader r = new BufferedReader(new FileReader("test.txt"));

            for (String line = r.readLine(); line != null; line = r.readLine()) {
                System.out.println(line);
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
