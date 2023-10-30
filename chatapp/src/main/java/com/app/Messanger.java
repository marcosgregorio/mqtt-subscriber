package com.app;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

final public class Messanger {

    private ArrayList<String> signedTopics = new ArrayList<>();;
    private String broker = "tcp://localhost:1883";
    private MqttConnectOptions options;
    private String userId;
    private String controllId;
    private MqttAsyncClient myClient;
    private IMqttToken token;

    public Messanger() throws MqttException {
        this.options = new MqttConnectOptions();
        this.userId = UUID.randomUUID().toString();
        this.controllId = this.userId + "_Controll";
        this.myClient = new MqttAsyncClient(this.broker, this.userId);
        MyCallback myCallback = new MyCallback();
        this.myClient.setCallback(myCallback);

        this.token = myClient.connect();
        token.waitForCompletion();
        this.myClient.subscribe(this.controllId, 1);
        myCallback.setClient(this);
        System.out.println("Este é seu ID: \n" + userId);
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }

    public void setOptions(MqttConnectOptions options) {
        this.options = options;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MqttAsyncClient getMyClient() {
        return myClient;
    }

    public void setMyClient(MqttAsyncClient myClient) {
        this.myClient = myClient;
    }

    public IMqttToken getToken() {
        return token;
    }

    public void setToken(IMqttToken token) {
        this.token = token;
    }

    public void sendMessage(String topic, String message) throws MqttPersistenceException, MqttException {
        MqttMessage msg = new MqttMessage(message.getBytes());
        IMqttToken token = this.getMyClient().publish(topic, msg);
    }

    public void askPermissionToChat(Scanner scan) throws MqttPersistenceException, MqttException {
        System.out.println("Qual o ID do usuario que desejava conversar?");
        String id = scan.next();
        System.out.println("passei daqui");
        String topic = id + "_Controll";
        String message = "O usuario com o ID " + this.userId
                + " deseja se conectar com você em um bate papo. Aprovar conexão?";
        this.sendMessage(topic, message);
    }

    public void subscribeToSpecifiedTopic(Scanner scan) throws MqttException {
        System.out.println("Digite o tópico que deseja se inscrever");
        String newTopic = scan.nextLine();
        System.out.println("Digite a qualidade do sinal que deseja ter.");
        int qualityOfSignal = scan.nextInt();
        this.subscribeToTopic(newTopic, qualityOfSignal);
    }

    public void subscribeToTopic(String topic, int qualityOfSignal) throws MqttException {
        this.getMyClient().subscribe(topic, qualityOfSignal);
        this.signedTopics.add(topic);
    }

    public void sendMessageToSpecifiedTopic(Scanner scan) throws MqttPersistenceException, MqttException {
        System.out.println("Qual tópico deseja enviar a mensagem?\n" +
                "Tópicos assinados:");
        this.printSignedTopics();
        int index = scan.nextInt();
        scan.nextLine();

        String selectedTopic = this.signedTopics.get(index);
        
        System.out.println("Digite sua mensagem: ");
        String message = scan.nextLine();
        scan.nextLine();
        System.out.println("Enviando...");

        this.sendMessage(selectedTopic, message);
    }

    public void printSignedTopics() {
        int i = 0;

        for (String topic : this.signedTopics) {
            System.out.println("[" + i + "]" + " " + topic);
            i++;
        }
    }
}
