import java.io.*;

/**
 * Created by Mikhail on 15.10.2014.
 */
public class PolishFile {
    public static void cleanJoins(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + "pf")));

        while (br.ready()) {
            String s = br.readLine();

            if (s.contains("}][{")) {
                s = s.replace("}][{","}\r\n{");
            }

            bw.write(s + "\r\n");

        }


/*      int num = 0;
        int fn = 0;

        while (br.ready()) { // slice big file
            String fileString = br.readLine();
            bw.write(fileString + "\r\n" );

            num++;

            if (num % 150000 == 0) {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + fn++)));
            }
        }*/

        br.close();
        bw.close();
    }

    public static void main(String args[]) throws IOException {
        cleanJoins("C:\\Data\\all.csv");
    }
}
