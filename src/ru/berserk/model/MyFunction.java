package ru.berserk.model;

import java.util.ArrayList;

import com.mysql.jdbc.StringUtils;

/**
 * Created by samsung on 21.01.2017.
 */
public class MyFunction {

	  public static int getEquipNumByType(String creatureType) {
	        switch(creatureType) {
	            case "Оружие":
	                return 2;
	            case "Броня":
	                return 0;
	            case "Амулет":
	                return 1;
	            case "Событие":
	                return 3;
	        }
	        return -1;
	    }
	

      enum WhatAbility {
          heroAbility(1), weaponAbility(2), toHandAbility(3), onUpkeepPlayed(4), onDeathPlayed(5), onOtherDeathPlayed(6), 
          spellAbility(7), onCryAbility(8), nothing(0);

          private final int value;

          WhatAbility(int value) {
              this.value = value;
          }

          public int getValue() {
              return value;
          }

          public static WhatAbility fromInteger(int x) {
              switch (x) {
                  case 0:
                      return nothing;
                  case 1:
                      return heroAbility;
                  case 2:
                      return weaponAbility;
                  case 3:
                      return toHandAbility;
                  case 4:
                      return onUpkeepPlayed;
                  case 5:
                      return onDeathPlayed;
                  case 6:
                      return onOtherDeathPlayed;
                  case 7:
                  	return spellAbility;
                  case 8:
                  	return onCryAbility;
              }
              return null;
          }
      }

      public static boolean isNameUserCorrect(String name){
          if (name.matches("^.*[^a-zA-Zа-яА-Я0-9].*$")) return false;
          //TODO Censored words
          return true;
      }
      
     public static class ActivatedAbility {
         Creature creature;
         boolean creatureTap;
         WhatAbility whatAbility=WhatAbility.nothing;
         boolean ableAbility = true;
         int battlecryPlayedTimes = 0;
         ArrayList<String> alreadyTargetId = new ArrayList<>();
         boolean battlecryTargetChoicedCorrect = false;
         ActivatedAbility(){
        	 
         }
     }

    public static Card searchCardInList(ArrayList<Card> list,String name){
        for (int i=0;i<list.size();i++){
            if (list.get(i).name.equals(name)) return list.get(i);
        }
        return null;
    }

