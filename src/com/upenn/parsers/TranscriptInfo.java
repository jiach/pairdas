package com.upenn.parsers;

/**
 * Created by cheng on 2/13/15.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.FastMath;
import java.util.*;

public class TranscriptInfo {
    String chr = null;
    String transcript_id = null;
    String transcript_name = null;
    String gene_id = null;
    String gene_name = null;
    int exon_numbers = 0;
    Map<Integer,Long[]> exons = new TreeMap<Integer, Long[]>();

    List<Long[]> CDs = new ArrayList<Long[]>();
    Boolean has_CDs = false;

    public TranscriptInfo(String line){
        String[] line_tokens = line.split("\t");

        if (this.transcript_id == null) {
            this.chr = line_tokens[0];
            String[] attribute_tokens =line_tokens[8].split(";");
            Map<String, String> attribute_maps = new HashMap<String, String>();
            for (String att_str:attribute_tokens){
                String att_name = att_str.trim().split(" ")[0];
                String att_value = StringUtils.substringBetween(att_str,"\"", "\"");
                attribute_maps.put(att_name,att_value);
            }

            this.transcript_id = attribute_maps.get("transcript_id");
            this.gene_id = attribute_maps.get("gene_id");

            if (attribute_maps.containsKey("transcript_name")) {
                this.transcript_name = attribute_maps.get("transcript_name");
            }

            if (attribute_maps.containsKey("gene_name")) {
                this.gene_name = attribute_maps.get("gene_name");
            }
        }

        this.add_line(line_tokens);
    }

    public void add_line(String line){
        String[] line_tokens = line.split("\t");
        if (line_tokens[2].equals("CDS")) {
            this.add_CDS(line_tokens);
        } else if (line_tokens[2].equals("exon")){
            this.add_exon(line_tokens);
        } else {
            return;
        }
    }

    public void add_line(String[] line_tokens){
        if (line_tokens[2].equals("CDS")) {
            this.add_CDS(line_tokens);
        } else if (line_tokens[2].equals("exon")){
            this.add_exon(line_tokens);
        } else {
            return;
        }
    }

    public long get_transcript_length(){
        long tx_len = 0;
        for(Iterator<Map.Entry<Integer, Long[]>> it = this.exons.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Long[]> entry = it.next();
            tx_len = tx_len + FastMath.abs(entry.getValue()[1] - entry.getValue()[0])+1;
        }
        return tx_len;
    }

    public void add_exon(String[] line){
        Long[] exon_pos = new Long[] {Long.parseLong(line[3]), Long.parseLong(line[4])};
        String exon_num = this.extract_attribute(line[8],"exon_number");
        if (exon_num!=null){
            this.exons.put(Integer.parseInt(exon_num), exon_pos);
        } else {
            this.exons.put(this.exon_numbers+1, exon_pos);
        }
        this.exon_numbers++;
    }

    public void add_CDS(String[] line){
        Long[] cds_pos = new Long[] {Long.parseLong(line[3]), Long.parseLong(line[4])};
        this.CDs.add(cds_pos);
        this.has_CDs = true;
    }

    public String get_tx_id(){
        return this.transcript_id;
    }

    public static String extract_attribute(String att_section, String attribute_name){

        String[] attribute_tokens = att_section.split(";");
        Map<String, String> attribute_maps = new HashMap<String, String>();
        for (String att_str:attribute_tokens){
            String att_name = att_str.trim().split(" ")[0];
            String att_value = StringUtils.substringBetween(att_str,"\"", "\"");
            attribute_maps.put(att_name,att_value);
        }

        if (attribute_maps.containsKey(attribute_name)) {
            return attribute_maps.get(attribute_name);
        } else {
            return null;
        }
    }

    public String get_gene_id(){return this.gene_id;}

}
