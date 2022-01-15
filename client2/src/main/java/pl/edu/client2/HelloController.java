package pl.edu.client2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class HelloController {
    @FXML
    private Label refreshLabel;

    @FXML
    private CheckBox isRest;

    @FXML
    private TextField userInput;

    @FXML
    private TextField sendInput;

    @FXML
    private VBox chatVBox;

    @FXML
    private VBox methodVBox;

    @FXML
    private Slider refreshBar;

    @FXML
    private ScrollPane scroll;

    @FXML
    protected void onSendButtonClick() throws IOException, XmlRpcException, InterruptedException {
        String user = userInput.getText();
        String message = sendInput.getText();
        if(isRest.isSelected()) {
            prepareMessages(RESTClient.sendMessage(user, message));
        } else {
            prepareMessages(HelloApplication.sendMessage(user, message));
        }
        scroll.setVvalue(scroll.getVmax());
    }


    @FXML
    protected void onRefreshButtonClick() throws IOException, XmlRpcException, InterruptedException {
        if(isRest.isSelected()) {
            prepareMessages(RESTClient.refresh());
        } else {
            prepareMessages(HelloApplication.refresh());
        }
        scroll.setVvalue(scroll.getVmax());
    }

    public void initRefreshTimeLabel() {
        refreshBar.setValue(5);

        refreshLabel.textProperty().setValue(
                "Refresh rate: " + 5);
        AtomicReference<Timeline> timeline = new AtomicReference<>(new Timeline(
                new KeyFrame(Duration.seconds(5),
                        event -> {
                            System.out.println("SCHEDULED XD");
                            try {
                                onRefreshButtonClick();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (XmlRpcException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        })));
        timeline.get().setCycleCount(Timeline.INDEFINITE);
        timeline.get().play();
        refreshBar.valueProperty().addListener((observableValue, oldValue, newValue) ->
        {
            refreshLabel.textProperty().setValue(
                    "Refresh rate: " + newValue.intValue());
            timeline.get().stop();
            if(newValue.intValue() > 0) {
                timeline.set(new Timeline(
                        new KeyFrame(Duration.seconds(newValue.intValue()),
                                event -> {
                                    try {
                                        onRefreshButtonClick();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (XmlRpcException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                })));
                timeline.get().setCycleCount(Timeline.INDEFINITE);
                timeline.get().play();
            }
        });
    }

    private void prepareMessages(Object[] messages) {
        chatVBox.getChildren().clear();
        methodVBox.getChildren().clear();
        List<Object> messageList = Arrays.asList(messages);
        messageList.forEach(message -> {
            String[] content = ((String) message).split("/%/");
            Label label = new Label();
            Label method = new Label();
            label.setText(content[0]);
            chatVBox.getChildren().add(label);
            method.setText(content[1]);
            methodVBox.getChildren().add(method);
        });
        chatVBox.getChildren().add(new Label());
        chatVBox.getChildren().add(new Label());
    }
}