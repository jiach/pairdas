package com.upenn.parsers;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by chengjia on 2/21/15.
 */
public class FeatureCountsParser {

    Map<String, FeatureCounts> fc_map = new TreeMap<String, FeatureCounts>();
    public FeatureCountsParser(File fc_counts_fn){
        BufferedReader fc_counts_fh;
        try{
            if (FilenameUtils.getExtension(fc_counts_fn.getName()).equals("gz")){
                GZIPInputStream gtf_gzip = new GZIPInputStream(new FileInputStream(fc_counts_fn));
                fc_counts_fh = new BufferedReader(new InputStreamReader(gtf_gzip));
            } else {
                fc_counts_fh = new BufferedReader(new FileReader(fc_counts_fn));
            }

            String line = fc_counts_fh.readLine();
            String header = fc_counts_fh.readLine();
            while ((line = fc_counts_fh.readLine())!= null) {
                String[] line_tokens = line.split("\t");
                String gene_id = line_tokens[0].split("_")[0];
                if (fc_map.containsKey(gene_id)) {
                    fc_map.get(gene_id).add_line(line_tokens);
                } else {
                    fc_map.put(gene_id, new FeatureCounts(line_tokens,gene_id));
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] get_arr_str(){
        List<String> all_str = new ArrayList<String>();
        for (Iterator<Map.Entry<String, FeatureCounts>> iterator=this.fc_map.entrySet().iterator();iterator.hasNext();){
            Map.Entry<String,FeatureCounts> entry = iterator.next();
            all_str.addAll(entry.getValue().get_str_arr());
        }
        return all_str.toArray(new String[all_str.size()]);
    }

}
