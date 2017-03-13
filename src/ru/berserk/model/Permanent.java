package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import ru.berserk.model.Creature.DamageSource;
import ru.berserk.model.Player.Effects;

public class Permanent extends Card {
	Gamer ownerGamer;

	Permanent thisP;
	Creature isThisCreature;
	Player isThisPlayer;

	boolean isTapped = false;
	int damage;
	int shield = 0;
	int magicShield = 0;

	Effects eff;

	public class Effects {
		Permanent whis;
		String additionalText = "";
		int bonusToShoot = 0;
		int nightmare = 0;
		int shield = 0;
		int magicShield = 0;
		int iceShield = 0;
		ArrayList<TemporaryTextEffect> temporaryTextEffects = new ArrayList<>();

		class TemporaryTextEffect {
			String textEffect = "";
			int lenght = 0;

			TemporaryTextEffect(String ef, int l) {
				textEffect = ef;
				lenght = l;
			}
		}

		Effects(Permanent _pl) {
			whis = _pl;
		}

		void takeNightmare(int n) throws IOException {
			nightmare += n;
			if (nightmare < 0)
				nightmare = 0;
			ownerGamer.sendBoth(
					"#TakeEffect(" + id + "," + MyFunction.Effect.nightmare.getValue() + "," + nightmare + ")");
		}

		String getAdditionalText() {
			String tmp = additionalText;
			for (TemporaryTextEffect te : temporaryTextEffects) {
				tmp += te.textEffect + " ";
			}
			return tmp;
		}

		// TODO Universal function to take effect
		void takeBonusToShoot(int n) throws IOException {
			bonusToShoot += n;
			if (bonusToShoot < 0)
				bonusToShoot = 0;
			ownerGamer.sendBoth(
					"#TakeEffect(" + id + "," + MyFunction.Effect.bonusToShoot.getValue() + "," + bonusToShoot + ")");
		}
	}

	public Permanent(Gamer o, int cost, String name, String creatureType, int color, int type, int targetType,
			int tapTargetType, String text, int power, int hp) {
		// TODO Auto-generated constructor stub
		super(cost, name, creatureType, color, type, targetType, tapTargetType, text, power, hp);
		ownerGamer = o;
	}

	public void tap() throws IOException {
		ownerGamer.sendBoth("#Tap(" + id + ",1)");
		isTapped = true;
	}

	public void untap() throws IOException {
		if (isTapped) {
			ownerGamer.sendBoth("#Tap(" + id + ",0)");
			if (eff.nightmare != 0) {
				this.takeDamage(eff.nightmare, this, Creature.DamageSource.magic);
				eff.takeNightmare(-eff.nightmare);
			}
		}
		isTapped = false;
	}

	void takeDamage(int dmg, Card damageSrc, DamageSource dmgsrc) throws IOException {

		if (ownerGamer.player.effects.iceShield > 0) {
			dmg -= ownerGamer.player.effects.getIceShield();
			ownerGamer.player.effects.takeIceShield(-dmg);
			if (dmg < 0)
				dmg = 0;
		}

		if (dmgsrc == DamageSource.physic) {
			dmg -= getShield();
			if (dmg < 0)
				dmg = 0;
		}
		if (dmgsrc == DamageSource.magic) {
			dmg -= getMagicShield();
			if (dmg < 0)
				dmg = 0;
		}

		damage += dmg;

		if (dmg != 0)
			ownerGamer.sendBoth("#TakeDamage(" + id + "," + dmg + ")");

		if (getTougness() <= damage) {
			if (isThisCreature != null)
				isThisCreature.die();
			else
				isThisPlayer.loseGame();
		}
	}

