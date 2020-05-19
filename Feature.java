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


public class Feature {
    /**
     * Stores the features of a data point
     */
    
    FeatureType featureType;
    private double continuousPayload; // If the feature is continuous
    private String categoricalPayload; // For categorical features
    private String label;
    
    /**
     * Creates a new continuous feature.
     * @param payload the value of the feature
     * @param name the label
     */
    
    public Feature(double payload, String name) {
        continuousPayload = payload;
        featureType = FeatureType.CONTINUOUS;
        label = name;
    }
    
    /**
     * Creates a new categorical feature.
     * @param payload the value of the feature
     * @param name the label
     */
    
    public Feature(String payload, String name) {
        categoricalPayload = payload;
        featureType = FeatureType.CATEGORICAL;
        label = name;
    }
    
    /**
     * Obtains the value of continuous features.
     * @return
     */
    
    public double obtainContinuousPayload() {return this.continuousPayload;}
    
    /**
     * Obtains the identifier for this feature.
     * @return
     */
    
    public String obtainLabel(){return this.label;}
    
    /**
     * Obtains the value of categorical features.
     * @return
     */
    
    public String obtainCategoricalPayload(){return this.categoricalPayload;}
    
    /**
     * @return the type of feature
     */
    
    public FeatureType obtainFeatureType(){return this.featureType;}
    
    @Override
    public String toString(){
        if (featureType == FeatureType.CATEGORICAL)
            return (label + "(C): " + categoricalPayload);
        else
            return (label + "(N): " + continuousPayload);
    }
    
    @Override
    public boolean equals(Object obj) {
        Feature other = (Feature) obj;
        if (other.featureType != this.featureType)
            return false;
        if (!other.label.equals(this.label))
            return false;
        if (this.featureType == FeatureType.CATEGORICAL)
            return (this.categoricalPayload.equals(other.categoricalPayload));
        if (this.featureType == FeatureType.CONTINUOUS)
            return (this.continuousPayload == other.continuousPayload);
        return false;
    }
    
}
