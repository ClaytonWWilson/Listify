import netscape.javascript.JSObject;

import java.io.*;
import java.net.ProtocolException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class KroggerAPITester {

    final static String client_id = "listify-f6e083b133a87ab8a98b2ec4f580dedb9125180887245441161";
    final static String client_secret = "hM88WJ3cJGou5jX1vNRZBqKKVmmcMMktTcTbvkRD";
    final static String redirect_uri = "https://example.com/callback"; //subject to change as needed
    final static String scope = "product.compact";

    final static String authString = "listify-f6e083b133a87ab8a98b2ec4f580dedb9125180887245441161:hM88WJ3cJGou5jX1vNRZBqKKVmmcMMktTcTbvkRD";

    public static void main(String[] args) {
        String s = getKroggerAuthKey();
        System.out.println(s);
        getProduct(s);
    }

    public static String getKroggerAuthKey() {
        String token = "not found";
        try {
            StringBuilder sb = new StringBuilder("https://api.kroger.com/v1/connect/oauth2/token");
            String urlParameters = "grant_type=client_credentials&scope=product.compact";
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL url = new URL(sb.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authString.getBytes()));
            connection.setRequestProperty("charset", "UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString( postDataLength ));
            try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {   //write body into POST request
                wr.write( postData );
            }

            int responseStatus = connection.getResponseCode();
            if(responseStatus == 200) {
                System.out.println("Success");
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    String jsonString = response.toString();
                    jsonString = jsonString.substring(1, jsonString.length() - 1);
                    HashMap<String, String> map = new HashMap<>();
                    String[] responses = jsonString.split(",");
                    for(int i = 0; i < responses.length; i++) {
                        String[] keyValue = responses[i].split(":");
                        map.put(keyValue[0], keyValue[1]);
                    }

                    if(map.containsKey("\"access_token\"")) {
                        token = map.get("\"access_token\"");
                        token = token.substring(1, token.length() - 1); //removes quotes at start and end
                    }

                }
            } else {
                System.out.println(responseStatus);
            }
            connection.disconnect();
            System.out.println(responseStatus);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

}
