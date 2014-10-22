package com.upenn.statistics;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

/**
 * Created by chengjia on 8/28/14.
 * This Class reads in a matrix with the colums being different regions,
 * and the rows being different subjects,
 * and perform TWO-SIDED ONE-SAMPLE Hotelling's T-test.
 */


public class HotellingOneSampleTTest {
    Array2DRowRealMatrix readsMat;
    Covariance regionCov;
    int numRegions;
    int numObs;

    public HotellingOneSampleTTest(Array2DRowRealMatrix readsMatrix){
        readsMat = readsMatrix;
        numObs = readsMatrix.getRowDimension();
        numRegions = readsMatrix.getColumnDimension();
        regionCov = new Covariance(readsMatrix);
    }

    Covariance getCovariance(){
        return(regionCov);
    }

    int getNumRegions(){
        return(numRegions);
    }

    int getNumObs(){
        return(numObs);
    }

}
