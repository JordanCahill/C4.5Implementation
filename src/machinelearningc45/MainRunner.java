package machinelearningc45;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Jordan Cahill
 * @date 18-Nov-2017
 * 
 *  This project is built to implement the C4.5 Machine Learning Algorithm.
 * 
 *  The location of the data file used to build and analyse the model is hardcoded programmatically and must be of 
 *  type .csv. The first row of the data must have attribute titles, and the data should not have any missing values. The dependent class variables must be the column
 *  furthest to the right.
 *  
 *  The program begins by allowing the user to input the size of the data set to be used as a training set, as well
 *  as the number of times the model runs to get an overall average accuracy. The accuracy of each run and the overall
 *  accuracy is printed to the console, and a sample run is outputted to a csv file.
 */
public class MainRunner {

    double totalEntropyG;

    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime(); // Variable used to measure total time taken
        int rows;
        int columns;
        int trainingSize = 0;
        int testSize = 0;

        // Pass in the .csv data 
        CSVData allData = new CSVData("C:/Users/Jorda/Documents/College/owls15.csv");
        
        String[][] dataArray = allData.toArray(); // parses the CSVData object to a 2D string array

        rows = allData.getRows() - 1;
        columns = allData.getColumns();

        // Allow user to input training set size
        System.out.print("Please choose the size of the training data: ");
        
        // Functionality to account for user entering an invalid size
        boolean validSize = false; 
        Scanner scanInput = new Scanner(System.in);
        while(!validSize){
            trainingSize = scanInput.nextInt();
            testSize = rows - trainingSize;
            if (trainingSize<=0||trainingSize>=rows){
                System.out.println("Chosen size is out of range, please try again..");
            }else{
                validSize=true;
            }
        }
        
        System.out.println("Sorting data into " + trainingSize + " random attributes..");
        
        // Allow user to input number of runs
        System.out.print("Please choose the number of test runs: ");
        int numRuns = scanInput.nextInt();
        
        float totalRunningAcc = 0; // Running variable for the total accuracy, to be divided by the num of runs
        float finalAccuracy = 0;
        
        System.out.println("Running " + numRuns + " times..\n");
        
        for(int i =0 ;i<numRuns;i++){ // Build i amount of models equal to numRuns to get an accurate avg accuracy

            // Shuffle the array
            FormatData shuffleArrConfig = new FormatData(dataArray);
            String[][] shuffledArray = shuffleArrConfig.shuffleArray();

            // Split the data into training,testing and class sets
            FormatData splitData = new FormatData(shuffledArray, trainingSize, testSize, dataArray);
            String[][] trainingData = splitData.GetTrainingData();
            String[][] testData = splitData.GetTestingData();
            String classes[] = new String[trainingData.length]; // Vector for the classes

            // Separate the classes from the independent variables
            for (int j = 0; j < trainingData.length; j++) {
                classes[j] = trainingData[j][4];
            }

            //Calculate the entropy of the entire data set
            InformationGain IG = new InformationGain();
            double totalEntropy = IG.calcSetEntropy(classes);

            Node parent = classify(trainingData, classes, totalEntropy); // Builds the model

            TestData td = new TestData(); // TestData object to store a single result
            ArrayList<TestData> testResult = td.testModel(parent, testData); // Stores all results of a single run

            float accuracy = getAccuracy(testResult,(testData.length-1));
            System.out.println("Accuracy of run " + (i+1) + ": " + accuracy + "%");

            totalRunningAcc+=accuracy;
            finalAccuracy = totalRunningAcc/(float) (i+1);
            
            outputToCSV(testResult,i,accuracy); // Outputs a single case to a .csv file
        
        }

