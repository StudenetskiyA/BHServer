package ru.berserk.model;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static final int COIN_START = 0;//TODO 0
    static final String CLIENT_VERSION = "0.02";
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

    public static boolean findFreePlayerFor(Gamer g) {
        synchronized (freePlayer) {
            for (int i = 0; i < Main.freePlayer.size(); i++) {
                if (!Main.freePlayer.get(i).name.equals(g.name) && Main.freePlayer.get(i).name != null) {
                    System.out.println("Pair found: " + g.name + "/" + Main.freePlayer.get(i).name);
                    g.opponent = Main.freePlayer.get(i);
                    g.opponent.opponent = g;
                    //opponent.gameQueue = gameQueue;
                    Main.freePlayer.remove(g);
                    Main.freePlayer.remove(g.opponent);
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
