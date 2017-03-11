package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
//    static final int COIN_START = 0;
//    static final String url = "jdbc:mysql://localhost:3306/jerry?useUnicode=yes&characterEncoding=utf8";
//    static final String user = "jerry";
//    static final String password = "QJaVlKVV";
    static final String url = "jdbc:mysql://localhost:3306/users?useUnicode=yes&characterEncoding=utf8";
    static final String user = "root";
    static final String password = "4lifewithBerserk";
    static final int COIN_START = 10;
    
    static final String CLIENT_VERSION = "0.02";
    static final String SERVER_VERSION = "0.030221";
    public static int randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);
    private static ArrayList<String> names = new ArrayList<>();
    private static ArrayList<Gamer> freePlayer = new ArrayList<>();

    public static Gamer start(ServerEndpointDemo server) {
        return new Gamer(server);
    }

    public static void removeFreePlayer(Gamer g) {
        synchronized (freePlayer) {
            freePlayer.remove(g);
        }
    }

    public static void addFreePlayer(Gamer g) {
        synchronized (freePlayer) {
            freePlayer.add(g);
        }
    }

    public static boolean findFreePlayerFor(Gamer g) throws IOException {
        synchronized (freePlayer) {
            for (int i = 0; i < Main.freePlayer.size(); i++) {
                if (!Main.freePlayer.get(i).name.equals(g.name) && Main.freePlayer.get(i).name != null) {
                    System.out.println("Pair found: " + g.name + "/" + Main.freePlayer.get(i).name);
                    g.opponent = Main.freePlayer.get(i);
                    g.opponent.opponent = g;
                    Main.freePlayer.remove(g);
                    Main.freePlayer.remove(g.opponent);
                    g.server.sendMessage("Your opponent " + g.opponent.name + ", play " + g.opponent.player.deck.cards.get(0).name + " hero.");
        			g.server.sendMessage("$OPPONENTCONNECTED(" + g.opponent.name + "," + g.opponent.player.deck.cards.get(0).name + "," + COIN_START + ")");
        			g.opponent.server.sendMessage("Your opponent " + g.name + ", play " + g.player.deck.cards.get(0).name + " hero.");
         			g.opponent.server.sendMessage("$OPPONENTCONNECTED(" + g.name + "," + g.player.deck.cards.get(0).name + "," + COIN_START + ")");
         			g.isGameStart= true;
         			g.opponent.isGameStart = true;
                    return true;
                }
            }
        }
        return false;
    }

    public static void addName(String name) {
        synchronized (names) {
            names.add(name);
        }
    }

    public static void removeName(String name) {
        synchronized (names) {
            names.remove(name);
        }
    }

    public static boolean containsName(String name) {
        synchronized (names) {
            return names.contains(name);
        }
    }
}
