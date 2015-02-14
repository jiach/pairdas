package com.upenn;


import com.upenn.annotation.GeneIsoformInfo;
import com.upenn.parsers.GTFParser;
import com.upenn.parsers.SimulatedCountParser;
import com.upenn.parsers.SimulatedCounts;
import com.upenn.utils.Logger;

import java.io.File;

import static java.lang.System.getenv;

/*try to commit some changes*/
/*
public class Main {

    public static void main(String[] args) {
        String[] isoHeader=null;
        String isoLine;
        List<Integer> isoLen = null;
        List<List<Integer>> isoMat = null;
        String homeDir = getenv("HOME");
        String isoFileName="/Dropbox/Dissertation_2014/DAS_Paird/isoform_info_09_22_2014.csv";
        String matrixFileName="/Dropbox/Dissertation_2014/DAS_Paird/sim_multiperm_data_09_30_2014.csv";

        try {
            BufferedReader in = new BufferedReader(new FileReader(homeDir + isoFileName));
            isoHeader = in.readLine().split(",");

            isoLen = new ArrayList<Integer>();
            isoMat = new ArrayList<List<Integer>>();

            while ((isoLine = in.readLine()) != null) {
                String[] ar = isoLine.split(",");
                isoLen.add(Integer.parseInt(ar[1]));
                List<Integer> isoRow = new ArrayList<Integer>();
                for (int i = 2; i < ar.length; i++) {
                    isoRow.add(Integer.parseInt(ar[i]));
                }
                isoMat.add(isoRow);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Cannot read"+homeDir+isoFileName);
        }

        GeneIsoformInfo newGene=new GeneIsoformInfo("generic","chr1",ArrayUtils.subarray(isoHeader, 2,isoHeader.length), isoLen, isoMat);
        newGene.printAll();

        try{
            BufferedReader in = new BufferedReader(new FileReader(homeDir+ matrixFileName));
            in.readLine();
            Integer prevPermIdx = -1;
            List<Integer> permIdx = new ArrayList<Integer>();
            List<List<String>> subjectIdx=new ArrayList<List<String>>();
            List<List<Integer>> readsBefore=new ArrayList<List<Integer>>();
            List<List<Integer>> readsAfter=new ArrayList<List<Integer>>();
            String matrixLine;
            Integer itrPermIdx = -1;
            while((matrixLine=in.readLine())!=null){
                String[] ar =matrixLine.split(",");
                if(Integer.parseInt(ar[0]) != prevPermIdx) {
                    itrPermIdx = itrPermIdx+1;
                    permIdx.add(Integer.parseInt(ar[0]));
                    subjectIdx.add(new ArrayList<String>());
                    readsBefore.add(new ArrayList<Integer>());
                    readsAfter.add(new ArrayList<Integer>());
                    prevPermIdx=Integer.parseInt(ar[0]);
                }
                subjectIdx.get(itrPermIdx).add(ar[1]);
                readsBefore.get(itrPermIdx).add(Integer.parseInt(ar[2]));
                readsAfter.get(itrPermIdx).add(Integer.parseInt(ar[3]));
            }
            List<Double[]> pValOutSignIncl = new ArrayList<Double[]>();
            List<Double[]> pValOutSignExcl = new ArrayList<Double[]>();
            List<Double[]> pValOutUngrouped = new ArrayList<Double[]>();
            List<Double[]> pValOutPairedT = new ArrayList<Double[]>();
            double[] cor = new double[subjectIdx.size()];
            int idx1=3,idx2=5;
            BufferedWriter outputLogRatio = new BufferedWriter(new FileWriter(homeDir+"/Dropbox/Dissertation_2014/DAS_Paird/logRatioPrintOut"+"_"+Integer.toString(idx1)+"_"+Integer.toString(idx2)+".txt"));
            String date = new SimpleDateFormat("MM_dd_yyyy").format(new Date());
            BufferedWriter outputPValsSigned = new BufferedWriter(new FileWriter(homeDir+"/Dropbox/Dissertation_2014/DAS_Paird/PVals_Signed_Sim_"+date+".txt"));
            BufferedWriter outputPValsUnsigned = new BufferedWriter(new FileWriter(homeDir+"/Dropbox/Dissertation_2014/DAS_Paird/PVals_Unsigned_Sim_"+date+".txt"));
            BufferedWriter outputPValsUngrouped = new BufferedWriter(new FileWriter
                    (homeDir+"/Dropbox/Dissertation_2014/DAS_Paird/PVals_Ungrouped_Sim_"+date+".txt"));
            BufferedWriter outputPValsPairedT = new BufferedWriter(new FileWriter
                    (homeDir+"/Dropbox/Dissertation_2014/DAS_Paird/PVals_PairedT_Sim_"+date+".txt"));
            for(int i=0;i<subjectIdx.size();i++){
                ReadsMatrix newMatrix = new ReadsMatrix(subjectIdx.get(i), readsBefore.get(i), readsAfter.get(i), newGene);
                Double[] idx = new Double[1];
                idx[0]=Double.valueOf(i);
                pValOutSignIncl.add(ArrayUtils.addAll(idx,newMatrix.getHotellingTPValsSignInc(false)));
                pValOutSignExcl.add(ArrayUtils.addAll(idx,newMatrix.getHotellingTPValsSignExcl(false)));
                pValOutUngrouped.add(ArrayUtils.addAll(idx, newMatrix.getHotellingTPValsUngrouped(false)));
                pValOutPairedT.add(ArrayUtils.addAll(idx, newMatrix.getHotellingTPValsPairedTSignExcl(false)));
                cor[i]=newMatrix.getRegionPairCorrelation(idx1,idx2,"spearman");
                newMatrix.printLogRatios(idx1,idx2,i,outputLogRatio);
            }
            outputLogRatio.close();
            //System.out.println(new Mean().evaluate(cor,0,cor.length));
            //System.out.println(Arrays.toString(cor));
            writeMatrixToFile("Hello", pValOutSignIncl,newGene.listRegionGroup.getGroupNames(), outputPValsSigned);
            writeMatrixToFile("Hello", pValOutSignExcl,newGene.listRegionGroup.getGroupNamesSignExcl(), outputPValsUnsigned);
            writeMatrixToFile("Hello", pValOutUngrouped, outputPValsUngrouped);
            writeMatrixToFile("Hello",pValOutPairedT,newGene.listRegionGroup.getGroupNamesSignExcl(),outputPValsPairedT);
        } catch (IOException e) {
            System.out.println("Cannot read "+homeDir+matrixFileName);
        }
    }

    public static void writeMatrixToFile(String fileName, List<Double[]> matrix, String[] header, BufferedWriter fileHandle){
        try {
            fileHandle.write(StringUtils.join(header,"\t"));
        } catch (IOException e) {t
            System.out.println("Cannot write to pvalues file!");
        }
        Iterator<Double[]> itrRowMatrix = matrix.iterator(); 
        try{
        while(itrRowMatrix.hasNext()){
            fileHandle.newLine();
            fileHandle.write(StringUtils.join(itrRowMatrix.next(), "\t"));
            }
            fileHandle.flush();
        }catch(IOException e){
            System.out.println("Cannot write pvalue file!");
        }
    }

    public static void writeMatrixToFile(String fileName, List<Double[]> matrix, BufferedWriter fileHandle){
        Iterator<Double[]> itrRowMatrix = matrix.iterator();
        try{
            while(itrRowMatrix.hasNext()){
                fileHandle.write(StringUtils.join(itrRowMatrix.next(),"\t")+"\n");
            }
            fileHandle.flush();
        }catch(IOException e){
            System.out.println("Cannot write to pvalues file!");
        }
    }
}*/

