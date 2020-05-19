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
public abstract class KMeansModel extends ClusteringModel {
    
    /**
     * Constructor
     * @param data data to be clustered
     */
    public KMeansModel(DataPoint[] data){super(data);}
    
    @Override
    public void cluster(int k){
        /**
         * Initialize random centroids and be more
         * intelligent about where we initialize points.
         */
        centroids = new Centroid[k];
        DataPoint[] randArray = Helper.scramble(data);
        for (int i = 0; i < k; i++){
            // Puts the centroid at a random point
            centroids[i] = new Centroid(randArray[i]);
        }
        
        /**
         * Find when to stop in a better way
         * and run 100 iterations to train.
         */
        for (int i = 0; i < 100; i++){this.runIteration();}
    }
    
    /**
     * Runs an iteration of the algorithm that assigns points
     * to centroids and moves the centroids to their averages.
     */
    private void runIteration() {
        // Step 1: Evaluate Group Membership
        this.clearCentroids(); // Empties out old data points for new iteration
        
        for (DataPoint point : data) {
            // Find distances from all current centroids to this point
            Double[] dists = Helper.getDistancesToPoints(point, this.centroids, this.similarityMatrices);
            // Find the centroid this point is closest to
            Centroid closestCentroid = this.centroids[Helper.findIndexOfMinimum(dists)];
            // Set the group of this point to be the group of the centroid
            closestCentroid.addPoint(point);
        }
        
        // Step 2: Compute New Centroids
        this.moveCentroids();
        this.assignClasses(); // So classes on centroid remains up-to-date
    }
    
}
