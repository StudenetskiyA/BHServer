package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

// Created by StudenetskiyA on 30.12.2016.

public class Creature extends Card {
    boolean isTapped;
    boolean isSummonedJust;
    boolean activatedAbilityPlayed = false;
    boolean takedDamageThisTurn = false;
    boolean attackThisTurn = false;
    boolean blockThisTurn = false;

    Player owner;
    Player trueOwner;//for change control
    int currentArmor = 0;
    int maxArmor = 0;
    int damage;//taked damage

    enum DamageSource {fightOffense, fightDefense, spell, poison, ability, scoot}

    Effects effects = new Effects(this);

    public class Effects {
        Creature whis;
        String additionalText = "";
        boolean changedControll=false;
        boolean isDie = false;
        int poison = 0;
        private int bonusPower = 0;
        private int bonusPowerUEOT = 0;
        private int bonusTougness = 0;
        private int bonusTougnessUEOT = 0;
        private int bonusArmor = 0;
        int turnToDie = 999;
        boolean vulnerability = false;
        boolean upkeepPlayed = false;
        boolean battlecryPlayed = false;
        boolean deathPlayed = false;
        boolean controlChanged = false;
        ArrayList<TemporaryTextEffect> temporaryTextEffects = new ArrayList<>();

        class TemporaryTextEffect{
            String textEffect="";
            int lenght=0;

            TemporaryTextEffect(String ef,int l){
                textEffect=ef;
                lenght=l;
            }
        }

        Effects(Creature _cr) {
            whis = _cr;
        }
        
        Effects(Creature _cr,Effects ef){
        	 whis = _cr;
        	  additionalText = ef.additionalText;
              changedControll= ef.changedControll;
              isDie = ef.isDie;
              poison = ef.poison;
              bonusPower = ef.bonusPower;
              bonusPowerUEOT = ef.bonusPowerUEOT;
              bonusTougness = ef.bonusTougness;
              bonusTougnessUEOT = ef.bonusTougnessUEOT;
              bonusArmor = ef.bonusArmor;
              turnToDie = 999;
              vulnerability = ef.vulnerability;
              upkeepPlayed = ef.upkeepPlayed;
              battlecryPlayed = ef.battlecryPlayed;
              deathPlayed = ef.deathPlayed;
              controlChanged = ef.controlChanged;
              temporaryTextEffects = new ArrayList<>(ef.temporaryTextEffects);
        }

        public void EOT() throws IOException {
            upkeepPlayed = false;
            turnToDie--;
            bonusPowerUEOT = 0;
            ArrayList<TemporaryTextEffect> efCopy = new ArrayList<>(temporaryTextEffects);
            ListIterator<TemporaryTextEffect> temp = efCopy.listIterator();
            while (temp.hasNext()) {
                TemporaryTextEffect te = temp.next();
                te.lenght--;
                if (te.lenght<=0) {
                    looseAdditionalText(te.textEffect);
                    temporaryTextEffects.remove(te);
                }
            }
            activatedAbilityPlayed = false;
        }

        String getAdditionalText(){
            String tmp=additionalText;
            for (TemporaryTextEffect te: temporaryTextEffects){
                tmp+=te.textEffect+" ";
            }
            return tmp;
        }

        boolean getVulnerability() {
            if (owner.owner.opponent.player.equpiment[3] != null && owner.owner.opponent.player.equpiment[3].name.equals("Аккения")) {
                System.out.println("Аккения детектед");
                return true;
            }
            return vulnerability;
        }

        int getBonusPower() {
            int staticBonus = 0;
            //TanGnome take + for power
            if ((creatureType.equals("Гном")) && (!name.equals("Тан гномов"))) {
                int tanFounded = 0;
                for (int i = 0; i < owner.creatures.size(); i++) {
                    if (owner.creatures.get(i).name.equals("Тан гномов")) tanFounded++;
                }
                staticBonus += tanFounded;
            }
            //Yatagan
            if (owner.equpiment[2]!=null && owner.equpiment[2].name.equals("Орочий ятаган")) {
                staticBonus += 1;
            }
            //Flaming giant
            if (name.equals("Пылающий исполин")) {
                staticBonus += damage;
            }
            //Vozd' clana on graveyard
            if (color == 2) {
                int vozdFounded = 0;
                for (int i = 0; i < owner.graveyard.size(); i++) {
                    if (owner.graveyard.get(i).name.equals("Вождь клана")) vozdFounded++;
                }
                staticBonus += vozdFounded;
            }
            //Chain dog take and get + power
            if ((name.equals("Цепной пес"))) {
                int houndFounded = 0;
                for (int i = 0; i < owner.creatures.size(); i++) {
                    if (owner.creatures.get(i).name.equals("Цепной пес")) houndFounded++;
                }
                //and for opponent
                for (int i = 0; i < owner.owner.opponent.player.creatures.size(); i++) {
                    if (owner.owner.opponent.player.creatures.get(i).name.equals("Цепной пес")) houndFounded++;
                }
                staticBonus += houndFounded - 1;
            }
            return staticBonus + bonusPower + bonusPowerUEOT;
        }

