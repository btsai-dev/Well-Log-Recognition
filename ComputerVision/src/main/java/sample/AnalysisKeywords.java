package sample;

import javafx.event.ActionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class AnalysisKeywords {
    private static HashSet<String> keywords;                                            // Hashset of all keywords
    private static HashMap<String, ArrayList<ScanTargetPair>> keyScanAssociations;      // Hashmap of each keyword and the pairs

    public void execute(ActionEvent actionEvent){
        // Run all checks
        if(!runChecks())
            return;

        // Get keywords
        keywords = Controller.getListOfKeywords();

        File directory = Controller.getTargetAnalysisDirectory();
        for (File file : directory.listFiles() ){
            if (!file.isDirectory())
                analyze(file);
        }
    }

    private static void analyze(File file){
        AnalysisMicrosoftAzure analysisObject = new AnalysisMicrosoftAzure(file.getPath());
        JSONObject fullScanResults = analysisObject.analyze();
        JSONArray fullScanContents = (JSONArray) ( ( (JSONObject) fullScanResults.get("recognitionResults")).get("lines") );
        int width = (int) ( ( (JSONObject) fullScanResults.get("recognitionResults")).get("width") );
        int height = (int) ( ( (JSONObject) fullScanResults.get("recognitionResults")).get("height") );

        // For each line in the contents
        for (int i = 0; i < fullScanContents.length(); i++) {
            JSONObject obj = fullScanContents.getJSONObject(i);
            // For each keyword submitted
            for (String keyword : keywords){
                if (keyword.contains((String) obj.get("text"))){
                    ScanProperties textScan = new ScanProperties(file, width, height);
                    textScan.addScan((int[]) obj.get("boundingBox"));
                    // Check
                }
            }
        }
    }

    public static void analyze(File file, HashSet<String> keywords){
        AnalysisKeywords.keywords = keywords;
        AnalysisMicrosoftAzure analysisObject = new AnalysisMicrosoftAzure(file.getPath(), "https://lsu-frank-tsai.cognitiveservices.azure.com/", "ffcd9ea1d1104c17b794879fa4262228");
        JSONObject fullScanResults = analysisObject.analyze();
        JSONArray results1 = (JSONArray) fullScanResults.get("recognitionResults");
        JSONObject results2 = results1.getJSONObject(0);
        JSONArray fullScanContents = (JSONArray) results2.get("lines");

        //JSONArray fullScanContents = (JSONArray) ( ( ).get("lines") );
        int width = (int)  ( ( ((JSONArray) fullScanResults.get("recognitionResults")).getJSONObject(0)).get("width") );
        int height = (int) ( ( ((JSONArray) fullScanResults.get("recognitionResults")).getJSONObject(0)).get("height") );

        // For each line in the contents
        for (int i = 0; i < fullScanContents.length(); i++) {
            JSONObject obj = fullScanContents.getJSONObject(i);
            // For each keyword submitted
            for (String keyword : keywords){
                String text = (String) obj.get("text");
                if (text.contains(keyword)){
                    ScanProperties textScan = new ScanProperties(file, width, height);
                    JSONArray arr = (JSONArray) obj.get("boundingBox");
                    int[] param = new int[8];
                    for (int j = 0; j < 8; j++){
                        param[j] = arr.getInt(j);
                    }
                    textScan.addScan(param);

                }
            }
        }
    }

    /**
     * If dimensions are within 5% of the expected dimension, return true
     * @return
     */
    private boolean isExpected(ScanProperties scan, String keyword){
        ArrayList<ScanTargetPair> list = keyScanAssociations.get(keyword);
        if (list != null){
            // Indicates that there is a stored scan section for that keyword
            // For each of the scan with the same keyword
            ScanTargetPair reference = null;

            for (ScanTargetPair pair : list) { // Scan every pair for that particular keyword
                // Check to see if any lengths are valid
                double[] compared = scan.compareTo(pair.getKeywordScan());
                // We got the correct property to reference from
                if (compared[0] < 1 || compared[1] < 1 || compared[2] < 1 || compared[3] < 1) {
                    reference = pair;
                    break;
                }
            }
            if (reference == null){
                // Indicates that nothing satisfied
                // TODO: DISPLAY IMAGE
                // TODO: USER SELECTS CORNERS
                // TODO: STORE CORNERS INTO SCANPROPERTIES
                // TODO: STORE SCANPROPERTIES AND SCAN INTO SCANTARGETPAIR
                // TODO: STORE KEYWORD AND SCANTARGETPAIR INTO KEYSCANASSOCIATIONS
            } else{
                // Indicates that we have a point of reference
                ScanProperties toBeScanned = reference.getTargetScan();
                // TODO: CROP OUT SECTION AND SEND TO MICROSOFT AZURE
            }
        } else{
            // Indicates that is yet a scan stored for the particular keyword
            // TODO: DISPLAY IMAGE
            // TODO: SURROUND KEYWORD IN RECTANGLE
            // TODO: USER SELECTS CORNERS
            // TODO: STORE CORNERS INTO SCANPROPERTIES
            // TODO: STORE SCANPROPERTIES AND SCAN INTO SCANTARGETPAIR
            // TODO: STORE SCANTARGETPAIR INTO KEYSCANASSOCIATIONS ARRAYLIST
        }
    }

    private boolean runChecks(){
        return true;
    }


}
