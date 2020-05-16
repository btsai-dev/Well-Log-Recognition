package sample;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;

public class AnalysisMicrosoftAzure {
    private String imagePath;
    private HashMap<String, String> authentication;
    private String endpoint;
    private String subscriptionKey;

    private JSONObject jsonResponse;

    private String uriBase;

    public AnalysisMicrosoftAzure(String imagePath){
        this.imagePath = imagePath;
        authentication = Controller.loadAzureData();
        endpoint = authentication.get("endpoint");
        subscriptionKey = authentication.get("key");
        if(endpoint != null && subscriptionKey != null){
            uriBase = endpoint + "vision/v2.1/ocr";
        }
    }

    public AnalysisMicrosoftAzure(String imagePath, String endpoint, String subscriptionKey){
        this.imagePath = imagePath;
        this.endpoint = endpoint;
        this.subscriptionKey = subscriptionKey;
        if(endpoint != null && subscriptionKey != null){
            uriBase = endpoint + "vision/v2.1/ocr";
        }
    }

    public JSONObject analyze(){
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body, Remote
            /*
            StringEntity requestEntity =
                    new StringEntity("{\"url\":\"" + imageToAnalyze + "\"}");
            request.setEntity(requestEntity);
            */

            // Request body, Local
            File file = new File(imagePath);
            FileEntity reqEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);



            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine());

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                jsonResponse = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                System.out.println(jsonResponse.toString(2));
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
        return jsonResponse;
    }

    private byte[] convertImageToBytes() throws IOException {
        BufferedImage bImage = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "PNG", bos );
        return bos.toByteArray();
    }

}
