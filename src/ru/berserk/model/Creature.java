package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import ru.berserk.model.Creature.DamageSource;

// Created by StudenetskiyA on 30.12.2016.

public class Creature extends Permanent {
	boolean isSummonedJust;
	boolean activatedAbilityPlayed = false;

	Player owner;
	Player trueOwner;// for change control

	enum DamageSource {
		fightOffense, fightDefense, spell, poison, ability, shoot, magic, physic
	}

	Effects effects = new Effects(this);

	public class Effects extends Permanent.Effects{
		Creature whisCreature;
		String additionalText = "";
		boolean changedControll = false;
		boolean isDie = false;

		private int bonusPower = 0;
		private int bonusPowerUEOT = 0;
		private int bonusTougness = 0;
		private int bonusTougnessUEOT = 0;
		private int bonusArmor = 0;
		private int bonusToShoot = 0;
		int turnToDie = 999;
		boolean vulnerability = false;
		boolean upkeepPlayed = false;
		boolean battlecryPlayed = false;

		boolean deathPlayed = false;
		boolean controlChanged = false;
	//	ArrayList<TemporaryTextEffect> temporaryTextEffects = new ArrayList<>();

		
		Effects(Creature _cr) {
			super(_cr);
			whisCreature = _cr;
		}

		Effects(Creature _cr, Effects ef) {
			super(_cr);
			whisCreature = _cr;
			additionalText = ef.additionalText;
			changedControll = ef.changedControll;
			isDie = ef.isDie;
			// poison = ef.poison;
			nightmare = ef.nightmare;
			bonusPower = ef.bonusPower;
			bonusPowerUEOT = ef.bonusPowerUEOT;
			bonusTougness = ef.bonusTougness;
			bonusTougnessUEOT = ef.bonusTougnessUEOT;
			bonusArmor = ef.bonusArmor;
			bonusToShoot = ef.bonusToShoot;
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
			looseBonusPowerUEOT();
			ArrayList<TemporaryTextEffect> efCopy = new ArrayList<>(temporaryTextEffects);
			ListIterator<TemporaryTextEffect> temp = efCopy.listIterator();
			while (temp.hasNext()) {
				TemporaryTextEffect te = temp.next();
				te.lenght--;
				if (te.lenght <= 0) {
					looseAdditionalText(te.textEffect);
					temporaryTextEffects.remove(te);
				}
			}
			activatedAbilityPlayed = false;

			if (whis.text.contains(" В конце хода если не имеет ран, вернуть его в руку.") && whis.damage == 0) {
				whisCreature.returnToHand();
			}

		}

//		String getAdditionalText() {
//			String tmp = additionalText;
//			for (TemporaryTextEffect te : temporaryTextEffects) {
//				tmp += te.textEffect + " ";
//			}
//			return tmp;
//		}

//		boolean getVulnerability() {
//			if (owner.opponent.player.equpiment[3] != null
//					&& owner.opponent.player.equpiment[3].name.equals("Аккения")) {
//				System.out.println("Аккения детектед");
//				return true;
//			}
//			return vulnerability;
//		}

		int getBonusPower() {
			// TODO Change name on have text! Creature may loose ability
			int staticBonus = 0;
			// Rage ors
			if (text.contains("Пока у вас есть другое существо, получает +2 к удару") && owner.creatures.size() > 1) {
				staticBonus += 2;
			}
			// TanGnome take + for power
			if ((creatureType.equals("Гном")) && (!name.equals("Тан гномов"))) {
				int tanFounded = 0;
				for (int i = 0; i < owner.creatures.size(); i++) {
					if (owner.creatures.get(i).name.equals("Тан гномов"))
						tanFounded++;
				}
				staticBonus += tanFounded;
			}
			// Yatagan
			if (owner.equpiment[2] != null && owner.equpiment[2].name.equals("Орочий ятаган")) {
				staticBonus += 1;
			}
			// Flaming giant
			if (name.equals("Пылающий исполин")) {
				staticBonus += damage;
			}
			// Vozd' clana on graveyard
			if (color == 2) {
				int vozdFounded = 0;
				for (int i = 0; i < owner.graveyard.size(); i++) {
					if (owner.graveyard.get(i).name.equals("Вождь клана"))
						vozdFounded++;
				}
				staticBonus += vozdFounded;
			}
			// Chain dog take and get + power
			if ((name.equals("Цепной пес"))) {
				int houndFounded = 0;
				for (int i = 0; i < owner.creatures.size(); i++) {
					if (owner.creatures.get(i).name.equals("Цепной пес"))
						houndFounded++;
				}
				// and for opponent
				for (int i = 0; i < owner.owner.opponent.player.creatures.size(); i++) {
					if (owner.owner.opponent.player.creatures.get(i).name.equals("Цепной пес"))
						houndFounded++;
				}
				staticBonus += houndFounded - 1;
			}
			return staticBonus + bonusPower + bonusPowerUEOT;
		}

