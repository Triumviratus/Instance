/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author ARZavier
 */
public class Instance {
    
    public static ArrayList<String> classes;
    private static int k = 7;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String[] allFiles = {"abalone", "car", "segmentation", "forestfires", "machine", "wine"};
        String[] classificationFiles = {"abalone", "car", "segmentation"};
        String[] regressionFiles = {"forestfires", "machine", "wine"};
        
        String[] CATEGORICAL_FEATURES = {"sex", "buying", "maintenance", "doors", "persons", "safety", "lug_boot", "month", "day"};
        
        for (String path : classificationFiles){
            // Reads the files
            System.out.println();
            System.out.println();
            System.out.println("Processing: " + path);
            String file = readEntireFile("Data/Assignment2/" + path + "_preprocessed.data"); // Reads in the data
            String[] lines = file.split("\n");
            DataPoint[] data = new DataPoint[lines.length]; // First line is feature labels
            String[] featureLabels = lines[0].split(",");
            classes = new ArrayList<>();
            // Generate the data points
            for (int i = 1; i < lines.length; i++)
            {
               data[i] = genDatapoint(lines[i], featureLabels, CATEGORICAL_FEATURES);
               if(!classes.contains(data[i].obtainClassMembership()))
                   classes.add(data[i].obtainClassMembership());
            }
            DataPoint[][] folds = fold(data); // Splits the data into 10 roughly equal folds
            if (Helper.contains(path, classificationFiles))
            {
                // Runs Classification Algorithms
                // Header labels for the *.csv file that metrics are written to
                String knnOutput = "train Fold, test Fold, KNN-ACC, KNN-PREC, KNN-RECALL, KNN-FSCORE, CKNN-ACC, CKNN-PREC,"
                        + "CKNN-RECALL, CKNN-FSCORE, EKNN-ACC, EKNN-PREC, EKNN-RECALL, EKNN-FSCORE\n";
                String clusterOutput = "train Fold, test Fold, KMEANS-DISTORTION, KMEANS-ACC, KMEANS-PREC, KMEANS-RECALL,"
                        + "KMEANS-FSCORE, KMETROIDS-DISTORTION, KMETROIDS-ACC, KMETROIDS-PREC, KMETROIDS-RECALL, KMETROIDS-RECALL";
                // Creates a new file for this run
                File knnFile = createNewFile("Data/Assignment2/outputs/classification/knn-k" + k + "-" + path);
                File clusterFile = createNewFile("Data/Assignment2/outputs/classification/clustering-k" + k + "-" + path);
                
                // Writes the header
                appendToFile(knnOutput, knnFile);
                appendToFile(clusterOutput, clusterFile);
                for(int trainFold = 0; trainFold < folds.length; trainFold++) {
                    System.out.println("Train Fold: " + trainFold);
                    DataPoint[] trainingData = folds[trainFold];
                    DataPoint[] testData; // Initializes it, but will be overwritten in the For loop
                    // Initializes the models
                    KNNModel knn = new KNNModel(trainingData);
                    KNNModel cknn = new CKNNModel(trainingData);
                    KNNModel eknn = new EKNNModel(k, trainingData);
                    
                    int clusters = eknn.data.length;
                    KMeansModel kmeans = new KMeansModel(trainingData) {};
                    KMetroidsModel kmetroids = new KMetroidsModel(trainingData) {};
                    
                    kmeans.cluster(clusters);
                    kmetroids.cluster(clusters);
                    
                    for (int fold = 0; fold < folds.length; fold++) {
                        // Runs the Models
                        System.out.println("Fold: " + fold); // To track progress while running
                        testData = folds[fold];
                        knnOutput = trainFold + "," + fold + ","; // fold information
                        clusterOutput = trainFold + "," + fold + ",";
                        
                        // Formats the KNN outputs for CSV
                        knnOutput = knnOutput + runKNNClassification(knn, testData) + ","; // Calculated
                        knnOutput = knnOutput + runKNNClassification(cknn, testData) + ","; // Calculated
                        knnOutput = knnOutput + runKNNClassification(eknn, testData) + "\n"; // Calculated
                        
                        // Formats the clustering outputs for CSV
                        clusterOutput = clusterOutput + runClusteringClassification(kmeans, testData) + ",";
                        clusterOutput = clusterOutput + runClusteringClassification(kmetroids, testData) + "\n";
                        
                        // Writes the data
                        appendToFile(knnOutput, knnFile);
                        appendToFile(clusterOutput, clusterFile);
                    }
                }
                System.out.println("Done Writing");
            }
            else {
                // Regression Model
                // Header for CSV
                String clusterOutput = "train Fold, KMEANS-DISTORTION, KMETROIDS-DISTORTION\n";
                File newFile = createNewFile("Data/Assignment2/outputs/regression/clusteringRegression-" + path);
                appendToFile(clusterOutput, newFile);
                for (int trainFold = 0; trainFold < folds.length; trainFold++) {
                    System.out.println("Fold: " + trainFold);
                    DataPoint[] trainData = folds[trainFold]; // Initializes it, but will be overwritten in the For loop
                    KMeansModel kmeans = new KMeansModel(trainData) {};
                    KMetroidsModel kmetroids = new KMetroidsModel(trainData) {};
                    kmeans.cluster(trainData.length / 4);
                    kmetroids.cluster(trainData.length / 4);
                    
                    // Run the Models
                    clusterOutput = trainFold + ","; // Fold Information
                    clusterOutput = runClusteringRegression(kmeans, clusterOutput) + ",";
                    clusterOutput = runClusteringRegression(kmetroids, clusterOutput) + "\n";
                    
                    appendToFile(clusterOutput, newFile);
                }
                System.out.println("Done Writing");
            }
            System.out.println(); // To separate files on the output console
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }
    
