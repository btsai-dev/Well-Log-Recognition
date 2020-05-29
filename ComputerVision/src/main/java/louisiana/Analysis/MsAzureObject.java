package louisiana.Analysis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

public class MsAzureObject {
    private JSONObject fullForm = null;
    private int version;
    Dimension dim = null;
    private JSONObject results = null;
    private JSONArray lines = null;

    public MsAzureObject(JSONObject fullForm){
        this.fullForm = fullForm;
        if (fullForm.has("recognitionResults"))
            version = 2;
        else if (fullForm.has("analyzeResult"))
            version = 3;
        else
            version = 0;
        if (version == 3){
            results = fullForm
                    .getJSONObject("analyzeResult")
                    .getJSONArray("readResults")
                    .getJSONObject(0);
            dim = new Dimension(
                    results.getInt("width"),
                    results.getInt("height")
            );
            lines = results.getJSONArray("lines");
        } else if(version == 2){
            results = (fullForm
                    .getJSONArray("recognitionResults"))
                    .getJSONObject(0);
            dim = new Dimension(
                    results.getInt("width"),
                    results.getInt("height")
            );
            lines = results.getJSONArray("lines");
        }
    }


    public JSONObject getResults(){
        return results;
    }

    public JSONArray getLines(){
        return lines;
    }

    public Dimension getDim(){
        return dim;
    }

    public boolean isValid(){
        if (version == 0)
            return false;
        return true;
    }
}
