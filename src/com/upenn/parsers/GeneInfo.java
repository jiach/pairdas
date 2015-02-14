package com.upenn.parsers;

import org.apache.commons.lang3.ArrayUtils;
import org.broadinstitute.gatk.utils.codecs.refseq.Transcript;

import java.util.*;

/**
 * Created by cheng on 2/13/15.
 */
public class GeneInfo {
    String gene_id;
    Map<String, TranscriptInfo> txid_to_tx= new TreeMap<String, TranscriptInfo>();
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
        }
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
        System.out.println("printingn coords:\n"+coord_output);
    }
    
    public void get_gene_intervals(){
        List<Coordinate> sorted_coord = new ArrayList<Coordinate>(this.get_coords());
        Collections.sort(sorted_coord);
        List<Long[]> intervals = new ArrayList<Long[]>();
        for(Iterator<Coordinate> it = sorted_coord.iterator();it.hasNext();){
            Coordinate cur_coord = it.next();
            if (!cur_coord.end){
                
            }
        }
    }
}
