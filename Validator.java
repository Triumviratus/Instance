/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

import java.util.ArrayList;

/**
 *
 * @author ARZavier
 */
public class Validator {
    
    /**
     * Tests the data on accuracy
     * @param model the model to be tested
     * @param k the k-value
     * @param testData the data to be tested
     * @return the accuracy of the model
     */
    
    public static double accuracy(KNNModel model, int k, DataPoint[] testData){
        
        int correctClassification = 0; // counters
        for (int i = 0; i < testData.length; i++){
            if (testData[i].obtainClassMembership() == null || model == null || testData[i] == null)
                System.out.println("We Have a Problem");
            if (model.predict(k, testData[i]).equals(testData[i].obtainClassMembership()))
            {
                // If we obtain the right value, add it to the total correct percentage
                correctClassification++;
            }
        }
        return (double) correctClassification/(double) testData.length;
    }
    
    /**
     * Calculate the precision of a model
     * @param model the model to be tested
     * @param k the k-value
     * @param testData the data to be tested
     * @return the precision of the model
     */
    
    public static double precision(KNNModel model, int k, DataPoint[] testData){
        int[][] classifications = obtainConfusionMatrix(model, k, testData);
        double precision = 0;
        
        for (int perspective = 0; perspective < classifications.length; perspective++)
        {
            // Iterates through the perspective
            int TP = 0; // True Positive
            int FP = 0; // False Positive
            for(int compare = 0; compare < classifications.length; compare++) {
                if (compare != perspective)
                    FP += classifications[compare][perspective]; // Checks Side to Side for False Positives
                else
                    TP += classifications[perspective][compare]; // Checks for the Correct Classifications
            }
            if (TP + FP != 0)
            {
                // Ascertain that we have values to avoid NaN
                precision += (double) TP / ((double) (TP + FP)); // Sums the Precision
            }
        }
        precision /= Instance.classes.size();
        return precision;
    }
    
    /**
     * Calculates the recall of a model
     * @param model the model to be tested
     * @param k the k-value
     * @param testData the data to be tested
     * @return the recall
     */
    
    public static double recall (KNNModel model, int k, DataPoint[] testData)
    {
        int[][] classifications = obtainConfusionMatrix(model, k, testData);
        double recall = 0;
        
        for (int perspective = 0; perspective < classifications.length; perspective++)
        {
            // Iterates through the perspective
            int TP = 0; // True Positive
            int FN = 0; // False Negative
            for (int compare = 0; compare < classifications.length; compare++)
            {
                if (compare != perspective)
                    FN += classifications[perspective][compare]; // Checks up and down for false negatives
                else
                    TP += classifications[perspective][compare]; // Checks for the correct classifications
            }
            if (TP + FN != 0)
            {
                // Ascertain that we have values to avoid NaN
                recall += (double) TP / ((double) (TP + FN)); // Sums the Recall
            }
        }
        recall /= Instance.classes.size();
        return recall;
    }
    
    /**
     * This calculates the F1 score of the testing data
     * @param model
     * @param k
     * @param testData the data to be tested
     * @return the F1 score
     */
    
    public static double fScore (KNNModel model, int k, DataPoint[] testData) {
        double precision = precision(model, k, testData);
        double recall = recall(model, k , testData);
        return (2 * (precision * recall) / (precision + recall));
    }
    
    /**
     * Calculates the confusion matrix of a model
     * @param model the model to be tested
     * @param k the k-value
     * @param testData the data to be tested
     * @return
     */
    
    private static int[][] obtainConfusionMatrix(KNNModel model, int k, DataPoint[] testData) {
        ArrayList<String> classes = Instance.classes;
        int[][] classifications = new int[classes.size()][classes.size()];
        for (int i = 0; i < testData.length; i++)
        {
            classifications[classes.indexOf(testData[i].obtainClassMembership())]
                    [classes.indexOf(model.predict(k, testData[i]))]++;
            // Add the value to the matrix at the location where e classified it
        }
        return classifications;
    }
    
}
