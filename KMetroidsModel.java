/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

/**
 *
 * @author ARZavier
 */
public abstract class KMetroidsModel extends ClusteringModel {
    
    int k;
    
    public KMetroidsModel(DataPoint[] data){super(data);}
    
    /**
     * Runs iteration of the model
     * This checks each data point to find the location of the centroid that minimizes distortion
     */
    
    private void runIteration(){
        this.clearCentroids();
        
        // Calculates the initial cost for comparison purposes
        double prevCost = cost(centroids.length);
        group();
        /**
         * Loops through each centroid comparing the cost of a swap 
         * with a non-Medoid to the currently stored cost, prevCost.
         */
        for (int j = 0; j < k; j++) {
            DataPoint[] points = centroids[j].getPoints();
            for (int i = 0; i < points.length; i++){
                // If not a centroid compute the following
                if (!Helper.contains(points[i], centroids)){
                    // Store Previous Centroid
                    DataPoint temp = centroids[j];
                    // Swap Data Point With Medoid
                    centroids[j].moveToPoint(points[i]);
                    // Calculate cost and compute against cost with previous centroid
                    double calcCost = cost(centroids.length);
                    if (prevCost < calcCost){
                        // If the cost worsens it swaps back
                        centroids[j].moveToPoint(temp);
                    } else {prevCost = calcCost;}
                }
            }
        }
    }
    
    /**
     * Puts points in centroids closest to them
     */
    
    private void group() {
        Double[][] dissimilarity = new Double[k][data.length];
        double minimum;
        int currCentroid;
        
        for (int j = 0; j < k; j++) {
            dissimilarity[j] = Helper.getDistancesToPoints(centroids[j], data, this.similarityMatrices);
        }
        
        for (int i = 0; i < data.length; i++) {
            minimum = dissimilarity[0][i];
            currCentroid = 0;
            
            for (int m = 0; m < k; m++) {
                if (minimum > dissimilarity[m][i]){
                    minimum = dissimilarity[m][i];
                    currCentroid = m;
                }
            }
            centroids[currCentroid].addPoint(data[i]);
        }
    }
    
    /**
     * Calculates total cost of the current medoids and the dataset
     * @param k
     * @return 
     */
    
    private double cost(int k) {
        Double[][] dissimilarity = new Double[k][data.length];
        double cost = 0;
        double minimum;
        
        for (int j = 0; j < k; j++) {
            dissimilarity[j] = Helper.getDistancesToPoints(centroids[j], data, this.similarityMatrices);
        }
        
        for (int i = 0; i < data.length; i++) {
            minimum = dissimilarity[0][i];
            for (int m = 0; m < k; m++) {
                if (minimum > dissimilarity[m][i]){
                    minimum = dissimilarity[m][i];
                }
            }
            cost += minimum;
        }
        return cost;
    }
    
    @Override
    public void cluster(int k) {
        this.k = k;
        centroids = new Centroid[k];
        // randomly grabs k medoids by scrambling the data set and grabbing the first k values
        DataPoint[] randArray = Helper.scramble(data);
        for(int i = 0; i < k; i++)
            centroids[i] = new Centroid(randArray[i]);
        double currCost = cost(k);
        double prevCost = currCost + 1;
        
        while (prevCost > currCost)
        {
            runIteration();
            prevCost = currCost;
            currCost = cost(k);
        }
    }
    
}
