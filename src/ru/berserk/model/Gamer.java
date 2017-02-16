package ru.berserk.model;

import static ru.berserk.model.Main.CLIENT_VERSION;
import static ru.berserk.model.Main.COIN_START;
import static ru.berserk.model.Main.randomNum;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.DatatypeConverter;

import ru.berserk.model.ServerEndpointDemo;

public class Gamer {
    Board board = new Board();
    String name;
    Deck simpleDeck = new Deck("defaultDeck");
    Player player = new Player(this, simpleDeck, "", "", 0);//For load deck, then set normal hero by new Player(Gamer _owner, Card _card, Deck _deck, String _playerName)
    GameQueue gameQueue = new GameQueue(this);
    int creatureWhoAttack;
    int creatureWhoAttackTarget;
    public final Object cretureDiedMonitor = new Object();
    public final Object monitor = new Object();
    public final Object yesNoChoiceMonitor = new Object();
    int sufflingConst;
    MyFunction.PlayerStatus status;
    MyFunction.PlayerStatus memPlayerStatus;
    Gamer opponent;
    //BufferedReader input;
    //PrintWriter output;
    String deckName;
    ArrayList<String> deckList = new ArrayList<>();
    boolean endMuligan = false;
    boolean ready = true;
    int choiceXcolor = 0;
    int choiceXtype = 0;
    String choiceXcreatureType = "";
    int choiceXcost = 0;
    int choiceXcostExactly = 0;
    int choiceYesNo=0;
    String choiceXname;

    ServerEndpointDemo server;

    Gamer(ServerEndpointDemo server) {
        this.server = server;
    }

    void printToView(int n, String txt) throws IOException {
        server.sendMessage("#Message(" + n + "," + txt + ")");
        System.out.println(txt);
    }

    void printToView(int n, Color c, String txt) throws IOException {//Depricated
        server.sendMessage("#Message(" + n + "," + txt + ")");
        System.out.println(txt);
    }

    void setPlayerGameStatus(MyFunction.PlayerStatus _status) throws IOException {
        status = _status;
        server.sendMessage("#PlayerStatus("+status.getValue() + ")");
    }

    void sendBoth(String message) throws IOException {
        System.out.println("Send both:" + message);
        server.sendMessage(message);
        opponent.server.sendMessage(message);
    }

    boolean isFirstPlayer(String name1, String name2) {
        byte[] b = (name1 + randomNum).getBytes();
        byte[] b2 = (name2 + randomNum).getBytes();
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
            String a = DatatypeConverter.printHexBinary(hash);
            byte[] hash2 = MessageDigest.getInstance("MD5").digest(b2);
            String a2 = DatatypeConverter.printHexBinary(hash2);
            return a.compareTo(a2) >= 0;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<String> getDeckList(String[] commands) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String card;
        int i = 1;
        while (!(card = commands[i]).equals("$ENDDECK")) {
            result.add(card);
            // System.out.println("Card = "+card);
            player.deck.cards.add(new Card(Card.getCardByName(card)));
            i++;
        }
        return result;
    }

    public void removePlayer() {
        System.out.println("Player " + name + " disconnected before.");
        Main.removeFreePlayer(this);
        Main.removeName(this.name);
    }

    private void addNewPlayer(String[] commands, ArrayList<String> parameter) throws IOException {

        name = parameter.get(0);
        System.out.println(name + " connected.");

        boolean nameCorrect = false;
        //TODO synchronized
        if (!Main.containsName(name)) {
            Main.addName(name);
            nameCorrect = true;
        } else {
            System.out.println("Name already exist.");
            //Other name?
        }
        deckName = parameter.get(1);
        deckList = getDeckList(commands);
        player = new Player(this, player.deck.cards.get(0), player.deck, name);

        player.creatures = new ArrayList<>(2);


        server.sendMessage("Hello, " + name + ", you going to play " + deckName + " deck.");
        server.sendMessage("Waiting for opponent to connect");
        //Const for shuffle
        server.sendMessage("$YOUAREOK(" + Main.randomNum + ")");
        if (!nameCorrect) return;
        //Main.writers.add(output);
        Main.addFreePlayer(this);

        boolean pairFounded = Main.findFreePlayerFor(this);

        if (pairFounded) {
            server.sendMessage("wait");
            server.sendMessage("ok");

            //Get shuffled deck and send to opponent
            opponent.server.sendMessage("Your opponent " + name + ", play " + deckList.get(0) + " hero.");
            opponent.server.sendMessage("$OPPONENTCONNECTED(" + name + "," + deckList.get(0) +","+COIN_START+ ")");
            startGame();
            opponent.startGame();
        }

    }

