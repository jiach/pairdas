package com.upenn.parsers;

import com.upenn.exceptions.IncompleteIntervalListException;
import com.upenn.utils.MiscCalc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.FastMath;
import org.broadinstitute.gatk.utils.codecs.refseq.Transcript;

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
        if (txid_to_tx.containsKey(tx_id)) {
            txid_to_tx.get(tx_id).add_line(line_tokens);

        } else {
            txid_to_tx.put(tx_id, new TranscriptInfo(line));
            tx_num ++;
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
    
    public void get_gene_intervals() throws IncompleteIntervalListException {
        List<Coordinate> sorted_coord = new ArrayList<Coordinate>(this.get_coords());
        Collections.sort(sorted_coord);
        if (sorted_coord.size()<2) throw new IncompleteIntervalListException("length: "+Integer.toString(sorted_coord.size()));
        Iterator<Coordinate> it = sorted_coord.iterator();
        Coordinate cur_coord = it.next();
        Coordinate next_coord = it.next();

        while(it.hasNext()){
            if (cur_coord.end) throw new IncompleteIntervalListException("coords array start with an end.coord!");
            if (!next_coord.end) {
                Long[] new_interval = new Long[] {cur_coord.coord, next_coord.coord-1};
                this.intervals.add(new_interval);
                cur_coord = next_coord;
                next_coord = it.next();
                continue;
            } else {
                Long[] new_interval = new Long[] {cur_coord.coord, next_coord.coord};
                this.intervals.add(new_interval);
                cur_coord = next_coord;
                cur_coord.coord = cur_coord.coord+1;
                cur_coord.end = false;
                next_coord = it.next();
                continue;
            }
        }
        for (Long[] i : this.intervals){
            System.out.println(ArrayUtils.toString(i));
        }
    }

    public void get_tx_interval_matrix(){
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

        int i = 0;
        for (Iterator<boolean[]> it = this.tx_interval_mat.iterator(); it.hasNext();) {
            boolean[] cur_has_intervals = it.next();
            if (MiscCalc.all_false(cur_has_intervals) || FastMath.abs(this.intervals.get(i)[1]-this.intervals.get(i)[0])<3) {
                it.remove();
                this.intervals.remove(i);
                continue;
            }
            i ++;
        }
        System.out.println("Interval\t"+ StringUtils.join(tx_ids,"\t"));
        for (int j = 0; j < this.tx_interval_mat.size(); j++) {
            System.out.println(StringUtils.join(this.intervals.get(i),":")+"\t"+StringUtils.join(this.tx_interval_mat.get(i),"\t"));
        }

    }

}