		int getBonusTougness() {
			return bonusTougness;
		}

		int getBonusToShoot() {
			return bonusToShoot;
		}

		// #TakeCreatureEffect(Player, CreatureNumOnBoard,Effect,EffectCount)
		// void takePoison(int p) throws IOException {
		// if (poison <= p)
		// poison = p;
		// owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + "," +
		// MyFunction.Effect.poison.getValue() + "," + p + ")");
		// }

		void takeBonusToScoot(int p) throws IOException {
			bonusToShoot += p;
			owner.owner.sendBoth(
					"#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.bonusToShoot.getValue() + "," + bonusToShoot + ")");
		}

		void takeTurnToDie(int t) throws IOException {
			turnToDie = t;
			owner.owner.sendBoth(
					"#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.turnToDie.getValue() + "," + t + ")");
		}

		void takeVulnerability() throws IOException {
			vulnerability = true;
			owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.vulnerability.getValue()
					+ "," + 0 + ")");
		}

		void takeDieEffect() throws IOException {
			isDie = true;
			owner.owner.sendBoth(
					"#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.die.getValue() + "," + 0 + ")");
		}

		void takeBonusPowerUEOT(int n) throws IOException {
			bonusPowerUEOT += n;
			owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.bonusPowerUEOT.getValue()
					+ "," + n + ")");
		}

		void looseBonusPowerUEOT() throws IOException {
			bonusPowerUEOT = 0;
			owner.owner.sendBoth(
					"#LooseCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.bonusPowerUEOT.getValue() + ")");
		}

		void takeControlChange() throws IOException {
			controlChanged = true;
			whisCreature.owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + ","
					+ MyFunction.Effect.controlChanged.getValue() + "," + 0 + ")");
		}

		void takeBonusPower(int n) throws IOException {
			bonusPower += n;
			owner.owner.sendBoth(
					"#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.bonusPower.getValue() + "," + n + ")");
		}

		void takeBonusTougnessUEOT(int n) throws IOException {
			bonusTougnessUEOT += n;
			owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + ","
					+ MyFunction.Effect.bonusTougnessUEOT.getValue() + "," + n + ")");
		}

		void takeBonusTougness(int n) throws IOException {
			bonusTougness += n;
			owner.owner.sendBoth("#TakeCreatureIdEffect(" + whis.id + "," + MyFunction.Effect.bonusTougness.getValue()
					+ "," + n + ")");
		}

		void takeAdditionalText(String txt) throws IOException {
			additionalText += txt;
			owner.owner.sendBoth("#TakeCreatureIdText(" + whis.id + "," + txt + ")");
		}

		void looseAdditionalText(String txt) throws IOException {
			owner.owner.sendBoth("#LooseCreatureIdText(" + whis.id + "," + txt + ")");
		}

		void takeTemporaryAdditionalText(String txt, int lenght) throws IOException {
			temporaryTextEffects.add(new TemporaryTextEffect(txt, lenght));
			owner.owner.sendBoth("#TakeCreatureIdText(" + whis.id + "," + txt + ")");
		}

	}

