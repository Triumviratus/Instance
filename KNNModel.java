/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ARZavier
 */
public class KNNModel {
    protected DataPoint[] data;
    protected List<SimilarityMatrix> similarityMatrices;
    
    public KNNModel(DataPoint[] data) {
        this.similarityMatrices = new ArrayList<>();
        this.data = data;
        for (Feature feat: this.data[0].obtainFeatures()) {
            if (feat.obtainFeatureType() == FeatureType.CATEGORICAL) {
                similarityMatrices.add(new SimilarityMatrix(this.data, feat.obtainLabel()));
            }
        }
    }
    
    /**
     * Predicts the class of a new data point
     * @param k the number of neighbors to compare to
     * @param newObservation the data point to predict
     * @return the predicted class value of the data point
     */
    
    public String predict (int k , DataPoint newObservation) {
        Double [] distances = Helper.getDistancesToPoints(newObservation, data, this.similarityMatrices);
        int[] indxs = Helper.findIndexOfMinimum(k, distances);
        DataPoint[] kNearest = new DataPoint[k];
        for (int i = 0; i < kNearest.length; i++){
            kNearest[i] = data[indxs[i]];
        }
        return Helper.vote(kNearest);
    }
}

