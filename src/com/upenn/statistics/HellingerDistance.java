package com.upenn.statistics;

/**
 * Created by cheng on 9/30/14.
 */
public class HellingerDistance {
    double[] p1;
    double[] p2;

    public HellingerDistance(double[] p1, double[] p2) {
        this.p1 = p1;
        this.p2 = p2;
        assert this.p1.length==this.p2.length;
    }

    double getDistance(){
        double distance=0;
        for(int i=0;i<this.p1.length;i++){
            distance = distance + Math.pow((Math.sqrt(p1[i])-Math.sqrt(p2[i])),2);
        }
        return Math.sqrt(distance/2);
    }

}
