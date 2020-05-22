package louisiana;

import com.sun.webkit.network.Util;
import javafx.scene.shape.Rectangle;
import louisiana.Analysis.MsAzureInstance;
import louisiana.Classes.ReferenceTargetPair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;

public class Execute {
    private static HashMap<String, ArrayList<ReferenceTargetPair>> associations;
    private static final String ID_IDENTIFIER_1 = Utils.filter("DO NOT USE");
    private static final String ID_IDENTIFIER_2 = Utils.filter("OFFICE USE ONLY");
    private static final String DATA_IDENTIFIER_1 = Utils.filter("");

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
                response = analyzeMsAzure(file, 10);
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

    private static HashMap<String, String> analyzeMsAzure(File wholeImage, int seconds){
        MsAzureInstance instance = new MsAzureInstance(wholeImage);
        JSONObject response = instance.analyzeGeneral(seconds * 1000);
        if (response == null) {
            // Indicates that response failed
            System.out.println("Response was null.");
            return null;
        }

        /**
        File jsonDir = new File("C:\\Users\\godon\\__LMAOOO\\JSONFiles");
        int result = writeJSONObjectToFile(
                response,
                jsonDir,
                FilenameUtils.removeExtension(wholeImage.getName())
        );
         */


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
        Rectangle idRect = null;
        int len = lines.length();
        for (int index = 0; index < len; index++){
            JSONObject line = lines.getJSONObject(index);
            if (idRect == null && Utils.filter(line.getString("text")).contains(ID_IDENTIFIER_1)){
                idRect = Utils.rectFromBounding(line.getJSONArray("boundingBox"));
            }
        }

        /**

        int imgWidth = (int) results.get("width");
        int imgHeight = (int) results.get("height");
        int[] imgDim = {imgWidth, imgHeight};
        Rectangle idRect1, idRect2;

        for (int index = 0; index < lines.length(); index++){
            JSONObject lineObj = lines.getJSONObject(index);
            String lineText = Utils.filter(lineObj.getString("text"));
            if (lineText.contains(ID_IDENTIFIER_1)){
                JSONArray jsonBox = lineObj.getJSONArray("boundingBox");
                int[] boundingBox = new int[8];
                for (int i = 0; i < 8; i++) {
                    boundingBox[i] = jsonBox.getInt(i);
                }
                idRect1 = Utils.rectFromBounding(boundingBox);
            } else if (lineText.contains(ID_IDENTIFIER_2)){
                JSONArray jsonBox = lineObj.getJSONArray("boundingBox");
                int[] boundingBox = new int[8];
                for (int i = 0; i < 8; i++) {
                    boundingBox[i] = jsonBox.getInt(i);
                }
                idRect2 = Utils.rectFromBounding(boundingBox);
            }





            if ( keyword != null
                    && idFound == false
                    && (keyword.equals("DONOTUSE") || keyword.equals("OFFICEUSEONLY"))
            ){
                idFound = true;

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
         */

        return data;
    }

    private static int writeJSONObjectToFile(JSONObject json, File dir, String name){
        System.out.println("Writing to new json file.");
        FileWriter file = null;
        try{
            file = new FileWriter(dir.getPath() + "/" + name + ".json");
            file.write(json.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (file != null)
                    file.flush();
                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

}