    public static ArrayList<String> getTextBetween(String fromText) {
        ArrayList<String> rtrn = new ArrayList<String>();
        String beforeText = "(";
        fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.indexOf(")"));
        String[] par = fromText.split(",");
        for (int i = 0; i < par.length; i++)
            rtrn.add(par[i]);
        return rtrn;
    }

    public static boolean isInOwnTextNotInTake(String fromText, String text){
        if (!fromText.contains(text)) return false;
        if (!fromText.contains("'")) return fromText.contains(text);
        String tmp = getTextBetweenSymbol(fromText,fromText.substring(0,fromText.indexOf("'")+1),"'");
        tmp=fromText.substring(0,fromText.indexOf(tmp))+fromText.substring(fromText.indexOf(tmp)+1+tmp.length(),fromText.length());
        //System.out.println(tmp);
        return tmp.contains(text);
    }

    public static String textNotInTake(String fromText){
        if (!fromText.contains("'")) return fromText;
        String tmp = getTextBetweenSymbol(fromText,fromText.substring(0,fromText.indexOf("'")+1),"'");
        tmp=fromText.substring(0,fromText.indexOf(tmp))+fromText.substring(fromText.indexOf(tmp)+1+tmp.length(),fromText.length());
        return tmp;
    }

    public static String getTextBetweenSymbol(String fromText, String afterText, String symbol){
        return fromText.substring(fromText.indexOf(afterText)+afterText.length(),fromText.indexOf(symbol,fromText.indexOf(afterText)+afterText.length()));
    }

    enum Effect {
        nightmare(1), vulnerability(2), turnToDie(3), die(4), bonusPowerUEOT(5), bonusPower(6), bonusTougnessUEOT(7), bonusTougness(8),
        bonusArmor(9), cantattackandblock(10), controlChanged(11), notOpenAtBeginNextTurn(12), bonusToShoot(13), iceShield(14);

        private final int value;

        Effect(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Effect fromInteger(int x) {
            switch (x) {
                case 1:
                    return nightmare;
                case 2:
                    return vulnerability;
                case 3:
                    return turnToDie;
                case 4:
                    return die;
                case 5:
                    return bonusPowerUEOT;
                case 6:
                    return bonusPower;
                case 7:
                    return bonusTougnessUEOT;
                case 8:
                    return bonusTougness;
                case 9:
                    return bonusArmor;
                case 10:
                    return cantattackandblock;
                case 11:
                    return controlChanged;
                case 12:
                    return notOpenAtBeginNextTurn;
                case 13:
                    return bonusToShoot;
                case 14:
                    return iceShield;
            }
            return null;
        }
    }
    
    enum Target {myPlayer,myCreature,enemyPlayer,enemyCreature,myEquip,enemyEquip,myEvent,enemyEvent}

    enum PlayerStatus {
        MyTurn(1), EnemyTurn(2), IChoiceBlocker(3), EnemyChoiceBlocker(4), EnemyChoiceTarget(5), MuliganPhase(6), waitingForConnection(7),
        waitOtherPlayer(8), waitingMulligan(9), choiseX(10), searchX(11), choiceTarget(12), digX(13), endGame(14), prepareForBattle(15),
        unknow(0), choiceYesNo(16);

        private final int value;

        PlayerStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PlayerStatus fromInteger(int x) {
            switch(x) {
                case 0:
                    return unknow;
                case 1:
                    return MyTurn;
                case 2:
                    return EnemyTurn;
                case 3:
                    return IChoiceBlocker;
                case 4:
                    return EnemyChoiceBlocker;
                case 5:
                    return EnemyChoiceTarget;
                case 6:
                    return MuliganPhase;
                case 7:
                    return waitingForConnection;
                case 8:
                    return waitOtherPlayer;
                case 9:
                    return waitingMulligan;
                case 10:
                    return choiseX;
                case 11:
                    return searchX;
                case 12:
                    return choiceTarget;
                case 13:
                    return digX;
                case 14:
                    return endGame;
                case 15:
                    return prepareForBattle;
                case 16:
                    return choiceYesNo;
            }
            return null;
        }
    }

    public static boolean canTargetComplex(Player pl, Creature cr){
    return canTargetComplex(pl,cr,"");
    }
    
    public static boolean canTargetComplex(Player pl, Creature cr, String ability){
        boolean canTarget=false;
        
        if (pl.getNumberOfAlivedCreatures() > 0 && MyFunction.canTarget(MyFunction.Target.myCreature,cr.targetType)) {
            if (cr.targetType==12) {
                if (pl.getNumberOfAlivedCreatures() > 1) return true;
                else return false;
            }
            if (cr.targetType==13) {
                if (pl.getNumberOfAlivedCreatures() > 1 || pl.owner.opponent.player.getNumberOfAlivedCreatures() > 1) return true;
                else return false;
            }
            canTarget=true;
        }
        if (pl.owner.opponent.player.getNumberOfAlivedCreatures() > 0 && MyFunction.canTarget(MyFunction.Target.enemyCreature,cr.targetType)) {
            if (cr.targetType==13) {
                if (pl.getNumberOfAlivedCreatures() > 1 || pl.owner.opponent.player.getNumberOfAlivedCreatures() > 1) return true;
                else return false;
            }
            canTarget=true;
        }
        if (MyFunction.canTarget(MyFunction.Target.enemyPlayer,cr.targetType)) canTarget=true;//Both players always stay on board
        if (MyFunction.canTarget(MyFunction.Target.myPlayer,cr.targetType)) canTarget=true;
        
        if (pl.getNotNullEqupiment()>0 && MyFunction.canTarget(MyFunction.Target.myEquip,cr.targetType)) canTarget = true;
        if (pl.owner.opponent.player.getNotNullEqupiment()>0 && MyFunction.canTarget(MyFunction.Target.enemyEquip,cr.targetType)) canTarget = true;
        
        if (pl.equpiment[3]!=null && MyFunction.canTarget(MyFunction.Target.myEvent,cr.targetType)) canTarget = true;
        if (pl.owner.opponent.player.equpiment[3]!=null && MyFunction.canTarget(MyFunction.Target.enemyEvent,cr.targetType)) canTarget = true;
        
        
        return canTarget;
    }

    public static boolean canTarget(Target target,int targetType){
        //10 my hero or my creature, not self
        //12 my creature, not self
        //13 any creature, not self
    	//21 any equip
    	//22 any event
        if (target==Target.myPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==9 || targetType==10 ) return true;
        }
        else if (target==Target.myCreature)
        {
            if (targetType==1 || targetType==3 || targetType==7 || targetType==9 || targetType==10 || targetType==12 || targetType==13) return true;
        }
        else if (target==Target.enemyPlayer)
        {
            if (targetType==2 || targetType==3 || targetType==5 || targetType==6) return true;
        }
        else if (target==Target.enemyCreature)
        {
            if (targetType==1 || targetType==3 || targetType==4 || targetType==6) return true;
        }
        else if (target==Target.myEquip || target==Target.enemyEquip)
        {
            if (targetType==21) return true;
        }
        else if (target==Target.myEvent || target==Target.enemyEvent)
        {
            if (targetType==22) return true;
        }

        if (targetType==0) return true;
        
        return false;
    }

    public static int searchCardInHandByName(ArrayList<Card> _array,String _name){
        for (int i=0;i<_array.size();i++) {
            if (_array.get(i).name.equals(_name)) return i;
        }
        return -1;
    }

    public static int getNumericAfterText(String fromText, String afterText) {
        int begin = fromText.indexOf(afterText);
        int end1 = fromText.indexOf(" ", begin + afterText.length() + 1);
        if (end1 == -1) end1 = 1000;
        int end2 = fromText.indexOf(".", begin + afterText.length() + 1);
        if (end2 == -1) end2 = 1000;
        int end3 = fromText.indexOf(",", begin + afterText.length() + 1);
        if (end3 == -1) end3 = 1000;
        int end = Math.min(end1, end2);
        end = Math.min(end, end3);
        if (end == 1000) end = fromText.length();
        String dmg = fromText.substring(begin + afterText.length(), end);
        int numdmg = 0;
        try {
            numdmg = Integer.parseInt(dmg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numdmg;
    }

    public static int getNumDiedButNotRemovedYet(ArrayList<Creature> list){
        int n=0;
        for (Creature cr:list){
            if (cr.getTougness()<=cr.damage) n++;
        }
        return n;
    }

}
