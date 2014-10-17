package ScoreSWN;

	import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

    public class Score {
        private String pathToSWN = "/Users/wilcovanleeuwen/Github/sentimentAnalysis/src/main/java/ScoreSWN/SWNdata.txt";
        private HashMap<String, Double> _dict;

        public Score(){

            _dict = new HashMap<String, Double>();
            HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
            try{
                BufferedReader csv =  new BufferedReader(new FileReader(pathToSWN));
                String line = "";           
                while((line = csv.readLine()) != null)
                {
                    String[] data = line.split("\t");
                    //System.out.println(data.length);
                    
                    if(data.length > 3 && !(data[0].charAt(0) == '#')) {
	                   // System.out.println(data[0]);
                    	Double score = Double.parseDouble(data[2])-Double.parseDouble(data[3]);
	                    String[] words = data[4].split(" ");
	                    
	                    for(String w:words)
	                    {
	                        String[] w_n = w.split("#");
	                        w_n[0] += "#"+data[0];
	                        int index = Integer.parseInt(w_n[1])-1;
	                        if(_temp.containsKey(w_n[0]))
	                        {
	                            Vector<Double> v = _temp.get(w_n[0]);
	                            if(index>v.size())
	                                for(int i = v.size();i<index; i++)
	                                    v.add(0.0);
	                            v.add(index, score);
	                            _temp.put(w_n[0], v);
	                        }
	                        else
	                        {
	                            Vector<Double> v = new Vector<Double>();
	                            for(int i = 0;i<index; i++)
	                                v.add(0.0);
	                            v.add(index, score);
	                            _temp.put(w_n[0], v);
	                        }
	                    }
                    }
                }
                Set<String> temp = _temp.keySet();
                for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
                    String word = (String) iterator.next();
                    Vector<Double> v = _temp.get(word);
                    double score = 0.0;
                    double sum = 0.0;
                    for(int i = 0; i < v.size(); i++)
                        score += ((double)1/(double)(i+1))*v.get(i);
                    for(int i = 1; i<=v.size(); i++)
                        sum += (double)1/(double)i;
                    score /= sum;
                    String sent = "";               
                    if(score>=0.75)
                        sent = "strong_positive";
                    else
                    if(score > 0.25 && score<=0.5)
                        sent = "positive";
                    else
                    if(score > 0 && score>=0.25)
                        sent = "weak_positive";
                    else
                    if(score < 0 && score>=-0.25)
                        sent = "weak_negative";
                    else
                    if(score < -0.25 && score>=-0.5)
                        sent = "negative";
                    else
                    if(score<=-0.75)
                        sent = "strong_negative";
                    _dict.put(word, score);
                }
            }
            catch(Exception e){e.printStackTrace();}        
        }

public Double extract(String word)
{
    Double total = new Double(0);
    if(_dict.get(word+"#n") != null)
         total = _dict.get(word+"#n") + total;
    if(_dict.get(word+"#a") != null)
        total = _dict.get(word+"#a") + total;
    if(_dict.get(word+"#r") != null)
        total = _dict.get(word+"#r") + total;
    if(_dict.get(word+"#v") != null)
        total = _dict.get(word+"#v") + total;
    return total;
}
//calculate the totalscore of the  
public static double calculate(String lineTxt){
	Score test = new Score();
	String[] words = lineTxt.split("\\s+"); 
    double totalScore = 0;
    for(String word : words) {
        word = word.replaceAll("([^a-zA-Z\\s])", "");
        if (test.extract(word) == null)
            continue;
        totalScore += test.extract(word);
    }
    //System.out.println(totalScore);
    writeToFile(lineTxt, totalScore);
	return totalScore;
}
//write the text and score to file
public static void writeToFile(String lineTxt, double totalScore){
	byte[] buffString=new byte[]{};  
	byte[] buffDouble=new byte[]{};
	byte[] newline = "\r\n".getBytes();
	byte[] divide = ";".getBytes();
    try   
    {  
    	buffString=lineTxt.getBytes();  
        FileOutputStream outString=new FileOutputStream("C:\\Users\\acer\\Dropbox\\study\\Quarter1\\IRDM\\Project\\resource\\output.txt",true);  
        outString.write(buffString,0,buffString.length); 
        outString.write(divide);
        String totalScoreString=String.valueOf(totalScore);
        buffDouble=totalScoreString.getBytes();  
        FileOutputStream outDouble=new FileOutputStream("C:\\Users\\acer\\Dropbox\\study\\Quarter1\\IRDM\\Project\\resource\\output.txt",true);  
        outDouble.write(buffDouble,0,buffDouble.length);
        outString.write(newline);
        outDouble.close();
        outString.close();
    } 
    catch (FileNotFoundException e)   
    {  
        e.printStackTrace();  
    }  
    catch (IOException e)   
    {  
        e.printStackTrace();  
    }
}

public static void main(String[] args) {
    String filePath = "C:\\Users\\acer\\Dropbox\\study\\Quarter1\\IRDM\\Project\\resource\\file.txt";
    try {
        String encoding="GBK";
        File file=new File(filePath);
        if(file.isFile() && file.exists()){ //ÅÐ¶ÏÎÄŒþÊÇ·ñŽæÔÚ
            InputStreamReader read = new InputStreamReader(
            new FileInputStream(file),encoding);//¿ŒÂÇµœ±àÂëžñÊœ
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                System.out.println(lineTxt);
               Double totalScore= calculate(lineTxt);

                
//                byte[] buff=new byte[]{};  
//                try   
//                {  
//                    buff=lineTxt.getBytes();  
//                    FileOutputStream out=new FileOutputStream("C:\\Users\\acer\\Dropbox\\study\\Quarter1\\IRDM\\Project\\resource\\output.txt");  
//                    out.write(buff,0,buff.length);         
//                } 
//                catch (FileNotFoundException e)   
//                {  
//                    e.printStackTrace();  
//                }  
//                catch (IOException e)   
//                {  
//                    e.printStackTrace();  
//                }
               
            }
            read.close();
        	}else{
        		System.out.println("ÕÒ²»µœÖž¶šµÄÎÄŒþ");
        	}
    	} catch (Exception e) {
    			System.out.println("¶ÁÈ¡ÎÄŒþÄÚÈÝ³öŽí");
    			e.printStackTrace();
    	}
    //¶àžö¿ÕžñÇÐžî

}

}
