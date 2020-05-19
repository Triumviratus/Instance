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
public class CKNNModel extends KNNModel {
    
    /**
     * Runs condensed KNN
     * @param rawData the data (not condensed)
     */
    
    public CKNNModel(DataPoint[] rawData) {
        super(rawData);
        data = condense(rawData);
    }
    
    /**
     * Condenses the dataset
     * @param data data to be condensed
     * @return the condensed dataset
     */
    
    private DataPoint[] condense(DataPoint[] data){
        ArrayList<DataPoint> newData = new ArrayList<>();
        data = Helper.scramble(data);
        newData.add(data[0]); // Inserts the first item
        
        int size = newData.size();
        int delta = 2;
        
        
        while(Math.abs(newData.size()-size) < delta) {
            // Until it does not change that much
            for (DataPoint x : data) {
                Double [] distance = Helper.getDistancesToPoints(x, newData, this.similarityMatrices);
                // Line 43 gets the distance to each point in a new set
                int index = Helper.findIndexOfMinimum(distance);
                if (!newData.get(index).obtainClassMembership().equals(x.obtainClassMembership())){
                    // Checks if the closest data point i different from this data point
                    newData.add(x);
                }
            }
        }
        return newData.toArray(new DataPoint[newData.size()]); // Condensed DataSet
    }
}
