package com.upenn;

import com.upenn.annotation.GeneIsoformInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.getenv;

/**
 * Created by chengjia on 10/2/14.
 */
public class IsoformRegionMatrixParser {
    List<GeneIsoformInfo> geneList = new ArrayList<GeneIsoformInfo>();

    public IsoformRegionMatrixParser(String filename) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line=null;
            String curGeneName = null;
            String curGeneChr = null;
            String curGeneStrand = null;
            int curGeneStart=0;
            int curGeneEnd=0;
            Boolean addElement = false;
            List<Integer> startPos = new ArrayList<Integer>();
            List<Integer> endPos = new ArrayList<Integer>();
            List<List<Integer>> regionIsoMat = new ArrayList<List<Integer>>();
            List<Integer> regionIsoMatBuffer = null;
            String[] isoNames = null;
            while ((line = in.readLine()) != null){
                String[] lineTokens = line.split("\t");
                if (!Character.isDigit(lineTokens[lineTokens.length - 1].charAt(0))){
                    //System.out.println(regionIsoMat);
                    if(addElement){
                        this.geneList.add(new GeneIsoformInfo(curGeneName, curGeneChr, curGeneStrand, curGeneStart, curGeneEnd, isoNames, startPos, endPos, regionIsoMat));
                    }
                    startPos.clear();
                    endPos.clear();
                    regionIsoMat.clear();

                    curGeneName=lineTokens[0];
                    curGeneChr=lineTokens[1];
                    curGeneStrand = lineTokens[2];
                    curGeneStart = Integer.parseInt(lineTokens[3]);
                    curGeneEnd = Integer.parseInt(lineTokens[4]);
                    isoNames = lineTokens[5].split(",");
                    addElement=true;
                }else{

                    startPos.add(Integer.parseInt(lineTokens[3]));
                    endPos.add(Integer.parseInt(lineTokens[4]));
                    regionIsoMatBuffer = new ArrayList<Integer>() ;
                    String[] ar = lineTokens[5].split(",");

                    for (int i = 0; i < ar.length; i++) {
                        regionIsoMatBuffer.add(Integer.parseInt(ar[i]));
                    }
                    regionIsoMat.add(regionIsoMatBuffer);
                }
            }
            this.geneList.add(new GeneIsoformInfo(curGeneName, curGeneChr, curGeneStrand, curGeneStart, curGeneEnd, isoNames, startPos, endPos, regionIsoMat));
        } catch (IOException e) {
            System.out.println("Cannot read "+filename);
        }
    }

    public void printAll(){
        
    }
}