/*
public class Main {
    public static void main(String[] args){
        String homeDir = getenv("HOME");
        String filename = homeDir+"/IdeaProjects/pairdas/data/refgene_combined";
        IsoformRegionMatrixParser genePredParser = new IsoformRegionMatrixParser(filename);
        SimulatedCountParser countParser = new SimulatedCountParser
                (homeDir+"/IdeaProjects/pairdas/data/simu_counts_alt.txt");
        SimulatedCounts altCounts = countParser.ReadAllCounts();

        for (GeneIsoformInfo curGene : genePredParser.geneList) {
            if (altCounts.containsGene(curGene.getName()) && curGene.getNumberRegions()==altCounts.getNumRegions
                    (curGene.getName())){
                System.out.println(curGene.getName()+"\t"+Double.toString(altCounts.getFishersPvalueForGene(curGene)));
            }

        }
    }
}*/


public class Main {
    public static void main(String[] args){
        
        boolean verbose = true;
        String log_fn = "/home/cheng/Dissertation/pairdas/pairdas.log";
        Logger pairdas_logger = new Logger(verbose, new File(log_fn));
        File gtf_file = new File("/home/cheng/Dissertation/pairdas/ensembl_sorted.gtf");
        pairdas_logger.log_message("Parsing gtf file: "+gtf_file.toString());
        GTFParser gtf_parser = new GTFParser(gtf_file);
        pairdas_logger.log_message(Integer.toString(gtf_parser.get_number_genes())+" genes parsed.");
        
        
        pairdas_logger.end_logging();
    }
}