package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import static java.math.BigDecimal.ROUND_FLOOR;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;

public class Main {

    static TravelGuide myGuide;
    final static double BINS = 100;
    static long count = 0;

    public static void main(String[] args) {
        //doExhaustiveSearch();
    }

    private static void doExhaustiveSearch(){
        //The value of 14!
        final BigInteger PERMUTATIONS = factorial(148);
        //Parse data to create a usable dictionary
        myGuide = new TravelGuide("data/data.txt");
        //Call the permutation tester. This will call usePermutation once per permutation
        runPermutation();
        //Print answers
        try {
            printAnswers(PERMUTATIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger factorial(int factorialSize){
        BigInteger value = new BigInteger("1");
        while(factorialSize > 0){
            String currentFactorial = Integer.toString(factorialSize);
            value = value.multiply(new BigInteger(currentFactorial));
            factorialSize--;
        }
        return value;
    }

    private static void runPermutation(){
        PermutationTester.val = new int[PermutationTester.V + 1];
        for (int i = 0; i <= PermutationTester.V; i++)
            PermutationTester.val[0] = 0;
        PermutationTester.p(0);
    }

    public static void usePermutation(int[] values){
        count++;
        if(count % 100000 == 0) {
            System.out.println(count);
        }
        //Determine the cost of the trip
        double cost = myGuide.tripCost(values);

        //Add cost of this trip to mean for calculation later
        myGuide.setMean(myGuide.getMean().add(new BigDecimal(Double.toString(cost))));

        BigDecimal tripCost = new BigDecimal(Double.toString(cost));
        BigDecimal tripCostSquared = tripCost.multiply(tripCost);
        myGuide.setSumSquares(myGuide.getSumSquares().add(tripCostSquared));
        myGuide.setSumTrips(myGuide.getSumTrips().add(tripCost));

        //Set this cost as the min or max trip (so far), if necessary
        setMinOrMaxCost(cost, values);
    }

    private static void printAnswers(BigInteger permutations) throws IOException {
        //Setup PrintWriter for file output
        FileWriter fileWriter = new FileWriter("data/out.txt");
        PrintWriter printWriter = new PrintWriter(fileWriter);

        //Find standard deviation
        BigDecimal sumOfTrips = myGuide.getSumTrips(); //Mean has not been divided by n yet, so this is just a sum
        BigDecimal stdev = calculateStandardDeviation(permutations, sumOfTrips);
        myGuide.setStd(stdev.doubleValue());
        printWriter.printf("The standard deviation is: %f\n", myGuide.getStd());

        //Calculate final mean
        BigDecimal bigMean = myGuide.getMean();
        BigDecimal permutationDecimal = new BigDecimal(permutations);
        bigMean = bigMean.divide(permutationDecimal, 6, BigDecimal.ROUND_HALF_DOWN);
        myGuide.setMean(bigMean);
        printWriter.printf("The mean is: %f\n", myGuide.getMean());

        //Display final minumum and maximum cost
        printWriter.printf("The minimum cost is: %f\n", myGuide.getMinCost());
        printWriter.print("Minimum cost order: ");
        for(int i = 1; i < myGuide.getMinOrder().length; i++){
            printWriter.printf("%d ", myGuide.getMinOrder()[i]);
        }
        System.out.println();
        printWriter.println();
        printWriter.printf("The maximum cost is: %f\n", myGuide.getMaxCost());
        printWriter.print("Maximum cost order: ");
        for(int i = 1; i < myGuide.getMaxOrder().length; i++){
            printWriter.printf("%d ", myGuide.getMaxOrder()[i]);
        }
        printWriter.println();

        //Calculate bins, and store each trip in the appropriate bin
        //TODO: Fix
        double interval = (myGuide.getMaxCost() - myGuide.getMinCost())/ BINS;
        printWriter.printf("The interval for each bin is: %f\n", interval);
//        ArrayList<Double>[] filledBins = calculateBins(interval);
//        printWriter.println("Frequency of each bin:");
//        double startingInterval = myGuide.getMinCost();
//        for(int i = 0; i < filledBins.length; i++){
//            printWriter.printf("Bucket %d (%f to %f): Count = %d\n",
//                    i,
//                    startingInterval + interval*i,
//                    startingInterval + interval*(i+1),
//                    filledBins[i].size());
//        }

        printWriter.close();
    }

    private static BigDecimal calculateStandardDeviation(BigInteger permutations, BigDecimal sumOfTrips){
        BigDecimal numTrips = new BigDecimal(permutations);

        BigDecimal sumOfTripsSquared = sumOfTrips.multiply(sumOfTrips);
        BigDecimal numerator = myGuide.getSumSquares().subtract(sumOfTripsSquared.divide(numTrips, 15, ROUND_HALF_DOWN));
        BigDecimal denominator = numTrips;
        BigDecimal value = numerator.divide(denominator, 15, ROUND_HALF_UP);
        return sqrt(value, 15);
    }

    private static BigDecimal sqrt(BigDecimal A, final int SCALE) {
        final BigDecimal TWO = BigDecimal.valueOf(2);
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = A.divide(TWO, 15, ROUND_FLOOR);
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, ROUND_HALF_UP);

        }
        return x1;
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
//TODO: Fix
//    private static ArrayList<Double>[] calculateBins(double interval){
//        ArrayList<Double>[] filledBins = fillBins(interval);
//
//        return filledBins;
//    }

    //TODO: Fix
//    private static ArrayList<Double>[] fillBins(double interval){
//        ArrayList<Double> tripLengths = myGuide.getAllTripLengths();
//        ArrayList<Double>[] bins = initializeBins();
//        //Add each trip to the correct bucket
//        for(int i = 0; i < tripLengths.size(); i++){
//            double currentTrip = tripLengths.get(i);
//            //The following line normalizes the value to return a value 0-9,
//            //Which corresponds to the appropriate bucket to place that trip in
//            int bucketIndex = (int) Math.floor((currentTrip - myGuide.getMinCost())/interval);
//            if(bucketIndex == bins.length){
//                bucketIndex--;
//            }
//            bins[bucketIndex].add(currentTrip);
//        }
//        return bins;
//    }

    private static ArrayList<Double>[] initializeBins(){
        ArrayList<Double>[] bins = (ArrayList<Double>[])new ArrayList[(int) BINS];
        for(int i = 0; i < bins.length; i++){
            bins[i] = new ArrayList<>();
        }

        return bins;
    }


}
