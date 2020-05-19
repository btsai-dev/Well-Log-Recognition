package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

public class AnalysisKeywords{
    private static HashSet<String> keywords;                                            // Hashset of all keywords
    private static HashMap<String, ArrayList<ScanTargetPair>> keyScanAssociations;      // Hashmap of each keyword and the pairs


    /**
     * Executes scanning and computer vision upon button press
     */
    public static void execute() throws IOException {
        // Run all checks
        if(!runChecks())
            return;

        // Get keywords
        keywords = Controller.getListOfKeywords();

        File directory = Controller.getTargetAnalysisDirectory();
        for (File file : directory.listFiles() ){
            if (!file.isDirectory()) {
                System.out.println("Analyzing.");
                analyze(file, Controller.getListOfKeywords());
            }
        }
    }

    /**
     * Helper program that analyzes files
     * @param file
     */
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

                }
            }
        }
    }

    /**
     * Helper program for debugging
     * @param completeImageFile
     * @param keywords
     */
    public static void analyze(File completeImageFile, HashSet<String> keywords) throws IOException {
        AnalysisKeywords.keywords = keywords;
        // TODO: USE PROPERTIES FILE
        AnalysisMicrosoftAzure analysisObject = new AnalysisMicrosoftAzure(completeImageFile.getPath(), "https://lsu-frank-tsai.cognitiveservices.azure.com/", "ffcd9ea1d1104c17b794879fa4262228");
        JSONObject fullScanResults = analysisObject.analyze();
        JSONArray results1 = (JSONArray) fullScanResults.get("recognitionResults");
        JSONObject results2 = results1.getJSONObject(0);
        JSONArray fullScanContents = (JSONArray) results2.get("lines");

        //JSONArray fullScanContents = (JSONArray) ( ( ).get("lines") );
        int completeImageWidth = (int)  ( ( ((JSONArray) fullScanResults.get("recognitionResults")).getJSONObject(0)).get("width") );
        int completeImageHeight = (int) ( ( ((JSONArray) fullScanResults.get("recognitionResults")).getJSONObject(0)).get("height") );
        int[] fullDim = {completeImageWidth, completeImageHeight};
        // For each line in the contents
        for (int i = 0; i < fullScanContents.length(); i++) {
            JSONObject lineJsonObject = fullScanContents.getJSONObject(i);
            // For each keyword submitted
            for (String keyword : keywords){
                String text = (String) lineJsonObject.get("text");
                if (text.contains(keyword)){
                    // Creates a new ScanProperties to store properties of the keyword scan
                    ScanProperties keywordScan = new ScanProperties(completeImageFile, completeImageWidth, completeImageHeight);
                    JSONArray arr = (JSONArray) lineJsonObject.get("boundingBox");
                    int[] param = new int[8];
                    for (int j = 0; j < 8; j++){
                        param[j] = arr.getInt(j);
                    }
                    keywordScan.addScan(param);
                    process(completeImageFile, keywordScan, keyword, fullDim);
                }
            }
        }
    }

    /**
     * Processes information
     * @return
     */
    private static void process(File fullImageFile, ScanProperties keywordScan, String keyword, int[] fullDim) throws IOException {
        if (keyScanAssociations == null){
            keyScanAssociations = new HashMap<>();
            // TODO: LOAD ASSOCIATIONS FROM FILE
        }
        ArrayList<ScanTargetPair> list = keyScanAssociations.get(keyword);
        if (list != null){
            // Indicates that there is a stored scan section for that keyword
            // For each of the scan with the same keyword
            ScanTargetPair reference = null;

            for (ScanTargetPair pair : list) { // Scan every pair for that particular keyword
                // Check to see if any lengths are valid
                double[] compared = keywordScan.compareTo(pair.getKeywordScan());
                if (compared[0] < 1 || compared[1] < 1 || compared[2] < 1 || compared[3] < 1) {
                    reference = pair;
                    break;
                }
            }
            if (reference == null){
                // Indicates that there has yet to be any reference-target association
                ScanProperties targetScan = executeKeywordCorrections(fullImageFile, keywordScan, fullDim);
                ScanTargetPair pair = new ScanTargetPair(keywordScan, targetScan);
                list.add(pair);
                keyScanAssociations.put(keyword, list);
                // TODO: APPLY CHANGES TO FILE
            } else{
                // Indicates that we have a point of reference
                ScanProperties toBeScanned = reference.getTargetScan();
                if (ImageUtils.confirmType(fullImageFile)) {     // Confirm that file is image
                    String directoryPath = Controller.getTargetAnalysisDirectory().getPath();
                    String fileName = fullImageFile.getName();
                    int pos = fileName.lastIndexOf(".");
                    if (pos > 0) {
                        fileName = fileName.substring(0, pos);
                    }
                    System.out.println("Loading file " + fileName);

                    String saveFolderPath = directoryPath + "/Cropped/" + fileName;
                    File saveDirectory = new File(saveFolderPath);
                    saveDirectory.mkdirs();
                    BufferedImage src = ImageIO.read(fullImageFile);

                    // Get Target from the ScanTargetPair
                    ScanProperties target = reference.getTargetScan();
                    Rectangle cropArea = target.getRectangle(fullDim[0], fullDim[1]);
                    BufferedImage cropped = src.getSubimage(
                            (int) cropArea.getX(),
                            (int) cropArea.getY(),
                            (int) cropArea.getWidth(),
                            (int) cropArea.getHeight()
                    );
                    ImageIO.write(cropped, "PNG", new File(saveFolderPath + "/" + keyword + ".png"));
                    System.out.println("Wrote to savefolderpath!");

                    // Loop through each section
                    /**
                    int counter = 0;
                    for (DefaultScan section : Controller.defaultPositions) {
                        System.out.println("Going through a section!");
                        Point2D[] points = section.returnCornersImageReference();
                        Rectangle rect = getRect(points[0], points[1]);
                        BufferedImage cropped = src.getSubimage((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                        System.out.println(cropped.getWidth());
                        System.out.println("Writing to path");
                        counter++;
                    }
                     */
                }

                // TODO: CROP OUT SECTION AND SEND TO MICROSOFT AZURE
            }
        } else{
            // Indicates that is yet a scan stored for the particular keyword
            // Indicates that there has yet to be any reference-target association
            ScanProperties targetScan = executeKeywordCorrections(fullImageFile, keywordScan, fullDim);
            ScanTargetPair pair = new ScanTargetPair(keywordScan, targetScan);
            list = new ArrayList<>();
            list.add(pair);
            keyScanAssociations.put(keyword, list);
        }
    }

    public static ScanProperties executeKeywordCorrections(File fullImage, ScanProperties keywordScan, int[] fullDim){
        Stage applyCorrection = new Stage();
        ScanProperties result = null;
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
            result = controller.getLastScan();
            System.out.println(result);
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
        return true;
    }
}
