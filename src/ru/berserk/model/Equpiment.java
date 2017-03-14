package ru.berserk.model;

// Created by StudenetskiyA on 18.01.2017.

import java.io.IOException;

import ru.berserk.model.Creature.DamageSource;

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
    
    int getPower(){
    	return power;
    }
    
    DamageSource getPowerType(){
		if (text.contains("Магический урон."))
			return DamageSource.magic;
		return DamageSource.physic;
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
       // isTapped = true;
    	owner.tap();
      //  owner.owner.sendBoth("#TapEqupiment("+owner.owner.player.playerName+","+id+",1)");
    }
    void untap() throws IOException {
       // isTapped = false;
        owner.owner.sendBoth("#TapEqupiment("+owner.owner.player.playerName+","+id+",0)");
    }

    public void tapAbility(Permanent _target) throws IOException {
    	String tapT = (_target == null) ? "ТАП:" : "ТАПТ:";
        String txt = this.text.substring(this.text.indexOf(tapT) + tapT.length() + 1, this.text.indexOf(".", this.text.indexOf(tapT)) + 1);
        System.out.println(tapT +" " + txt);
        Card.ability(this, owner, _target, txt);
        tap();
    }

    public void cry(Permanent _target) throws IOException {
        String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
        System.out.println("Наймт: " + txt);
        Card.ability(this, owner, _target, txt);
    }
}
