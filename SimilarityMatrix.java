/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ARZavier
 */
public class SimilarityMatrix {
    
    private final Map<String, Map<String, Double>> distances;
    private final String featLabel;
    
    /**
     * Returns the similarity (distance) between two categorical variables.
     * @param payload1 the first payload to compare
     * @param payload2 the second payload to compare
     * @return the similarity (distance) between the two categorical variables
     */
    
    public double obtainDistance(String payload1, String payload2) {
        return this.distances.get(payload1).get(payload2);
    }
    
    /**
     * Obtains the label for the feature that this matrix corresponds to.
     * @return label for the feature that this matrix corresponds to
     */
    
    public String obtainFeatLabel(){return this.featLabel;}
    
    /**
     * Creates a similarity matrix by calculating the similarity (distance)
     * for one feature, identified by its label
     * @param data the data to be utilized to calculate the matrix
     * @param featLabel the label for the feature to calculate the
     * matrix of (utilizes the data of the object)
     */
    
    public SimilarityMatrix(DataPoint[] data, String featLabel) {
        // Maps Payload to its Occurrence
        Map<String, Integer> numberOfEntriesPerPayload = new HashMap<String, Integer>();
        // Maps Class to (Map Payload to its Occurrence)
        Map<String, Map<String, Integer>> numberOfEntriesPerClassPerPayload = new HashMap<String, Map<String, Integer>>();
        /**
         * Maps a payload x to (map to payload y
         * and its distance from payload x).
         */
        Map<String, Map<String, Double>> distance = new HashMap<String, Map<String, Double>>();
        
        for (DataPoint DP : data) {
            String payload = DP.obtainFeatureByLabel(featLabel).obtainCategoricalPayload();
            String classMembership = DP.obtainClassMembership();
            
            // First, add number of occurrences per payload
            if (numberOfEntriesPerPayload.containsKey(payload)) {
                numberOfEntriesPerPayload.put(payload, numberOfEntriesPerPayload.get(payload) + 1);
            } else {
                numberOfEntriesPerPayload.put(payload, 1);
            }
            // Second, add number of occurrences per payload per class
            if(!numberOfEntriesPerClassPerPayload.containsKey(classMembership)) {
                // Contains Class
                numberOfEntriesPerClassPerPayload.put(classMembership, new HashMap<String, Integer>());
            }
            
            Map<String, Integer> mapForClass = numberOfEntriesPerClassPerPayload.get(classMembership);
            
            if (mapForClass.containsKey(payload)) {
                // Contains Payload
                mapForClass.put(payload, mapForClass.get(payload) + 1);
            } else {
                // Does Not Contain Payload
                mapForClass.put(payload, 1);
            }
            // Counting Done
        }
        
        // Calculate Similarities
        for (String payload1: numberOfEntriesPerPayload.keySet()) {
            for (String payload2: numberOfEntriesPerPayload.keySet()) {
                if (!distance.containsKey(payload1)) {
                    // Initialize New Map
                    distance.put(payload1, new HashMap<String, Double>());
                }
                double prob = 0.0;
                for (String classMembership: numberOfEntriesPerClassPerPayload.keySet()) {
                    double payload1OccurrenceInClass;
                    double payload2OccurrenceInClass;

                    double payload1Occurrence = (double) numberOfEntriesPerPayload.get(payload1);
                    double payload2Occurrence = (double) numberOfEntriesPerPayload.get(payload2);
                    
                    try {
                        payload1OccurrenceInClass = (double) numberOfEntriesPerClassPerPayload.get(classMembership).get(payload1);
                    } catch (NullPointerException exception) {
                        payload1OccurrenceInClass = 0.0;
                    }
                    try {
                        payload2OccurrenceInClass = (double) numberOfEntriesPerClassPerPayload.get(classMembership).get(payload2);
                    } catch (NullPointerException exception) {
                        payload2OccurrenceInClass = 0.0;
                    }
                    
                    prob += Math.abs((payload1OccurrenceInClass / payload1Occurrence)-(payload2OccurrenceInClass / payload2Occurrence));
                }
                distance.get(payload1).put(payload2, prob);
            }
        }
        this.distances = distance;
        this.featLabel = featLabel;
    }
    
    @Override
    public String toString() {
        String ans = "Similarity Matrix For " + featLabel + "\n";
        for(Map.Entry<String, Map<String, Double>> level1 : distances.entrySet()) {
            for (Map.Entry<String, Double> level2 : level1.getValue().entrySet()) {
                
                String payload2 = level2.getKey();
                Double distance = level2.getValue();
                
                String payload1 = level1.getKey();
                ans = ans + "\t" + payload1 + " to " + payload2 + ": " + distance.toString() + "\n";
            }
        }
        return ans;
    }
    
}
