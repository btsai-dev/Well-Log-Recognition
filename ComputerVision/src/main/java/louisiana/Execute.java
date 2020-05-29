package louisiana;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.jshell.execution.Util;
import louisiana.Analysis.ApplyKeywordCorrections;
import louisiana.Analysis.MsAzureInstance;
import louisiana.Analysis.MsAzureObject;
import louisiana.Classes.IdLogPair;
import louisiana.Classes.ReferenceTargetPair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.naming.ldap.Control;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.sql.Ref;
import java.util.*;
import java.util.List;

public class Execute {
    private static HashMap<String, ArrayList<IdLogPair>> formTargetAssociations;
    private static ArrayList<String> formIDs;
    private static void loadAssociations(){
        if (formIDs == null){
            formIDs = new ArrayList<>();
            formIDs.add("DNRGW1");
            formIDs.add("LDPWGW1R983");
            formIDs.add("LDPWGW1R178");
            formIDs.add("LDPWGW1");
            formIDs.add("REV992");
            formIDs.add("REV1185");
            formIDs.add("LDPWGW1R1084");
            formIDs.add("DNRGW1Rev0411");
        }
        if (formTargetAssociations == null) {
            formTargetAssociations = new HashMap<>();
            for (String id : formIDs){
                formTargetAssociations.put(id, new ArrayList<>());
            }
        }
    }

    public static void executeLouisiana(){
        // TODO: LOAD IN ASSOCIAITONS FILE
        loadAssociations();

        Controller.setAzureData(
                "ffcd9ea1d1104c17b794879fa4262228",
                "https://lsu-frank-tsai.cognitiveservices.azure.com/"
        );

        File targetDir = Controller.getTargetDir();
        File writeTo = Controller.getWriteTo();
        File[] listOfFiles = targetDir.listFiles();
        String response = null;
        for (File file : listOfFiles){
            if (!file.isDirectory()) {
                String filename = FilenameUtils.removeExtension(file.getName());
                System.out.printf("Analyzing File %s\n", filename);
                response = analyzeMsAzure(file, 5);
                if (response == null) {
                    Controller.submitFileForReview(file, Utils.NULL_RESPONSE);
                } else {
                    try{
                        PrintWriter pw = new PrintWriter(new FileWriter(writeTo, true));
                        pw.println(file.getName());
                        pw.println(response);
                        pw.flush();
                        pw.close();
                    } catch (Exception e){
                        e.printStackTrace();
                        Controller.submitFileForReview(file, Utils.CANNOT_READ);
                    }
                }
            }
        }
    }

