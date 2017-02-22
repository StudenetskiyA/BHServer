package ru.berserk.model;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import static ru.berserk.model.MyFunction.ActivatedAbility;
import static ru.berserk.model.MyFunction.ActivatedAbility.WhatAbility.*;

// Created by StudenetskiyA on 30.12.2016.

public class Player extends Card {
    ArrayList<Card> cardInHand = new ArrayList<>();//Don't do cardInHand.add or remove! Use addToGra...
    ArrayList<Card> graveyard = new ArrayList<>();//Same for graveyard
    ArrayList<Creature> creatures = new ArrayList<>();//And same for creatures

    Gamer owner;
    int numberPlayer;
    int damage;
    int cantDrawCardAtBeginTurn=0;
    String playerName;
    int totalCoin;
    int untappedCoin;
    int temporaryCoin = 0;
    boolean isTapped = false;
    public Deck deck;

    Equpiment equpiment[];//0-armor,1-amulet,2-weapon,3-event
    public ArrayList<Creature> crDied;//Temporary list for queue
    public ArrayList<Creature> crCryed;
    public ArrayList<Creature> crUpkeeped;
    //
    public Effects effect = new Effects(this);

    private static int tempX;//For card with X, for correct minus cost

    public class Effects {
        Player whis;
        String additionalText = "";
        private boolean bbShield = false;
        int bonusToShoot = 0;

        Effects(Player _pl) {
            whis = _pl;
        }

        boolean getBBShield() {
            return bbShield;
        }

        int getBonusToShoot(){
        	return bonusToShoot;
        }
        
        //#TakeCreatureEffect(Player, CreatureNumOnBoard,Effect,EffectCount)
        void takeBBShield(boolean take) throws IOException {
            bbShield = take;
            int t = (take) ? 1 : 0;
            owner.sendBoth("#TakePlayerEffect(" + playerName + "," + MyFunction.EffectPlayer.bbShield.getValue() + "," + t + ")");
        }
        
        void takeBonusToShoot(boolean take,int n) throws IOException {
            bonusToShoot = (take) ? n:0 ;
            int t = (take) ? 1 : 0;
            owner.sendBoth("#TakePlayerEffect(" + playerName + "," + MyFunction.EffectPlayer.bonusToShoot.getValue() + "," + t + ")");
        }
        
    }

    void addCreatureToList(Creature c) throws IOException {
        creatures.add(c);
        owner.sendBoth("#PutCreatureToBoard(" + playerName + "," + c.name + "," + c.id + ")");
        owner.sendStatus();
        owner.opponent.sendStatus();
    }

    void removeCreatureFromList(Creature c) throws IOException {
        owner.sendBoth("#DieCreature(" + playerName + "," + c.id + ")");
        creatures.remove(c);
    }

    void addCardToHand(Card c) throws IOException {
        cardInHand.add(c);
        owner.server.sendMessage("#AddCardToHand(" + playerName + "," + c.name + ","+c.id+")");
    }

    void addCardToGraveyard(Card c) throws IOException {
        graveyard.add(c);
        owner.sendBoth("#AddCardToGraveyard(" + playerName + "," + c.name + "," + c.id + ")");
    }

    void removeCardFromGraveyard(Card c) throws IOException {
        graveyard.remove(c);
        owner.sendBoth("#RemoveCardFromGraveyard(" + playerName + "," + c.id + ")");
    }

    void removeCardFromHand(Card c) throws IOException {
        cardInHand.remove(c);
        owner.sendBoth("#RemoveCardFromHandById(" + playerName + "," + c.id + ")");
    }

    void removeCardFromHand(int n) throws IOException {
    	owner.sendBoth("#RemoveCardFromHandById(" + playerName + "," + cardInHand.get(n).id + ")");
        cardInHand.remove(n);
    }

    int getNumberOfCreature(Creature _cr) {
    	for (int i=0;i<creatures.size();i++){
    		if (creatures.get(i).id.equals(_cr.id)) return i;
    	}
        return -1;
    }