//	String getText() {
//		return text + "." + effects.getAdditionalText();
//	}

	Boolean isContainsOwn(String what) {
		return MyFunction.isInOwnTextNotInTake(text + "." + effects.getAdditionalText(), what);
	}

	boolean getCanAttack() {
		if (getIsSummonedJust())
			return false;
		if (isTapped)
			return false;
		return !MyFunction.textNotInTake(getText()).contains("Не может атаковать");
	}

//	boolean getCanBlock() {
//		if (isTapped)
//			return false;
//		return !MyFunction.textNotInTake(getText()).contains("Не может блокировать");
//	}

	boolean getIsSummonedJust() {
		if (text.contains("Рывок"))
			return false;
		if (effects.additionalText.contains("Рывок"))
			return false;
		// Chain dog take charge
		if ((name.equals("Цепной пес"))) {
			int houndFounded = 0;
			for (int i = 0; i < owner.creatures.size(); i++) {
				if (owner.creatures.get(i).name.equals("Цепной пес"))
					houndFounded++;
			}
			// and for opponent
			for (int i = 0; i < owner.owner.opponent.player.creatures.size(); i++) {
				if (owner.owner.opponent.player.creatures.get(i).name.equals("Цепной пес"))
					houndFounded++;
			}
			if (houndFounded > 1)
				return false;
		}
		return isSummonedJust;
	}

//	boolean getAttackSkill() {
//		if (text.contains("Опыт в атаке"))
//			return true;
//		if (effects.getAttackSkill())
//			return true;
//		return false;
//	}
//
//	boolean getDefenseSkill() {
//		if (text.contains("Опыт в защите"))
//			return true;
//		if (effects.additionalText.contains("Опыт в защите"))
//			return true;
//		return false;
//	}

	// boolean getTargetStrike() {
	// if (MyFunction.isInOwnTextNotInTake(text, "Направленный удар")) return
	// true;
	// if (effects.additionalText.contains("Направленный удар")) return true;
	// return false;
	// }

//	boolean getAgility() {
//		if (MyFunction.isInOwnTextNotInTake(text, "Ловкость"))
//			return true;
//		if (effects.additionalText.contains("Ловкость"))
//			return true;
//		return false;
//	}


//	int getPower() {
//		return power + effects.getBonusPower();
//	}
//
//	DamageSource getPowerType(){
//		if (text.contains("Магический урон.") || effects.additionalText.contains("Магический урон."))
//			return DamageSource.magic;
//		return DamageSource.physic;
//	}
//

	Creature(Creature _card) {
		super(_card.owner.owner, _card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType,
				_card.tapTargetType, _card.text, _card.power, _card.hp);

		isThisPlayer=null;
		isThisCreature=this;
		image = _card.image;
		cost = _card.cost;
		isTapped = _card.isTapped;
		isSummonedJust = _card.isSummonedJust;
		name = _card.name;
		owner = _card.owner;
		trueOwner = _card.owner;
		effects = new Effects(_card, _card.effects);
		id = _card.id;
		shield = _card.shield;
		magicShield = _card.magicShield;
		damage = _card.damage;
		
		eff = this.effects;
	}

	Creature(Card _card, Player _owner) {
		super(_owner.owner, _card.cost, _card.name, _card.creatureType, _card.color, _card.type, _card.targetType,
				_card.tapTargetType, _card.text, _card.power, _card.hp);
		isThisPlayer=null;
		isThisCreature=this;
		image = _card.image;
		cost = _card.cost;
		id = _card.id;
		isTapped = false;
		isSummonedJust = true;
		name = _card.name;
		owner = _owner;
		trueOwner = _owner;
		if (text.contains("Щит ")) {
			shield = MyFunction.getNumericAfterText(text, "Щит ");
		}
		if (text.contains("Магический щит ")) {
			magicShield = MyFunction.getNumericAfterText(text, "Магический щит ");
		}
		eff = this.effects;
	}

	void tapCreature() throws IOException {
		tap();
		//isTapped = true;
		//owner.owner.sendBoth("#TapCreature(" + owner.playerName + "," + this.id + ",1)");
	}

	void untapCreature() throws IOException {
		untap();
//		if (isTapped) {
//			if (effects.nightmare != 0 && !isDie()) {
//				takeDamage(effects.nightmare, this, Creature.DamageSource.magic);
//				effects.takeNightmare(-effects.nightmare);
//			}
//		}
//		isTapped = false;
//		owner.owner.sendBoth("#TapCreature(" + owner.playerName + "," + this.id + ",0)");
	}

