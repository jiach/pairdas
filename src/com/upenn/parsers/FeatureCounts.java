package com.upenn.parsers;

import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.summary.Sum;

import java.util.*;

/**
 * Created by chengjia on 2/21/15.
 */
public class FeatureCounts {
    String gene_id;
    Map<Integer, List<Integer>> interval_id_to_counts_before = new TreeMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> interval_id_to_counts_after = new TreeMap<Integer, List<Integer>>();
    int idx = 0;
    long total_counts;
    public FeatureCounts(String[] line, String gene_id){
        this.gene_id = gene_id;
        this.idx = 0;
        this.interval_id_to_counts_before.put(this.idx, new ArrayList<Integer>());
        this.interval_id_to_counts_after.put(this.idx, new ArrayList<Integer>());
        this.add_line(line);
    }
    
    public void add_line(String[] line_tokens){
        this.interval_id_to_counts_before.put(this.idx, new ArrayList<Integer>());
        this.interval_id_to_counts_after.put(this.idx, new ArrayList<Integer>());
        for (int i = 6; i < line_tokens.length; i+=2) {
            interval_id_to_counts_before.get(this.idx).add(Integer.parseInt(line_tokens[i]));
            interval_id_to_counts_after.get(this.idx).add(Integer.parseInt(line_tokens[i+1]));
        }
        this.idx++;
    }
    
    public String get_str(){
        List<String> str_arr = new ArrayList<String>();
        for (int j = 0; j < this.interval_id_to_counts_before.get(0).size(); j++) {
            for (int i = 0; i < this.idx; i++) {
                str_arr.add(this.gene_id + "\t" + Integer.toString(j) + "\t" + this.interval_id_to_counts_before.get(i).get(j).toString() + "\t" + this.interval_id_to_counts_after.get(i).get(j).toString());
            }
        }
        return StringUtils.join(str_arr,"\n");
    }
    
    public List<String> get_str_arr(){
        List<String> str_arr = new ArrayList<String>();
        for (int j = 0; j < this.interval_id_to_counts_before.get(0).size(); j++) {
            for (int i = 0; i < this.idx; i++) {
                str_arr.add(this.gene_id + "\t" + Integer.toString(j) + "\t" + this.interval_id_to_counts_before.get(i).get(j).toString() + "\t" + this.interval_id_to_counts_after.get(i).get(j).toString());
            }
        }
        return str_arr;
    }
    
    public Long get_total_count(){
        Long tot_count = Long.valueOf(0);

        for (Iterator<Map.Entry<Integer,List<Integer>>> it_after = interval_id_to_counts_after.entrySet().iterator(); it_after.hasNext(); ){
            Map.Entry<Integer, List<Integer>> cur_after = it_after.next();
            tot_count += sum_Integer_list(cur_after.getValue());
        }
        for (Iterator<Map.Entry<Integer,List<Integer>>> it_before = interval_id_to_counts_before.entrySet().iterator(); it_before.hasNext(); ) {
            Map.Entry<Integer, List<Integer>> cur_before = it_before.next();
            tot_count += sum_Integer_list(cur_before.getValue());
        }
        this.total_counts = tot_count;
        return tot_count;
    }
    
    public long sum_Integer_list(List<Integer> list){
        long sum = 0;
        for (Iterator<Integer> it_int = list.iterator();it_int.hasNext();){
            sum+=it_int.next();
        }
        return sum;
    }
}
