package com.upenn.parsers;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chengjia on 2/21/15.
 */
public class FeatureCounts {
    String gene_id;
    Map<Integer, List<Integer>> before = new TreeMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> after = new TreeMap<Integer, List<Integer>>();
    int idx = 0;
    public FeatureCounts(String[] line, String gene_id){
        this.gene_id = gene_id;
        idx = 0;
        before.put(idx, new ArrayList<Integer>());
        after.put(idx, new ArrayList<Integer>());
        this.add_line(line);
    }
    
    public void add_line(String[] line_tokens){
        for (int i = 6; i < line_tokens.length; i+=2) {
            before.get(this.idx).add(Integer.parseInt(line_tokens[i]));
            after.get(this.idx).add(Integer.parseInt(line_tokens[i+1]));
        }
        this.idx++;
    }
    
    public String get_str(){
        List<String> str_arr = new ArrayList<String>();
        for (int i = 0; i < this.idx; i++) {
            for (int j = 0; j < this.before.size(); j++) {
                str_arr.add(this.gene_id+"\t"+Integer.toString(i)+"\t"+this.before.get(j).toString()+"\t"+this.after.get(j).toString());
            }
        }
        return StringUtils.join(str_arr,"\n");
    }
    
    public List<String> get_str_arr(){
        List<String> str_arr = new ArrayList<String>();
        for (int i = 0; i < this.idx; i++) {
            for (int j = 0; j < this.before.size(); j++) {
                str_arr.add(this.gene_id+"\t"+Integer.toString(i)+"\t"+this.before.get(j).toString()+"\t"+this.after.get(j).toString());
            }
        }
        return str_arr;
    }
}
