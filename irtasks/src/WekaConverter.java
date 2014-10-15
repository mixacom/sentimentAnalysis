/**
 * Created by Mikhail on 12.10.2014.
 */

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;

public class WekaConverter  {

        public static void main(String[] args) throws Exception {

            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File("C:\\trainingSet.csv"));
            Instances data = loader.getDataSet();

            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File("training.arff"));
            saver.setDestination(new File("C:\\training.arff"));
            saver.writeBatch();
        }

}
