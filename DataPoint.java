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
public class DataPoint {
    // Creates an object to store the data
    
    // Form of { label: payload }
    protected Feature[] features;
    protected String classMembership;
    
    // Default Constructor
    public DataPoint(Feature[] features){
        this.features = features;
    }
    
    // Constructor with Class Membership
    public DataPoint(Feature[] features, String classMembership) {
        this(features);
        this.classMembership = classMembership;
    }
    
    /**
     * @return the class of this point
     */
    
    public String obtainClassMembership(){return this.classMembership;}
    
    /**
     * @return the number of features that this point contains
     */
    
    public int obtainNumberOfFeatures(){return this.features.length;}
    
    /**
     * Obtains the feature value based on the label.
     * @param label String Identifier of the Feature
     * @return the feature value
     */
    
    public Feature obtainFeatureByLabel(String label) {
        for (Feature feat: this.features) {
            if (feat.obtainLabel().equals(label))
                return feat;
        }
        return null; // If there is no feature with that label
    }
    
    /**
     * Obtains the feature at index i.
     * @param i index of the feature
     * @return the feature
     */
    
    public Feature obtainFeatureAt(int i){return features[i];}
    
    /**
     * @return all of the features of this point
     */
    
    public Feature[] obtainFeatures(){return features;}
    
    /**
     * Assign a new class to this point.
     * @param newClassMembership the new class
     */
    
    public void setClassMembership(String newClassMembership) {
        this.classMembership = newClassMembership;
    }
    
    @Override
    public String toString(){
        List<String> featureStrings = new ArrayList<>();
        for (Feature feat: features){
            featureStrings.add(feat.toString());
        }
        return ("Features: {" + String.join(", ", featureStrings) 
                + "} | Class: " + classMembership);
    }
    
    @Override
    public boolean equals(Object obj) {
        DataPoint other = (DataPoint) obj;
        for (Feature feat: features) {
            if(!feat.equals(other.obtainFeatureByLabel(feat.obtainLabel())))
                return false;
        }
        return true;
    }
}