    /**
     * Runs the validation on KNN models and returns a
     * string with the data formatted for CSV files.
     * @param model
     * @param k
     * @param testData
     * @return
     */
    
    private static String printKNNMetrics(KNNModel model, int k, DataPoint[] testData) {
        double acc = Validator.accuracy(model, k, testData);
        double prec = Validator.precision(model, k, testData);
        double recall = Validator.recall(model, k, testData);
        double fScore = Validator.fScore(model, k, testData);
        // Stores the metrics for each entity
        String output = acc + "," + prec + "," + recall + "," + fScore;
        return output;
    }
    
    /**
     * Creates a data point based on a string input
     * @param featureString String defining the data point
     * @return data point generated
     */
    
    private static DataPoint genDatapoint(String featureString, String[] featureLabels, String[] categoricalFeatures) {
        String[] splice = featureString.split(",");
        Feature[] features = new Feature[splice.length - 1]; // Assume last value is class
        for (int i = 0; i < features.length; i++) {
            if (Arrays.asList(categoricalFeatures).contains(featureLabels[i]))
                features[i] = new Feature(splice[i], featureLabels[i]);
            else {
                try {
                    features[i] = new Feature(Double.parseDouble(splice[i]), featureLabels[i]);
                } catch (NumberFormatException exception) {
                    features[i] = new Feature(splice[i], featureLabels[i]);
                }
            }
        }
        DataPoint d = new DataPoint(features, splice[splice.length - 1]);
        return d;
    }
    
    /**
     * Runs the validation on KNN models and returns a string with the data formatted for CSV files
     * @param model
     * @param testData
     * @return
     */
    
    private static String runKNNClassification(KNNModel model, DataPoint[] testData) {
        String output = printKNNMetrics(model, k, testData);
        return output;
    }
    
    /**
     * Runs the validation on clustering models for classification and
     * returns a string with the data formatted for CSV files.
     * @param model
     * @param testData
     * @return
     */
    
    private static String runClusteringClassification(ClusteringModel model, DataPoint[] testData) {
        double distortion = 0;
        try {
            distortion = model.getDistortion();
        } catch(Exception e) {
            e.printStackTrace();
        }
        model.assignClasses();
        KNNModel knnModel = new KNNModel(model.getCentroids());
        knnModel.similarityMatrices = model.similarityMatrices;
        String output = distortion + "," + printKNNMetrics(knnModel, k, testData);
        return output;
    }
    
    /**
     * Runs the clustering models and returns a string formated for CSV
     * @param model
     * @param output
     * @return
     */
    
    private static String runClusteringRegression(ClusteringModel model, String output) {
        
        try {
            double distortion = model.getDistortion();
            output = output + distortion;
        } catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }
    
    private static String readEntireFile(String filePath) {
        // Reads the file
        File file = new File(filePath);
        String retString = "";
        if (file.exists()) {
            try {
                Scanner scan = new Scanner(file);
                scan.useDelimiter("\\Z");
                if (scan.hasNext())
                    retString = scan.next();
                scan.close();
            } catch (FileNotFoundException ignored){
                return ("File Not Found For Path: " + file);
            }
        }
        else
            System.out.println("File Does Not Exist");
        return retString;
    }
    
    /**
     * Adds the string to the end of a file
     * @param line string to be added
     * @param file the file to be added to
     */
    
    public static void appendToFile(String line, File file) {
        // Adds on to file
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(line);
            writer.close();
        } catch (IOException ignored){}
    }
    
    /**
     * Creates a file if there does not exist one already, and then return the file at the file path.
     * @param filePath file path
     * @return the file (either old or newly created)
     */
    
    public static File createNewFile(String filePath) {
        // Creates a file
        String newPath = filePath;
        File file = new File(newPath + ".csv");
        int i = 2;
        
        while(file.exists()) {
            newPath = filePath + "-" + i;
            file = new File(newPath + ".csv");
            i += 1;
        }
        try {
            file.createNewFile();
        } catch (IOException ignored){
            ignored.printStackTrace();
        }
        return file;
    }
    
    /**
     * Folds the data into 10 relatively equal folds
     * @param points the data to be folded
     * @return a folded list
     */
    
    private static DataPoint[][] fold (DataPoint[] points){
        points = Helper.scramble(points); // Scrambles the data
        DataPoint[][] data = new DataPoint[10][points.length];
        int[] counters = new int[10];
        /**
         * Line 302 is designed so that the elements go into the
         * array in order (i.e., all the null values are at the end).
         */
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            // Ascertains that all folds have one data point at minimum
            data[i][counters[i]++] = points[i];
        }
        for (int i = 10; i < points.length; i++){
            int random = rand.nextInt(10);
            data[random][counters[random]++] = points[i];
            /**
             * Line 314 places the points into the folds in
             * order so as to avoid null values.
             */
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = trim(data[i]); // Eliminate trailing null values
        }
        return data;
    }
    
    /**
     * Removes the null values from the end of the array
     * @param points the array
     * @return the values in the same order as the appear in points without trailing null values
     */
    
    private static DataPoint[] trim (DataPoint[] points){
        int i = 0;
        DataPoint point = points[i++];
        while(point != null){point = points[i++];}
        return (DataPoint[]) Arrays.copyOf(points, i-1);
    }
    
}