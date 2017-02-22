package ru.berserk.model;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/serverendpointdemo")
public class ServerEndpointDemo {

    Session session;
    Gamer gamer;

    @OnOpen
    public void handleOpen(Session session) {
        this.session = session;
        System.out.println("client is now connected");
        gamer = Main.start(this);
    }

    @OnMessage
    public String handleMessage(String message) {
        System.out.println("receive from client: " + message);
        //String replyMessage = "echo " + message;
        String replyMessage="";
        System.out.println("send to client: " + replyMessage);
        try {
            gamer.run(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return replyMessage;
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public void disconnect() throws IOException {
        this.session.close();
    }

    @OnClose
    public void handleClose() throws IOException {
        System.out.println("clien is now disconnected");
        this.gamer.removePlayer();
    }

    @OnError
    public void handleError(Throwable t) {
        System.out.println("error: " + t.getMessage());
        t.printStackTrace();
    }


}
