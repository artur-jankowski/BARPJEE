package pl.edu.client2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;
import java.util.Vector;

public class HelloApplication extends Application {
    public static XmlRpcClient client = null;
    private static Object[] messages;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        HelloController controller = (HelloController) fxmlLoader.getController();
        controller.initRefreshTimeLabel();
        stage.setTitle("Chat!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main (String [] args) throws MalformedURLException {
        initClient();
        launch(args);
    }


    public static Object[] sendMessage(String user, String message) throws IOException, XmlRpcException {
        Vector params = new Vector();
        params.addElement(user);
        params.addElement(message);
        Object result = client.execute("messenger.sendMessage", params);
        messages = (Object[]) result;
        return messages;
    }

    public static Object[]  refresh() throws IOException, XmlRpcException {
        Object result = client.execute("messenger.refresh", new Vector<>());
        messages = (Object[]) result;
        return messages;
    }

    public static void initClient() throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(
                "http://localhost:8081/RPC2"));
        client = new XmlRpcClient();
        client.setConfig(config);
    }
}