//	private boolean canAnyoneBlock(Creature target) {
//		if (!target.owner.isTapped && target.owner.equpiment[0]!=null) return true;
//		
//		ArrayList<Creature> crt = new ArrayList<>(owner.owner.opponent.player.creatures);
//		ArrayList<Creature> crtCopy = new ArrayList<>(crt);
//		ListIterator<Creature> temp = crtCopy.listIterator();
//
//		while (temp.hasNext()) {
//			Creature tmp = temp.next();
//			if (!tmp.getCanBlock()) {
//				crt.remove(tmp);
//			} else {
//				if (this.getAgility() && !tmp.getAgility())
//					crt.remove(tmp);
//			}
//		}
//		if (crt.contains(target))
//			crt.remove(target);
//
//		if (crt.size()!=0) return true;
//		
//		return false;
//	}
	
//	private ArrayList<Permanent> canAnyoneBlock(Creature target) {
//		// Return list of creature, who may be block this
//		ArrayList<Permanent> crt = new ArrayList<>(owner.owner.opponent.player.creatures);
//		ArrayList<Creature> crtCopy = new ArrayList<>(crt);
//		ListIterator<Creature> temp = crtCopy.listIterator();
//
//		while (temp.hasNext()) {
//			Creature tmp = temp.next();
//			if (!tmp.getCanBlock()) {
//				crt.remove(tmp);
//			} else {
//				if (this.getAgility() && !tmp.getAgility())
//					crt.remove(tmp);
//			}
//		}
//		if (crt.contains(target))
//			crt.remove(target);
//		return crt;
//	}

//	void fightCreature(Creature second) throws IOException {
//		DamageSource att = this.getPowerType();
//		DamageSource def = second.getPowerType();
//		
//		if (!second.isTapped) {// First is passive
//			if ((second.text.contains("Первый удар.")) && (!this.text.contains("Первый удар."))) {
//				if (this.damage < this.hp)
//					second.takeDamage(this.getPower(), this, att);
//				this.takeDamage(second.getPower(), second, def);
//			} else if ((this.text.contains("Первый удар.")) && (!second.text.contains("Первый удар."))) {
//				second.takeDamage(this.getPower(), this, att);
//				if (second.damage < second.hp)
//					this.takeDamage(second.getPower(), second, def);
//			} else {
//				second.takeDamage(this.getPower(), this, att);
//				this.takeDamage(second.getPower(), second, def);
//			}
//		} else {
//			owner.owner.printToView(0, this.name + " ударяет " + second.name + ".");
//			second.takeDamage(this.getPower(), this, att);
//		}
//		// Killing
//		if (second.isDie() && !this.isDie())
//			this.killing();
//		if (!second.isDie() && this.isDie())
//			second.killing();
//	}

//	void fightPlayer(Player second) throws IOException {
//		second.takeDamage(this.getPower(), this, getPowerType());
//		if (!second.isTapped){
//			if (second.getPower()>0){
//			takeDamage(second.getPower(), second, second.getPowerType());
//			second.equpiment[2].takeDamage(1);
//			}
//		}
//	}

//	void attackCreature(Creature target) throws IOException {
//		if (!getCanAttack()) {
//			owner.owner.printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
//			return;
//		}
//
//		owner.owner.sendBoth("#Attack(" + owner.playerName + "," + owner.getNumberOfCreature(this) + ","
//				+ owner.owner.opponent.player.getNumberOfCreature(target) + ")");
//
//		tapCreature();
//
//		if (canAnyoneBlock(target)) {
//			int nc = owner.creatures.indexOf(this);
//			int nt = owner.owner.opponent.player.creatures.indexOf(target);
//			owner.owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.IChoiceBlocker);
//			owner.owner.opponent.creatureWhoAttack = nc;
//			owner.owner.opponent.creatureWhoAttackTarget = nt;
//			owner.owner.opponent.server
//					.sendMessage("#ChoiceBlocker(" + owner.owner.opponent.name + "," + nc + "," + nt + ")");
//			owner.owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceBlocker);
//		} else {
//			fightCreature(target);
//		}
//	}

