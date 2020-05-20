package louisiana;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class AnalysisMicrosoftAzure {
    private String imagePath;
    private HashMap<String, String> authentication;
    private String endpoint;
    private String subscriptionKey;
    private String operationLocation;

    private String uriBase;



    public AnalysisMicrosoftAzure(String imagePath){
        this.imagePath = imagePath;
        authentication = Controller.loadAzureData(); // TODO: ADD DECRYPTION OF DATA
        endpoint = authentication.get("endpoint");
        subscriptionKey = authentication.get("key");
        if(endpoint != null && subscriptionKey != null){
            uriBase = endpoint + "vision/v2.1/read/core/asyncBatchAnalyze";
        }
    }

    public AnalysisMicrosoftAzure(String imagePath, String endpoint, String subscriptionKey){
        this.imagePath = imagePath;
        this.endpoint = endpoint;
        this.subscriptionKey = subscriptionKey;
        if(endpoint != null && subscriptionKey != null){
            uriBase = endpoint + "vision/v2.1/read/core/asyncBatchAnalyze";
        }
    }



    /**
     * Analyzes and returns JSONObject from Microsoft Azure
     * @return
     */
    public JSONObject analyze() {

        JSONObject result = null;

        System.out.println("Sending to Azure.");

        CloseableHttpClient httpTextClient = HttpClientBuilder.create().build();
        CloseableHttpClient httpResultClient = HttpClientBuilder.create().build();;

        try {
            // This operation requires two REST API calls. One to submit the image
            // for processing, the other to retrieve the text found in the image.

            URIBuilder builder = new URIBuilder(uriBase);

            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            boolean remote = false;
            // Request body, Remote
            if (remote) {
                String url = "https://i.imgur.com/E5K6YaF.png";
                StringEntity requestEntity;
                requestEntity =
                        new StringEntity("{\"url\":\"" + url + "\"}");
                request.setEntity(requestEntity);
            } else {
                File file = new File(imagePath);
                FileEntity reqEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
                request.setEntity(reqEntity);
            }

            // Two REST API methods are required to extract text.
            // One method to submit the image for processing, the other method
            // to retrieve the text found in the image.

            // Call the first REST API method to detect the text.
            HttpResponse response = httpTextClient.execute(request);

            // Check for success.
            if (response.getStatusLine().getStatusCode() != 202) {
                // Format and display the JSON error message.
                HttpEntity entity = response.getEntity();
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                CompletableFuture.runAsync(()->System.out.println("Error:\n"));
                CompletableFuture.runAsync(()->System.out.println(json.toString(2)));
                return null;
            }

            // Store the URI of the second REST API method.
            // This URI is where you can get the results of the first REST API method.
            operationLocation = null;

            // The 'Operation-Location' response header value contains the URI for
            // the second REST API method.
            Header[] responseHeaders = response.getAllHeaders();
            for (Header header : responseHeaders) {
                if (header.getName().equals("Operation-Location")) {
                    operationLocation = header.getValue();
                    break;
                }
            }

            if (operationLocation == null) {
                CompletableFuture.runAsync(()->System.out.println("\nError retrieving Operation-Location.\nExiting."));
                System.exit(1);
            }

            CompletableFuture.runAsync(()->System.out.println("Text submitted."));

            HttpEntity responseEntity = null;
            int checks = 0;

            do {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CompletableFuture.runAsync(()->System.out.println("Checking for response."));

                // Call the second REST API method and get the response.
                HttpGet resultRequest = new HttpGet(operationLocation);
                resultRequest.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

                HttpResponse resultResponse = httpResultClient.execute(resultRequest);

                responseEntity = resultResponse.getEntity();

                if (responseEntity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(responseEntity);
                    result = new JSONObject(jsonString);
                    CompletableFuture.runAsync(()->System.out.println("Response Received."));
                    // CompletableFuture.runAsync(()->System.out.println(jsonString));
                    if (
                            ((String)result.get("status")).equals("Running")
                                    || ((String)result.get("status")).equals("NotStarted")
                    )
                        responseEntity = null;
                    else if ( ((String)result.get("status")).equals("Failed")) {
                        checks = 11;
                        break;
                    } else if( ((String)result.get("status")).equals("Succeeded")){
                        break;
                    } else{
                        // Unknown response
                        responseEntity = null;
                    }
                }
            }  while (responseEntity == null && checks <= 10);

            // If response entity is null, then there was something wrong
            if (responseEntity == null){
                if (checks > 10) {
                    CompletableFuture.runAsync(() -> System.out.println("Response timeout."));
                    return null;
                }
                CompletableFuture.runAsync(() -> System.out.println("No JSON was returned."));
                return null;
            }
        } catch (Exception e) {
            CompletableFuture.runAsync(()->System.out.println(e.getMessage()));
        }
        // Return the json response
        return result;
    }


/**
 if (analyzePrint) {
 CloseableHttpClient httpClient = HttpClientBuilder.create().build();
 try {
 URIBuilder uriBuilder = new URIBuilder(uriBase);

 uriBuilder.setParameter("language", "unk");
 uriBuilder.setParameter("detectOrientation", "true");

 // Request parameters.
 URI uri = uriBuilder.build();
 HttpPost request = new HttpPost(uri);

 // Request headers.
 request.setHeader("Content-Type", "application/octet-stream");
 request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

 boolean remote = false;
 // Request body, Remote
 if (remote) {
 String url = "https://i.imgur.com/E5K6YaF.png";
 StringEntity requestEntity;
 requestEntity =
 new StringEntity("{\"url\":\"" + url + "\"}");
 request.setEntity(requestEntity);
 } else {
 File file = new File(imagePath);
 FileEntity reqEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
 request.setEntity(reqEntity);
 }

 // Request body, Local


 // Call the REST API method and get the response entity.
 HttpResponse response = httpClient.execute(request);
 HttpEntity entity = response.getEntity();
 System.out.print("Status: ");
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
 System.out.print("Error: ");
 System.out.println(e);
 }
 }
 else{
 **/
}
