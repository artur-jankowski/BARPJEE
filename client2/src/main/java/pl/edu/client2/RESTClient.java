package pl.edu.client2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RESTClient {
    public static Object[] sendMessage(String user, String message) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("message", message);
        URL url = new URL("http://localhost:8000/rest/send-message?user=" + URLEncoder.encode(user, StandardCharsets.UTF_8).replace(" ", "%20") + "&message=" + URLEncoder.encode(message,StandardCharsets.UTF_8).replace(" ", "%20"));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("\n");
        }
        in.close();
        con.disconnect();
        return mapContent(content);
    }

    public static Object[] refresh() throws IOException {
        URL url = new URL("http://localhost:8000/rest/refresh");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("\n");
        }
        in.close();
        con.disconnect();
        return mapContent(content);
    }

    private static Object[] mapContent(StringBuffer content) {
        return Arrays.asList(content.toString().split("\n")).stream().collect(Collectors.toList()).toArray();
    }

    private static String getParamsString(Map<String, String> parameters) {
        StringBuilder result = new StringBuilder();
        parameters.entrySet().forEach(parameter -> {
            result.append(URLEncoder.encode(parameter.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(parameter.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        });
        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
