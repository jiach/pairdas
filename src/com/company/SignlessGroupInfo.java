package com.company;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cheng on 9/25/14.
 */
public class SignlessGroupInfo {

    List<Integer> regionIdx = new ArrayList<Integer>();
    List<Integer> regionLen = new ArrayList<Integer>();
    List<String> regionIds = new ArrayList<String>();
    String isoformNames = null;
    int[] isoformComposition = null;
    int[] isoformCompositionBool = null;
    int numberIsoforms=0;
    int numberRegions;

    public SignlessGroupInfo(int regionIdx, int[] allRegionLens, String[] allIsoformNames, int[][] isoMat) {
        this.regionIdx.add(regionIdx);
        this.regionLen.add(allRegionLens[regionIdx]);
        this.regionIds.add(Integer.toString(regionIdx));
        this.isoformCompositionBool = this.getIsoformCompositionBool(regionIdx,isoMat);
        this.isoformComposition = this.getIsoformComposition();
        this.numberRegions=1;
        this.getIsoformNameString(allIsoformNames);
    }

    private void getIsoformNameString(String[] allIsoformNames){
        for (int i=0;i<this.isoformComposition.length;i++) {
            if (this.isoformNames == null){
                this.isoformNames = allIsoformNames[this.isoformComposition[i]];
            }else{
                this.isoformNames = this.isoformNames + "," + allIsoformNames[this.isoformComposition[i]];
            }
        }
    }

    protected int[] getIsoformCompositionBool(int regionIdx, int[][] isoMat){
        int[] isoformCompBool = new int[isoMat[regionIdx].length];
        for(int i=0;i<isoMat[regionIdx].length;i++){
            isoformCompBool[i] = isoMat[regionIdx][i];
            if(isoformCompBool[i]==1){
                this.numberIsoforms++;
            }
        }

        return isoformCompBool;
    }
    protected int[] getIsoformComposition(){
        int[] isoformIdx = new int[this.numberIsoforms];
        int itrIsoformIdx = 0;
        for (int i = 0; i < this.isoformCompositionBool.length; i++) {
            if(this.isoformCompositionBool[i]==1){
                isoformIdx[itrIsoformIdx]=i;
                itrIsoformIdx++;
            }
        }
        return isoformIdx;
    }

    public void printAll(){
        System.out.println("regionIdx:");
        System.out.println(regionIdx);
        System.out.println("regionIds:");
        System.out.println(regionIds);
        System.out.println("isoformNames:");
        System.out.println(isoformNames);
        System.out.println("isoformComposition:");
        System.out.println(Arrays.toString(isoformComposition));
        System.out.println("isoformCompositionBool:");
        System.out.println(Arrays.toString(isoformCompositionBool));
    }
    public Boolean getGroupMembership(int regionIdx, int[][] isoMat){
        Boolean isGroupMember = true;
        for(int i=0;i<this.isoformCompositionBool.length;i++){
            if(isoMat[regionIdx][i]!=this.isoformCompositionBool[i]){
                isGroupMember = false;
            }
        }
        return(isGroupMember);
    }
    public void addRegionToGroup(int regionIdx, int[] allRegionLens, int[][] isoMat){
        this.regionIdx.add(regionIdx);
        this.regionLen.add(allRegionLens[regionIdx]);
        this.regionIds.add(Integer.toString(regionIdx));
        this.numberRegions++;
    }

    public int[] getRegionIdx(){
        return ArrayUtils.toPrimitive(this.regionIdx.toArray(new Integer[this.regionIdx.size()]));
    }

}
