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
public class EKNNModel extends KNNModel {
    
    int k;
    
    /**
     * Runs edited KNN on a dataset
     * @param k the k value to utilize
     * @param rawData the unedited data
     */
    
    public EKNNModel(int k, DataPoint[] rawData){
        super(rawData);
        this.k = k;
        edit();
    }
    
    /**
     * Edits the dataset and remove data points that are incorrectly classified
     */
    
    private void edit(){
        ArrayList<DataPoint> newData = new ArrayList<>();
        int prevSize = newData.size();
        int delta = 2;
        
        while(Math.abs(prevSize - newData.size()) < delta) {
            // Do this until it does not change that much
            prevSize = newData.size();
            for(DataPoint x : data) {
                if (this.predict(k, x).equalsIgnoreCase(x.obtainClassMembership())){
                    // If it turns out to be wrong, remove it from the data set
                    newData.add(x);
                }
            }
            data = Helper.listToArr(newData); // Safe Cast
        }
        data = Helper.listToArr(newData); // Safe Cast
    }
}
