package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.Classes.Scan;
import sample.Classes.ScanPair;
import sample.Utility.ImageUtils;
import sample.Utility.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AnalysisKeywords{
    private static HashMap<String, ArrayList<ScanPair>> keyScanAssociations;      // Hashmap of each keyword and the pairs
    private static HashSet<String> keywords;
    /**
     * Executes scanning and computer vision upon button press
     */
    public static void execute() throws IOException {
        // Run all checks
        if(!runChecks())
            return;

        File directory = Controller.getTargetAnalysisDirectory();
        HashMap<String, String> results = new HashMap<>();
        for (File file : directory.listFiles() ){
            if (!file.isDirectory()) {
                System.out.println("Analyzing.");
                keywords = new HashSet<>();
                for (String keyword : Controller.getListOfKeywords())
                    keywords.add(keyword);
                results = analyze(file);
            }
            System.out.println("======================");
        }
        // TODO: PARSE THE RESULTS HASHMAP
        // TODO: DETERMINE IF RESULTS ARE VALID
        // TODO: VALID RESULTS ARE PUT INTO PRIMARY CSV
        // TODO: INVALID RESULTS ARE PUT INTO SECONDARY CSV
        // TODO: INVALID RESULTS IMAGE FILE IS PUT FOR REVIEW
    }

    /**
     * Helper program for debugging
     * @param completeImageFile
     */
    private static HashMap<String, String> analyze(File completeImageFile) throws IOException {
        // TODO: USE PROPERTIES FILE

        AnalysisMicrosoftAzure analysisObject = new AnalysisMicrosoftAzure(
                completeImageFile.getPath(),
                "https://lsu-frank-tsai.cognitiveservices.azure.com/",
                "ffcd9ea1d1104c17b794879fa4262228"
        );
        JSONObject fullScanResults = analysisObject.analyze();
        JSONArray results1 = (JSONArray) fullScanResults.get("recognitionResults");
        JSONObject results2 = results1.getJSONObject(0);
        JSONArray fullScanContents = (JSONArray) results2.get("lines");

        HashMap<String, String> retrievedData = new HashMap<>();

        int completeImageWidth = (int)(
                (((JSONArray)fullScanResults.get("recognitionResults")).getJSONObject(0)).get("width")
        );
        int completeImageHeight = (int)(
                (((JSONArray)fullScanResults.get("recognitionResults")).getJSONObject(0)).get("height")
        );
        int[] fullDim = {completeImageWidth, completeImageHeight};
        // For each line in the contents
        for (int i = 0; i < fullScanContents.length(); i++) {
            JSONObject lineJsonObject = fullScanContents.getJSONObject(i);
            String text = Utils.filter((String) lineJsonObject.get("text"));
            // For each keyword submitted
            for (String keyword : keywords){
                if (text.contains(keyword)){
                    // Creates a new Scan to store properties of the keyword scan
                    Scan keywordScan = new Scan(completeImageFile, completeImageWidth, completeImageHeight);
                    JSONArray arr = (JSONArray) lineJsonObject.get("boundingBox");
                    int[] param = new int[8];
                    for (int j = 0; j < 8; j++){
                        param[j] = arr.getInt(j);
                    }
                    keywordScan.addScan(param);
                    String result = process(completeImageFile, keywordScan, keyword, fullDim);
                    retrievedData.put(keyword, result);
                    if (result == null){
                        return null;
                    }
                    keywords.remove(keyword); // Remove keywords, duplicate keywords not allowed in document
                }
            }
        }

        if (keywords.size() > 0) {
            System.out.println("Keywords still remain!");
            // Indicates that not all keywords were found in the entire document
            Controller.submitFileForReview(completeImageFile);
            return null;
        }
        return retrievedData;
    }

    /**
     * Processes the file
     * @return returns corresponding text accoridng to the keyword
     */
    private static String process(File fullImageFile, Scan keywordScan, String keyword, int[] fullDim) throws IOException {
        System.out.println("Loading file " + fullImageFile.getName());
        if (keyScanAssociations == null){
            keyScanAssociations = new HashMap<>();
            // TODO: LOAD ASSOCIATIONS FROM FILE
        }
        File cropped = null;

        ArrayList<ScanPair> pairsOfPotentialTargets = keyScanAssociations.get(keyword);
        if (pairsOfPotentialTargets != null){
            // Indicates that there is a stored scan section for that keyword
            // For each of the scan with the same keyword
            ScanPair reference = null;

            for (ScanPair pair : pairsOfPotentialTargets) { // Scan every pair for that particular keyword
                // Check to see if any lengths are valid
                Scan potentialValidScan = pair.getKeywordScan();
                double[] compared = keywordScan.compareTo(potentialValidScan);
                if (compared[0] < 1 && compared[1] < 1 && compared[2] < 1 && compared[3] < 1) {
                    reference = pair;
                    System.out.println("A fitted section was detected.");
                    System.out.print("Keyword Scan: ");
                    System.out.println(keywordScan.toString());
                    System.out.print("Potential Scan: ");
                    System.out.println(potentialValidScan.toString());
                    break;
                }
                System.out.println("A fitted section was not detected.");
                System.out.print("Keyword Scan: ");
                System.out.println(keywordScan.toString());
                System.out.print("Potential Scan: ");
                System.out.println(potentialValidScan.toString());
            }
            if (reference == null){
                // Indicates that there has yet to be any reference-target association
                Scan targetScan = executeKeywordCorrections(fullImageFile, keywordScan, fullDim);
                if (targetScan == null)
                    return null;
                ScanPair pair = new ScanPair(keywordScan, targetScan);
                pairsOfPotentialTargets.add(pair);
                keyScanAssociations.put(keyword, pairsOfPotentialTargets);
                if (ImageUtils.confirmType(fullImageFile)) {     // Confirm that file is image
                    cropped = applyCropping(
                            fullImageFile,
                            targetScan,
                            fullDim,
                            keyword
                    );
                }

            } else{
                // Indicates that we have a point of reference
                Scan target = reference.getTargetScan();
                if (ImageUtils.confirmType(fullImageFile)) {     // Confirm that file is image
                    cropped = applyCropping(fullImageFile,
                            target,
                            fullDim,
                            keyword
                    );
                }

            }
        } else{
            // Indicates that is yet a scan stored for the particular keyword
            // Indicates that there has yet to be any reference-target association
            Scan targetScan = executeKeywordCorrections(fullImageFile, keywordScan, fullDim);
            if (targetScan == null)
                return null;
            ScanPair pair = new ScanPair(keywordScan, targetScan);
            pairsOfPotentialTargets = new ArrayList<>();
            pairsOfPotentialTargets.add(pair);
            keyScanAssociations.put(keyword, pairsOfPotentialTargets);
            if (ImageUtils.confirmType(fullImageFile)) {     // Confirm that file is image
                cropped = applyCropping(fullImageFile, targetScan, fullDim, keyword);
            }
        }
        if (cropped != null){
            // TODO: SEND FILE TO MICROSOFT AZURE FOR ANALYSIS
            // TODO: RETURN RESULT AS DATA
        }
        return null;
    }

    private static File applyCropping(File fullImageFile, Scan target, int[] fullDim, String keyword){
        String cachePath = Controller.getCacheFolder().getPath();
        String fileName = fullImageFile.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        try {
            BufferedImage src = ImageIO.read(fullImageFile);
            // Get Target from the ScanPair
            Rectangle cropArea = target.getRectangle(fullDim[0], fullDim[1]);
            BufferedImage cropped = src.getSubimage(
                    (int) cropArea.getX(),
                    (int) cropArea.getY(),
                    (int) cropArea.getWidth(),
                    (int) cropArea.getHeight()
            );
            File result = new File(cachePath + "/" + keyword + ".png");
            ImageIO.write(cropped,
                    "PNG",
                    result
            );
            System.out.println("Wrote to cache!");
            return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Scan executeKeywordCorrections(File fullImage, Scan keywordScan, int[] fullDim){
        System.out.printf("Executing Corrections on file %s\n", fullImage.getName());
        Stage applyCorrection = new Stage();
        Scan result = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    AnalysisKeywords.class.getResource(
                            "/ApplyImageCorrections.fxml"
                    )
            );
            applyCorrection.setTitle("Apply Corrections");
            applyCorrection.setScene(
                    new Scene(
                            loader.load()
                    )
            );
            applyCorrection.setResizable(false);
            applyCorrection.setMaximized(true);
            ApplyKeywordCorrections controller =
                    loader.<ApplyKeywordCorrections>getController();
            controller.initData(fullImage, keywordScan, fullDim);
            applyCorrection.initStyle(StageStyle.UNDECORATED);
            applyCorrection.showAndWait();
            if (!controller.submittedForReview())
                result = controller.getLastScan();
            else
                return null;
        } catch(IOException e){
            e.printStackTrace();
            System.exit(0);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return result;
    }

    /**
     * Helper program that determines execution should run
     * @return
     */
    private static boolean runChecks(){
        Controller.makeCacheFolder();
        return true;
    }
}
