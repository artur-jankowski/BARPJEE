import com.sun.net.httpserver.HttpServer;
import org.apache.xmlrpc.WebServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

public class MessengerServer {
    static Vector<String> messages = new Vector<>();

    public Vector<String> sendMessage(String user, String message) {
        messages.add(
          user + ": " + message + "/%/XML-RPC"
        );
        return messages;
    }

    public Vector<String>  refresh() {
        return messages;
    }

    public static void main (String [] args) throws IOException {
        startRestServer();
        try {

            System.out.println("Attempting to start XML-RPC Server...");

            WebServer server = new WebServer(8081);
            server.addHandler("messenger", new MessengerServer());
            server.start();

            System.out.println("Started successfully.");
            System.out.println("Accepting requests. (Halt program to stop.)");

        } catch (Exception exception){
            System.err.println("JavaServer: " + exception);
        }
    }

    private static void startRestServer() throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/rest/send-message", (exchange -> {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            messages.add(params.get("user").get(0) + ": " + params.get("message").get(0)+" /%/REST");
            StringBuilder respText = new StringBuilder();
            messages.forEach(message -> respText.append(message).append("\n"));
            exchange.sendResponseHeaders(200, respText.toString().getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.toString().getBytes());
            output.flush();
            exchange.close();
        }));
        server.createContext("/rest/refresh", (exchange -> {
            StringBuilder respText = new StringBuilder();
            messages.forEach(message -> respText.append(message).append("\n"));
            exchange.sendResponseHeaders(200, respText.toString().getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.toString().getBytes());
            output.flush();
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }
    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }
}
