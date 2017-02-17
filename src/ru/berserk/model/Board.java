package ru.berserk.model;

import java.io.IOException;

/**
 * Created by StudenetiskiyA on 30.12.2016.
 */

public class Board {

    String whichTurn = "";
    int turnCount = 0;

    public Board() {
    }

    void addExistCreatureToBoard(Creature _creature, Player _player) throws IOException {
        addCreatureToBoard(_creature,_player,true, _creature);
    }

    void addCreatureToBoard(Card _creature, Player _player) throws IOException {
        addCreatureToBoard(_creature, _player, false, null);
    }

    void addCreatureToBoard(Card _creature, Player _player, Boolean existCreature, Creature _crEx) throws IOException {
        Creature summonCreature;
        if (!existCreature) {
            summonCreature = new Creature(_creature, _player);
            summonCreature.id = _creature.id;
            _player.addCreatureToList(summonCreature);
            _player.owner.gameQueue.push(new GameQueue.QueueEvent("Summon", summonCreature, 0));
        } else {
            summonCreature = _crEx;
            _player.addCreatureToList(summonCreature);
        }
        if (summonCreature.text.contains("Уникальность.")) {
            for (int i = _player.creatures.size() - 1; i >= 0; i--) {
                if (_player.creatures.get(i).name.equals(_creature.name) && _player.creatures.get(i) != summonCreature) {
                    System.out.println("Double uniqe creature, die.");
                    _player.owner.sendBoth("Double uniqe creature, die.");
                    _player.creatures.get(i).die();
                    break;
                }
            }
        }
        if (summonCreature.getTougness() <= 0) {
            System.out.println("Creature hp less 0, die.");
            summonCreature.die();
        }
    }
}
