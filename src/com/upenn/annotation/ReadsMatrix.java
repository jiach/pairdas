package com.upenn.annotation;

import com.upenn.statistics.HotellingTDistribution;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.inference.TTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by chengjia on 9/9/14.
 */
public class ReadsMatrix {

    int[][] matrixRawReads;
    private int subjectNumber;
    Set<String> uniqueID=null;
    String[] subjectID;
    int[][] subjectReadsMatrix;//rows: person, columns, before/after
    int[][] beforeReadMatrix;
    int[][] afterReadMatrix;
    Array2DRowRealMatrix logRatioMatrix; //the above 3 matrices. columns: region, rows: person
    String[] uniqueSubjectIDList;
    GeneIsoformInfo geneIsoformInfo;

    List<double[][]> listLogRatioMatrices = new ArrayList<double[][]>();
    List<double[][]> listLogRatioMatricesSignExcl = new ArrayList<double[][]>();
    public ReadsMatrix(List<String> subjectID, List<Integer> readsBefore, List<Integer> readsAfter, GeneIsoformInfo geneIsoformInfo) {
        matrixRawReads =new int[subjectID.size()][2];
        for(int i=0;i<subjectID.size();i++){
            this.matrixRawReads[i][0]=readsBefore.get(i);
            this.matrixRawReads[i][1]=readsAfter.get(i);
        }
        this.subjectID = new String[subjectID.size()];
        subjectID.toArray(this.subjectID);
        this.uniqueID = new TreeSet<String>();
        this.uniqueID.addAll(subjectID);
        this.subjectNumber=this.uniqueID.size();
        this.computeSubjectReadsMatrix();
        this.geneIsoformInfo = geneIsoformInfo;
        this.computeLogRatioMatrix();
        this.testMatrixDimensionality(this.geneIsoformInfo);
    }

    private void computeLogRatioMatrix(){
        int numReg = this.geneIsoformInfo.getNumberRegions();
        assert this.subjectID.length == numReg * this.subjectNumber;

        this.beforeReadMatrix = new int[this.subjectNumber][numReg];
        this.afterReadMatrix = new int[this.subjectNumber][numReg];
        double[][] logRatioMatrix = new double[this.subjectNumber][numReg];
        for (int i = 0; i < this.subjectNumber; i++) {
            for (int j = 0; j < numReg; j++) {
                this.beforeReadMatrix[i][j]=this.matrixRawReads[i*numReg+j][0];
                this.afterReadMatrix[i][j]=this.matrixRawReads[i*numReg+j][1];
                logRatioMatrix[i][j]=java.lang.Math.log(this.beforeReadMatrix[i][j]+0.5)
                        -java.lang.Math.log(this.afterReadMatrix[i][j]+0.5)
                        -java.lang.Math.log(this.subjectReadsMatrix[i][0]+0.5)
                        +java.lang.Math.log(this.subjectReadsMatrix[i][1]+0.5);
            }
        }
        this.logRatioMatrix=new Array2DRowRealMatrix(logRatioMatrix);
    }

    public void printSubjectReadsMatrix(){
        System.out.println(Arrays.deepToString(this.subjectReadsMatrix));
    }
    /*didn't consider data malformation here. Need perfect equally sized blocks of data!*/
    private void computeSubjectReadsMatrix() {
        this.subjectReadsMatrix= new int[this.subjectNumber][2];
        this.uniqueSubjectIDList = new String[this.subjectNumber];
        for(int i = 0; i<this.subjectReadsMatrix.length;i++){
            for(int j=0;j<this.subjectReadsMatrix[0].length;j++){
                this.subjectReadsMatrix[i][j]=0;
            }
        }
        String prevIDString ="";
        int itrSubjectReadsMatrix = -1;
        for(int i=0;i<this.matrixRawReads.length;i++){
            if(!this.subjectID[i].equals(prevIDString)){
                itrSubjectReadsMatrix+=1;
                prevIDString=this.subjectID[i];
                this.uniqueSubjectIDList[itrSubjectReadsMatrix]=this.subjectID[i];
            }
            //System.out.println(Integer.toString(itrSubjectReadsMatrix)+","+this.subjectID[i]+","+prevIDString);
            this.subjectReadsMatrix[itrSubjectReadsMatrix][0]+=this.matrixRawReads[i][0];
            this.subjectReadsMatrix[itrSubjectReadsMatrix][1]+=this.matrixRawReads[i][1];
        }
    }

