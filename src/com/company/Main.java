package com.company;

import java.util.ArrayList;

public class Main {

    static TravelGuide myGuide;
    final static double BINS = 10;

    public static void main(String[] args) {
        doExhaustiveSearch();
    }

    private static void doExhaustiveSearch(){
        //The value of 14!
        final double PERMUTATIONS = 6*5*4*3*2;
        //Parse data to create a useable dictionary
        myGuide = new TravelGuide("data/data.txt");
        //Call the permutation tester. This will call usePermutation once per permutation
        runPermutation();
        //Print answers
        printAnswers(PERMUTATIONS);
    }

    private static void runPermutation(){
        PermutationTester.val = new int[PermutationTester.V + 1];
        for (int i = 0; i <= PermutationTester.V; i++)
            PermutationTester.val[0] = 0;
        PermutationTester.p(0);
    }

    public static void usePermutation(int[] values){

        //Determine the cost of the trip
        double cost = myGuide.tripCost(values);

        //Add cost of this trip to mean for calculation later
        myGuide.setMean(myGuide.getMean() + cost);

        //Set this cost as the min or max trip (so far), if necessary
        setMinOrMaxCost(cost, values);
    }

    private static void printAnswers(double permutations){
        //Find standard deviation
        double sumOfTrips = myGuide.getMean();
        double stdev = calculateStandardDeviation(sumOfTrips);
        System.out.printf("The standard deviation is: %f\n", stdev);

        //Calculate final mean
        myGuide.setMean(myGuide.getMean() / permutations);
        System.out.printf("The mean is: %f\n", myGuide.getMean());

        //Display final minumum and maximum cost
        System.out.printf("The minimum cost is: %f\n", myGuide.getMinCost());
        System.out.print("Minimum cost order: ");
        for(int element: myGuide.getMinOrder()){
            System.out.printf("%d ", element);
        }
        System.out.println();
        System.out.printf("The maximum cost is: %f\n", myGuide.getMaxCost());
        System.out.print("Maximum cost order: ");
        for(int element: myGuide.getMaxOrder()){
            System.out.printf("%d ", element);
        }
        System.out.println();

        //Calculate bins, and store each trip in the appropriate bin
        ArrayList<Double>[] filledBins = calculateBins();
    }

    private static double calculateStandardDeviation(double sumOfTrips){
        ArrayList<Double> allTrips = myGuide.getAllTripLengths();
        int numTrips = allTrips.size();

        //Calculate squares of each trip
        double sumsSquared = 0.0;
        for(int i = 0; i < numTrips; i++){
            Double currentTrip = allTrips.get(i);
            sumsSquared += Math.pow(currentTrip, 2);
        }
        double sumOfTripsSquared = Math.pow(sumOfTrips, 2);

        double numerator = sumsSquared - sumOfTripsSquared/numTrips;
        double denominator = numTrips;
        return Math.sqrt(numerator/denominator);
    }

    private static void setMinOrMaxCost(double cost, int[] values){
        if(cost < myGuide.getMinCost()){
            myGuide.setMinCost(cost);
            myGuide.setMinOrder(values);
        }
        if(cost > myGuide.getMaxCost()){
            myGuide.setMaxCost(cost);
            myGuide.setMaxOrder(values);
        }
    }

    private static ArrayList<Double>[] calculateBins(){
        //Determine the interval for the number of bins
        double interval = (myGuide.getMaxCost() - myGuide.getMinCost())/ BINS;
        System.out.printf("The interval for the bins is: %f\n", interval);

        ArrayList<Double>[] filledBins = fillBins(interval);

        return filledBins;
    }

    private static ArrayList<Double>[] fillBins(double interval){
        ArrayList<Double> tripLengths = myGuide.getAllTripLengths();
        ArrayList<Double>[] bins = initializeBins();
        //Add each trip to the correct bucket
        for(int i = 0; i < tripLengths.size(); i++){
            double currentTrip = tripLengths.get(i);
            //The following line normalizes the value to return a value 0-9,
            //Which corresponds to the appropriate bucket to place that trip in
            int bucketIndex = (int) Math.floor((currentTrip - myGuide.getMinCost())/interval);
            if(bucketIndex == bins.length){
                bucketIndex--;
            }
            bins[bucketIndex].add(currentTrip);
        }
        return bins;
    }

    private static ArrayList<Double>[] initializeBins(){
        ArrayList<Double>[] bins = (ArrayList<Double>[])new ArrayList[(int) BINS];
        for(int i = 0; i < bins.length; i++){
            bins[i] = new ArrayList<>();
        }

        return bins;
    }


}