	void attack(Permanent target) throws IOException {
		if (!getCanAttack()) {
			ownerGamer.printToView(0, "Повернутое/атаковавшее/т.д. существо не может атаковать.");
			return;
		}
		ownerGamer.sendBoth("#Attack(" + id + "," + target.id + ")");

		tap();
		if (canAnyoneBlock(target)) {
			ownerGamer.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.IChoiceBlocker);
			ownerGamer.opponent.creatureWhoAttack = id;
			ownerGamer.opponent.creatureWhoAttackTarget = target.id;
			ownerGamer.opponent.server.sendMessage("#ChoiceBlocker(" + id + "," + target.id + ")");
			ownerGamer.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceBlocker);
		} else {
			fight(target);
		}
	}

	void ability(int n, Permanent _target) throws IOException {
		String tapT = (n == 0) ? "2" : "";
		tapT += (_target == null) ? "ТАП:" : "ТАПТ:";
		String txt = "";
		txt = this.text.substring(this.text.indexOf(tapT) + tapT.length() + 2,
				this.text.indexOf(".", this.text.indexOf(tapT) + 1));
		System.out.println(tapT + " " + txt);
		tap();
		Card.ability(this, this, _target, txt);
	}
	
	void deathratle(Permanent _target) throws IOException {
		String t = (_target==null) ? "Гибель:" : "Гибельт:";

		String txt = this.text.substring(this.text.indexOf(t) + t.length() + 1,
				this.text.indexOf(".", this.text.indexOf(t)) + 1);
		System.out.println(t + " " + txt);
		Card.ability(this, this, _target, txt);
	}
	void battlecry(Permanent _target) throws IOException {
		String t = (_target==null) ? "Найм:" : "Наймт:";
		String txt = this.text.substring(this.text.indexOf(t) + t.length() + 1,
				this.text.indexOf(".", this.text.indexOf(t)) + 1);
		System.out.println(t + " " + txt);
		Card.ability(this, this, _target, txt);
	}
	
	void fight(Permanent second) throws IOException {
		second.takeDamage(this.getPower(), this, getPowerType());
		if (!second.isTapped) {
			if (second.getPower() > 0) {
				takeDamage(second.getPower(), second, second.getPowerType());
				if (second.isThisPlayer != null)
					second.isThisPlayer.equpiment[2].takeDamage(1);
			}
		}

		// Killing
		if (second.isDie() && !this.isDie())
			this.killing();
		if (!second.isDie() && this.isDie())
			second.killing();
	}

	// void fightCreature(Creature second) throws IOException {
	// DamageSource att = this.getPowerType();
	// DamageSource def = second.getPowerType();
	//
	// if (!second.isTapped) {// First is passive
	// if ((second.text.contains("Первый удар.")) &&
	// (!this.text.contains("Первый удар."))) {
	// if (this.damage < this.hp)
	// second.takeDamage(this.getPower(), this, att);
	// this.takeDamage(second.getPower(), second, def);
	// } else if ((this.text.contains("Первый удар.")) &&
	// (!second.text.contains("Первый удар."))) {
	// second.takeDamage(this.getPower(), this, att);
	// if (second.damage < second.hp)
	// this.takeDamage(second.getPower(), second, def);
	// } else {
	// second.takeDamage(this.getPower(), this, att);
	// this.takeDamage(second.getPower(), second, def);
	// }
	// } else {
	// owner.owner.printToView(0, this.name + " ударяет " + second.name + ".");
	// second.takeDamage(this.getPower(), this, att);
	// }
	// // Killing
	// if (second.isDie() && !this.isDie())
	// this.killing();
	// if (!second.isDie() && this.isDie())
	// second.killing();
	// }

	void killing() throws IOException {
		if (getText().contains("Убийство: ")) {
			String txt = this.text.substring(this.text.indexOf("Убийство: ") + "Убийство: ".length(),
					this.text.indexOf(".", this.text.indexOf("Убийство: ") + 1));
			Card.ability(this, this, this, txt);
		}
	}

	void heal(int dmg) throws IOException {
		damage -= dmg;
		if (damage < 0)
			damage = 0;
		ownerGamer.sendBoth("#TakeDamage(" + id + "," + (-dmg) + ")");
	}

	boolean getCanAttack() {
		if (isTapped)
			return false;
		if (isThisCreature != null && isThisCreature.getIsSummonedJust())
			return false;
		return !MyFunction.textNotInTake(getText()).contains("Не может атаковать");
	}

	String getText() {
		return text + "." + eff.getAdditionalText();
	}

	int getTougness() {
		return hp;
	}

	int getShield() {
		return shield;// + effects.getBonusShield();
	}

	int getMagicShield() {
		return magicShield;// + effects.getBonusShield();
	}

	boolean isDie() {
		// TODO And other method to die!
		// if (eff.isDie)
		// return true;
		return (getTougness() <= damage);
	}

	boolean getCanBlock() {
		if (isTapped)
			return false;
		return !MyFunction.textNotInTake(getText()).contains("Не может блокировать");
	}

	boolean getAgility() {
		if (MyFunction.isInOwnTextNotInTake(text, "Ловкость"))
			return true;
		if (eff.additionalText.contains("Ловкость"))
			return true;
		return false;
	}

	int getPower() {
		if (isThisCreature != null)
			return isThisCreature.power + isThisCreature.effects.getBonusPower();
		else {
			if (isThisPlayer.equpiment[2] != null) {
				return isThisPlayer.equpiment[2].getPower();
			} else
				return 0;
		}
	}

	DamageSource getPowerType() {
		if (text.contains("Магический урон.") || eff.additionalText.contains("Магический урон."))
			return DamageSource.magic;
		return DamageSource.physic;
	}

	private boolean canAnyoneBlock(Permanent target) {
		ArrayList<Permanent> crt = new ArrayList<>();
		if (target.ownerGamer.player.equpiment[0] != null)
			crt.add(target.ownerGamer.player);
		for (int i = 0; i < ownerGamer.opponent.player.creatures.size(); i++)
			crt.add(ownerGamer.opponent.player.creatures.get(i));

		ArrayList<Permanent> crtCopy = new ArrayList<>(crt);
		ListIterator<Permanent> temp = crtCopy.listIterator();

		while (temp.hasNext()) {
			Permanent tmp = temp.next();
			if (!tmp.getCanBlock()) {
				crt.remove(tmp);
			} else {
				if (this.getAgility() && !tmp.getAgility())
					crt.remove(tmp);
			}
		}
		if (crt.contains(target))
			crt.remove(target);

		if (crt.size() != 0)
			return true;

		return false;
	}

}