    public void testMatrixDimensionality(GeneIsoformInfo geneIsoformInfo) {
        int numberRegions = geneIsoformInfo.numberRegions;
        Iterator<String> itr=this.uniqueID.iterator();
        while(itr.hasNext()){
            String curSubIds=itr.next();
            int curSubCount=0;
            for (int i = 0; i < subjectID.length; i++) {
                if (subjectID[i]==curSubIds){
                    curSubCount++;
                }
            }
            assert(curSubCount==numberRegions);
        }
    }

    public int getSubjectNumber() {
        return this.subjectNumber;
    }

    public void printSubjectID(){
        System.out.println(this.uniqueID.toString());
    }

    /*OK, the getHotellingTPVals() method assumes BLOCK input
    * Input need to be in blocks with the data of one subject in one block
    * and sorted according to region.*/
    public Double getFishersPValue(Boolean verbose){
        Double[] rawPValues = this.getHotellingTPValsPairedTSignExcl(false);
        if (rawPValues.length==0) return Double.valueOf(-1);
        ChiSquaredDistribution chiSquared = new ChiSquaredDistribution(2*rawPValues.length);
        Double chiStat = (double)0;
        Log logFunc = new Log();
        for (int i = 0; i < rawPValues.length; i++) {
            if (rawPValues[i]>0){
                chiStat += (-2*logFunc.value(rawPValues[i]));
            }
        }
        return 1-chiSquared.cumulativeProbability(chiStat);
    }

    public Double[] getHotellingTPValsSignInc(Boolean verbose){
        Iterator<GroupInfo> itrRegion=this.geneIsoformInfo.listRegionGroup.listRegionGroup.iterator();
        List<Double> allPVals = new ArrayList<Double>();
        List<String> isoformNames = new ArrayList<String>();
        int[] allRowsSelector = new int[this.subjectNumber];
        for (int i = 0; i < allRowsSelector.length; i++) {
            allRowsSelector[i]=i;
        }
        while(itrRegion.hasNext()){
            GroupInfo curGroup = itrRegion.next();
            allPVals.add(this.getHotellingTPvalsFromMatrix(this.logRatioMatrix.getSubMatrix(allRowsSelector,curGroup.getRegionIdx()),verbose));
        }
        return allPVals.toArray(new Double[allPVals.size()]);
    }

    public Double[] getHotellingTPValsSignExcl(Boolean verbose){
        Iterator<SignlessGroupInfo> itrRegion=this.geneIsoformInfo.listRegionGroup.listRegionGroupSignExcl.iterator();
        List<Double> allPVals = new ArrayList<Double>();
        int[] allRowsSelector = new int[this.subjectNumber];
        for (int i = 0; i < allRowsSelector.length; i++) {
            allRowsSelector[i]=i;
        }
        while(itrRegion.hasNext()){
            SignlessGroupInfo curGroup = itrRegion.next();
            allPVals.add(this.getHotellingTPvalsFromMatrix(this.logRatioMatrix.getSubMatrix(allRowsSelector,curGroup.getRegionIdx()),verbose));
        }
        return allPVals.toArray(new Double[allPVals.size()]);
    }

    public Double getHotellingTPValsUngrouped(Boolean verbose){
        return this.getHotellingTPvalsFromMatrix(this.logRatioMatrix,verbose);
    }

