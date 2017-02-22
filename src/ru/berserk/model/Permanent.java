package ru.berserk.model;

import java.io.IOException;

public class Permanent extends Card{
    Gamer owner;
    String playerName;
    
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

 Permanent(Card c){
	 super(c);
 }
 
}
