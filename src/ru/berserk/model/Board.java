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

    public static Creature getCreatureById(Player _pl, String _id){
    	for (int i=0;i<_pl.creatures.size();i++){
    		if (_pl.creatures.get(i).id.equals(_id)) return _pl.creatures.get(i);
    	}
    	for (int i=0;i<_pl.owner.opponent.player.creatures.size();i++){
    		if (_pl.owner.opponent.player.creatures.get(i).id.equals(_id)) return _pl.owner.opponent.player.creatures.get(i);
    	}
    	return null;
    }
    
    public static Equpiment getEqupimentByID(Player _pl, String id){
        for (int i=0;i<_pl.equpiment.length;i++){
            if (_pl.equpiment[i]!=null && _pl.equpiment[i].id.equals(id))
                return _pl.equpiment[i];
        }
        for (int i=0;i<_pl.owner.opponent.player.equpiment.length;i++){
            if (_pl.owner.opponent.player.equpiment[i]!=null && _pl.owner.opponent.player.equpiment[i].id.equals(id))
                return _pl.owner.opponent.player.equpiment[i];
        }
        System.out.println("Not found equpiment by ID");
        return null;
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
            summonCreature = new Creature(_crEx);
            summonCreature.id = _creature.id;
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