    public Double[] getHotellingTPValsPairedTSignExcl(Boolean verbose){
        Iterator<SignlessGroupInfo> itrRegion=this.geneIsoformInfo.listRegionGroup.listRegionGroupSignExcl.iterator();
        double[] beforeReadTotal=new double[this.subjectNumber];
        double[] afterReadTotal=new double[this.subjectNumber];
        List<Double> allPVals = new ArrayList<Double>();
        while(itrRegion.hasNext()){
            SignlessGroupInfo curGroup = itrRegion.next();
            for (int i = 0; i < this.subjectNumber; i++) {
                Iterator<Integer> itrRegionIdx = curGroup.regionIdx.iterator();
                while(itrRegionIdx.hasNext()){
                    int regionIdx = itrRegionIdx.next();
                    beforeReadTotal[i]=beforeReadTotal[i]+this.beforeReadMatrix[i][regionIdx];
                    afterReadTotal[i]=afterReadTotal[i]+this.afterReadMatrix[i][regionIdx];
                }
            }
            if(verbose){
                System.out.println(Arrays.toString(beforeReadTotal));
                System.out.println(Arrays.toString(afterReadTotal));
            }
            TTest pairedT = new TTest();
            allPVals.add(pairedT.pairedTTest(beforeReadTotal,afterReadTotal));
        }

        return allPVals.toArray((new Double[allPVals.size()]));
    }

    private Double getHotellingTPvalsFromMatrix(RealMatrix logRatiosMatrix, Boolean verbose){
        Covariance logRatioCov = new Covariance(logRatiosMatrix,true);
        //covariance is bias-corrected, meaning using n-1 instead of n
        double[] vectorOfOnes = new double[logRatiosMatrix.getRowDimension()];
        Arrays.fill(vectorOfOnes,1/(double)this.subjectNumber);
        RealMatrix meanLogRatiosMat = new Array2DRowRealMatrix(logRatiosMatrix.preMultiply(vectorOfOnes));

        RealMatrix covInverse = null;
        try {
            covInverse = MatrixUtils.inverse(logRatioCov.getCovarianceMatrix());
        } catch (SingularMatrixException e) {
            return(Double.valueOf(-1));
        }
        double tSquared = (double)this.subjectNumber*meanLogRatiosMat.transpose().multiply(covInverse).multiply(meanLogRatiosMat).getData()[0][0];
        //System.out.println(tSquared);

        if (logRatiosMatrix.getColumnDimension()>=logRatiosMatrix.getRowDimension()) return (Double.valueOf(-1));

        double pValue = 0;

        HotellingTDistribution hotellingT = new HotellingTDistribution(logRatiosMatrix.getColumnDimension(),logRatiosMatrix.getRowDimension()-1);
        pValue = hotellingT.getPValue(tSquared);

        //            if(Double.compare(pValue,Double.NaN)==0){
//                System.out.println("Log-Ratio Matrix:");
//                System.out.println(Arrays.deepToString(logRatios));
//                System.out.println("Log-Ratio Covariance Matrix:");
//                System.out.println(Arrays.deepToString(logRatioCov.getCovarianceMatrix().getData()));
//                System.out.println("Log-Ratio Convariance Matrix Inverse:");
//                System.out.println(Arrays.deepToString(covInverse.getData()));
//                System.out.println(tSquared);
//                System.out.println("Isoform: "+curGroup.isoformNames+"; Number of regions: "+Integer.toString(curGroup.numberRegions)+"; Number of Subjects:"+ Integer.toString(subjectNumber)+"; T-value: "+Double.toString(tSquared));
//                System.exit(100);
//            };
        return pValue;
    }

    public Double getRegionPairCorrelation(int idx1, int idx2, String type){
        double[] col1 = this.logRatioMatrix.getColumn(idx1);
        double[] col2 = this.logRatioMatrix.getColumn(idx2);
        if(type.equals("pearson")){
            PearsonsCorrelation pearsonCorRegions = new PearsonsCorrelation();
            return(pearsonCorRegions.correlation(col1, col2));
        }else{
            SpearmansCorrelation spearmanCorRegions = new SpearmansCorrelation();
            return(spearmanCorRegions.correlation(col1, col2));
        }
    }

    public void printLogRatios(int idx1, int idx2, int perm, BufferedWriter fileHandle){
        double[] col1 = this.logRatioMatrix.getColumn(idx1);
        double[] col2 = this.logRatioMatrix.getColumn(idx2);
        for (int i = 0; i < col1.length; i++) {
            String out=Integer.toString(perm)+"\t"+Double.toString(col1[i])+"\t"+Double.toString(col2[i])+"\n";
            try {
                fileHandle.write(out,0,out.length());
            } catch (IOException e) {
                System.out.println("Cannot write to file!");
            }
        }
    }
}