        int getBonusTougness() {
            return bonusTougness;
        }

        //#TakeCreatureEffect(Player, CreatureNumOnBoard,Effect,EffectCount)
        void takePoison(int p) throws IOException {
            if (poison <= p)
                poison = p;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.poison.getValue() + "," + p + ")");
        }

        void takeTurnToDie(int t) throws IOException {
            turnToDie = t;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.turnToDie.getValue() + "," + t + ")");
        }

        void takeVulnerability() throws IOException {
            vulnerability = true;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.vulnerability.getValue() + "," + 0 + ")");
        }

        void takeDieEffect() throws IOException {
            isDie = true;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.die.getValue() + "," + 0 + ")");
        }

        void takeBonusPowerUEOT(int n) throws IOException {
            bonusPowerUEOT += n;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.bonusPowerUEOT.getValue() + "," + n + ")");
        }

        void takeControlChange() throws IOException {
            controlChanged = true;
            whis.owner.owner.sendBoth("#TakeCreatureBEffect("+ whis.id + "," + MyFunction.Effect.controlChanged.getValue() + "," + 0 + ")");
        }
        
        void takeBonusPower(int n) throws IOException {
            bonusPower += n;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.bonusPower.getValue() + "," + n + ")");
        }

        void takeBonusTougnessUEOT(int n) throws IOException {
            bonusTougnessUEOT += n;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.bonusTougnessUEOT.getValue() + "," + n + ")");
        }

        void takeBonusTougness(int n) throws IOException {
            bonusTougness += n;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.bonusTougness.getValue() + "," + n + ")");
        }

        void takeBonusArmor(int n) throws IOException {
            bonusArmor += n;
            currentArmor += n;
            owner.owner.sendBoth("#TakeCreatureEffect(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + MyFunction.Effect.bonusArmor.getValue() + "," + n + ")");
        }

        void takeAdditionalText(String txt) throws IOException {
            additionalText += txt;
            owner.owner.sendBoth("#TakeCreatureText(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + txt + ")");
        }

        void looseAdditionalText(String txt) throws IOException {
            owner.owner.sendBoth("#LooseCreatureText(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + txt + ")");
        }

        void takeTemporaryAdditionalText(String txt, int lenght) throws IOException {
            temporaryTextEffects.add(new TemporaryTextEffect(txt,lenght));
            owner.owner.sendBoth("#TakeCreatureText(" + owner.playerName + "," + owner.getNumberOfCreature(this.whis) + "," + txt + ")");
        }
    }

    String getText() {
        return text + "." + effects.getAdditionalText();
    }

    Boolean isContainsOwn(String what) {
        return MyFunction.isInOwnTextNotInTake(text + "." + effects.getAdditionalText(), what);
    }

    boolean getCanAttack(){
        if (getIsSummonedJust()) return false;
        if (isTapped) return false;
        if (attackThisTurn) return false;
        return !MyFunction.textNotInTake(getText()).contains("Не может атаковать");
    }
    boolean getCanBlock(){
        if (isTapped) return false;
        if (blockThisTurn) return false;
        return !MyFunction.textNotInTake(getText()).contains("Не может блокировать");
    }
    boolean getIsSummonedJust() {
        if (text.contains("Рывок")) return false;
        if (effects.additionalText.contains("Рывок")) return false;
        //Chain dog take charge
        if ((name.equals("Цепной пес"))) {
            int houndFounded = 0;
            for (int i = 0; i < owner.creatures.size(); i++) {
                if (owner.creatures.get(i).name.equals("Цепной пес")) houndFounded++;
            }
            //and for opponent
            for (int i = 0; i < owner.owner.opponent.player.creatures.size(); i++) {
                if (owner.owner.opponent.player.creatures.get(i).name.equals("Цепной пес")) houndFounded++;
            }
            if (houndFounded > 1) return false;
        }
        return isSummonedJust;
    }

    boolean getAttackSkill() {
        if (text.contains("Опыт в атаке")) return true;
        if (effects.additionalText.contains("Опыт в атаке")) return true;
        return false;
    }

    boolean getDefenseSkill() {
        if (text.contains("Опыт в защите")) return true;
        if (effects.additionalText.contains("Опыт в защите")) return true;
        return false;
    }

    boolean getTargetStrike() {
        if (MyFunction.isInOwnTextNotInTake(text, "Направленный удар")) return true;
        if (effects.additionalText.contains("Направленный удар")) return true;
        return false;
    }

    int getMaxArmor() {
        return maxArmor + effects.bonusArmor;
    }

    public int getCurrentArmor() {
        return currentArmor;
    }

    int getPower() {
        return power + effects.getBonusPower();
    }

    int getTougness() {
        return hp + effects.getBonusTougness();
    }

    Creature(Creature _card) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        image = _card.image;
        cost = _card.cost;
        isTapped = _card.isTapped;
        isSummonedJust = _card.isSummonedJust;
        name = _card.name;
        owner = _card.owner;
        trueOwner= _card.owner;
        effects = new Effects(_card,_card.effects);
        takedDamageThisTurn = _card.takedDamageThisTurn;
        attackThisTurn = _card.attackThisTurn;
        blockThisTurn = _card.blockThisTurn;
        id = _card.id;
        currentArmor = _card.currentArmor;
        maxArmor = _card.maxArmor;
        damage = _card.damage;
    }

    Creature(Card _card, Player _owner) {
        super(_card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType, _card.tapTargetType, _card.text, _card.power, _card.hp);
        image = _card.image;
        cost = _card.cost;
        isTapped = false;
        isSummonedJust = true;
        name = _card.name;
        owner = _owner;
        trueOwner = _owner;
        if (text.contains("Броня ")) {
            maxArmor = MyFunction.getNumericAfterText(text, "Броня ");
            currentArmor = getMaxArmor();
        }
    }

    void tapCreature() throws IOException {
        isTapped = true;
        owner.owner.sendBoth("#TapCreature(" + owner.playerName + "," + owner.getNumberOfCreature(this) + ",1)");
    }

    void untapCreature() throws IOException {
        isTapped = false;
        owner.owner.sendBoth("#TapCreature(" + owner.playerName + "," + owner.getNumberOfCreature(this) + ",0)");
    }

    private ArrayList<Creature> canAnyoneBlock(Creature target) {
        //Return list of creature, who may be block this
        ArrayList<Creature> crt = new ArrayList<>(owner.owner.opponent.player.creatures);
        ArrayList<Creature> crtCopy = new ArrayList<>(crt);
        ListIterator<Creature> temp = crtCopy.listIterator();

        while (temp.hasNext()) {
            Creature tmp = temp.next();
            if (!tmp.getCanBlock())
                crt.remove(tmp);
        }
        if (crt.contains(target)) crt.remove(target);
        return crt;
    }

    void fightCreature(Creature second) throws IOException {
        if (!second.isTapped) {//First is passive
            owner.owner.printToView(0, this.name + " сражается с " + second.name + ".");
            if ((second.text.contains("Первый удар.")) && (!this.text.contains("Первый удар."))) {
                if (this.damage < this.hp)
                    second.takeDamage(this.getPower(), this, DamageSource.fightDefense, second.haveRage());
                this.takeDamage(second.getPower(), second, DamageSource.fightOffense, second.haveRage());
            } else if ((this.text.contains("Первый удар.")) && (!second.text.contains("Первый удар."))) {
                second.takeDamage(this.getPower(), this, DamageSource.fightDefense, second.haveRage());
                if (second.damage < second.hp)
                    this.takeDamage(second.getPower(), second, DamageSource.fightOffense, second.haveRage());
            } else {
                second.takeDamage(this.getPower(), this, DamageSource.fightDefense, second.haveRage());
                this.takeDamage(second.getPower(), second, DamageSource.fightOffense, second.haveRage());
            }
        } else {
            owner.owner.printToView(0, this.name + " ударяет " + second.name + ".");
            second.takeDamage(this.getPower(), this, DamageSource.fightDefense, second.haveRage());
        }
    }

    void heal(int dmg) throws IOException {
        damage -= dmg;
        if (damage < 0) damage = 0;
        owner.owner.sendBoth("#TakeCreatureDamage(" + owner.playerName + "," + owner.getNumberOfCreature(this) + "," + (-dmg) + ")");
    }

    void fightPlayer(Player second) throws IOException {
        second.takeDamage(this.getPower());
    }

    void attackCreature(Creature target) throws IOException {
        if (target.owner.effect.getBBShield()) {
            owner.owner.printToView(0, "Первая атака должна быть в Бьорнбона.");
            return;
        }
        if (!getCanAttack()) {
            owner.owner.printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
            return;
        }

        owner.owner.sendBoth("#Attack(" + owner.playerName + "," + owner.getNumberOfCreature(this) + "," +
                owner.owner.opponent.player.getNumberOfCreature(target) + ")");

        if (!getAttackSkill())
            tapCreature();
        attackThisTurn = true;

        if (this.getTargetStrike()) {
            fightCreature(target);
        } else {
            ArrayList<Creature> blocker;
            blocker = canAnyoneBlock(target);
            if (blocker.size() != 0) {
                int nc = owner.creatures.indexOf(this);
                int nt = owner.owner.opponent.player.creatures.indexOf(target);
                owner.owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.IChoiceBlocker);
                owner.owner.opponent.creatureWhoAttack = nc;
                owner.owner.opponent.creatureWhoAttackTarget = nt;
                owner.owner.opponent.server.sendMessage("#ChoiceBlocker(" + owner.owner.opponent.name + "," + nc + "," + nt + ")");
                owner.owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceBlocker);
            } else {
                fightCreature(target);
            }
        }
    }

    void attackPlayer(Player target) throws IOException {
        target.effect.takeBBShield(false);
        if (!getCanAttack()) {
            owner.owner.printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
            return;
        }

        owner.owner.sendBoth("#Attack(" + owner.playerName + "," + owner.getNumberOfCreature(this) + ",-1)");

        if (!getAttackSkill())
            tapCreature();
        attackThisTurn = true;

        if (this.getTargetStrike()) {
            fightPlayer(target);
        } else {
            ArrayList<Creature> blocker = canAnyoneBlock(null);
            if (blocker.size() != 0) {
                int nc = owner.creatures.indexOf(this);
                int nt = -1;
                owner.owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.IChoiceBlocker);
                owner.owner.opponent.creatureWhoAttack = nc;
                owner.owner.opponent.creatureWhoAttackTarget = nt;
                owner.owner.opponent.server.sendMessage("#ChoiceBlocker(" + owner.owner.opponent.name + "," + nc + "," + nt + ")");
                owner.owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceBlocker);
            } else {
                fightPlayer(target);
            }
        }
    }

    void takeDamage(int dmg, Card damageSrc, DamageSource dmgsrc, Boolean... rage) throws IOException {
        //Protection from.
        if (this.isContainsOwn("Не получает ран")) {
            owner.owner.printToView(0, this.name + " не получает ран.");
            return;
        }
        if (this.isContainsOwn("Не получает от ударов ран") && (dmgsrc == DamageSource.fightDefense || dmgsrc==DamageSource.fightOffense)) {
            owner.owner.printToView(0, this.name + " не получает от ударов ран.");
            return;
        }
        if (this.isContainsOwn("Защита от выстрелов") && dmgsrc == DamageSource.scoot) {
            owner.owner.printToView(0, "У " + this.name + " защита от выстрелов.");
            return;
        }
        if (this.isContainsOwn("Защита от атак") && (dmgsrc == DamageSource.scoot || dmgsrc == DamageSource.fightDefense)) {
            if (this.isContainsOwn("Защита от атак цвет ")) {
                int c = MyFunction.getNumericAfterText(this.getText(), "Защита от атак цвет ");
                if (damageSrc.color == c) {
                    owner.owner.printToView(0, "У " + this.name + " защита от атак " + damageSrc.name + ".");
                    return;
                }
            }
            if (this.isContainsOwn("Защита от атак стоимость менее ")) {
                int c = MyFunction.getNumericAfterText(this.getText(), "Защита от атак стоимость менее ");
                if (this.getCost(this.owner) < c) {
                    owner.owner.printToView(0, "У " + this.name + " защита от атак " + damageSrc.name + ".");
                    return;
                }
            }
        }

        if (dmgsrc == DamageSource.scoot || dmgsrc==DamageSource.fightOffense || dmgsrc == DamageSource.fightDefense) {
            if ((takedDamageThisTurn) && (rage[0])) {
                dmg++;
                // System.out.println("RAGE!");
            }
            int tmp = dmg;
            dmg -= currentArmor;
            currentArmor -= tmp;
            if (dmg < 0) dmg = 0;
            if (currentArmor < 0) currentArmor = 0;
        }
        if ((effects.getVulnerability())) dmg++;

        damage += dmg;
       // owner.owner.sendBoth("#TakeCreatureDamage(" + owner.playerName + "," + owner.getNumberOfCreature(this) + "," "+ dmg + ")");
        owner.owner.sendBoth("#TakeCreatureDamage(" + owner.playerName + "," + this.id+","+ dmg + ")");

        takedDamageThisTurn = true;

        if (getTougness() <= damage) {
            die();
        }
    }

    void tapNoTargetAbility() throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
        System.out.println("ТАПТ: " + txt);
        tapCreature();
        Card.ability(owner.owner, this, owner, null, null, txt);
    }

    void tapTargetAbility(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1, this.text.indexOf(".", this.text.indexOf("ТАПТ:")) + 1);
        System.out.println("ТАПТ: " + txt);
        //Cant attack here?
        if (!getCanAttack() && txt.contains("Выстрел")) {
            owner.owner.server.sendMessage(this.name+" не может атаковать(выстрел - тоже атака).");
            return;
        }
        tapCreature();
        Card.ability(owner.owner, this, owner, _cr, _pl, txt);
    }

    void deathratle(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("Гибельт:") + "Гибельт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Гибельт:")) + 1);
        System.out.println("Гибельт: " + txt);
        Card.ability(owner.owner, this, owner, _cr, _pl, txt);
    }

    void battlecryNoTarget() throws IOException {
        String txt = this.text.substring(this.text.indexOf("Найм:") + "Найм:".length() + 1, this.text.indexOf(".", this.text.indexOf("Найм:")) + 1);
        Card.ability(owner.owner, this, this.owner, this, null, txt);//Only here 3th parametr=1th
    }

    void battlecryTarget(Creature _cr, Player _pl) throws IOException {
        String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1, this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
        System.out.println("Наймт: " + txt);
        Card.ability(owner.owner, this, owner, _cr, _pl, txt);
    }

    static void deathratleNoTarget(Creature _card, Player _owner) throws IOException {
        String txt = _card.text.substring(_card.text.indexOf("Гибель:") + "Гибель:".length() + 1, _card.text.indexOf(".", _card.text.indexOf("Гибель:")) + 1);
        Card.ability(_owner.owner, _card, _owner, _card, null, txt);//Only here 3th parametr=1th
    }

    void die() throws IOException {
        System.out.println("Die!");
        //Cards put to graveyard instantly, not in end of queue!
        trueOwner.addCardToGraveyard(this);
        owner.owner.gameQueue.push(new GameQueue.QueueEvent("Die", this, 0));
    }