        System.out.println("\nTotal accuracy: " + finalAccuracy + "%\n");


        
        // Calculate and print total time taken, used for testing performance on large runs
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        double msTime = (double) elapsedTime / 1000000.0;
        System.out.println("Time elapsed: " + msTime + " ms\n");
    }

    /**
     * Used to build a C4.5 implemented decision tree, calls itself within the method to
     * recursively return the next node on the tree
     * 
     * @param data - Data set to be classified
     * @param classesData - Independent variables of the data set, needs to be in the same order as the data in "data"
     * @param setEntropy - Entropy of the entire data set
     * @return 
     */
    public static Node classify(String[][] data, String[] classesData, double setEntropy) {

        if (data == null || data.length == 0) { // If no data, return a null node
            return null;
        }

        int numCols = data[0].length;   // Number of columns
        int numAtts = numCols - 1;  // Number of independent variables
        Node n;  // "Current" node to be set as the best node -> highest IG
        Node best = new Node("Best node", 0, null, null, null, 0, 0);  // Default node with highest IG
        Node leftChild; // Node to become left child of current node
        Node rightChild; // Node to become right child of current node
        String allAtts[] = new String[numAtts]; // Array of all current attributes
        FormatData sortDat = new FormatData(data.length, numCols, data); // FormatData object used for sorting
        int uniqueElements = 0; // Used for leaves and probability nodes
        int loopCount = 0; // Counter used to find colNumber
        int colNumber = -1; // Column containing the highest IG
        
        for (int i = 1; i < (data.length - 1); i++) { // Loop over data set
            
            if (!(data[i][numAtts].equals(data[i+1][numAtts]))) { // Check "attribute = next attribute"
                if (uniqueElements > 2) {												//Willing to accept a single, odd element ---- can be altered
                    i = data.length; 														//break the for loop when >1 classes recognised
                } else {
                    uniqueElements++;
                }
                
            }else if (i==(data.length-2) && uniqueElements == 0) { // All elements in data set are the same, return one of them
                return new Node(data[1][numAtts], 0, null, null, null, -1, -1);
                
            }else if (i==(data.length - 2) && uniqueElements == 1) { // 50/50 chance -> return a probability node	
                Node probNode = new Node(data, numAtts);
                best = probNode.probabilityNode();		
                return best;
            }
        }

        // Create new array of "allAtts"
        System.arraycopy(data[0], 0, allAtts, 0, numCols - 1);

        // This for loop is used to find the best threshold to split on 
        for (int i = 0; i < numAtts; i++) {
            
            data = sortDat.columnSort(i); // Sort the data in ascending order based on column i
           
            InformationGain IG = new InformationGain(data, classesData, data.length, numCols);
            n = IG.getBestThreshold(i, setEntropy); // Returns the node with the highest IG in that column

            if (n.getInformationGain() > best.getInformationGain()) {
                best = n; // Update best with the highest value for IG in n
            }

        }

        while (loopCount < numCols - 1 ) { // Find column with highest IG
            if (allAtts[loopCount].equals(best.getTitle())) {
                colNumber = loopCount;
            }
            loopCount++;
        }

        Double splitThresh = best.getThreshold(); // Value for the best threshold
        String[][] newData; // Data to pass into the recursing method

        for (int leftRight = 0; leftRight < 2; leftRight++) { // Do once for left child and once for right

            data = sortDat.columnSort(colNumber); // Sort the data based on the column with best thresh
            FormatData newDataSort = new FormatData(data);
            newData = newDataSort.leftRightSort(data, colNumber, leftRight, splitThresh); // Sort the data above and below the threshold

            // In this case, either the data has been split on a single element, or the best threshold is the highest available
            // Either way, returning a probablitity node is acceptable
            if (newData.length == 2 || Double.parseDouble(data[data.length - 1][colNumber]) == splitThresh) {
                Node probNode = new Node(newData, numAtts);
                best = probNode.probabilityNode();
                return best;
            }

            if (leftRight == 0) { // Creating the left child
                //System.out.println("Creating new left child with " + newData.length + " elements..");
                leftChild = classify(newData, classesData, setEntropy); // Recursive call of method to build model
                best.setLeftChild(leftChild); // Node is returned as the left child
            } else { // Creating the right child
                //System.out.println("Creating new right child with " + newData.length + " elements..");
                rightChild = classify(newData, classesData, setEntropy); // Recursive call of method to build model
                best.setRightChild(rightChild); // Node is returned as the right child
            }
        }
        return best;
    }
    
    /**
     * Outputs the first run in the main method's "for loop" of runs
     * 
     * @param td - ArrayList of the results to output
     * @param i - Index of the first run, assumes the model has ran at least once
     * @param runAccuracy - Accuracy of the run being outputted
     * @throws IOException 
     */
    private static void outputToCSV(ArrayList<TestData> td, int i, float runAccuracy) throws IOException{
        if (i==0){ // As the method is called within the for loop, i is used to output the first ("0th") run
            
            // Prepare the FileWriter and output the headings
            FileWriter out = new FileWriter("Results.csv");
            float accuracy = runAccuracy;
            String runAcc = String.valueOf(accuracy);
            out.append("accuracy of run " + i + ":");
            out.append(',');
            out.append(runAcc + "%");
            out.append('\n');
            out.append("result");
            out.append(',');
            out.append("predicted");
            out.append(',');
            out.append("actual");
            out.append('\n');
            
            for (TestData t : td){ // Loop through td foreach TestData
                
                // Get the String values for each result and add to the .csv
                String TorF = String.valueOf(t.getResult());
                String predictedOut = t.getPredicted();
                String actualOut = t.getActual();
                out.append(TorF);
                out.append(",");
                out.append(predictedOut);
                out.append(",");
                out.append(actualOut);
                out.append("\n");

            }
            out.flush();
            out.close();
        }
    }
    
    /**
     * Returns the accuracy of a given test run
     * 
     * @param testResult - ArrayList of results to calculate the accuracy
     * @param totalCases - Total number of data cases
     * @return 
     */
    private static float getAccuracy(ArrayList<TestData> testResult, int totalCases) {
        int numTrue = 0;
        
        // Loop through each TestData object and get the number of correct predictions
        for (TestData t: testResult){
            if (t.getResult()){
                numTrue++;
            }
        }
        
        float accuracy = ((float) numTrue/(float) totalCases)*100;
        return accuracy;
    }

}
