package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static java.math.BigDecimal.ROUND_FLOOR;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;

public class Main {

    static TravelGuide myGuide;
    final static double BINS = 100;
    static long count = 0;

    public static void main(String[] args) {
        //doExhaustiveSearch();
//        doRandomSearch();
//        doGeneticSearch();

        doSimulatedAnnealing();
        //Some testing methods
//        testMedian();
//        testKill();
    }

    private static void testMedian(){
        HashMap<Integer, Double> testMap = new HashMap<>();
        testMap.put(0,1.0);
        testMap.put(1,2.0);
        testMap.put(2,3.0);
        testMap.put(3,4.0);
        testMap.put(4, 5.0);

        double testResult = calculateMedianFitness(testMap);
        System.out.println("Should be '3.0'");
        System.out.println(testResult);
    }

    private static void testKill(){
        ArrayList<ArrayList<Integer>> totalPopulation = new ArrayList<>();
        HashMap<Integer,Double> testMap = new HashMap<>();
        double medianFitness = 5.0;


//        kill()
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

//    private static ArrayList<Double>[] initializeBins(){
//        ArrayList<Double>[] bins = (ArrayList<Double>[])new ArrayList[(int) BINS];
//        for(int i = 0; i < bins.length; i++){
//            bins[i] = new ArrayList<>();
//        }
//
//        return bins;
//    }

    public static void doRandomSearch(){
        final int NUM_TRIPS = 1000000;
        final int NUM_CITIES = 14;
        TripData tripData = new TripData(NUM_TRIPS);
        tripData.setAllTripOrders(generateRandomTrips(NUM_TRIPS, NUM_CITIES));
        calculateTripData(tripData);
    }

    public static ArrayList<ArrayList<Integer>> generateRandomTrips(int numTrips, int numCities){
        ArrayList<ArrayList<Integer>> allTripOrders = new ArrayList<>();
        for(int x = 0; x < numTrips; x++){
            //Create random values
            ArrayList<Integer> trip = new ArrayList<>();
            Random r = new Random();
            for(int i = 0; i < numCities; i++) {
                int result = -1;
                boolean isUnique = false;
                //generates a number in range that is not already in ArrayList
                while(!isUnique) {
                    result = r.nextInt(numCities);
                    isUnique = !trip.contains(result);
                }
                trip.add(result);
            }
            allTripOrders.add(trip);
        }
        return allTripOrders;
    }

    public static void calculateTripData(TripData tripData){

        for(int i = 0; i < tripData.getAllTripOrders().size(); i++){
            //Calculate trip length
            double tripLength = tripData.tripCost(tripData.getAllTripOrders().get(i));
            tripData.setTripLength(i, tripLength);
            //Add trip length to running sum
            tripData.setSum(tripData.getSum() + tripLength);
            //Test for maximum and minimum
            tripData.setIfMaxOrMin(i, tripLength);
        }

        //Calculate the mean
        tripData.setMean(tripData.getSum() / tripData.getTripLengths().length);

        System.out.printf("\nMean is %f\nMaximum trip length is %f\nMax Order: %s\nMin trip length: %f\nMin order: %s",
                tripData.getMean(),
                tripData.getMaxTripLength(),
                tripData.getMaxTripLengthOrder(),
                tripData.getMinTripLength(),
                tripData.getMinTripLengthOrder());

        tripData.fillBins();
    }

    //A genetic search implemented using selective crossover
    private static void doGeneticSearch(){
        int generations = 1000;
        //Generate the initial population
        final int NUM_TRIPS = 1000;
        final int NUM_CITIES = 14;
        TripData tripData = new TripData(NUM_TRIPS);
        tripData.setAllTripOrders(generateRandomTrips(NUM_TRIPS, NUM_CITIES));
        ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<>();
        for(int i = 0; i < generations; i++) {
            //Reproduce two sets of offspring
            ArrayList<ArrayList<Integer>> totalPopulation = new ArrayList<>();
            totalPopulation.addAll(tripData.getAllTripOrders());
            totalPopulation.addAll(reproduce(tripData));
            //Determine fitness
            HashMap<Integer, Double> fitnessRating = calculateFitness(tripData, totalPopulation); //key = index, value = fitness
            double medianFitness = calculateMedianFitness(fitnessRating);
            //Eliminate bottom 50%
            newPopulation = new ArrayList<>();
            newPopulation.addAll(kill(totalPopulation, fitnessRating, medianFitness));
            tripData.setAllTripOrders(newPopulation);
        }
        calculateTripData(tripData);

    }

    //Method summary:
    //Selective crossover: For an n-sized AL of trip orders, take the first n/2 objects from parent A, and
    //Then iterate through parent B and append the n/2 objects that were not given from parent A
    //If n is odd, then take the first n//2+1 values from Parent A
    private static ArrayList<ArrayList<Integer>> reproduce(TripData tripData){
        ArrayList<ArrayList<Integer>> offspring = new ArrayList<>();

        int populationSize = tripData.getAllTripOrders().size();
            //Create offspring by the first and last
            for(int i = 0; i < populationSize/2; i++){
                offspring.add(createOffspringByOpposites(tripData, populationSize, i));
            }
            //Create offspring in pairs
            if(populationSize % 2 != 0){
                for(int i = 0; i < populationSize-1; i+=2) {
                    offspring.add(createOffspringByPairs(tripData, populationSize, i));
                }
            }
            else{
                for(int i = 0; i < populationSize; i+=2) {
                    offspring.add(createOffspringByPairs(tripData, populationSize, i));
                }
            }
        return offspring;
    }

    private static ArrayList<Integer> createOffspringByOpposites(TripData tripData, int populationSize, int i){
        //Create parents
        ArrayList<Integer> parentA = new ArrayList<>();
        parentA.addAll(tripData.getAllTripOrders().get(i));
        ArrayList<Integer> parentB = new ArrayList<>();
        parentB.addAll(tripData.getAllTripOrders().get(populationSize-i-1));

        //Create offspring1
        ArrayList<Integer> offspring1 = new ArrayList<>();
        if(parentA.size() %2 == 0){
            for(int j = 0; j < parentA.size()/2; j++){
                offspring1.add(parentA.get(j));
            }
            //Selective crossover of Parent B
            for(int j = 0; j < parentB.size(); j++){
                int currentIndex = parentB.get(j);
                if(!offspring1.contains(currentIndex)){
                    offspring1.add(currentIndex);
                }
            }
        } else {
            for (int j = 0; j < parentA.size() / 2 + 1; j++) {
                offspring1.add(parentA.get(j));
            }
            //Selective crossover of Parent B
            int offspringIndex = parentA.size() / 2 + 1;
            for (int j = 0; j < parentB.size(); j++) {
                int currentIndex = parentB.get(j);
                if (!offspring1.contains(currentIndex)) {
                    offspring1.add(currentIndex);
                    offspringIndex++;
                }
            }
        }
        return offspring1;
    }

    private static ArrayList<Integer> createOffspringByPairs(TripData tripData, int populationSize, int i) {
        //Create parents
        ArrayList<Integer> parentA = new ArrayList<>();
        parentA.addAll(tripData.getAllTripOrders().get(i));
        ArrayList<Integer> parentB = new ArrayList<>();
        parentB.addAll(tripData.getAllTripOrders().get(i + 1));

        //Create offspring2
        ArrayList<Integer> offspring2 = new ArrayList<>();
        if (parentA.size() % 2 == 0) {
            for (int j = 0; j < parentA.size() / 2; j++) {
                offspring2.add(parentA.get(j));
            }
            //Selective crossover of Parent B
            for (int j = 0; j < parentB.size(); j++) {
                int currentIndex = parentB.get(j);
                if (!offspring2.contains(currentIndex)) {
                    offspring2.add(currentIndex);
                }
            }
        } else {
            for (int j = 0; j < parentA.size() / 2 + 1; j++) {
                offspring2.add(parentA.get(j));
            }
            //Selective crossover of Parent B
            for (int j = 0; j < parentB.size(); j++) {
                int currentIndex = parentB.get(j);
                if (!offspring2.contains(currentIndex)) {
                    offspring2.add(currentIndex);
                }
            }
        }
        return offspring2;
    }

    private static HashMap<Integer, Double> calculateFitness(TripData tripData, ArrayList<ArrayList<Integer>> totalPopulation){
        HashMap<Integer, Double> fitnessMap = new HashMap<Integer, Double>();
        for(int i = 0; i < totalPopulation.size(); i++){
            ArrayList<Integer> currentTrip = totalPopulation.get(i);
            double tripCost = tripData.tripCost(currentTrip);
            fitnessMap.put(i, tripCost);
        }

        return fitnessMap;
    }

    private static double calculateMedianFitness(HashMap<Integer, Double> fitnessRating){
        Object[] fitnessValues = fitnessRating.values().toArray();
        double[] fitnessDoubles = new double[fitnessValues.length];
        for(int i = 0; i < fitnessDoubles.length; i++){
            fitnessDoubles[i] = Double.parseDouble(fitnessValues[i].toString());
        }
        Arrays.sort(fitnessDoubles);
        if(fitnessDoubles.length % 2 !=0 ){
            return fitnessDoubles[fitnessDoubles.length/2];
        }
        else{
            double[] middleValues = new double[2];
            middleValues[1] = fitnessDoubles[fitnessDoubles.length/2];
            middleValues[0] = fitnessDoubles[fitnessDoubles.length/2 - 1];
            double median = (middleValues[0] + middleValues[1]) / 2.0;
            return median;
        }
    }

    private static ArrayList<ArrayList<Integer>> kill(ArrayList<ArrayList<Integer>> totalPopulation,HashMap<Integer,Double> fitnessRating,double medFitness){

        int killCount = totalPopulation.size() - 1000; //adjust elimination based on population changes


        int currentIndex = 0;
        int startingSize = totalPopulation.size();
        for(int i = 0; i < startingSize; i++){
            if(killCount > 0) {
                if (fitnessRating.get(i) >= medFitness) {
                    totalPopulation.remove(currentIndex);
                    killCount--;
                } else {
                    currentIndex++;
                }
            }
        }
        return totalPopulation;
    }

    private static void doSimulatedAnnealing(){
        double coolingConstant = 0.0003;
        TripData tripData = new TripData(50);
        for(int i = 0; i < 50; i++) {

            //Create initial random solutions
            ArrayList<Integer> bestSolution = generateRandomTrips(1, 14).get(0);
            int temperature = 1000;
            while (temperature > 0) {
                //Create a small change in the current best solution
                ArrayList<Integer> newSolution = new ArrayList<>();
                newSolution.addAll(bestSolution);

                //Swap two random cities for minor change
                Random random = new Random();
                int pivotOne;
                int pivotTwo;
                do {
                    pivotOne = random.nextInt(14);
                    pivotTwo = random.nextInt(14);
                } while (pivotOne == pivotTwo);
                int temp = newSolution.get(pivotOne);
                newSolution.set(pivotOne, newSolution.get(pivotTwo));
                newSolution.set(pivotTwo, temp);

                //Determine fitness
                double newSolutionCost = tripData.tripCost(newSolution);
                double bestSolutionCost = tripData.tripCost(bestSolution);

                //Acceptance function
                boolean isAcceptable = shouldAccept(newSolutionCost, bestSolutionCost, temperature);
                if (isAcceptable) {
                    bestSolution = new ArrayList<>();
                    bestSolution.addAll(newSolution);
                }

                temperature -= coolingConstant;
            }
            tripData.getAllTripOrders().add(bestSolution);
        }
        //Get data
        calculateTripData(tripData);
    }

    private static boolean shouldAccept(double newSolutionCost, double bestSolutionCost, int temperature){
        boolean isAcceptable = false;
        double probability = determineProbability(newSolutionCost, bestSolutionCost, temperature);
        if(probability > .999){
            isAcceptable = true;
        }
        return  isAcceptable;
    }

    private static double determineProbability(double newSolutionCost, double bestSolutionCost, int temperature){
        if(newSolutionCost < bestSolutionCost){
            return 1.0;
        } else {
            double prob = Math.exp((bestSolutionCost - newSolutionCost) / temperature);
            return prob;
        }
    }


}
