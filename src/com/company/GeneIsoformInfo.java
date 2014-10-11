package com.company;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cheng on 8/29/14.
 */
public class GeneIsoformInfo {
    static int totalGeneNum = 0;
    String name="Generic";
    int numberRegions = 0;
    int numberIsoforms = 0;
    int[] regionLen = null;
    int[][] regIsoMat;
    int[] isoformLen = null;
    String[] isoNames=null;
    GroupList listRegionGroup;
    String chr="";
    int[] startPos = null;
    int[] endPos = null;
    String strand = "+";

    public GeneIsoformInfo(String geneName, String chr, String[] isoformHeaderLine, List<Integer> regionLength, List<List<Integer>> regionIsoformMatrix){
        this.name = geneName;
        this.regionLen = ArrayUtils.toPrimitive(regionLength.toArray(new Integer[regionLength.size()]));
        assert regionLength.size() == regionIsoformMatrix.size();
        this.numberRegions = regionLength.size();
        this.numberIsoforms = regionIsoformMatrix.get(1).size();
        this.regIsoMat = new int[numberRegions][numberIsoforms];
        this.isoformLen = new int[this.numberIsoforms];
        Arrays.fill(this.isoformLen,0);


        for(int i=0;i < this.numberRegions; i++){
            for(int j=0;j < this.numberIsoforms; j++){
                this.regIsoMat[i][j]=regionIsoformMatrix.get(i).get(j);
                this.isoformLen[j]+=this.regionLen[i]*this.regIsoMat[i][j];
            }
        }

        isoNames=isoformHeaderLine;
        this.listRegionGroup=new GroupList(this.regionLen, this.isoNames, this.regIsoMat);
        totalGeneNum++;
    }

    public GeneIsoformInfo(String geneName, String chr, String strand, int geneStart, int geneEnd, String[] isoformHeaderLine, List<Integer> regionStart, List<Integer> regionEnd, List<List<Integer>> regionIsoformMatrix){
        this.name = geneName;
        assert regionStart.size() == regionEnd.size();
        this.regionLen = new int[regionStart.size()];
        for(int i=0;i< regionStart.size();i++){
            this.regionLen[i]=regionEnd.get(i)-regionStart.get(i)+1;
        }
        this.startPos=ArrayUtils.toPrimitive(regionStart.toArray(new Integer[regionStart.size()]));
        this.endPos=ArrayUtils.toPrimitive(regionEnd.toArray(new Integer[regionEnd.size()]));
        assert this.regionLen.length == regionIsoformMatrix.size();
        this.numberRegions = this.regionLen.length;
        this.numberIsoforms = regionIsoformMatrix.get(0).size();
        this.regIsoMat = new int[numberRegions][numberIsoforms];
        this.isoformLen = new int[this.numberIsoforms];
        Arrays.fill(this.isoformLen,0);
        this.strand=strand;

        for(int i=0;i < this.numberRegions; i++){
            for(int j=0;j < this.numberIsoforms; j++){
                this.regIsoMat[i][j]=regionIsoformMatrix.get(i).get(j);
                this.isoformLen[j]+=this.regionLen[i]*this.regIsoMat[i][j];
            }
        }

        //System.out.println(Arrays.deepToString(this.regIsoMat));

        isoNames=isoformHeaderLine;
        this.listRegionGroup=new GroupList(this.regionLen, this.isoNames, this.regIsoMat);
        totalGeneNum++;
    }

    int getTotalGeneNum(){
        return(totalGeneNum);
    }

    int getNumberRegions()
    {
        return(this.numberRegions);
    }

    int getNumberIsoforms(){
        return(this.numberIsoforms);
    }

    int[] getRegionLen(){
        return(this.regionLen);
    }

    int[][] getRegIsoMat(){
        return(this.regIsoMat);
    }

    public void verifyAllIso(){
        Iterator<SignlessGroupInfo> itrCurGroup = this.listRegionGroup.listRegionGroupSignExcl.iterator();
        while(itrCurGroup.hasNext()){
            SignlessGroupInfo curGroup = itrCurGroup.next();
            System.out.println(this.name+":"+Integer.toString(curGroup.numberIsoforms)+":"+Integer.toString(this
                    .numberIsoforms));
            if(curGroup.numberIsoforms == this.numberIsoforms){
                System.out.println(this.name+":"+Integer.toString(curGroup.numberIsoforms)+":"+Integer.toString(this
                        .numberIsoforms));
            }
        }
    }

    public void printAll(){
        System.out.println(this.name);
        System.out.println("Total Isoform Lengths: "+Arrays.toString(this.isoformLen));
        this.listRegionGroup.printAll();
    }

    public void printNumRegions(BufferedWriter file1, BufferedWriter file2){
        try {
            file1.write(this.listRegionGroup.printNumRegions(this.name)+"\n",0,this.listRegionGroup.printNumRegions
                    (this.name).length()+1);
            file2.write(this.listRegionGroup.printNumRegionsUnsigned(this.name)+"\n",0,this.listRegionGroup.printNumRegionsUnsigned(this.name).length()+1);
        } catch (IOException e) {
            System.out.println("Cannot write to files!");
        }
    }
}
