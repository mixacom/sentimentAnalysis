import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mikhail on 14.10.2014.
 */
public class AddSentiment {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\thirteen.csv")));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\twelve_ahp.csv")));
        List<String> ls = new ArrayList<String>();

        while (br.ready()) {
            String s = br.readLine();
            s = s.replaceAll("\"","");
            s = s.trim();
            ls.add(s);
        }

        int index = 0;
        while (br2.ready()) {
            String s = br2.readLine();
            String[] sArray = s.split(" ");
            String text = ls.get(index);
            ls.set(index++, text + ", " + sArray[sArray.length-1]);
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("almostNew.csv")));

        for (String sinfo : ls) {
            bw.write(sinfo + "\r\n");
        }

        br.close();
        br2.close();
        bw.close();
    }
}
