package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;

import static ru.berserk.model.MyFunction.ActivatedAbility;
import static ru.berserk.model.MyFunction.ActivatedAbility.WhatAbility.nothing;
import static ru.berserk.model.MyFunction.PlayerStatus;


// Created by StudenetskiyA on 25.01.2017.

public class ResponseClientMessage extends Thread {
    Gamer gamer;
    Player player;
    String fromServer = "";

    ResponseClientMessage(Gamer _gamer, String _fromServer) {
        gamer = _gamer;
        player = gamer.player;
        fromServer = _fromServer;
    }

    public synchronized void run() {
        boolean dontDoQueue = false;
        boolean freeMonitor = false;
        gamer.ready = false;

        try {
        System.out.println(fromServer);
        if (fromServer.startsWith("$ENDTURN(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            System.out.println("End turn " + parameter.get(0));
            gamer.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyTurn);
            gamer.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.MyTurn);
            player.endTurn();
            gamer.opponent.sendUntapAll();
            gamer.opponent.player.newTurn();
        } else if (fromServer.startsWith("$DISCONNECT")) {
           // System.out.println(name + " normal disconnected.");
            gamer.opponent.server.sendMessage("$DISCONNECT");
            gamer.removePlayer();
            return;
        }  else if (fromServer.startsWith("$SURREND")) {
            //System.out.println(name + " surrend.");
        	gamer.opponent.server.sendMessage("#Surrend("+player.playerName+")");
            gamer.server.sendMessage("#Surrend("+player.playerName+")");
            gamer.opponent.removePlayer();
            gamer.removePlayer();
            return;
        } else if (fromServer.startsWith("$CHOISEBLOCKER(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            gamer.opponent.status = PlayerStatus.IChoiceBlocker;
            gamer.opponent.creatureWhoAttack = Integer.parseInt(parameter.get(1));
            gamer.opponent.creatureWhoAttackTarget = Integer.parseInt(parameter.get(2));
            dontDoQueue = true;
        } else if (fromServer.startsWith("$TAPNOTARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Board.getCreatureById(player, parameter.get(1)).tapNoTargetAbility();
        } else if (fromServer.startsWith("$DISCARD(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int n = Integer.parseInt(parameter.get(1));
            gamer.printToView(0, player.playerName + " сбрасывает " + player.cardInHand.get(n).name);
            player.addCardToGraveyard(player.cardInHand.get(n));
            player.cardInHand.remove(n);
            dontDoQueue = true;
            freeMonitor = true;
        } else if (fromServer.startsWith("$SPELLCHOICECREATURETARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            gamer.choiceCreature = Board.getCreatureById(player, parameter.get(0));
            dontDoQueue = true;
            synchronized (gamer.yesNoChoiceMonitor) {
                gamer.yesNoChoiceMonitor.notify();
            }
        } else if (fromServer.startsWith("$CHOICEYESNO(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            gamer.choiceYesNo = Integer.parseInt(parameter.get(0));
            dontDoQueue = true;
            synchronized (gamer.yesNoChoiceMonitor) {
                gamer.yesNoChoiceMonitor.notify();
            }
        } else if ((fromServer.startsWith("$CRYEQUIPTARGET("))) {//CreatureID,EquipID
        	 ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
             Creature cr = Board.getCreatureById(player, parameter.get(0));
             Equpiment eq = Board.getEqupimentByID(player, parameter.get(1));
             System.out.println("Eq id = "+eq.id);
             cr.battlecryEquipTarget(eq);
             dontDoQueue = true;
             freeMonitor = true;
             System.out.println("BET complite");
        } else if ((fromServer.startsWith("$CRYTARGET(")) || (fromServer.startsWith("$TAPTARGET("))) {
            // CRYTARGET also for DeathratleTarget and TapTarget
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Creature cr;
            boolean death = false;
            if (parameter.get(1).equals("-1")) {
                //died creature ability.
                death = true;
                cr = new Creature(MyFunction.ActivatedAbility.creature);
            } else {
                cr =  Board.getCreatureById(player, parameter.get(1));
            }
           if (parameter.get(3).equals("-1")) {
        	   Player tmpPlayer = (parameter.get(2).equals("1")) ? gamer.opponent.player : player;
                if (fromServer.contains("$CRYTARGET("))
                    if (death) cr.deathratle(null, tmpPlayer);
                    else cr.battlecryTarget(null, tmpPlayer);
                else cr.tapTargetAbility(null, tmpPlayer);
            } else {
            	Creature t = Board.getCreatureById(player, parameter.get(3));
                    if (fromServer.contains("$CRYTARGET("))
                        if (death) cr.deathratle(t, null);
                        else cr.battlecryTarget(t, null);
                    else cr.tapTargetAbility(t, null);
            }
            dontDoQueue = true;
            freeMonitor = true;
        } else if (fromServer.startsWith("$EQUIPTARGET(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            gamer.status = PlayerStatus.MyTurn;
            int equip = Integer.parseInt(parameter.get(1));//ID?
            Creature t = Board.getCreatureById(player, parameter.get(3));
            if (parameter.get(2).equals("1")) {
                if (parameter.get(3).equals("-1"))
                    player.equpiment[equip].tapTargetAbility(null, gamer.opponent.player);
                else
                    player.equpiment[equip].tapTargetAbility(t, null);
            } else {
                if (parameter.get(3).equals("-1"))
                    player.equpiment[equip].tapTargetAbility(null, player);
                else
                    player.equpiment[equip].tapTargetAbility(t, null);
            }
        } else if (fromServer.startsWith("$HEROTARGET(")) {//PlayerName,Nability,HalfBoard,CreatureId||-1,cost
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            player.untappedCoin -= Integer.parseInt(parameter.get(4));
            Player who = (parameter.get(2).equals("0"))? player:gamer.opponent.player;
            int n = Integer.parseInt(parameter.get(1));
                if (parameter.get(3).equals("-1")) player.ability(n,null, who);
                else {
                	Creature t = Board.getCreatureById(player, parameter.get(3));
                	player.ability(n,t, null);
                }
             dontDoQueue = true;
        } else if (fromServer.startsWith("$HERONOTARGET(")) {//PlayerName,Nability,cost
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            player.tap();
            player.untappedCoin -= Integer.parseInt(parameter.get(2));
            player.abilityNoTarget(Integer.parseInt(parameter.get(1)));
        } else if (fromServer.startsWith("$BLOCKER(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);

            Creature cr = gamer.opponent.player.creatures.get(Integer.parseInt(parameter.get(1)));//Who attack
            if (Integer.parseInt(parameter.get(2)) == -1) {
                if (Integer.parseInt(parameter.get(3)) == -1) {
                    //Fight with hero
                    cr.fightPlayer(player);
                } else {
                    Creature block = player.creatures.get(Integer.parseInt(parameter.get(3)));
                    //Fight with bocker
                    block.blockThisTurn = true;
                    cr.fightCreature(block);
                    if (Integer.parseInt(parameter.get(4)) == 1) {
                        if (!block.getDefenseSkill())
                            block.tapCreature();
                    }
                }
            } else {
                if (Integer.parseInt(parameter.get(3)) == -1) {
                    //Fight with first target
                    Creature block = player.creatures.get(Integer.parseInt(parameter.get(2)));
                    cr.fightCreature(block);
                } else {
                    Creature block = player.creatures.get(Integer.parseInt(parameter.get(3)));
                    //Fight with blocker
                    cr.fightCreature(block);
                    if (Integer.parseInt(parameter.get(4)) == 1) {
                        block.tapCreature();
                    }
                }
            }
            gamer.setPlayerGameStatus(PlayerStatus.EnemyTurn);
            gamer.opponent.setPlayerGameStatus(PlayerStatus.MyTurn);
            //dontDoQueue = true;
        } else if (fromServer.startsWith("$PLAYCARD(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            Card tmp = player.getCardByID(parameter.get(1));
            if (!parameter.get(3).equals("-1")) {//if card targets creature
                if ((parameter.get(4).equals(gamer.opponent.player.playerName)))
                    player.playCard(Integer.parseInt(parameter.get(2)), tmp, gamer.opponent.player.creatures.get(Integer.parseInt(parameter.get(3))), null);
                else //to self creature
                    player.playCard(Integer.parseInt(parameter.get(2)), tmp, gamer.player.creatures.get(Integer.parseInt(parameter.get(3))), null);
            } else {
                if (parameter.get(4).equals(gamer.opponent.player.playerName))//enemy
                    player.playCard(Integer.parseInt(parameter.get(2)), tmp, null, gamer.opponent.player);
                else if (parameter.get(4).equals(player.playerName))//target - self player
                    player.playCard(Integer.parseInt(parameter.get(2)), tmp, null, gamer.player);
                else {
                	player.playCard(Integer.parseInt(parameter.get(2)), tmp, null, null);
                }
                }
        } else if (fromServer.startsWith("$PLAYWITHX(")) {
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            int x = Integer.parseInt(parameter.get(5));
            Card tmp = player.getCardByID(parameter.get(1));
            Player apl = gamer.opponent.player;
            if (!parameter.get(3).equals("-1")) {//if card targets creature
                if ((parameter.get(4).equals(apl.playerName)))
                    player.playCardX(Integer.parseInt(parameter.get(2)), tmp, apl.creatures.get(Integer.parseInt(parameter.get(3))), null, x);
                else //to self creature
                    player.playCardX(Integer.parseInt(parameter.get(2)), tmp, player.creatures.get(Integer.parseInt(parameter.get(3))), null, x);
            } else {
                if (parameter.get(4).equals(apl.playerName))//enemy
                    player.playCardX(Integer.parseInt(parameter.get(2)), tmp, null, apl, x);
                else if (parameter.get(5).equals(player.playerName))//target - self player
                    player.playCardX(Integer.parseInt(parameter.get(2)), tmp, null, player, x);
                else
                    player.playCardX(Integer.parseInt(parameter.get(2)), tmp, null, null, x);
            }
        } else if (fromServer.startsWith("$ATTACKPLAYER(")) {//$ATTACKPLAYER(Player, Creature)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            //TODO Check exist and can attack. Client may lie!
             player.creatures.get(Integer.parseInt(parameter.get(1))).attackPlayer(gamer.opponent.player);
        } else if (fromServer.startsWith("$ATTACKCREATURE(")) {//$ATTACKREATURE(Player, Creature, TargetCreature)
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            player.creatures.get(Integer.parseInt(parameter.get(1))).attackCreature(gamer.opponent.player.creatures.get(Integer.parseInt(parameter.get(2))));
        } else if (fromServer.startsWith("$FOUND(")) {//$FOUND(Player, Card)
            gamer.choiceXcolor = 0;
            gamer.choiceXtype = 0;
            gamer.choiceXcost = 0;
            gamer.choiceXcostExactly = 0;
            gamer.choiceXcreatureType = "";
            gamer.setPlayerGameStatus(PlayerStatus.MyTurn);
            gamer.opponent.setPlayerGameStatus(PlayerStatus.EnemyTurn);
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (parameter.get(1).equals("-1")) {
                gamer.printToView(0, "Вы ищете в колоде, но ничего подходящего не находите.");
                gamer.opponent.printToView(0, "Противник ищет в колоде, но ничего подходящего не находит.");
            } else {
                Card card = player.deck.searchCard(parameter.get(1));
                //TODO Check exist card and may it be founded. Player may lie.
                player.drawSpecialCard(card);
                gamer.printToView(0, "Вы находите в колоде " + card.name + ".");
                gamer.opponent.printToView(0, "Противник находит в колоде " + parameter.get(1) + ".");
            }
            dontDoQueue = true;
            freeMonitor = true;
        } else if (fromServer.startsWith("$DIGFOUND(")) {
            gamer.choiceXcolor = 0;
            gamer.choiceXtype = 0;
            gamer.choiceXcost = 0;
            gamer.choiceXcostExactly = 0;
            gamer.choiceXcreatureType = "";
            gamer.setPlayerGameStatus(PlayerStatus.MyTurn);
            gamer.opponent.setPlayerGameStatus(PlayerStatus.EnemyTurn);
            ArrayList<String> parameter = MyFunction.getTextBetween(fromServer);
            if (parameter.get(1).equals("-1")) {
                gamer.printToView(0, "Вы ищете на кладбище, но ничего подходящего не находите.");
                gamer.opponent.printToView(0, "Противник ищет на кладбище, но ничего подходящего не находит.");
            } else {
                Card card = MyFunction.searchCardInList(player.graveyard,parameter.get(1));
                //TODO Check exist card and may it be founded. Player may lie.
                player.digSpecialCard(card);
                gamer.printToView(0, "Вы берете с кладбища " + card.name + ".");
                gamer.opponent.printToView(0, "Противник берет с кладбища " + parameter.get(1) + ".");
            }
            dontDoQueue = true;
            freeMonitor = true;
        } else if (fromServer.startsWith("$CHAT(")) {
            fromServer = fromServer.substring(fromServer.indexOf("(") + 1);
            fromServer = fromServer.substring(0, fromServer.lastIndexOf(")"));
            gamer.sendBoth("#Chat(" + player.playerName + ": " + fromServer + ")");
            dontDoQueue = true;
        }


        if (!dontDoQueue) {
            while(gamer.gameQueue.size()!=0 || gamer.opponent.gameQueue.size()!=0) {
                gamer.opponent.gameQueue.responseAllQueue();
                gamer.gameQueue.responseAllQueue();
            }
        }

        gamer.sendStatus();
        gamer.opponent.sendStatus();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (freeMonitor) freeMonitor();

        synchronized (gamer.monitor) {
            gamer.ready = true;
            gamer.monitor.notifyAll();
        }
    }

    private void freeMonitor() {
        synchronized (gamer.cretureDiedMonitor) {
            ActivatedAbility.whatAbility = nothing;
            gamer.cretureDiedMonitor.notify();
        }
    }
}
