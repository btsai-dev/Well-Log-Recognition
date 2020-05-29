package louisiana.Analysis;

import louisiana.Controller;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.json.JSONObject;
import java.io.File;

public class MsAzureInstance {
    private File img;
    private String uriBase;

    private static final String RUNNING = "Running";
    private static final String NOT_STARTED = "NotStarted";
    private static final String FAILED = "Failed";
    private static final String SUCCEEDED = "Succeeded";

    public MsAzureInstance(File img){
        this.img = img;
    }

    public JSONObject analyzeGeneral(){
        return analyzeGeneral(10000);
    }

    /**
     * Analyzes and returns JSONObject from Microsoft Azure from both printed and handwritten
     * @return jsonObject if valid, null if invalid
     */
    public JSONObject analyzeGeneral(int milliseconds) {
        if (Controller.getEndpoint() == null || Controller.getKey() == null){
            System.out.println("Missing Endpoint or Key.");
            return null;
        }
        String uriBase = Controller.getEndpoint() + "vision/v2.1/read/core/asyncBatchAnalyze";
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
            request.setHeader("Ocp-Apim-Subscription-Key", Controller.getKey());

            // Request body.
            FileEntity reqEntity = new FileEntity(img, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);

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
                System.out.println("Error:\n");
                System.out.println(json.toString(2));
                return null;
            }

            // Store the URI of the second REST API method.
            // This URI is where you can get the results of the first REST API method.
            String operationLocation = null;

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
                System.out.println("\nError retrieving Operation-Location.\nExiting.");
                System.exit(1);
            }

            // If the first REST API method completes successfully, the second
            // REST API method retrieves the text written in the image.
            //
            // Note: The response may not be immediately available. Text
            // recognition is an asynchronous operation that can take a variable
            // amount of time depending on the length of the text.
            // You may need to wait or retry this operation.

            System.out.printf("Text submitted.\n" +
                    "Waiting %d milliseconds to retrieve the recognized text.\n", milliseconds);
            do {
                Thread.sleep(milliseconds);

                // Call the second REST API method and get the response.
                HttpGet resultRequest = new HttpGet(operationLocation);
                resultRequest.setHeader("Ocp-Apim-Subscription-Key", Controller.getKey());

                HttpResponse resultResponse = httpResultClient.execute(resultRequest);
                HttpEntity responseEntity = resultResponse.getEntity();

                if (responseEntity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(responseEntity);
                    JSONObject json = new JSONObject(jsonString);
                    //System.out.println("Text recognition result response: \n");
                    //System.out.println(json.toString(2));
                    String status = (String) json.get("status");
                    if (status == null) {
                        System.out.println("Malformed JSON returned, likely error..");
                        System.out.printf("Response: %s", json.toString(2));
                        return null;
                    }
                    switch (status) {
                        case MsAzureInstance.SUCCEEDED:
                            System.out.println("Success. Returning JSONObject.");
                            return json;
                        case MsAzureInstance.FAILED:
                            System.out.println("MsAzure Analysis Failed.");
                            return null;
                        case MsAzureInstance.NOT_STARTED:
                            System.out.println("Not yet started. Waiting.");
                            break;
                        case MsAzureInstance.RUNNING:
                            System.out.println("Still running. Waiting.");
                            break;
                        default:
                            System.out.println("Unknown status, likely error.");
                            System.out.printf("Status: %s", status);
                            return null;
                    }
                }
            } while(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public JSONObject analyzeGeneralPreview(int milliseconds) {
        System.out.println("Using 3.0 API");
        if (Controller.getEndpoint() == null || Controller.getKey() == null){
            System.out.println("Missing Endpoint or Key.");
            return null;
    }
        //String uriBase = Controller.getEndpoint() + "vision/v3.0/read/analyze";
        String uriBase = Controller.getEndpoint() + "/vision/v3.0-preview/read/analyze";
        CloseableHttpClient httpTextClient = HttpClientBuilder.create().build();
        CloseableHttpClient httpResultClient = HttpClientBuilder.create().build();;

        try {
            // This operation requires two REST API calls. One to submit the image
            // for processing, the other to retrieve the text found in the image.

            URIBuilder builder = new URIBuilder(uriBase);
            builder.setParameter("language", "en");


            // Prepare the URI for the REST API method.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/octet-stream");
            //request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", Controller.getKey());
            // Request body.
            FileEntity reqEntity = new FileEntity(img, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);
            //StringEntity reqEntity = new StringEntity("{\"url\":\"" + "https://i.imgur.com/KLxE0tk.png" + "\"}");
            //request.setEntity(reqEntity);

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
                System.out.println("Error:\n");
                System.out.println(json.toString(2));
                return null;
            }

            // Store the URI of the second REST API method.
            // This URI is where you can get the results of the first REST API method.
            String operationLocation = null;

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
                System.out.println("\nError retrieving Operation-Location.\nExiting.");
                System.exit(1);
            }

            // If the first REST API method completes successfully, the second
            // REST API method retrieves the text written in the image.
            //
            // Note: The response may not be immediately available. Text
            // recognition is an asynchronous operation that can take a variable
            // amount of time depending on the length of the text.
            // You may need to wait or retry this operation.

            System.out.printf("Text submitted.\n" +
                    "Waiting %d milliseconds to retrieve the recognized text.\n", milliseconds);
            do {
                Thread.sleep(milliseconds);

                // Call the second REST API method and get the response.
                HttpGet resultRequest = new HttpGet(operationLocation);
                resultRequest.setHeader("Ocp-Apim-Subscription-Key", Controller.getKey());

                HttpResponse resultResponse = httpResultClient.execute(resultRequest);
                HttpEntity responseEntity = resultResponse.getEntity();

                if (responseEntity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(responseEntity);
                    JSONObject json = new JSONObject(jsonString);
                    //System.out.println("Text recognition result response: \n");
                    //System.out.println(json.toString(2));
                    String status = (String) json.get("status");
                    if (status == null) {
                        System.out.println("Malformed JSON returned, likely error..");
                        System.out.printf("Response: %s", json.toString(2));
                        return null;
                    }
                    switch (status) {
                        case "succeeded":
                            System.out.println("Success. Returning JSONObject.");
                            return json;
                        case "failed":
                            System.out.println("MsAzure Analysis Failed.");
                            return null;
                        case "running":
                            System.out.println("Still running");
                            break;
                        case "notStarted":
                            System.out.println("Not yet started. Waiting.");
                            break;
                        default:
                            System.out.println("Unknown status, likely error.");
                            System.out.printf("Status: %s", status);
                            return null;
                    }
                }
            } while(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Analyzes and returns JSONObject from Microsoft Azure from only printed
     * @return jsonObject if valid, null if invalid
     */
    public JSONObject analyzePrinted(int milliseconds){
        String uriBase = Controller.getEndpoint() + "vision/v3.0/ocr";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject json = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", Controller.getKey());

            // Request body.
            FileEntity reqEntity = new FileEntity(img, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);

            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                json = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                System.out.println(json.toString(2));
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
        return json;
    }


}
