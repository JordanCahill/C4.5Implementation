
package machinelearningc45;

import java.util.ArrayList;

/**
 *
 * @author Jordan Cahill
 * @date 19-Dec-2017
 * 
 * Used to apply the test case to the hypothesis
 * 
 */
public class TestData {
    
    private boolean resultG;
    private String predictedG;
    private String actualG;


    // Constructor used to test the data
    public TestData(boolean result, String predicted, String actual){
            this.resultG = result;
            this.predictedG = predicted;
            this.actualG = actual;
            
    }

    // Blank constructor
    public TestData(){
        
    }

    /**
     * Tests the model and returns an arraylist with the results
     * 
     * @param tree - Node object representing the hypothesis tree
     * @param testData - Data to test against the hypothesis
     * @return - ArrayList with the results
     */
    ArrayList<TestData> testModel(Node tree, String[][] testData) {
        
        int numAtts = testData[0].length-1;
        ArrayList<TestData> results = new ArrayList<>();	
	ArrayList<String> atts = new ArrayList<>(); // All possible attributes
        
        for(int a=0; a<numAtts; a++){ // Index of each column
            atts.add(testData[0][a]);
	}
        
        for(int i=1;i<testData.length;i++){ // Loop through the test data
            
            boolean finished = false; // To check if the current tuple has finished traversing the tree
            Node currentNode = tree;
            String currentAtt;
            double currentThresh = 0;
            int colNum;
            String classPrediction = null;
            
            while(!finished){ // Loop until test case is assigned a class
                currentAtt = currentNode.getTitle(); 
                currentThresh = currentNode.getThreshold();
                colNum = atts.indexOf(currentAtt);
            
                if(currentNode.getThreshold() == -1.0){	// Case for single nodes			
                    classPrediction = currentAtt;
                    finished = true;							
                }

                else if(currentAtt.equals("Probability")){ // Case for probability nodes
                    // Check probability and assign class appropriately
                    if(currentNode.getProb1() > currentNode.getProb2()){	
                                    classPrediction = currentNode.getValOfClass1();
                                    finished = true;
                    }
                    else{
                                    classPrediction = currentNode.getValOfClass2();                                   
                                    finished = true;
                        }
                    }
                else{
                    if(Double.parseDouble(testData[i][colNum]) <= currentThresh){ // Check current value against threshold at current node		
                        if(currentNode.getLeftChild() == null){	// If the current node has no left child, and is lower than threshold, assign current class
                            classPrediction = currentNode.getTitle();	
                            finished = true;
                        }else{									
                            currentNode = currentNode.getLeftChild(); // Keep traversing the tree	
                        }
                    }else{					
                        if(currentNode.getRightChild() == null){ // If the current node has no right child, assign current class			
                            classPrediction = currentNode.getTitle();
                            finished = true;
                        }
                        else{													
                            currentNode = currentNode.getRightChild(); // Keep traversing the tree
                        }	
                    }
                }
            }
            String classActual = testData[i][numAtts]; // Class to compare against predicted class
            boolean result;
            
            int numTrue = 0; // Number of times predicted correctly
            result = ((classPrediction.equals(classActual))); // Check to see if comparision was correct
                       
            if (result){
                numTrue++;
            }
            // Store the result, predicition and actual value
            TestData currentResult = new TestData(result, classPrediction, classActual);
            results.add(currentResult); // Add case to TestData object
            
        }
        // Return the TestData object containing the results
        return results;
    }
    /** TestData Object result getters **/
    public boolean getResult(){
        return resultG;
    }
    public String getPredicted(){
        return predictedG;
    }
    public String getActual(){
        return actualG;
    }
}
