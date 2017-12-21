
package machinelearningc45;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Jordan Cahill
 * @date 21-Nov-2017
 * 
 * This class uses various methods to manipulate the data array into various formats
 * Methods include sorting, shuffling, and splitting the data
 * 
 */
class FormatData {
    
    private String[] inputS;
    private String[][] inputA;
    private String[][] inputShuffled;
    private int rows;
    private int columns;            
    private int trainSize;
    private int testingSize;
    private int rowCount;
    
    // Constructor to take in a string array vector
    public FormatData(String[] inputString){
        inputS = inputString;
    }
    
    // Constructor to take in a 2D String array
    public FormatData(String[][] inputArray) {
        inputA = inputArray;
    }
    
    // Constructor to take in the number of rows, the number of columns, and a 2D String array
    public FormatData(int numRow, int numCol, String[][] data){
        this.rows = numRow;
        this.columns = numCol;
        this.inputA = data;
    }

    // Constructor for splitting data into training and testing
    FormatData(String[][] shuffledArray, int trainingSize, int testSize, String[][] data) {
        inputShuffled = shuffledArray;
        trainSize = trainingSize;
        testingSize = testSize;
        inputA = data;
    }

    // Blank constructor
    FormatData() {
    }

    /**
     * Seperates a String array into an array with one occurence of each value,
     * specifically, the independent variables
     * 
     * @return - Array of unique Strings taken from another String array
     */
    public String[] getUniqueClasses() {       
        Set<String> t = new HashSet<>(Arrays.asList(this.inputS));
        String[] uniqueClasses = t.toArray(new String[t.size()]);
        return uniqueClasses;    
    }
    
    /**
     * Used to shuffle a 2D String array while keeping the integrity of each row
     * 
     * @return - Shuffled 2D String array
     */
    public String[][] shuffleArray(){
        
        int arrayIndex;
        Random rndNum = new Random();

        for(int i=this.inputA.length-1; i>1; i--){ // Loop backwards through the array

                arrayIndex = rndNum.nextInt(i+1); // Assign a random index	
                while (arrayIndex == 0){ // Loop until index is 0
                    arrayIndex = rndNum.nextInt(i+1); 
		}
                
                String[] t = this.inputA[arrayIndex]; // Sets up the array
                this.inputA[arrayIndex] = this.inputA[i]; // Adds a random row to the array
                this.inputA[i] = t;
        }
        return this.inputA;
    }

    /**
     * Takes a specified amount of rows starting at the top of the data set and 
     * working down, based on a user input
     * 
     * @return - 2D String array to use as a training set
     */
    String[][] GetTrainingData() {
        String[][] trainingData = new String[trainSize+1][];
        System.arraycopy(inputShuffled, 0, trainingData, 0, trainingData.length);
        return trainingData;     
    }

    /**
     * Takes a specified amount of rows starting at the bottom of the data set and 
     * working up, based on a user input
     * 
     * @return - 2D String array to use as a test set
     */
    String[][] GetTestingData() {
        String[][] testData = new String[testingSize+1][];	
        testData[0]=this.inputA[0];
        System.arraycopy(inputShuffled, (inputShuffled.length - 1 - testingSize), testData, 1,testData.length-1);
        return testData;    
    }

    /**
     * BubbleSort algorithm used to sort specific columns in ascending order.
     * 
     * @param input - The vector to be sorted
     */
    public void sortAscending(String[] input) {
        String[] tmp;
	String tmp2; 

        // Implementation of the bubble sort algorithm
        for(int i=1; i<input.length; i++) {								
            for(int j=1; j<input.length-1; j++) {
                if(Double.parseDouble(input[j]) > Double.parseDouble((input[j+1]))) { 
                    tmp = inputA[j];
                    tmp2 = input[j];
                    inputA[j] = inputA[j+1];
                    input[j] = input[j+1];
                    inputA[j+1] = tmp;
                    input[j+1] = tmp2;
                }
            }
        }
    }


    /**
     * 
     * Calls the BubbleSort method to sort a specific column in ascending
     * order based on a parameter, used when calculating the IG
     * 
     * @param column - Indicates which column to sort
     * @return - Sorted 2D String array
     */
    String[][] columnSort(int column) {
        
        String[] colToSort = new String[rows];
                
        for(int k=0; k<rows ; k++){ // Iterate through the rows
           
            colToSort[k] = inputA[k][column]; // Sort each column
            
	}
        
        sortAscending(colToSort);
      
        return inputA;
        
        
    }
 
    /**
     * Sorts the data above or below a threshold based on an input parameter
     * 
     * @param data - Data to split based on the threshold
     * @param splitColumn - Column containing the threshold to split on
     * @param leftRight - Indicator whether to split above or below the threshold
     * @param splitThresh - The value of the threshold splitting the value
     * @return 
     */
    String[][] leftRightSort(String[][] data, int splitColumn, int leftRight, Double splitThresh) {
        
        double index;
        
        switch (leftRight) { // Decide whether to split the data above, or below
            
            case 0: // Below threshold <=> Left child
            {
                
                // If single value, or threshold is last value, return the data
                if(data.length<=2||Double.parseDouble(data[data.length-1][splitColumn]) == splitThresh){
                    return data; 
                }
                
                int counter = 1;
                boolean belowThresh = true;
                
                while(belowThresh){ // Loop through the data until the threshold is reached
                    if(counter==data.length){ // Case where the threshold isn't found
                        return null;
                    }
                    index = Double.parseDouble(data[counter][splitColumn]); // Value to compare against the threshold
                    if (index>splitThresh){
                        belowThresh=false; // Loop until the threshold is reached
                    }
                    counter++;
                }
                
                // Copy the data below the threshold into a new array
                String[][] leftDat = new String[counter-1][columns];
                System.arraycopy(data, 0, leftDat, 0, leftDat.length);
                
                return leftDat;
                
            }
            case 1:
            {
                
                int counter = 1;
                boolean belowThresh = true;
                
                while(belowThresh){ // Loop through the data until the threshold is reached
                    if(counter==data.length){ // Case where the threshold isn't found
                        return null;
                    }
                    index = Double.parseDouble(data[counter][splitColumn]); // Value to compare against the threshold
                    if (index>splitThresh){
                        belowThresh=false; // Loop until threshold is reached
                    }
                    counter++;
                }
                
                // Copy the data above the threshold into a new array
                String[][] rightDat = new String[data.length-counter+2][columns];
                System.arraycopy(data, 0, rightDat, 0, rightDat.length);
                System.arraycopy(data, counter-1, rightDat, 1, rightDat.length-1);
                
                return rightDat;
                
                
            }
            default: // Default case if "leftRight" isn't 0 or 1, shouldn't happen
                System.out.println("ERROR: Left-Right index incremented too high, check code!");
                return null;
        }
    }
}
