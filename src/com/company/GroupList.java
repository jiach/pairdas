package com.company;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by cheng on 9/8/14.
 */
public class GroupList {
    List<GroupInfo> listRegionGroup = new ArrayList<GroupInfo>();
    List<SignlessGroupInfo> listRegionGroupSignExcl = new ArrayList<SignlessGroupInfo>();
    int numIsoforms;
    int numRegions;

    int[][] isoMat;
    public GroupList(int[] allRegionLens, String[] allIsoformNames, int[][] isoMat) {
        this.isoMat=isoMat;
        this.numRegions = isoMat.length;
        this.numIsoforms = isoMat[0].length;
        this.listRegionGroup.add(new GroupInfo(0,allRegionLens, allIsoformNames, isoMat));
        this.listRegionGroupSignExcl.add(new SignlessGroupInfo(0,allRegionLens, allIsoformNames, isoMat));
        for(int i=1;i<allRegionLens.length;i++){
            Boolean alreadyInList=false;
            for(int j=0;j<this.listRegionGroup.size();j++){
                if(this.listRegionGroup.get(j).getGroupMembership(i,isoMat)){
                    alreadyInList=true;
                    this.listRegionGroup.get(j).addRegionToGroup(i,allRegionLens, isoMat);
                }
            }
            if(alreadyInList==false){
                this.listRegionGroup.add(new GroupInfo(i,allRegionLens,allIsoformNames,isoMat));
            }
        }

        for(int i=1;i<allRegionLens.length;i++){
            Boolean alreadyInList=false;
            for(int j=0;j<this.listRegionGroupSignExcl.size();j++){
                if(this.listRegionGroupSignExcl.get(j).getGroupMembership(i,isoMat)){
                    alreadyInList=true;
                    this.listRegionGroupSignExcl.get(j).addRegionToGroup(i,allRegionLens, isoMat);
                }
            }
            if(alreadyInList==false){
                this.listRegionGroupSignExcl.add(new SignlessGroupInfo(i,allRegionLens,allIsoformNames,isoMat));
            }
        }

        for (int i = this.listRegionGroupSignExcl.size()-1; i>=0; i--) {
            if(this.listRegionGroupSignExcl.get(i).numberIsoforms == this.numIsoforms||this.listRegionGroupSignExcl
                    .get(i).numberIsoforms==0){
                this.listRegionGroupSignExcl.remove(i);
            }
        }

        for (int i = this.listRegionGroup.size()-1; i >=0 ; i--) {
            if(this.listRegionGroup.get(i).numIsoforms==this.numIsoforms||this.listRegionGroup.get(i).numIsoforms==0){
                this.listRegionGroup.remove(i);
            }
        }
    }

    public void printAll(){
        for(int i=0;i<this.listRegionGroupSignExcl.size();i++){
            this.listRegionGroupSignExcl.get(i).printAll();
            System.out.println("\n");

        }
    }

    public int getNumberGroups(){
        return(this.listRegionGroup.size());
    }

    public int getNumberGroupsSignExcl(){
        return(this.listRegionGroupSignExcl.size());
    }

    public String[] getGroupNames(){
        String[] groupNames = new String[this.listRegionGroup.size()];
        for(int i=0;i<groupNames.length;i++){
            groupNames[i] = this.listRegionGroup.get(i).isoformNames;
        }
        return(groupNames);
    }

    public String[] getGroupNamesSignExcl(){
        String[] groupNames = new String[this.listRegionGroupSignExcl.size()];
        for(int i=0;i<groupNames.length;i++){
            groupNames[i] = this.listRegionGroupSignExcl.get(i).isoformNames;
        }
        return(groupNames);
    }



    public String printNumRegions(String geneName){
        Map<Integer, Integer> countUniqNumRegions = new TreeMap<Integer, Integer>();

        for (int i = 0; i < this.getNumberGroups(); i++) {
            int numRegions = this.listRegionGroup.get(i).numberRegions;
            if(countUniqNumRegions.containsKey(numRegions)){
                countUniqNumRegions.put(numRegions,countUniqNumRegions.get(numRegions)+1);
            }else{
                countUniqNumRegions.put(numRegions,1);
            }
        }
        Integer[] uniqNumRegions=countUniqNumRegions.keySet().toArray(new Integer[countUniqNumRegions.keySet().size()]);
        String[] headerLine = new String[uniqNumRegions.length+3];
        headerLine[0]="Name";
        headerLine[1]="NumRegions";
        headerLine[2]="NumIso";

        for (int i = 0;i<uniqNumRegions.length;i++){
            headerLine[i+3]=Integer.toString(uniqNumRegions[i]);
        }

        String[] numRegionsStrOut = new String[uniqNumRegions.length+3];
        numRegionsStrOut[0]=geneName;
        numRegionsStrOut[1]=Integer.toString(this.numRegions);
        numRegionsStrOut[2]=Integer.toString(this.numIsoforms);

        for (int i = 0; i < uniqNumRegions.length; i++) {
            numRegionsStrOut[i+3]=Integer.toString(countUniqNumRegions.get(uniqNumRegions[i]));
        }
        return StringUtils.join(headerLine,"\t")+"\n"+StringUtils.join(numRegionsStrOut,"\t");
    }

    public String printNumRegionsUnsigned(String geneName){
        Map<Integer, Integer> countUniqNumRegions = new TreeMap<Integer, Integer>();

        for (int i = 0; i < this.getNumberGroupsSignExcl(); i++) {
            int numRegions = this.listRegionGroupSignExcl.get(i).numberRegions;
            if(countUniqNumRegions.containsKey(numRegions)){
                countUniqNumRegions.put(numRegions,countUniqNumRegions.get(numRegions)+1);
            }else{
                countUniqNumRegions.put(numRegions,1);
            }
        }
        Integer[] uniqNumRegions=countUniqNumRegions.keySet().toArray(new Integer[countUniqNumRegions.keySet().size()]);
        String[] headerLine = new String[uniqNumRegions.length+3];
        headerLine[0]="Name";
        headerLine[1]="NumRegions";
        headerLine[2]="NumIso";

        for (int i = 0;i<uniqNumRegions.length;i++){
            headerLine[i+3]=Integer.toString(uniqNumRegions[i]);
        }

        String[] numRegionsStrOut = new String[uniqNumRegions.length+3];
        numRegionsStrOut[0]=geneName;
        numRegionsStrOut[1]=Integer.toString(this.numRegions);
        numRegionsStrOut[2]=Integer.toString(this.numIsoforms);

        for (int i = 0; i < uniqNumRegions.length; i++) {
            numRegionsStrOut[i+3]=Integer.toString(countUniqNumRegions.get(uniqNumRegions[i]));
        }
        return StringUtils.join(headerLine,"\t")+"\n"+StringUtils.join(numRegionsStrOut,"\t");
    }
}