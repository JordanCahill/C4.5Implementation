
package machinelearningc45;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


/**
 *
 * @author Jordan Cahill
 * @date 19-Nov-2017
 * 
 * This class contains methods used to carry out calculations on entropy and
 * information gain
 * 
 */
public class InformationGain {
    
    int rows;
    int columns;
    String[][] in;
    String[] classes;
    
    // Blank constructor
    public InformationGain(){
        
    }
    
    // Constructor to take in the data array, classes, number of rows and number of columns
    public InformationGain(String[][] dataIn, String[] classesIn, int rowNums, int colNums){
        this.in = dataIn;
        this.classes = classesIn;
        this.rows = rowNums;
        this.columns = colNums;
        
    }
    
    /**
     * Calculates the entropy of the entire data set
     * 
     * @param classArray - Array of independent variables
     * @return - Total set entropy
     */
    public double calcSetEntropy(String[] classArray){
        
        double setEntropy=0;
        FormatData numUnique = new FormatData(classArray);
        String[] uniqueClasses = numUnique.getUniqueClasses(); // String array of unique classes
        int count = 0;
        int[] occ = new int[uniqueClasses.length]; // Number of occurences of each class
        
        // Loop through each class and count the number of occurences
        for (String s: uniqueClasses){
            for (String classArray1 : classArray) {
                if (s.equals(classArray1)) {
                    occ[count]++;
                }
            }
            count++;
        }
        
        // Calculate the entropy of the data set based on the number of occurences
        for(int i=0;i<occ.length;i++){ 
            setEntropy+=(-((double)((double)occ[i]/(double)classArray.length))*(Math.log(((double)occ[i]/(double)classArray.length))/Math.log(2))); 
        }
        return setEntropy;
    }

    /**
     * Used to find the node with the best threshold to split on, assumes
     * the column has already been found
     * 
     * @param col - Column containing the element with the best threshold
     * @param setEntropy - Total set entropy
     * @return - Node to split on, with the threshold and IG as parameters 
     */
    Node getBestThreshold(int col, double setEntropy) {
        
        double infoGain; // Variable to store the information gain values
        double dataVal; // Running variable to store the value of each row for each attribute
        double totalSetEntropy = setEntropy; // Total entropy of the set as calculated in the main method
        String[][] data = this.in; // Pass in the data set
        ArrayList<Double> thresholdTable = new ArrayList<>(); // List to store unique values
        HashMap<Double, Double> threshGain = new HashMap<>(); // Gain and threshold HashMap pair
        ArrayList<String> uniqueClasses = new ArrayList<>();
        String dataLabel;
        int i = 1;
        
        while(i<rows){ // Loop through all rows
            
            dataVal = Double.parseDouble(data[i][col]); // Current value
            if (!thresholdTable.contains(dataVal)){ // Create an array with unique values from the data
		thresholdTable.add(dataVal);
            }

            
            dataLabel = data[i][columns-1]; // Same as the threshold table, but with the classes
            if (!uniqueClasses.contains(dataLabel)){
                uniqueClasses.add(dataLabel);
            }
            
            i++;
        }
        

        for (double value : thresholdTable){ // Loop through each unique data value
            infoGain = getInfoGain(value, col, data, uniqueClasses, totalSetEntropy); // Calculate the information gain at each value
            threshGain.put(value,infoGain); // Add the threshold and IG gain pair to the HashMap
        }
        
        int splitColumn = 0;
        double bestGain=0;
        double bestThreshold=0;
        double currentGain;
        double currentThreshold;
        
        // For each entry in the HashMap, find the one with the highest IG
        for(Entry<Double, Double> map : threshGain.entrySet()){ // Loop through the HashMap
            currentGain = map.getValue(); // get the current IG value
            currentThreshold = map.getKey(); // get the current thresh value
            
            if (currentGain>bestGain){ // Update the bestGain variable
                bestGain = currentGain;
                bestThreshold = currentThreshold;
                splitColumn = col;
            }
        }
        
        Node node = new Node(data[0][col],splitColumn,null,null,null,bestThreshold,bestGain);
        return node; // Return the node with the highest IG
    }

    /**
     * Calculates the Information Gain of each element in a specific column
     * 
     * @param threshVal - Threshold value of the current element
     * @param col - Column to iterate through
     * @param data - Current data set
     * @param classes - Class vector
     * @param setEntropy - Entropy of the entire set
     * @return 
     */
    private double getInfoGain(double threshVal, int col, String[][] data, ArrayList<String> classes, double setEntropy) {
        
        // Variables used to find the entropy and info gain at a node
        double infoGain;
        double numGreater = 0;
        double numLess = 0;
        double entropyTotal = setEntropy;
        double entropyLower = 0; // Entropy of the values<threshold
        double entropyUpper = 0; // Entropy of the values>threshold
        ArrayList<String[]> lowerArray = new ArrayList<>();
        ArrayList<String[]> upperArray = new ArrayList<>();
        
        for (int i=0;i<rows-1;i++){ // Loop through the rows
            if (Double.parseDouble(data[i+1][col])<=threshVal){
                lowerArray.add(data[i+1]); // Add values<threshold to an array
            }else{
                upperArray.add(data[i+1]); // Add values>threshold to an array
            }
        }
        
        //          ---- Calculate entropy lower than threshold ----              //
        for(int i=0; i<classes.size();i++){ // Loop through each class
            String currentVal = classes.get(i); // Get current class
            double numOccurences=0;
            
            for(int j=0;j<lowerArray.size();j++){ // Loop through each row in the lower array
                String[] lVal = lowerArray.get(j); // Get current value of lower array
                if(lVal[columns-1].equals(currentVal)){
                    numOccurences++; // Count number of occurences
                }
            }
            numLess = lowerArray.size();
            if(numOccurences!=0){ // Calculate the entropy lower than the threshold
                entropyLower +=  -(numOccurences/numLess)*(Math.log((numOccurences/numLess)/Math.log(2)));
                
            }else{ // No occurences -> 0 entropy
                entropyLower +=0;
            }
        }
        
        //          ---- Calculate entropy greater than threshold ----              //
        for(int i=0; i<classes.size();i++){ // Loop through each class
            String currentVal = classes.get(i); // Get current class
            double numOccurences=0;
            
            for(int j=0;j<upperArray.size();j++){ // Loop through each row in the upper array
                String[] uVal = upperArray.get(j); // Get current value of upper array
                if(uVal[columns-1].equals(currentVal)){
                    numOccurences++; // Count number of occurences
                }
            }
            numGreater = upperArray.size();
            if(numOccurences!=0){ // Calculate the entropy lower than the threshold
                entropyUpper +=  -(numOccurences/numGreater)*(Math.log((numOccurences/numGreater)/Math.log(2)));
                
            }else{// No occurences -> 0 entropy
                entropyUpper +=0;
            }
        }
        
        // Calculate the information gain for the given threshold value
        infoGain = entropyTotal - (entropyLower*numLess)/(data.length-1) - ((entropyUpper*numGreater)/(data.length-1));
        return infoGain;
    }
}
