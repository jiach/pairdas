package com.upenn.parsers;

import java.util.Map;
import java.util.TreeMap;

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
}
