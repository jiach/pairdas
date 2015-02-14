package com.upenn.parsers;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by cheng on 2/13/15.
 * take in a gtf file, based on ensemble gtf, and return a map of gene_name to genes*
 */
public class GTFParser {
    Map<String, GeneInfo> geneid_to_geneinfo = new TreeMap<String, GeneInfo>();
    
    public GTFParser(File gtf_fn){
        try {
            BufferedReader gtf_fh = new BufferedReader(new FileReader(gtf_fn));
            
            String line = null;
            while ((line = gtf_fh.readLine())!= null) {
                String gene_name = TranscriptInfo.extract_attribute(line.split("\t")[8], "gene_id");
                if (geneid_to_geneinfo.containsKey(gene_name)){
                    geneid_to_geneinfo.get(gene_name).add_line(line);
                } else {
                    geneid_to_geneinfo.put(gene_name,new GeneInfo(gene_name));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public int get_number_genes(){
        return geneid_to_geneinfo.size();
    }
}
