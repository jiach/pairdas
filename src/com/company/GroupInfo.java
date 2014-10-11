package com.company;

import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cheng on 9/5/14.
 */
public class GroupInfo {
    List<Integer> regionIdx = new ArrayList<Integer>();
    List<Integer> regionLen = new ArrayList<Integer>();
    List<Integer> regionSign = new ArrayList<Integer>();
    List<String> regionIds = new ArrayList<String>();
    String isoformNames = null;
    int[] isoformComposition = null;
    int[] isoformCompositionBool = null;
    int numberRegions;
    int numIsoforms=0;

    public GroupInfo(int regionIdx, int[] allRegionLens, String[] allIsoformNames, int[][] isoMat) {
        this.regionIdx.add(regionIdx);
        this.regionLen.add(allRegionLens[regionIdx]);
        this.regionIds.add(Integer.toString(regionIdx));
        this.isoformComposition = this.getIsoformComposition(regionIdx, isoMat);
        this.isoformCompositionBool = this.getIsoformCompositionBool(regionIdx,isoMat);
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
        }

        if (isoformCompBool[0]==1) {
            for(int i=0;i<isoformCompBool.length;i++){
                isoformCompBool[i]=1-isoformCompBool[i];
            }
        }

        for(int i=0;i<isoformCompBool.length;i++){
            if(isoformCompBool[i]==1){
                this.numIsoforms++;
            }
        }

        if(isoMat[regionIdx][0]==1){
            this.regionSign.add(-1);
        }else{
            this.regionSign.add(1);
        }

        return isoformCompBool;
    }
    protected int[] getIsoformComposition(int regionIdx, int[][] isoMat){
        List<Integer> isoformComp = new ArrayList<Integer>();
        for(int i=0;i<isoMat[regionIdx].length;i++){
            if(isoMat[regionIdx][i] == 1) isoformComp.add(i);
        }
        return ArrayUtils.toPrimitive(isoformComp.toArray(new Integer[isoformComp.size()]));
    }

    public void printAll(){
        System.out.println("regionIdx:");
        System.out.println(regionIdx);
        System.out.println("regionIds:");
        System.out.println(regionIds);
        System.out.println("regionSign:");
        System.out.println(regionSign);
        System.out.println("isoformNames:");
        System.out.println(isoformNames);
        System.out.println("isoformComposition:");
        System.out.println(Arrays.toString(isoformComposition));
        System.out.println("isoformCompositionBool:");
        System.out.println(Arrays.toString(isoformCompositionBool));
    } 
    public Boolean getGroupMembership(int regionIdx, int[][] isoMat){
        int[] tempIsoComp = new int[isoMat[regionIdx].length];
        if (isoMat[regionIdx][0] == 1){
            for(int i=0;i<isoMat[regionIdx].length;i++){
                tempIsoComp[i]= 1-isoMat[regionIdx][i];
            }
        }else{
            for(int i=0;i<isoMat[regionIdx].length;i++){
                tempIsoComp[i]= isoMat[regionIdx][i];
            }
        }
        Boolean isGroupMember = true;
        for(int i=0;i<this.isoformCompositionBool.length;i++){
            if(tempIsoComp[i]!=this.isoformCompositionBool[i]){
                isGroupMember = false;
            }
        }
        return(isGroupMember);
    }
    public void addRegionToGroup(int regionIdx, int[] allRegionLens, int[][] isoMat){
        this.regionIdx.add(regionIdx);
        this.regionLen.add(allRegionLens[regionIdx]);
        this.regionIds.add(Integer.toString(regionIdx));
        if(isoMat[regionIdx][0]==1){
            this.regionSign.add(-1);
        }else{
            this.regionSign.add(1);
        }
        this.numberRegions++;
    }

    public int[] getRegionIdx(){
        return ArrayUtils.toPrimitive(this.regionIdx.toArray(new Integer[this.regionIdx.size()]));
    }

}
