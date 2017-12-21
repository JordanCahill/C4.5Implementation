
package machinelearningc45;

/**
 *
 * @author Jordan Cahill
 * @date 21-Nov-2017
 * 
 * Class used to represent the "nodes" on a decision tree. Contains a label, 
 * values for the parent and children of the node, and the split threshold, information
 * gain and the attribute to split on at that node.
 * 
 */
public class Node {
    
    private String label;
    private int splitCol; // Column containing the attribute to split on
    private Node parent;
    private Node childR;
    private Node childL;
    private double threshold; // Split threshold
    private double infoGain; // Information gain at that node
    private String[][] threshDat; // New data to feed into classify()
    private int numAttributes;
    
    // In case of probability nodes
    private String value1;
    private String value2;
    private double prob1; // Probability of choosing value 1
    private double prob2; // Probability of choosing value 2
    
    // Generic node constructor
    public Node(String label, int s, Node p, Node r, Node l, double t, double gain){
        this.label = label;
        splitCol = s;
        parent = p;
        childR = r;
        childL = l;
        threshold = t;
        infoGain = gain;
        
    }

    // Constructor used to create probability nodes
    Node(String[][] newData, int numAtts) {
        this.threshDat = newData;
        this.numAttributes = numAtts;
    }

    // Probability node constructor
    private Node(String title, String v1, String v2, double probability1, double probability2) {
        this.label = title;
        this.value1 = v1;
        this.value2 = v2;
        this.prob1 = probability1;
        this.prob2 = probability2;
    }
    
    /**
     * Used to create a probability node
     * @return - Probability node object 
     */
    Node probabilityNode() {
        String v1 = threshDat[1][numAttributes];
        String v2 = "Error, no value added to probability node";
        boolean valFound = false;
        double total = threshDat.length-1;
        double count1 = 1;
        double count2 = 0;
        
        for(int i=2; i<total; i++){ // Skip first element and loop over data set						
            if (! (threshDat[i][numAttributes].equals(v1)) ){ // Find the class of the second element	
                    v2 = threshDat[i][numAttributes];
                    valFound = true;
                    count2++; // Count the number of occurences for v2									
            }
            else{
                    count1++; // Count the number of occurences for v1
            }
        }
        
        if(valFound == true){ // Create a probability node by dividing the occurences by the total
            double probability1 = count1/total;
            double probability2 = count2/total;
            return new Node("Probability",v1,v2,probability1,probability2);
        }else{
            return null;
        }
    }
    /** Generic node getters **/
    public double getInformationGain() {
        return this.infoGain;
    }
    public int getSplitColumn(){
        return this.splitCol;
    }  
    public String getTitle(){
        return this.label;
    }
    public double getThreshold(){
        return this.threshold;
    }
    public Node getLeftChild() {
        return childL;
    }
    public Node getRightChild() {
        return childR;
    }
    /** Generic node setters **/
     void setLeftChild(Node leftChild) {
         this.childL = leftChild;
    }
    void setRightChild(Node rightChild) {
        this.childR = rightChild;
    }    
    /** Probability node getters **/
    public double getProb1(){
        return prob1;
    }
    public double getProb2(){
        return prob2;
    }
    public String getValOfClass1(){
        return value1;
    }
    public String getValOfClass2(){
        return value2;
    }

}
