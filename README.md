This project is built to implement the C4.5 Machine Learning Algorithm for the CT475 module.
 
The location of the data file used to build and analyse the model is hardcoded programmatically and must be of 
type .csv. The first row of the data must have attribute titles, and the data should not have any missing values. The 
dependent class variables must be the column furthest to the right.

The program begins by allowing the user to input the size of the data set to be used as a training set, as well
as the number of times the model runs to get an overall average accuracy. The accuracy of each run and the overall
accuracy is printed to the console, and a sample run is outputted to a csv file.

A sample dataset "owls15.csv" is included for testing purposes.