//    void sendAllAboutCreature() throws IOException{
//    	//TODO All field!
//    	String s="#AllAboutCreature(";
//    	s+=this.owner.playerName+",";
//    	s+=this.id+",";		
//    	s+=isTapped+",";
//    	s+=isSummonedJust+",";
//        s+=damage+",";
//        s+=effects.additionalText+",";
//    	s+=")";
//    	owner.owner.server.sendMessage(s);
//    }
    
    void changeControll() throws IOException {
        //add to opponent
        Creature tmp = new Creature(this);
        owner.owner.opponent.board.addExistCreatureToBoard(tmp,owner.owner.opponent.player);//without cry
        tmp.owner = owner.owner.opponent.player;
        tmp.effects = new Effects(tmp,this.effects);
        tmp.effects.battlecryPlayed=true;
        tmp.isSummonedJust=true;
        //Send message
        owner.owner.sendBoth("#ChangeControll(" + owner.playerName + "," + owner.getNumberOfCreature(this) +")");
        tmp.effects.takeControlChange();
        
        //remove from my board
        removeCreatureFromPlayerBoard();
    }

    boolean isDie() {
        if (effects.isDie) return true;
        return (getTougness() <= damage);//And other method to die!
    }

    void returnToHand() throws IOException {
        owner.owner.sendBoth("#ReturnToHand(" + owner.playerName + "," + owner.getNumberOfCreature(this) +")");
        System.out.println("True owner to return = "+trueOwner.playerName);
        trueOwner.addCardToHand(this);
        removeCreatureFromPlayerBoard();
    }

    void removeCreatureFromPlayerBoard() {
        owner.creatures.remove(this);
    }
}
