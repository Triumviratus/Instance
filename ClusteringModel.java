/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instance;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ARZavier
 */
abstract class ClusteringModel {
    
    protected DataPoint [] data;
    protected Centroid[] centroids;
    protected List<SimilarityMatrix> similarityMatrices;
    
    /**
     * Initializes the clustering model with some data to be clustered
     * @param data the dataset
     */
    
    public ClusteringModel(DataPoint[] data){
        this.data = data;
        similarityMatrices = new ArrayList<>();
        for (Feature feat: this.data[0].obtainFeatures()) {
            if (feat.obtainFeatureType() == FeatureType.CATEGORICAL) {
                similarityMatrices.add(new SimilarityMatrix(this.data, feat.obtainLabel()));
            }
        }
    }
    
    /**
     * Creates k clusters on data
     * @param k the number of clusters
     */
    
    public abstract void cluster(int k);
    
    /**
     * Obtains the distortion value of the clusters
     * cluster() must be called before getDistortion
     * @return the distortion of the clusters
     * @throws Exception if the number of features is not the same between the points being compared
     */
    
    public double getDistortion() throws Exception {
        double answer = 0.0;
        // Cycle Through All Points
        for (DataPoint point : this.data) {
            // Obtain the group average for the group this point belongs
            DataPoint average = getCentroid(point);
            // Check to ascertain that we are comparing points of the same dimensionality
            if (average.obtainNumberOfFeatures() != point.obtainNumberOfFeatures())
                throw new Exception("Attempting to Compare 2 Vectors of Different Dimensions. Probable Error in Dataset.");
            answer += Math.pow(Helper.getDistance(point, average, this.similarityMatrices), 2);
        }
        return answer;
    }
    
    /**
     * Moves all the centroids to their averages
     */
    public void moveCentroids() {
        for(Centroid centroid : centroids){centroid.moveToAvg();}
    }
    
    /**
     * Returns the centroid to which the point belongs
     * @param p point being queried
     * @return the centroid to which the point belongs
     */
    
    private Centroid getCentroid(DataPoint p) {
        for (Centroid centroid : centroids) {
            if (centroid.contains(p))
                return centroid;
        }
        // Error catching in case the point does not belong to any centroid
        Double[] distance = Helper.getDistancesToPoints(p, centroids, this.similarityMatrices);
        Centroid c = centroids[Helper.findIndexOfMinimum(distance)];
        c.addPoint(p);
        return c;
    }
    
    /**
     * Obtains the centroids
     * @returns the centroids
     */
    
    public Centroid[] getCentroids(){return this.centroids;}
    
    /**
     * Obtains the data being clustered
     * @return the data
     */
    
    public DataPoint[] getData(){return this.data;}
    
    /**
     * Sets the centroids to a new Centroid[]
     * @param newPoints the new centroids
     */
    
    protected void setCentroids(Centroid[] newPoints){this.centroids = newPoints;}
    
    /**
     * Sets the class membership of the centroid based on the majority class of its points.
     */
    
    public void assignClasses(){
        for (Centroid centroid : centroids) {
            if (centroid.getPoints().length > 0)
                centroid.setClassMembership(Helper.vote(centroid.getPoints()));
        }
    }
    
    /**
     * Empties the centroids of all their points (i.e.,
     * removes all point-centroid associations).
     */
    
    public void clearCentroids(){
        for(Centroid centroid : centroids){centroid.clearPoints();}
    }
}
