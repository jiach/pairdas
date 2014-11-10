package com.upenn.parsers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.Hash;

import java.util.*;

/**
 * Created by cheng on 11/10/14.
 * The input format is four columns without headers.
 * gene_name & sub_idx & before_count & after_count
 */
public class SimulatedCounts {
    Table<String,Integer,List<ArrayList<Integer> > > countTable = null;

    public SimulatedCounts() {
        this.countTable = HashBasedTable.create();
    }

    public void addLine(String line){
        String[] lineTokens = line.split("\t");
        if (this.countTable.contains(lineTokens[0], Integer.parseInt(lineTokens[1]))) {
            this.countTable.get(lineTokens[0], Integer.parseInt(lineTokens[1])).get(0).add(Integer.parseInt(lineTokens[2]));
            this.countTable.get(lineTokens[0], Integer.parseInt(lineTokens[1])).get(1).add(Integer.parseInt(lineTokens[3]));
        }else{
            List<ArrayList<Integer>> beforeAfter = new ArrayList<ArrayList<Integer>>();
            beforeAfter.add(new ArrayList<Integer>());
            beforeAfter.add(new ArrayList<Integer>());
            beforeAfter.get(0).add(Integer.parseInt(lineTokens[2]));
            beforeAfter.get(1).add(Integer.parseInt(lineTokens[3]));
            this.countTable.put(lineTokens[0], Integer.parseInt(lineTokens[1]), beforeAfter);
        }
    }

}
