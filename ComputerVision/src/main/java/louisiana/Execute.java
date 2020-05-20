package louisiana;

import javafx.scene.shape.Rectangle;
import louisiana.Analysis.MsAzureInstance;
import louisiana.Classes.ReferenceTargetPair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;

public class Execute {
    private static HashMap<String, ArrayList<ReferenceTargetPair>> associations;
    public static void executeLouisiana(){

        // TODO: LOAD IN ASSOCIAITONS FILE

        Controller.setAzureData(
                "ffcd9ea1d1104c17b794879fa4262228",
                "https://lsu-frank-tsai.cognitiveservices.azure.com/"
        );

        File targetDir = Controller.getTargetDir();
        File[] listOfFiles = targetDir.listFiles();
        HashMap<String, String> response = new HashMap<>();
        for (File file : listOfFiles){

            if (!file.isDirectory()){
                System.out.printf("Analyzing File %s\n", file.getName());
                response = analyzeMsAzure(file);
            }
            if (response == null){
                Controller.submitFileForReview(file, Utils.CAUSE_UNKNOWN);
            } else{
                // TODO: PARSE THE RESULTS HASHMAP
                // TODO: DETERMINE IF RESULTS ARE VALID
                // TODO: VALID RESULTS ARE PUT INTO PRIMARY CSV
                // TODO: INVALID RESULTS ARE PUT INTO SECONDARY CSV
                // TODO: INVALID RESULTS IMAGE FILE IS PUT FOR REVIEW
            }
        }
    }

    private static HashMap<String, String> analyzeMsAzure(File wholeImage){
        MsAzureInstance instance = new MsAzureInstance(wholeImage);
        JSONObject response = instance.analyzeGeneral();
        if (response == null) {
            // Indicates that response failed
            return null;
        }
        JSONObject results = (response.getJSONArray("recognitionResults")).getJSONObject(0);
        JSONArray lines = null;
        try {
            lines = results.getJSONArray("lines");
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        if (lines == null){
            System.out.println("No lines found.");
            return null;
        }
        HashMap<String, String> data = new HashMap<>();

        int imgWidth = (int) results.get("width");
        int imgHeight = (int) results.get("height");
        int[] imgDim = {imgWidth, imgHeight};
        boolean idFound = false;
        boolean logsFound = false;
        for (int index = 0; index < lines.length(); index++){
            JSONObject lineObj = lines.getJSONObject(index);
            String lineText = Utils.filter(lineObj.getString("text"));
            String keyword = null;
            if (lineText.contains(Utils.filter("DO NOT USE"))){
                keyword = "DONOTUSE";
            } else if (lineText.contains(Utils.filter("OFFICE USE ONLY"))){
                keyword = "OFFICEUSEONLY";
            }

            if ( keyword != null
                    && idFound == false
                    && (keyword.equals("DONOTUSE") || keyword.equals("OFFICEUSEONLY"))
            ){
                idFound = true;
                JSONArray jsonBox = lineObj.getJSONArray("boundingBox");
                int[] boundingBox = new int[8];
                for (int i = 0; i < 8; i++) {
                    boundingBox[i] = jsonBox.getInt(i);
                }
                Rectangle refRect = Utils.rectFromBounding(boundingBox);
                ReferenceTargetPair newPair = new ReferenceTargetPair(refRect, imgDim);
                Rectangle tarRect = null;
                ArrayList<ReferenceTargetPair> pairs = associations.get(keyword);
                if (pairs == null || pairs.size() <= 0){
                    // No pair created yet
                    // Display image and have user select
                    // Save selection as rectangle into ReferenceTargetPair
                    // Process selection
                } else{
                    double closestDist = 2;
                    ReferenceTargetPair closestPair = null;
                    for (ReferenceTargetPair pair : pairs){
                        double relDist = newPair.compareTo(pair);
                        if (relDist < closestDist) {
                            closestDist = relDist;
                            closestPair = pair;
                        }
                    }

                    if (closestDist < 0.02){
                        tarRect = closestPair.getRect(refRect, imgDim);
                        // Process targetRect
                    } else{
                        // No suitable reference stored
                        // Display image and have user select
                        // Save selection as rectangle into ReferenceTargetPair
                        // Process selection
                    }
                }
            }
        }

        return data;
    }


}