    private void startGame() throws IOException {
        //Begin game
        player.deck.cards.remove(0);//Remove hero from deck
        Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);//reroll for next
        player.deck.suffleDeck(Main.randomNum);
        Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);//reroll for next
        player.drawCard();
        player.drawCard();
        player.drawCard();
        player.drawCard();

        player.untappedCoin = Main.COIN_START;
        player.totalCoin = Main.COIN_START;
        status = MyFunction.PlayerStatus.MuliganPhase;
        sendStatus();
    }

    public void run(String command) throws IOException {
        try {
            String[] commands = command.split("\n");
            if (commands[0].contains("$IAM")) {
                ArrayList<String> parameter = getTextBetween(commands[0]);
                String ver = parameter.get(2);
                if (ver.equals(CLIENT_VERSION)) {
                    this.addNewPlayer(commands, parameter);
                } else {
                    server.sendMessage("Your client version is depricated! Update it.");
                    server.sendMessage("$YOUARENOTOK(" + "Your client version is depricated! Update it." + ")");
                    //Do something with it!
                }
                return;
            }

            // Repeatedly get commands from the client and process them.
            System.out.println(name + ":" + command);
            if (command.contains("$DISCONNECT")) {
                System.out.println(name + " normal disconnected.");
                opponent.server.sendMessage("$DISCONNECT");
                // This client is going down!  Remove it
                Main.removeFreePlayer(this);
                Main.removeName(name);
                this.server.disconnect();
                return;
            } else if (command.contains("$MULLIGANEND")) {
                endMuligan = true;
                ArrayList<String> parameter = MyFunction.getTextBetween(command);
                int nc = Integer.parseInt(parameter.get(1));
                status = MyFunction.PlayerStatus.waitingMulligan;
                for (int i = 0; i < nc; i++) {
                    player.deck.putOnBottomDeck(parameter.get(i + 2));
                    int a = MyFunction.searchCardInHandByName(player.cardInHand, parameter.get(i + 2));
                    player.removeCardFromHand(a);
                }
                for (int i = 0; i < nc; i++) player.drawCard();
                sendStatus();
                if (opponent.endMuligan) {
                    //START
                    System.out.println("Game for " + name + " and " + opponent.name + " started.");
                    //Choice, who first. Today at random
                    if (isFirstPlayer(name, opponent.name)) {
                        status = MyFunction.PlayerStatus.MyTurn;
                        opponent.status = MyFunction.PlayerStatus.EnemyTurn;
                        player.setNumberPlayer(0);
                        opponent.player.setNumberPlayer(1);
                        player.newTurn();
                    } else {
                        status = MyFunction.PlayerStatus.EnemyTurn;
                        opponent.status = MyFunction.PlayerStatus.MyTurn;
                        player.setNumberPlayer(1);
                        opponent.player.setNumberPlayer(0);
                        opponent.player.newTurn();
                    }
                    sendStatus();
                    opponent.sendStatus();
                    Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);//reroll for next
                }
            } else {
                ResponseClientMessage responseClientMessage = new ResponseClientMessage(this, command);
                responseClientMessage.start();
            }

        } catch (IOException e) {
            System.out.println("Player disconnected: " + name);
            //Reconnect?
        } finally {
//            System.out.println("Finaly " + name);
//            Main.freePlayer.remove(this);
//            if (opponent != null) opponent.server.sendMessage("$DISCONNECT");
//            // This client is going down!  Remove it.
//            if (name != null) {
//                Main.names.remove(name);
//            }
//            if (output != null) {
//                Main.writers.remove(output);
//            }
//            try {
//                socket.close();
//            } catch (IOException e) {
//            }
        }

    }

    void sendChoiceSearch(boolean dig,String message) throws IOException{
        //#ChoiceSearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message).
        System.out.println("Sending choice search to " + player.playerName);
        String s;
        if (!dig) s = "#ChoiceSearchInDeck(";
        else s = "#ChoiceSearchInGraveyard(";
        s+=player.playerName+",";
        s+=choiceXtype+",";
        s+=choiceXcolor+",";
        s+=choiceXcreatureType+",";
        s+=choiceXcost+",";
        s+=choiceXcostExactly+",";
        s+=message+")";
        server.sendMessage(s);
    }

    void sendChoiceTarget(String message) throws IOException{
        System.out.println("Sending choice target to " + player.playerName + ", whatAbility= " + MyFunction.ActivatedAbility.whatAbility.getValue());
        String s = "#ChoiceTarget(";
        s+=player.playerName+",";
        s+= status.getValue() + ",";
        s+=player.creatures.indexOf(MyFunction.ActivatedAbility.creature)+",";
        s+=MyFunction.ActivatedAbility.whatAbility.getValue()+",";
        s+=message+")";
        server.sendMessage(s);
    }

    void sendChoiceYesNo(String message,String card, String yes, String no){
        //#ChoiceSearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message).
        System.out.println("Sending choice Yes/no to " + player.playerName);
        String s;
        s = "#ChoiceYesNo(";
        s+=player.playerName+",";
        s+=card+",";
        s+=message+",";
        s+=yes+",";
        s+=no+")";
        try {
            server.sendMessage(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendChoiceForSpell(int targetType, int cost, String message){
        //#ChoiceForSpell(PlayerName,Status,TargetType,costN-)
        System.out.println("Sending choice for spell to " + player.playerName);
        String s = "#ChoiceForSpell(";
        s+=player.playerName+",";
        s+=status.getValue() + ",";
        s+=targetType + ",";
        s+=cost+",";
        s+=message+")";
        try {
            server.sendMessage(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendUntapAll() throws IOException{
        server.sendMessage("#UntapAll("+this.name+")");
        opponent.server.sendMessage("#UntapAll("+this.name+")");
    }

    void sendStatus() throws IOException {
        System.out.println("Sending status to " + player.playerName + ", status= " + status.getValue());
        String s = "#TotalStatusPlayer(";
        s += player.playerName+",";
        s += status.getValue() + ",";
        s += player.damage + ",";
        s += player.untappedCoin + ",";
        s += player.totalCoin + ",";
        s += player.temporaryCoin+",";
        s += player.owner.opponent.player.untappedCoin + ",";
        s += player.owner.opponent.player.totalCoin + ",";
        s += player.owner.opponent.player.temporaryCoin+",";
        s += player.deck.getCardExpiried() + ",";
        s += player.owner.opponent.player.cardInHand.size()+",";
        s += player.cardInHand.size() + ",";
        for (int i = 0; i < player.cardInHand.size(); i++) {
            s += player.cardInHand.get(i).name + ",";
        }
        s += ")";
        server.sendMessage(s);
    }

//    void removeBothClient() {
//        if (name != null) {
//            Main.names.remove(name);
//        }
//        if (output != null) {
//            Main.writers.remove(output);
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//        }
//        if (opponent.name != null) {
//            Main.names.remove(opponent.name);
//        }
//        if (opponent.output != null) {
//            Main.writers.remove(opponent.output);
//        }
//        try {
//            opponent.socket.close();
//        } catch (IOException e) {
//        }
//    }

    public static ArrayList<String> getTextBetween(String fromText) {
        ArrayList<String> rtrn = new ArrayList<>();
        String beforeText = "(";
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.length() - 1);
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
    }
}