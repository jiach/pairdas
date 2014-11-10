package com.upenn.parsers;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by cheng on 11/10/14.
 * The input format is four columns without headers.
 * gene_name & sub_idx & before_count & after_count
 */
public class SimulatedCounts {
    Table<String,String,List<ArrayList<Integer> > > countTable = null;

    public SimulatedCounts() {
        this.countTable = TreeBasedTable.create();
    }

    public void addLine(String line){
        String[] lineTokens = line.split("\t");
        if (this.countTable.contains(lineTokens[0], lineTokens[1])) {
            this.countTable.get(lineTokens[0], lineTokens[1]).get(0).add(Integer.parseInt(lineTokens[2]));
            this.countTable.get(lineTokens[0], lineTokens[1]).get(1).add(Integer.parseInt(lineTokens[3]));
        }else{
            List<ArrayList<Integer>> beforeAfter = new ArrayList<ArrayList<Integer>>();
            beforeAfter.add(new ArrayList<Integer>());
            beforeAfter.add(new ArrayList<Integer>());
            beforeAfter.get(0).add(Integer.parseInt(lineTokens[2]));
            beforeAfter.get(1).add(Integer.parseInt(lineTokens[3]));
            this.countTable.put(lineTokens[0], lineTokens[1], beforeAfter);
        }
    }

    public void printAll(){
        for (Table.Cell<String, String, List<ArrayList<Integer>>> cell: this.countTable.cellSet()){
            System.out.println(cell.getRowKey()+" "+cell.getColumnKey()+" "+ cell.getValue());
        }
    }

    public int getNumSubject(){
        return this.countTable.columnKeySet().size();
    }

    public String[] getAllSubjectIDs() {
        return this.countTable.columnKeySet().toArray(new String[this.countTable.columnKeySet().size()]);
    }

    public String[] getAllGenes(){
        return this.countTable.rowKeySet().toArray(new String[this.countTable.rowKeySet().size()]);
    }

    public String[] getSortedSubIDFromGene(String geneName){
        Set<String> subIDSet = this.countTable.row(geneName).keySet();
        String[] sortedID = subIDSet.toArray(new String[subIDSet.size()]);
        Arrays.sort(sortedID);
        return sortedID;
    }

    public int getNumRegions(String geneName){
        if (!this.countTable.containsRow(geneName)) return -1;
        String[] allIDs = this.getSortedSubIDFromGene(geneName);
        int firstPersonRegionCount = this.countTable.row(geneName).get(allIDs[0]).get(0).size();
        for (int i = 1; i < allIDs.length; i++) {
            if (firstPersonRegionCount != this.countTable.row(geneName).get(allIDs[i]).get(0).size()) {
                return -1;
            }
        }
        return firstPersonRegionCount;

    }

    public Boolean containsGene(String geneName){
        return this.countTable.containsRow(geneName);
    }

    public getFishersPvalueForGene(String geneName){
        
    }

}