//	void attackPlayer(Player target) throws IOException {
//		if (!getCanAttack()) {
//			owner.owner.printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
//			return;
//		}
//
//		owner.owner.sendBoth("#Attack(" + owner.playerName + "," + owner.getNumberOfCreature(this) + ",-1)");
//
//		tap();
//
//		if (canAnyoneBlock(null)) {
//			int nc = owner.creatures.indexOf(this);
//			int nt = -1;
//			owner.owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.IChoiceBlocker);
//			owner.owner.opponent.creatureWhoAttack = nc;
//			owner.owner.opponent.creatureWhoAttackTarget = nt;
//			owner.owner.opponent.server
//					.sendMessage("#ChoiceBlocker(" + owner.owner.opponent.name + "," + nc + "," + nt + ")");
//			owner.owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceBlocker);
//		} else {
//			fightPlayer(target);
//		}
//	}

//	void takeDamage(int dmg, Card damageSrc, DamageSource dmgsrc) throws IOException {
//		// Protection from.
//		// if (this.isContainsOwn("Не получает ран")) {
//		// owner.owner.printToView(0, this.name + " не получает ран.");
//		// return;
//		// }
//		// if (this.isContainsOwn("Не получает от ударов ран") && (dmgsrc ==
//		// DamageSource.fightDefense || dmgsrc==DamageSource.fightOffense)) {
//		// owner.owner.printToView(0, this.name + " не получает от ударов
//		// ран.");
//		// return;
//		// }
//		// if (this.isContainsOwn("Защита от атак") && (dmgsrc ==
//		// DamageSource.shoot || dmgsrc == DamageSource.fightDefense)) {
//		// if (this.isContainsOwn("Защита от атак цвет ")) {
//		// int c = MyFunction.getNumericAfterText(this.getText(), "Защита от
//		// атак цвет ");
//		// if (damageSrc.color == c) {
//		// owner.owner.printToView(0, "У " + this.name + " защита от атак " +
//		// damageSrc.name + ".");
//		// return;
//		// }
//		// }
//		// if (this.isContainsOwn("Защита от атак стоимость менее ")) {
//		// int c = MyFunction.getNumericAfterText(this.getText(), "Защита от
//		// атак стоимость менее ");
//		// if (this.getCost(this.owner) < c) {
//		// owner.owner.printToView(0, "У " + this.name + " защита от атак " +
//		// damageSrc.name + ".");
//		// return;
//		// }
//		// }
//		// }
//
//		if (owner.effects.iceShield>0) {
//			 dmg -= owner.effects.getIceShield();
//			 owner.effects.takeIceShield(-dmg);
//			 if (dmg < 0) dmg = 0;
//		}
//		
//		if (dmgsrc == DamageSource.physic) {
//			dmg -= getShield();
//			if (dmg < 0)
//				dmg = 0;
//		}
//		if (dmgsrc == DamageSource.magic) {
//			dmg -= getMagicShield();
//			if (dmg < 0)
//				dmg = 0;
//		}
//
//		damage += dmg;
//		
//		if (dmg!=0)
//		owner.owner.sendBoth("#TakeCreatureDamage(" + owner.playerName + "," + this.id + "," + dmg + ")");
//
//		if (getTougness() <= damage) {
//			die();
//		}
//	}

