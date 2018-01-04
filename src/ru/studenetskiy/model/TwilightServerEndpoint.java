package ru.studenetskiy.model;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/serverendpoint")
public class TwilightServerEndpoint {

    Session session;

    @OnOpen
    public void handleOpen(Session session) throws IOException {
        this.session = session;
        System.out.println("NEW client is now connected");
        sendMessage("Server online.");
    }

    @OnMessage
    public void handleMessage(String message) throws IOException {
        System.out.println("receive from client: " + message);
        ClientMessageHandler cmh = new ClientMessageHandler(this,message);
        cmh.proceed();
    }

    public void sendMessage(String message) throws IOException {
    	System.out.println("send to client: " + message);
        this.session.getBasicRemote().sendText(message);
    }

    public void disconnect() throws IOException {
        this.session.close();
    }

    @OnClose
    public void handleClose() throws IOException {
        System.out.println("clien is now disconnected");
    }

    @OnError
    public void handleError(Throwable t) {
        System.out.println("error: " + t.getMessage());
       // t.printStackTrace();
    }


}
