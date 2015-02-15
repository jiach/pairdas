package com.upenn.parsers;

import com.upenn.exceptions.IncompleteIntervalListException;
import com.upenn.utils.MiscCalc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.FastMath;

import java.util.*;

/**
 * Created by cheng on 2/13/15.
 */
public class GeneInfo {

    String gene_id;
    Map<String, TranscriptInfo> txid_to_tx= new TreeMap<String, TranscriptInfo>();
    List<Long[]> intervals = new ArrayList<Long[]>();
    List<boolean[]> tx_interval_mat;
    int tx_num = 0;
    public GeneInfo(String gene_id){
        this.gene_id = gene_id;
    }
    
    public void add_line(String line){
        String[] line_tokens = line.split("\t");
        String tx_id = TranscriptInfo.extract_attribute(line_tokens[8],"transcript_id");

        if (this.txid_to_tx.containsKey(tx_id)) {
            this.txid_to_tx.get(tx_id).add_line(line_tokens);
        } else {
            this.txid_to_tx.put(tx_id, new TranscriptInfo(line));
            this.tx_num ++;
        }
        
    }

    public int getTx_num(){
        return this.tx_num;
    }

    public Set<Coordinate> get_coords(){
        Set<Coordinate> gene_coords = new HashSet<Coordinate>();
        for(Iterator<Map.Entry<String, TranscriptInfo>> it = this.txid_to_tx.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, TranscriptInfo> entry = it.next();
            gene_coords.addAll(entry.getValue().get_coords());
        }
        return gene_coords;
    }
    
    public void print_all_coords(){
        String coord_output = "";
        List<Coordinate> sorted_coord = new ArrayList<Coordinate>(this.get_coords());
        Collections.sort(sorted_coord);
        for (Iterator<Coordinate> coord_it = sorted_coord.iterator(); coord_it.hasNext();){
            coord_output = coord_output+ coord_it.next().print_coord()+"\n";
        }
        System.out.println("printing coords:\n"+coord_output);
    }
    
    public void print_all_intervals(){
        List<GenomicInterval> intervals_from_tx = new ArrayList<GenomicInterval>();

        for(Iterator<Map.Entry<String, TranscriptInfo>> it = this.txid_to_tx.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, TranscriptInfo> entry = it.next();
            intervals_from_tx.addAll(entry.getValue().get_intervals());
        }

        Set<GenomicInterval> intervals_uniq = new HashSet<GenomicInterval>(intervals_from_tx);
        
        List<GenomicInterval> sorted_intervals = new ArrayList<GenomicInterval>(intervals_uniq);
        Collections.sort(sorted_intervals);

        for (Iterator<GenomicInterval> it = sorted_intervals.iterator();it.hasNext();){
            it.next().print_me();
        }
    }
    
    public void get_gene_intervals() throws IncompleteIntervalListException {
        List<Coordinate> sorted_coord = new ArrayList<Coordinate>(this.get_coords());
        Collections.sort(sorted_coord);
        if (sorted_coord.size()<2) throw new IncompleteIntervalListException("length: "+Integer.toString(sorted_coord.size()));
        Iterator<Coordinate> it = sorted_coord.iterator();

        Coordinate cur_coord = it.next();
        Coordinate next_coord = it.next();

        do {
            if (cur_coord.end) throw new IncompleteIntervalListException("coords array start with an end.coord!");
            if (!next_coord.end) {
                Long[] new_interval = new Long[] {cur_coord.coord, next_coord.coord-1};
                this.intervals.add(new_interval);
                if(it.hasNext()){
                    cur_coord = next_coord;
                    next_coord = it.next();
                    continue;
                }else{
                    break;
                }
            } else {
                Long[] new_interval = new Long[] {cur_coord.coord, next_coord.coord};
                this.intervals.add(new_interval);
                cur_coord = next_coord;
                
                if(it.hasNext()){
                    cur_coord.coord = cur_coord.coord+1;
                    cur_coord.end = false;
                    next_coord = it.next();
                    continue;
                } else {
                    break;
                }
            }
        } while(it.hasNext());
        

    }

    public void get_tx_interval_matrix(){
        try {
            this.get_gene_intervals();
        } catch (IncompleteIntervalListException e) {
            e.printStackTrace();
        }
        this.tx_interval_mat = new ArrayList<boolean[]>();

        String[] tx_ids = this.txid_to_tx.keySet().toArray(new String[this.txid_to_tx.keySet().size()]);
        for (Long[] cur_interval : this.intervals) {
            boolean [] has_cur_interval = new boolean[tx_ids.length];
            for (int i = 0; i < tx_ids.length; i++) {
                has_cur_interval[i] = this.txid_to_tx.get(tx_ids[i]).has_interval(cur_interval);
            }
            this.tx_interval_mat.add(has_cur_interval);
        }

        // now we trim all the stupid empty intervals that do not belong to any tx.

        List<boolean[]> new_tx_interval_mat = new ArrayList<boolean[]>();
        List<Long[]> new_intervals = new ArrayList<Long[]>();

        for (int i = 0; i < this.tx_interval_mat.size(); i++) {
            boolean[] cur_has_intervals = this.tx_interval_mat.get(i);
            Long[] cur_interval = this.intervals.get(i);
            
            if ((!MiscCalc.all_false(cur_has_intervals)) && (!MiscCalc.all_true(cur_has_intervals)) && FastMath.abs(this.intervals.get(i)[1]-this.intervals.get(i)[0])> 3) {
                new_tx_interval_mat.add(cur_has_intervals);
                new_intervals.add(cur_interval);
            }
        }
        
        this.tx_interval_mat = new_tx_interval_mat;
        this.intervals = new_intervals;
        System.out.println("Interval\t"+ StringUtils.join(tx_ids,"\t"));
        for (int j = 0; j < this.tx_interval_mat.size(); j++) {
            System.out.println(StringUtils.join(this.intervals.get(j),":")+"\t"+StringUtils.join(ArrayUtils.toString(this.tx_interval_mat.get(j)),"\t"));
        }

    }
    
    public void print_all_tx() {
        for(Iterator<Map.Entry<String, TranscriptInfo>> it = this.txid_to_tx.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, TranscriptInfo> entry = it.next();
            System.out.println(entry.getKey());
            for (Iterator<GenomicInterval> it_gi = entry.getValue().get_intervals().iterator();it_gi.hasNext();){
                it_gi.next().print_me();
            }
        }
    }
    
}