    int getNumberOfAlivedCreatures() {
        int a = 0;
        for (Creature c : creatures) {
            if (!c.isDie()) a++;
        }
        return a;
    }

    void setNumberPlayer(int _n) {
        numberPlayer = _n;
    }
    
    Player(Gamer _owner, Card _card, String _playerName) {
        super(0, _card.name, _card.creatureType, 1, 0, _card.targetType, _card.tapTargetType, _card.text, 0, _card.hp);
        owner = _owner;
        isTapped = false;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        equpiment = new Equpiment[4];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
        equpiment[3] = null;
    }

    Player(Gamer _owner, String _heroName, String _playerName, int _hp) {
        super(0, _heroName, "", 1, 0, 0, 0, "", 0, _hp);
        owner = _owner;
        isTapped = false;
        playerName = _playerName;
        cardInHand = new ArrayList<>();
        graveyard = new ArrayList<>();
        //  numberPlayer = _n;
        equpiment = new Equpiment[4];
        equpiment[0] = null;
        equpiment[1] = null;
        equpiment[2] = null;
        equpiment[3] = null;
    }

    void endTurn() throws IOException {
        totalCoin -= temporaryCoin;

        //BBShield don't disappear at end of turn.
        //if (owner.opponent.player.effect.getBBShield()) owner.opponent.player.effect.takeBBShield(false);

        if (untappedCoin > totalCoin) untappedCoin = totalCoin;
        temporaryCoin = 0;
        //Creature effects until eot
        if (!creatures.isEmpty()) {
            for (int i = creatures.size() - 1; i >= 0; i--)
                creatures.get(i).effects.EOT();
        }
        if (!owner.opponent.player.creatures.isEmpty()) {
            for (int i = owner.opponent.player.creatures.size() - 1; i >= 0; i--)
                owner.opponent.player.creatures.get(i).effects.EOT();
        }
        owner.sendStatus();
    }

    ArrayList<Creature> diedCreatureOnBoard() {
        ArrayList<Creature> r = new ArrayList<>();
        for (Creature c : creatures) {
            if (c.isDie()) {
                r.add(c);
            }
        }
        return r;
    }

    ArrayList<Creature> searchWhenOtherDieAbility(Creature cr) {
    	ArrayList<Creature> tmp = new ArrayList<>();
        for (Creature p : creatures) {
            if (p.text.contains("При гибели другого вашего существа:") && p != cr && !p.isDie())
                tmp.add(p);
            if (p.text.contains("При гибели в ваш ход другого вашего существа:") && p.owner.playerName.equals(owner.board.whichTurn) && p != cr && !p.isDie())
                tmp.add(p);
        }
        return tmp;
    }

