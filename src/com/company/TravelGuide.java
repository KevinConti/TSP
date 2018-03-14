package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//This class takes the data and converts it into useable format
public class TravelGuide {
    Map<Integer, double[]> coordinates;
    private BigDecimal mean;
    private double std;
    private double minCost;
    private BigDecimal sumTrips;
    private BigDecimal sumSquares;
    private int[] minOrder;
    private double maxCost;
    private int[] maxOrder;

    public TravelGuide(String filepath) {
        coordinates = new HashMap<>();
        mean = new BigDecimal("0");
        std = 0.0;
        minCost = 999999;
        maxCost = -999999;
        minOrder = new int[PermutationTester.V + 1];
        maxOrder = new int[PermutationTester.V + 1];
        sumTrips = new BigDecimal("0");
        sumSquares = new BigDecimal("0");

        try {
            File file = new File(filepath);
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

    public double travelCost(int fromCityIndex, int toCityIndex){
        fromCityIndex--;
        toCityIndex--;
        double deltaXSquared = Math.pow(this.getCityX(fromCityIndex) - this.getCityX(toCityIndex), 2);
        double deltaYSquared = Math.pow(this.getCityY(fromCityIndex) - this.getCityY(toCityIndex), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }

    public double tripCost(int[] indexArray){
        double sum = 0.0;

        for(int i = 1; i < indexArray.length-1; i++){
            sum += this.travelCost(indexArray[i], indexArray[i+1]);
        }
        sum += this.travelCost(indexArray[indexArray.length-1], indexArray[1]);

        return sum;
    }

    //GETTERS AND SETTERS

    public double[] getCityCoordinates(int cityIndex){
        return coordinates.get(cityIndex);
    }

    public double getCityX(int cityIndex){
        double[] city = coordinates.get(cityIndex);
        return city[0];
    }

    public double getCityY(int cityIndex){
        double[] city = coordinates.get(cityIndex);
        return city[1];
    }

    public BigDecimal getMean() {
        return mean;
    }

    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getMinCost() {
        return minCost;
    }

    public void setMinCost(double minCost) {
        this.minCost = minCost;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public int[] getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(int[] newMinOrder) {
        for(int i = 0; i < minOrder.length; i++){
            minOrder[i] = newMinOrder[i];
        }
    }

    public int[] getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(int[] newMaxOrder) {
        for(int i = 0; i < maxOrder.length; i++){
            maxOrder[i] = newMaxOrder[i];
        }
    }

    public BigDecimal getSumTrips() {
        return sumTrips;
    }

    public void setSumTrips(BigDecimal sumTrips) {
        this.sumTrips = sumTrips;
    }

    public BigDecimal getSumSquares() {
        return sumSquares;
    }

    public void setSumSquares(BigDecimal sumSquares) {
        this.sumSquares = sumSquares;
    }

    @Override
    public String toString() {
        return "TravelGuide{" +
                "coordinates=" + coordinates +
                '}';
    }
}
