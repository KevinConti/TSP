package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripData {
    Map<Integer, double[]> coordinates;
    private ArrayList<ArrayList<Integer>> allTripOrders;
    private double[] tripLengths;
    private ArrayList<Double>[] bins;
    private double sum;
    private double mean;
    private double maxTripLength;
    private ArrayList<Integer> maxTripLengthOrder;
    private double minTripLength;
    private ArrayList<Integer> minTripLengthOrder;
    private double standardDeviation;

    public final double BIN_SIZE = 0.078144;
    public final int NUM_BINS = 100;

    public TripData(int numberOfTrips) {
        coordinates = new HashMap<>();
        tripLengths = new double[numberOfTrips];
        allTripOrders = new ArrayList<>();
        bins = initializeBins();
        mean = standardDeviation = 0.0;
        maxTripLength = -999999;
        minTripLength = 999999;
        maxTripLengthOrder = new ArrayList<>();
        minTripLengthOrder = new ArrayList<>();

        try {
            File file = new File("data/data.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int cityIndex = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(" ");
                double[] currentCoordinates = new double[2];
                currentCoordinates[0] = Double.parseDouble(splited[0]);
                currentCoordinates[1] = Double.parseDouble(splited[1]);

                coordinates.put(cityIndex, currentCoordinates);
                cityIndex++;
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double[] getTripLengths() {
        return tripLengths;
    }

    public void setTripLengths(double[] tripLengths){
        this.tripLengths = tripLengths;
    }

    public void setTripLength(int index, double tripLength){
        tripLengths[index] = tripLength;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getMaxTripLength() {
        return maxTripLength;
    }

    public void setMaxTripLength(double maxTripLength) {
        this.maxTripLength = maxTripLength;
    }

    public ArrayList<Integer> getMaxTripLengthOrder() {
        return maxTripLengthOrder;
    }

    public void setMaxTripLengthOrder(ArrayList<Integer> order) {
        maxTripLengthOrder = new ArrayList<>();

        for (int i = 0 ; i<order.size();i++){
            maxTripLengthOrder.add(order.get(i)) ;
        }
    }

    public double getMinTripLength() {
        return minTripLength;
    }

    public void setMinTripLength(double minTripLength) {
        this.minTripLength = minTripLength;
    }

    public ArrayList<Integer> getMinTripLengthOrder() {
        return minTripLengthOrder;
    }

    public void setMinTripLengthOrder(ArrayList<Integer> order) {
        minTripLengthOrder = new ArrayList<>() ;

        for (int i = 0 ; i<order.size();i++){
            minTripLengthOrder.add(order.get(i)) ;
        }
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public Map<Integer, double[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Map<Integer, double[]> coordinates) {
        this.coordinates = coordinates;
    }

    public double getCityX(int cityIndex){
        double[] cityXAndY = coordinates.get(cityIndex);
        return cityXAndY[0];
    }

    public double getCityY(int cityIndex){
        double[] cityXAndY = coordinates.get(cityIndex);
        return cityXAndY[1];
    }

    public ArrayList<ArrayList<Integer>> getAllTripOrders() {
        return allTripOrders;
    }

    public void setAllTripOrders(ArrayList<ArrayList<Integer>> allTripOrders) {
        this.allTripOrders = allTripOrders;
    }

    public ArrayList<Double>[] getBins() {
        return bins;
    }

    public void setBins(ArrayList<Double>[] bins) {
        this.bins = bins;
    }

    //Instance Methods

    public double travelCost(int fromCityIndex, int toCityIndex){
        double deltaXSquared = Math.pow(this.getCityX(fromCityIndex) - this.getCityX(toCityIndex), 2);
        double deltaYSquared = Math.pow(this.getCityY(fromCityIndex) - this.getCityY(toCityIndex), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }

    public double tripCost(ArrayList<Integer> indexes){
        double sum = 0.0;

        for(int i = 0; i < indexes.size()-1; i++){
            sum += this.travelCost(indexes.get(i), indexes.get(i+1));
        }
        sum += this.travelCost(indexes.get(indexes.size() - 1), indexes.get(0));

        return sum;
    }

    public boolean setIfMaxOrMin(int indexOfTripInAllTrips, double tripCost){
        ArrayList<Integer> trip = this.getAllTripOrders().get(indexOfTripInAllTrips);
        boolean wasChanged = false;
        if(tripCost > this.getMaxTripLength()){
            this.setMaxTripLength(tripCost);
            this.setMaxTripLengthOrder(trip);
            wasChanged = true;
        }
        if(tripCost < this.getMinTripLength()){
            this.setMinTripLength(tripCost);
            this.setMinTripLengthOrder(trip);
            wasChanged = true;
        }
        return wasChanged;
    }

    public void fillBins(TripData tripData) {
        double[] tripLengths = tripData.getTripLengths();
        //Add each trip to the correct bucket
        for (int i = 0; i < tripLengths.length; i++) {
            double currentTrip = tripLengths[i];
            //The following line normalizes the value to return a value 0-9,
            //Which corresponds to the appropriate bucket to place that trip in
            int bucketIndex = (int) Math.floor((currentTrip - tripData.getMinTripLength()) / tripData.BIN_SIZE);
            if (bucketIndex == bins.length) {
                bucketIndex--;
            }
            bins[bucketIndex].add(currentTrip);
        }
    }

    private ArrayList<Double>[] initializeBins(){
        ArrayList<Double>[] bins = (ArrayList<Double>[])new ArrayList[(int) NUM_BINS];
        for(int i = 0; i < bins.length; i++){
            bins[i] = new ArrayList<>();
        }

        return bins;
    }

}