    void massDieCheckNeededTarget() throws IOException {//if someone wants to choice target at death(self or other) - pause game
        crDied = new ArrayList<>(diedCreatureOnBoard());//died creature
        ListIterator<Creature> temp = crDied.listIterator();
        System.out.println("massDie, pl=" + playerName + ", found died " + crDied.size());
        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at death
            ArrayList<Creature> crArray = searchWhenOtherDieAbility(tmp);//creature, who wants to other die(ex. Падальщик Пустоши)
            ListIterator<Creature> crList = crArray.listIterator();
            while (crList.hasNext()) {
                Creature cr = crList.next();
            if (cr != null && crDied.size() > 0 && !cr.activatedAbilityPlayed) {
                System.out.println("Падальщик, Orc-revenger for " + playerName);
                if (cr.targetType == 0) {
                    //Today only Ork. When you add more with target=0, correct here
                	//TODO call ability
                    owner.choiceXtype = 0;
                    owner.choiceXcolor = 0;
                    owner.choiceXcreatureType = "";
                    owner.choiceXcost = 0;
                    owner.choiceXcostExactly = 0;
                    owner.choiceXname = "Орк-мститель";
                    cr.activatedAbilityPlayed = true;//if you remove it, may play any times at turn.
                    owner.setPlayerGameStatus(MyFunction.PlayerStatus.searchX);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                    owner.sendChoiceSearch(false, "Орк-мститель ищет в колоде.");
                    System.out.println("pause");
                    synchronized (owner.cretureDiedMonitor) {
                        try {
                            owner.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                } else {
                    //CHECK EXIST TARGET
                    if (MyFunction.canTargetComplex(this, cr)) {
                        owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                        owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                        ActivatedAbility.creature = cr;
                        ActivatedAbility.whatAbility = onOtherDeathPlayed;
                        //pause until player choice target.
                        owner.sendChoiceTarget(cr.name + " просит выбрать цель.");
                        System.out.println("pause");
                        ActivatedAbility.creature.activatedAbilityPlayed = true;//if you remove it, may play any times at turn.
                        synchronized (owner.cretureDiedMonitor) {
                            try {
                                owner.cretureDiedMonitor.wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        System.out.println("resume");
                    } else {
                        owner.printToView(0, "Целей для " + cr.name + " нет.");
                        cr.activatedAbilityPlayed = true;//If you can't target, after you can't play this ability
                    }
                }
            }
            }
            if (tmp.text.contains("Гибель:")) {
                tmp.deathratleNoTarget(tmp, tmp.owner);
                tmp.effects.deathPlayed = true;
            }
            if (tmp.text.contains("Гибельт:") && !tmp.effects.deathPlayed) {
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(this, tmp)) {
                    owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                    ActivatedAbility.creature = tmp;
                    ActivatedAbility.whatAbility = onDeathPlayed;
                    //pause until player choice target.
                    owner.sendChoiceTarget(tmp.name + " просит выбрать цель.");
                    System.out.println("pause");
                    synchronized (owner.cretureDiedMonitor) {
                        try {
                            owner.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    tmp.effects.deathPlayed = true;
                } else if (tmp.targetType == 99) {//Discard
                    //Check n card
                    int n = cardInHand.size();
                    if (n > 1) {
                        owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                        owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                        ActivatedAbility.creature = new Creature(tmp);
                        ActivatedAbility.whatAbility = ActivatedAbility.WhatAbility.toHandAbility;
                        //pause until player choice target.
                        owner.sendChoiceTarget(tmp.name + " просит cбросить карту.");
                        System.out.println("pause");
                        synchronized (owner.cretureDiedMonitor) {
                            try {
                                owner.cretureDiedMonitor.wait();
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                        System.out.println("resume");
                    }
                    if (n == 1) {
                        owner.printToView(0, tmp.name + " заставляет сбросить " + cardInHand.get(0));
                        this.addCardToGraveyard(cardInHand.get(0));
                        removeCardFromHand(cardInHand.get(0));
                    }
                    if (n == 0) {
                        owner.printToView(0, tmp.name + " заставляет сбросить карту, но ее нет.");
                    }
                } else {
                    owner.printToView(0, "Целей для " + tmp.name + " нет.");
                    tmp.effects.deathPlayed = true;//If you can't target, after you can't play this ability
                }
            }
        }
    }

    void massUpkeepCheckNeededTarget() throws IOException {//if someone wants to choice target at death(self or other) - pause game
        crUpkeeped = new ArrayList<>(creatures);//died creature
        ListIterator<Creature> temp = crUpkeeped.listIterator();
        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at upkeep
            if (tmp.text.contains("В начале хода") || tmp.text.contains("В начале вашего хода") && tmp.getTougness() > tmp.damage)
                if (creatures.size() > 1 && !tmp.effects.upkeepPlayed) {
                    System.out.println("Амбрадоринг " + playerName);
                    //CHECK EXIST TARGET
                    owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);

                    ActivatedAbility.creature = tmp;
                    ActivatedAbility.whatAbility = onUpkeepPlayed;
                    //pause until player choice target.
                    owner.sendChoiceTarget(tmp.name + " просит выбрать цель.");
                    System.out.println("pause");
                    tmp.effects.upkeepPlayed = true;
                    synchronized (owner.cretureDiedMonitor) {
                        try {
                            owner.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    break;//Upkeep played creature by creature.
                }
        }
    }
    
    void massSummonCheckNeededTarget() throws IOException {//if someone wants to choice target at death(self or other) - pause game
        crCryed = new ArrayList<>(creatures);//died creature
        ListIterator<Creature> temp = crCryed.listIterator();
        while (temp.hasNext()) {
            Creature tmp = temp.next();
            //Creature ability at enter to board
            if (tmp.text.contains("Найм:") && !tmp.effects.battlecryPlayed && !tmp.isDie()) {
                tmp.battlecryNoTarget();
                tmp.effects.battlecryPlayed = true;
            }
            if (tmp.text.contains("Наймт:") && !tmp.effects.battlecryPlayed && !tmp.isDie())
                //CHECK EXIST TARGET
                if (MyFunction.canTargetComplex(this, tmp)) {
                	
                    owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                    ActivatedAbility.creature = tmp;
                    ActivatedAbility.whatAbility = onCryAbility;
                    if (tmp.text.contains("Цель не обязательно.")) ActivatedAbility.ableAbility=false;
                    else ActivatedAbility.ableAbility=true;
                    //pause until player choice target.
                    owner.sendChoiceTarget(tmp.name + " просит выбрать цель.");
                    System.out.println("pause");
                    synchronized (owner.cretureDiedMonitor) {
                        try {
                            owner.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    tmp.effects.battlecryPlayedTimes--;
                    if (tmp.effects.battlecryPlayedTimes<=0)
                    tmp.effects.battlecryPlayed = true;
                    else {
                    	massSummonCheckNeededTarget();
                    }
                    break;//Cry played creature by creature.
                } else {
                    owner.printToView(0, "Целей для " + tmp.name + " нет.");
                    tmp.effects.battlecryPlayed = true;//If you can't target, after you can't play this ability
                }
        }
    }

    void newTurn() throws IOException {
        owner.board.whichTurn = playerName;
        owner.opponent.board.whichTurn = playerName;
        owner.board.turnCount++;
        owner.opponent.board.turnCount++;

        owner.printToView(0, "Ход номер " + owner.board.turnCount + ", игрок " + playerName);
        owner.opponent.printToView(0, "Ход номер " + owner.board.turnCount + ", игрок " + playerName);
        owner.printToView(1, Color.GREEN, "Ваш ход");
        owner.opponent.printToView(1, Color.RED, "Ход противника");

        //Tull-Bagar
        if (this.equpiment[3] != null && this.equpiment[3].name.equals("Пустошь Тул-Багара")) {
            owner.printToView(0, "Пустошь Тул-Багара ранит всех героев.");
            this.takeDamage(1);
            owner.opponent.player.takeDamage(1);
        }
        if (owner.opponent.player.equpiment[3] != null && owner.opponent.player.equpiment[3].name.equals("Пустошь Тул-Багара")) {
            owner.printToView(0, "Пустошь Тул-Багара ранит всех героев.");
            this.takeDamage(1);
            owner.opponent.player.takeDamage(1);
        }

        //Search for upkeep played effects

        ArrayList<Creature> crtCopy = new ArrayList<>(owner.opponent.player.creatures);
        ListIterator<Creature> temp = crtCopy.listIterator();
        while (temp.hasNext()) {
            Creature p = temp.next();
            boolean isOnBoard = owner.opponent.player.creatures.contains(p);
            if (p.text.contains("В начале хода") || p.text.contains("В начале противника хода") && !p.isDie() && isOnBoard)
                owner.opponent.gameQueue.push(new GameQueue.QueueEvent("Upkeep", p, 0));
            if (p.effects.turnToDie == 0 && !p.isDie() && isOnBoard) {
                p.effects.takeDieEffect();
                owner.opponent.gameQueue.push(new GameQueue.QueueEvent("Die", p, 0));
            }
            while (owner.gameQueue.size() != 0 || owner.opponent.gameQueue.size() != 0) {
                owner.opponent.gameQueue.responseAllQueue();
                owner.gameQueue.responseAllQueue();
            }
        }
        //And for my creatures
        crtCopy = new ArrayList<>(creatures);
        temp = crtCopy.listIterator();
        while (temp.hasNext()) {
            Creature p = temp.next();
            boolean isOnBoard = creatures.contains(p);
            if (p.text.contains("В начале хода") || p.text.contains("В начале вашего хода") && !p.isDie() && isOnBoard) {
                owner.gameQueue.push(new GameQueue.QueueEvent("Upkeep", p, 0));
            }
            if (p.effects.poison != 0 && !p.text.contains("Защита от отравления.") && !p.isDie() && isOnBoard) {
                p.takeDamage(p.effects.poison, p, Creature.DamageSource.poison);
                //It may push die to queue.
            }
            if (p.effects.turnToDie == 0 && !p.isDie() && isOnBoard) {
                p.effects.takeDieEffect();
                owner.gameQueue.push(new GameQueue.QueueEvent("Die", p, 0));
            }
            while (owner.gameQueue.size() != 0 || owner.opponent.gameQueue.size() != 0) {
                owner.opponent.gameQueue.responseAllQueue();
                owner.gameQueue.responseAllQueue();
            }
        }

        untap();
        //Get coin
        if (totalCoin < 10) totalCoin++;
        //Untap
        untappedCoin = totalCoin;

        if (equpiment[0] != null) equpiment[0].untap();
        if (equpiment[1] != null) equpiment[1].untap();
        if (equpiment[2] != null) equpiment[2].untap();

        for (int i = creatures.size() - 1; i >= 0; i--) {
            //untap
            creatures.get(i).isSummonedJust = false;
            creatures.get(i).untapCreature();
            //armor
            creatures.get(i).currentArmor = creatures.get(i).maxArmor;
            //for rage
            creatures.get(i).takedDamageThisTurn = false;
            creatures.get(i).attackThisTurn = false;
            creatures.get(i).blockThisTurn = false;
        }

        //Draw
        if (owner.board.turnCount != 1) {//First player not draw card in first turn. It's rule.
            if (deck.haveTopDeck())
            drawCard();
            else {
                cantDrawCardAtBeginTurn++;
                owner.printToView(0,playerName+" не смог взять карту в начале хода("+cantDrawCardAtBeginTurn+")");
                if (cantDrawCardAtBeginTurn>=3){
                    loseGame();
                }
            }
        }
        //Send status
        owner.sendStatus();
        //owner.opponent.sendStatus();
    }

    Card getCardByID(String id) {
    	for (int i=0;i<cardInHand.size();i++){
    		if (cardInHand.get(i).id.equals(id)) return cardInHand.get(i);
    	}
    	//TODO Other 
    	return null;
    }
    
    void playCardX(int num, Card _card, Creature _targetCreature, Player _targetPlayer, int x) throws IOException {
        owner.printToView(0, "X = " + x + ".");
        _card.text = _card.text.replace("ХХХ", String.valueOf(x));
        System.out.println("text after replace:" + _card.text);
        tempX = x;
        playCard(num, _card, _targetCreature, _targetPlayer);
        tempX = 0;
    }

    void playCard(int num, Card _card, Creature _targetCreature, Player _targetPlayer) throws IOException {
        int effectiveCost = _card.getCost(this);
        if (tempX != 0) effectiveCost += tempX;

        if (untappedCoin >= effectiveCost) {
            untappedCoin -= effectiveCost;
           //owner.printToView(0, "Розыгрышь карты " + _card.name + ".");
            //remove from hand
            removeCardFromHand(num);
            //put on table or cast spell
            if (_card.type == 1) {
                //release text on spell
                //#PlaySpell(Player, SpellName, TargetHalfBoard[0-self,1-enemy], TargetCreatureNum[-1 means targets player])

                //Card put to graveyard instantly!
                this.addCardToGraveyard(_card);
                if (_targetPlayer != null) {
                    if (_targetPlayer == this)
                        owner.sendBoth("#PlaySpell(" + playerName + "," + _card.name + ",0,-1)");
                    else owner.sendBoth("#PlaySpell(" + playerName + "," + _card.name + ",1,-1)");
                    _card.playOnPlayer(this, _targetPlayer);
                }
                if (_targetCreature != null) {
                    if (_targetCreature.owner == this)
                        owner.sendBoth("#PlaySpell(" + playerName + "," + _card.name + ",0," + getNumberOfCreature(_targetCreature) + ")");
                    else
                        owner.sendBoth("#PlaySpell(" + playerName + "," + _card.name + ",1," + owner.opponent.player.getNumberOfCreature(_targetCreature) + ")");
                    _card.playOnCreature(this, _targetCreature);
                }
                //No target
                if ((_targetCreature == null) && (_targetPlayer == null)) {
                    owner.sendBoth("#PlaySpell(" + playerName + "," + _card.name + ",-1,-1)");
                    _card.playNoTarget(this);
                }
            } else if (_card.type == 2) {
                //creature
                owner.board.addCreatureToBoard(_card, this);
            } else if (_card.type == 3) {
                //TODO Equpiment command server
                owner.sendBoth("#PutEquipToBoard(" + playerName + "," + _card.name + ","+ _card.id + ")");
                if (_card.creatureType.equals("Броня")) {
                    if (this.equpiment[0] != null) removeEqupiment(this.equpiment[0]);
                    this.equpiment[0] = new Equpiment(_card, this);
                } else if (_card.creatureType.equals("Амулет")) {
                    if (this.equpiment[1] != null) removeEqupiment(this.equpiment[1]);
                    this.equpiment[1] = new Equpiment(_card, this);
                } else if (_card.creatureType.equals("Оружие")) {
                    if (this.equpiment[2] != null) removeEqupiment(this.equpiment[2]);
                    this.equpiment[2] = new Equpiment(_card, this);
                }
            } else if (_card.type == 4) {
                //Event is equip with n=3
                owner.sendBoth("#PutEquipToBoard(" + playerName + "," + _card.name + "," + _card.id + ")");
                if (this.equpiment[3] != null) removeEqupiment(this.equpiment[3]);
                this.equpiment[3] = new Equpiment(_card, this);
            }

        } else {
            owner.printToView(0, "Не хватает монет.");
        }
    }

    void drawCard() throws IOException {
        if (deck.haveTopDeck())
            addCardToHand(deck.drawTopDeck());
        else {
            owner.printToView(0, "Deck of " + playerName + " is empty.");
        }
    }

    void drawSpecialCard(Card c) throws IOException {
        addCardToHand(c);
        deck.cards.remove(c);
        deck.suffleDeck(owner.sufflingConst);
    }

    void digSpecialCard(Card c) throws IOException {
        addCardToHand(c);
        removeCardFromGraveyard(c);
    }

    void takeDamage(int dmg) throws IOException {
        //equpiment[1]
        if (equpiment[1] != null) {
            if (equpiment[1].name.equals("Браслет подчинения")) {
                //Плащ исхара
                if (dmg != 1)
                    owner.printToView(0, "Браслет подчинения свел атаку к 1.");
                	owner.opponent.printToView(0, "Браслет подчинения свел атаку к 1.");
                dmg = 1;
            }
        }
        //equpiment[0]
        if (equpiment[0] != null) {
            if (equpiment[0].name.equals("Плащ Исхара")) {
                //Плащ исхара
                int tmp = dmg;
                dmg -= equpiment[0].hp;
                equpiment[0].hp -= tmp;
                if (dmg < 0) dmg = 0;
                //TODO Send equip effect
                owner.sendBoth("#AddEquipEffectHP("+owner.player.playerName+","+"0"+","+equpiment[0].hp+")");
                owner.printToView(0, "Плащ Исхара предотвратил " + (tmp - dmg) + " урона.");
                owner.opponent.printToView(0, "Плащ Исхара предотвратил " + (tmp - dmg) + " урона.");
                if (equpiment[0].hp <= 0) {
                    removeEqupiment(equpiment[0]);
                    equpiment[0] = null;
                }
            }
        }
        damage += dmg;
        if (dmg != 0) {
            owner.sendBoth("#TakeHeroDamage(" + playerName + "," + dmg + ")");
            owner.printToView(0, this.name + " получет " + dmg + " урона.");
        }

        if (hp <= damage) {
               loseGame();
            }
    }

    void loseGame() throws IOException {
        owner.server.sendMessage("#LoseGame(" + playerName + ")");
        owner.opponent.server.sendMessage("#LoseGame(" + playerName + ")");
        owner.setPlayerGameStatus(MyFunction.PlayerStatus.endGame);//It is not matter.
        owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.endGame);

        //TODO End game, get bonus etc.
        //owner.removeBothClient();
    }

    void heal(int dmg) throws IOException {
        if (equpiment[1]!=null && equpiment[1].name.equals("Браслет подчинения")) {
            owner.printToView(0, name + " не может быть излечен.");
        } else {
            damage -= dmg;
            if (damage < 0) damage = 0;
            owner.sendBoth("#TakeHeroDamage(" + playerName + ",-" + dmg + ",0)");
        }
    }

    void abilityNoTarget(int n) throws IOException {
    	String txt="";
    	if (n==0){
         txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:0".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАП HERO: " + txt);
    	}
    	else {
    		 txt = this.text.substring(this.text.indexOf("2ТАП:") + "2ТАП:0".length() + 1, this.text.indexOf(".", this.text.indexOf("2ТАП:")) + 1);
            System.out.println("ТАП2 HERO: " + txt);	
    	}
        tap();
        Card.ability(owner, this, this, null, null, null, txt);
    }

    void ability(int n, Creature _cr, Player _pl) throws IOException {
    	String txt="";
    	if (n==0){
        txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ: ".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:") + 1));
        System.out.println("TAPT HERO: " + txt);
    	}
    	else {
    		 txt = this.text.substring(this.text.indexOf("2ТАПТ:") + "2ТАПТ: ".length() + 1, this.text.indexOf(".", this.text.indexOf("2ТАПТ:") + 1));
    	        System.out.println("TAPT2 HERO: " + txt);	
    	}
        tap();
        Card.ability(owner, this, this, null, _cr, _pl, txt);
    }

    public Card searchInGraveyard(String name) {
        for (int i = 0; i <= graveyard.size(); i++) {
            if (graveyard.get(i).name.equals(name)) return graveyard.get(i);
        }
        return null;
    }

    public void tap() throws IOException {
        //TODO #Tap
        owner.sendBoth("#TapPlayer(" + playerName + ",1)");
        isTapped = true;
    }

    public void untap() throws IOException {
        owner.sendBoth("#TapPlayer(" + playerName + ",0)");
        isTapped = false;
    }

    public void removeEqupiment(Equpiment eq) throws IOException{
    	owner.sendBoth("#RemoveEquip("+playerName+","+eq.id+")");
    	addCardToGraveyard(eq);
    	int n =  MyFunction.getEquipNumByType(eq.creatureType);
    	System.out.println("EQ n= "+n);
    	this.equpiment[n]=null;
    }

    public int getNotNullEqupiment(){
    	//Event on other method
    	int count=0;
    	for (int i=0;i<equpiment.length;i++){
    		if (equpiment[i]!=null) count++; 
    	}
    	return count;
    }
}
