package ru.berserk.model;

// Created by StudenetskiyA on 18.01.2017.

import java.io.IOException;

public class Equpiment extends Card {
    public boolean isTapped;
    public Player owner;

    public void takeDamage(int damage) throws IOException{
    	hp-=damage;
    	int n =  MyFunction.getEquipNumByType(this.creatureType);
    	owner.owner.sendBoth("#AddEquipEffectHP("+owner.playerName+","+n+","+this.hp+")");
    	if (hp<=0) {
             die();	
    	}
    }
    
    public void die() throws IOException{
    	owner.owner.sendBoth("#RemoveEquip("+owner.playerName+","+this.id+")");
    	owner.addCardToGraveyard(this);
    	int n =  MyFunction.getEquipNumByType(this.creatureType);
    	System.out.println("EQ n= "+n);
    	owner.equpiment[n]=null;	
    }
    
    public Equpiment(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        isTapped = false;
        id = _card.id;
        owner = _owner;
    }

    void tap() throws IOException {
        isTapped = true;
        owner.owner.sendBoth("#TapEqupiment("+owner.owner.player.playerName+","+id+",1)");
    }
    void untap() throws IOException {
        isTapped = false;
        owner.owner.sendBoth("#TapEqupiment("+owner.owner.player.playerName+","+id+",0)");
    }

    public void tapNoTargetAbility() throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАП: " + txt);
        tap();
        Card.ability(owner.owner,this, owner, null, null, null, txt);
    }

    public void tapTargetAbility(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:")) + 1);
        System.out.println("ТАПТ: " + txt);
        tap();
        Card.ability(owner.owner,this, owner, null, _cr, _pl, txt);
    }

    public void cry(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
        System.out.println("Наймт: " + txt);
        Card.ability(owner.owner,this, owner, null, _cr, _pl, txt);
    }
}