    private static ApplyKeywordCorrections requestUserSegment(File wholeImage, String message, Dimension fullDim){
        Stage applyCorrection = new Stage();
        String result = null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    Execute.class.getResource(
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
            ApplyKeywordCorrections applyController =
                    loader.<ApplyKeywordCorrections>getController();
            applyController.initData(wholeImage, message);
            applyCorrection.initStyle(StageStyle.UNDECORATED);
            applyCorrection.showAndWait();
            if (applyController.submittedForReview())
                return null;
            return applyController;
            /**
            File res = Utils.applyCropping(wholeImage, refRect, "ID");
            MsAzureInstance instance = new MsAzureInstance(res);
            JSONObject response = instance.analyzeGeneral(10000);
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
            return lines;
            */
        } catch(IOException e){
            e.printStackTrace();
            System.exit(0);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private static String analyzeMsAzure(File wholeImage, int seconds) {
        // Analyze a smaller instance of the form
        MsAzureInstance instance = new MsAzureInstance(wholeImage);
        JSONObject response = instance.analyzeGeneralPreview(seconds * 1000);
        if (response == null) {
            // Indicates that response failed
            System.out.println("Response was null.");
            return null;
        }

        MsAzureObject results = new MsAzureObject(response);
        JSONArray lines = results.getLines();
        Dimension fullDim = results.getDim();

        /**
        // Hunt for the Parish location
        final String PARISH = Utils.filter("PARISH");
        JSONObject ParishLine = null;
        for (int index = 0; index < lines.length(); index ++){
            JSONObject line = lines.getJSONObject(index);
            String text = Utils.filter(line.getString("text"));
            if (Utils.levenshteinDistance(text, PARISH) < 3){
                ParishLine = line;
                break;
            }
        }
        if (ParishLine == null){
            System.out.println("Parish not found.");
            Controller.submitFileForReview(wholeImage, Utils.KEYWORD_NOT_FOUND);
            return null;
        }

        // Hunt for the Well No. location
        final String WELL_NO = Utils.filter("Well No.");
        JSONObject WellNoLine = null;
        for (int index = 0; index < lines.length(); index ++){
            JSONObject line = lines.getJSONObject(index);

            String text = Utils.filter(line.getString("text"));
            if (text.contains(WELL_NO) || Utils.levenshteinDistance(text, WELL_NO) < 2){
                WellNoLine = line;
                break;
            }
        }

        if (WellNoLine == null){
            System.out.println("Well No. not found.");
            Controller.submitFileForReview(wholeImage, Utils.KEYWORD_NOT_FOUND);
            return null;
        }
         */

        // Hunt for Driller's log
        final String DRILLER_LOG = Utils.filter("FROM TO DESCRIPTION");
        String concat = Utils.ALPHABET;
        JSONObject DrillerLogLine = null;
        int index = 0;
        while (index < lines.length()){
            JSONObject line = lines.getJSONObject(index);
            String text = Utils.filter(line.getString("text"));
            concat = concat + text;
            concat = concat.substring(concat.length() - 26);
            if (Utils.levenshteinDistance(concat.substring(concat.length()-17),DRILLER_LOG) <= 5
                    || concat.contains(DRILLER_LOG)
            ){
                DrillerLogLine = line;
                break;
            }
            index ++;
        }

        if (DrillerLogLine == null){
            System.out.println("Description and color not found.");
            Controller.submitFileForReview(wholeImage, Utils.KEYWORD_NOT_FOUND);
            return null;
        }


        // Crop out well logs, from both sides of page, and down to bottom of page
        Rectangle LogRect = Utils.rectFromBounding(DrillerLogLine.getJSONArray("boundingBox"));
        Rectangle cropLog = new Rectangle(
                0 ,
                LogRect.getY() + LogRect.getHeight() - 2,
                fullDim.width - 10,
                fullDim.height - (LogRect.getY() + LogRect.getHeight()) - 10
        );

        //DisplayImageTesting(wholeImage, cropRect, null);
        /**
        File idCropped = null;
        try {
            idCropped = Utils.applyCropping(wholeImage, cropID, "LogID", 1);
        } catch (Exception e){
            e.printStackTrace();
            Controller.submitFileForReview(wholeImage, Utils.CROP_FAILURE);
            return null;
        }
         */

        File logCropped = null;
        try{
            logCropped = Utils.applyCropping(wholeImage, cropLog, "LogData", 1);
        } catch(Exception e){
            e.printStackTrace();
            Controller.submitFileForReview(wholeImage, Utils.CROP_FAILURE);
            return null;
        }

        //MsAzureInstance idCrop = new MsAzureInstance(idCropped);
        MsAzureInstance logCrop = new MsAzureInstance(logCropped);
        // JSONObject idResponse = idCrop.analyzeGeneralPreview(5000);
        JSONObject logResponse = logCrop.analyzeGeneralPreview(5000);

        if (logResponse == null)
            return null;

        //File idJsonDir = new File("C:\\Users\\godon\\__LMAOOO\\JSONFiles\\idDir");
        //File logJsonDir = new File("C:\\Users\\godon\\__LMAOOO\\JSONFiles\\logDir");
        // writeJSONObjectToFile(idResponse, idJsonDir, wholeImage.getName());
        // writeJSONObjectToFile(logResponse, logJsonDir, wholeImage.getName());
        MsAzureObject logs = new MsAzureObject(logResponse);
        JSONArray logLines = logs.getLines();

        System.out.println("Looping through the header until first digit.");
        index = 0;
        do{
            JSONObject line = logLines.getJSONObject(index);
            String text = line.getString("text");
            if (Character.isDigit(text.charAt(0)) || text.length() == 1 || text.equals('o') || text.equals('O'))
                break;
            index ++;
        } while (index < logLines.length());


        if (index == logLines.length()) // No data found.
            return null;


        boolean newline = false;

        JSONObject line = logLines.getJSONObject(index);
        String text = line.getString("text");
        int[] bounding = Utils.arrayFromBounding(line.getJSONArray("boundingBox"));
        int lowerPos = bounding[5];
        String result = "";

        System.out.println("Looping through data until first newline with a nondigit");
        do{
            line = logLines.getJSONObject(index);
            text = line.getString("text");
            bounding = Utils.arrayFromBounding(line.getJSONArray("boundingBox"));
            if (bounding[1] >= lowerPos) { // TODO: MODIFY IT SO IT INSTEAD GOES BELOW A DIFF VALUE
                newline = true;
                lowerPos = bounding[5];
            }
            if (newline){
                if (!Character.isDigit(text.charAt(0)))
                    break;
            }
            // If everything passes without breaking out, then write to the line
            result = (new StringBuilder()).append(result).append(" ").append(text).toString();
            newline = false;
            index ++;
        } while (index < logLines.length());

        System.out.println(result);
        /**
         * Issues:
         *  - Multiline Description terminates too early
         *  -
         */

        return result;
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


    private static void DisplayImageTesting(File wholeImage, Rectangle rect1, Rectangle rect2){
        Stage showImage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(
                    Execute.class.getResource(
                            "/DisplayImageTesting.fxml"
                    )
            );
            showImage.setTitle("Apply Corrections");
            showImage.setScene(
                    new Scene(
                            loader.load()
                    )
            );
            DisplayImageTesting displayController =
                    loader.<DisplayImageTesting>getController();
            displayController.initData(wholeImage, rect1, rect2);
            showImage.setResizable(false);
            showImage.showAndWait();
        } catch(IOException e){
            e.printStackTrace();
            System.exit(0);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

}


/*
    private void FindParishWell() {

        // We assume that all three positions are accounted for.
        // Crop between between Parish left + 5%, Well No right + 5%, down 5%.

        Rectangle ParishRect = Utils.rectFromBounding(ParishLine.getJSONArray("boundingBox"));
        Rectangle WellNoRect = Utils.rectFromBounding(WellNoLine.getJSONArray("boundingBox"));
        //DisplayImageTesting(wholeImage, ParishRect, WellNoRect);
        int leftBoundary = Math.min(
                (int) ParishRect.getX(),
                (int) WellNoRect.getX()
        ) - (int) (fullDim.width * 0.02);
        // Account for border
        int rightBoundary = Math.max(
                (int) (ParishRect.getX() + ParishRect.getWidth()),
                (int) (WellNoRect.getX() + WellNoRect.getWidth())
        ) + (int) (fullDim.width * 0.02);
        int upperBoundary = Math.min(
                (int) (ParishRect.getY() + ParishRect.getHeight()),
                (int) (WellNoRect.getY() + ParishRect.getHeight())
        ) - 5;
        int lowerBoundary = upperBoundary + (int) (fullDim.height * 0.02);

        // Account for possible border issues
        leftBoundary = Math.max(0 + 5, leftBoundary);
        rightBoundary = Math.min(fullDim.width - 5, rightBoundary);
        upperBoundary = Math.max(0 + 5, upperBoundary);
        lowerBoundary = Math.min(fullDim.height - 5, lowerBoundary);


        Rectangle cropID = new Rectangle(
                leftBoundary,
                upperBoundary,
                rightBoundary - leftBoundary,
                lowerBoundary - upperBoundary
        );
    }

 */