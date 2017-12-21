package machinelearningc45;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Jordan Cahill
 * @date 20-Nov-2017
 * 
 * This class is used to read data in from a CSV file, and parse it as a 2D String array
 * 
 */
public class CSVData{
    
    public ArrayList<ArrayList<String>> Data = new ArrayList<>();
    public int rows; // Row count
    public int cols; // Column count
       
    public CSVData(String csvFile){ // Constructor to take in the location path of the csv file
        
        // Data set is read from the location using the "ReadCSV" class method and parsed to a 2D String arraylist
        File DataFile = new File(csvFile); // Creates a file object using the csvFile location
        ReadCSV inner = new ReadCSV(); // Class used to invoke the ReadCSVFile method
	ArrayList<ArrayList<String>> dataIn = inner.readCSVFile(DataFile);	
    }
    
    
    /**
     * Method used to read the CSV into the project, the main class defines the variables used by the actual 
     * method to return a 2D String arraylist
     */
    public class ReadCSV{
        
        private final ArrayList<String[]> data = new ArrayList<>();
        private String[] row;
        private int colNums;
        private int counter;
        private int colIndex;
        private boolean first = true;
        
        /**
         * 
         * Method uses a buffered reader to take in the csv file and return it as an array of strings
         * 
         * @param csvFile the File Object of the csv
         * @return - 2D String arraylist taken from the csv
         */
        public ArrayList<ArrayList<String>> readCSVFile(File csvFile){
            
            BufferedReader br = null;
            String delimiter = ",|\\s|;"; // Comma and 'next line' used as delimiters
                        
            try {

            br = new BufferedReader(new FileReader(csvFile)); // Define the buffered reader with the csvFile Object
                      
            while (br.ready()) { // While there is more data to read

                String line = br.readLine(); // Read each line as a string
                row = line.split(delimiter); // Split the line based on the comma and next line delimiters
                
                if (first == true){ // Structure the data										
                    	colNums = row.length;				
                    	while(counter < colNums){ // Loop through the columns									
                    		Data.add(new ArrayList<>());
                    		counter++;
                    	}
                    	first=false; // Only want to loop through the first row								
                }
      
                for(ArrayList<String> col: Data){ // Iterate through the data set and add each row to the ArrayList			
                    	
                        data.add(row);
                    	col.add(row[colIndex]);
                    	colIndex++;                  
                    }
                colIndex = 0; // Reset the index
                rows++;
                

            }
                       

            // Catch blocks
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (br != null) {
                    try {
                        br.close(); // Close the buffer
                    } catch (IOException e) {
                    }
                }
            }
            
            return Data;
        }
    }
    
    /**
     * Used to convert the 2D ArrayList to a 2D String Array
     * 
     * @return - 2D String Array 
     */
    public  String[][] toArray(){
        int columnCount = Data.size();
        int currentColumn = 0;
        int rowCount = rows;
        int currentRow = 0;
        String[][] datArray = new String[rowCount][columnCount]; // Preparing the array to return

        for(ArrayList<String> columnValue : Data){ // Loop through each column
            while(currentRow < rowCount){ // Loop through each row in the current column
                datArray[currentRow][currentColumn] = columnValue.get(currentRow); // Add the current element to the array
                currentRow++;
            }
            currentColumn++;
            currentRow=0; // Reset the row index
        }
        return datArray;
    }
    
    public int getColumns(){ // Returns the number of columns in the data set
        return Data.size();
    }
    
    public int getRows(){ // Returns the number of rows in the data set
        return rows;
    }
}


