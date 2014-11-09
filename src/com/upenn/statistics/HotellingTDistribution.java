package com.upenn.statistics;

/**
 * Created by chengjia on 8/28/14.
 */
import org.apache.commons.math3.distribution.FDistribution;

public class HotellingTDistribution {
    int p;
    int m;
    FDistribution FDist;

    public HotellingTDistribution(int dimensionalityPar, int degreesOfFreedom)
    {
        p = dimensionalityPar;
        m = degreesOfFreedom;
        FDist = new FDistribution((double)p,(double)(m-p+1));
    }


    public double getPValue(double q){
        return(1-this.FDist.cumulativeProbability((double)(m-p+1)/(double)p/(double)m*q));
    }

}