//	void tapNoTargetAbility() throws IOException {
//		String txt = this.text.substring(this.text.indexOf("ТАП:") + "ТАП:".length() + 1,
//				this.text.indexOf(".", this.text.indexOf("ТАП:")) + 1);
//		System.out.println("ТАПТ: " + txt);
//		tapCreature();
//		Card.ability(owner.owner, this, owner, this, null, null, txt);
//	}
//
//	void tapTargetAbility(Creature _cr, Player _pl) throws IOException {
//		String txt = this.text.substring(this.text.indexOf("ТАПТ:") + "ТАПТ:".length() + 1,
//				this.text.indexOf(".", this.text.indexOf("ТАПТ:")) + 1);
//		System.out.println("ТАПТ: " + txt);
//		// Cant attack here?
//		if (!getCanAttack() && txt.contains("Выстрел")) {
//			owner.owner.server.sendMessage(this.name + " не может атаковать(выстрел - тоже атака).");
//			return;
//		}
//		tapCreature();
//		Card.ability(owner.owner, this, owner, this, _cr, _pl, txt);
//	}

//	void deathratle(Permanent _target) throws IOException {
//		String t = (_target==null) ? "Гибель:" : "Гибельт:";
//
//		String txt = this.text.substring(this.text.indexOf(t) + t.length() + 1,
//				this.text.indexOf(".", this.text.indexOf(t)) + 1);
//		System.out.println(t + " " + txt);
//		Card.ability(this, _target, txt);
//	}

	void battlecryEquipTarget(Equpiment _eq) throws IOException {
		String txt = this.text.substring(this.text.indexOf("Наймт:") + "Наймт:".length() + 1,
				this.text.indexOf(".", this.text.indexOf("Наймт:")) + 1);
		System.out.println("CryEq: " + txt);
		Card.ability(owner.owner, this, owner, _eq, txt);
	}

//	void battlecry(Permanent _target) throws IOException {
//		String t = (_target==null) ? "Найм:" : "Наймт:";
//		String txt = this.text.substring(this.text.indexOf(t) + t.length() + 1,
//				this.text.indexOf(".", this.text.indexOf(t)) + 1);
//		System.out.println(t + " " + txt);
//		Card.ability(this, _target, txt);
//	}

	void die() throws IOException {
		// Cards put to graveyard instantly, not in end of queue!
		effects.isDie = true;
		trueOwner.addCardToGraveyard(this);
		owner.owner.gameQueue.push(new GameQueue.QueueEvent("Die", this, 0));
	}

	// void sendAllAboutCreature() throws IOException{
	// //TODO All field!
	// String s="#AllAboutCreature(";
	// s+=this.owner.playerName+",";
	// s+=this.id+",";
	// s+=isTapped+",";
	// s+=isSummonedJust+",";
	// s+=damage+",";
	// s+=effects.additionalText+",";
	// s+=")";
	// owner.owner.server.sendMessage(s);
	// }

	void changeControll() throws IOException {
		if (!this.isDie()) {
			// add to opponent
			Creature tmp = new Creature(this);
			owner.owner.opponent.board.addExistCreatureToBoard(tmp, owner.owner.opponent.player);// without
																									// cry
			// It send AddCreatureToBoard
			tmp.owner = owner.owner.opponent.player;
			// System.out.println("True owner = "+tmp.trueOwner.playerName);
			// System.out.println("Owner = "+tmp.owner.playerName);
			tmp.effects = new Effects(tmp, this.effects);
			tmp.effects.battlecryPlayed = true;
			tmp.isSummonedJust = true;
			// Send message
			owner.owner.sendBoth("#ChangeControll(" + owner.playerName + "," + this.id + ")");
			tmp.effects.takeControlChange();
			// remove from my board
			removeCreatureFromPlayerBoard();
		}
	}

//	boolean isDie() {
//		if (effects.isDie)
//			return true;
//		return (getTougness() <= damage);// And other method to die!
//	}

	void returnToHand() throws IOException {
		owner.owner.sendBoth("#ReturnToHand(" + this.id + ")");
		// System.out.println("True owner to return = "+trueOwner.playerName);
		trueOwner.addCardToHand(this);
		removeCreatureFromPlayerBoard();
	}

	void removeCreatureFromPlayerBoard() {
		owner.creatures.remove(this);
	}
}
