package ru.berserk.model;

// Created by StudenetskiyA on 18.01.2017.

import java.io.IOException;

public class Equpiment extends Card {
    public boolean isTapped;
    public Player owner;

    public Equpiment(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        isTapped = false;
        owner = _owner;
    }

    void tap() {
        isTapped = true;
    }

    public void tapNoTargetAbility() throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАП: " + txt);
        tap();
        Card.ability(owner.owner,this, owner, null, null, txt);
    }

    public void tapTargetAbility(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:")) + 1);
        System.out.println("ТАПТ: " + txt);
        tap();
        Card.ability(owner.owner,this, owner, _cr, _pl, txt);
    }

    public void cry(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
        System.out.println("Наймт: " + txt);
        Card.ability(owner.owner,this, owner, _cr, _pl, txt);
    }
}
