package ru.berserk.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import ru.berserk.model.Creature.DamageSource;
import ru.berserk.model.MyFunction.PlayerStatus;
import ru.berserk.model.MyFunction.WhatAbility;

// Created by StudenetskiyA on 30.12.2016.

class Card {
	// Gamer owner;
	String id = "";
	int cost;
	String name;
	String text;
	String image;
	String creatureType;
	int color;// 1-swamp,2-field,3-mountain,4-forest,5-dark,6-neutral,7 and more
				// - multicolor
	int type;// 1 for spell, 2 for creature, 3 equpiment and event
	int targetType;// Battlecry 1 for creatures, 2 for heroes, 3 for heroes and
					// creatures, 4 for only opponent creature, 9 for my
					// creature or hero, 10 as 9, but not self
	int tapTargetType;// May exist cards with Battlecry and TAP. Today its only
						// one)))
	int power;// only for creature, ignore for other
	int hp;// only for creature and hero, its maximum health, not current
	String hash;// for suffling

	static Card simpleCard = new Card(0, "", "", 0, 0, 0, 0, "", 0, 0);

	public Card(Card _card) {
		// owner=_owner;
		name = _card.name;
		text = _card.text;
		cost = _card.cost;
		image = _card.image;
		color = _card.color;
		type = _card.type;
		power = _card.power;
		hp = _card.hp;
		id = _card.id;
		targetType = _card.targetType;
		tapTargetType = _card.tapTargetType;
		creatureType = _card.creatureType;
	}

	public Card(int _cost, String _name, String _crtype, int _color, int _type, int _targetType, int _tapTargetType,
			String _text, int _power, int _hp) {
		// board=_board;
		// owner=_owner;
		name = _name;
		text = _text;
		cost = _cost;
		image = _name + ".jpg";
		color = _color;
		type = _type;
		power = _power;
		hp = _hp;
		targetType = _targetType;
		tapTargetType = _tapTargetType;
		creatureType = _crtype;
	}

	public void playOnCreature(Player _pl, Creature creature) throws IOException {
		if (creature.text.contains("Если выбрана целью заклинание - погибает.")) {
			creature.die();
		} // delete else
		ability(_pl.owner, this, _pl, null, creature, null, text);
	}

	public void playOnPlayer(Player _pl, Player _player) throws IOException {
		ability(_pl.owner, this, _pl, null, null, _player, text);
	}

	void playNoTarget(Player _pl) throws IOException {
		ability(_pl.owner, this, _pl, null, null, null, this.text);
	}

	public static Card createCardWithID(Gamer gamer, String name){
		Card c = createCardByName(name);
		c.id = gamer.name + gamer.idCount;
		gamer.idCount++;
		return c;
	}
	
	public static Card createCardByName(String name) {
		// Here is all cards!
		switch (name) {
		case "Тарна":
			return new Card(0, "Тарна", "", 1, 0, 0, 0, "ТАП:4 Взять карт 1.", 0, 28);
		case "Рейвенкар":
			return new Card(0, name, "", 5, 0, 0, 0, "ТАП:4 Ранить героя противника на 2, Излечить вашего героя на 2.",
					0, 24);
		case "Бьорнбон":
			return new Card(0, name, "", 3, 0, 0, 0, "ТАП:0 Получить щит ББ.", 0, 30);
		case "Тиша":
			return new Card(0, "Тиша", "", 1, 0, 0, 1, "ТАПТ:2 Отравить+ выбранное существо на 1.", 0, 26);
		   case "Тейа":
               return new Card(0, name, "", 1, 0, 0, 3, "ТАПТ:2 Ранить персонажа на 1. 2ТАПТ:5 Ранить персонажа на 2.", 0, 26);
		   case "Илариэль":
               return new Card(0, name, "", 4, 0, 0, 3, "ТАПТ:2 Выстрел на 1. 2ТАПТ:6 Получить плюс к выстрелам на 1.", 0, 28);
		   case "Алирра":
               return new Card(0, name, "Герой-маг", 4, 0, 3, 3, "ТАПТ:1 Излечить персонажа на 1. 2ТАПТ:3 Излечить персонажа на 2.", 0, 32);
         case "Свирепый резак":
			return new Card(0, name, "", 2, 0, 0, 1, "ТАПТ:2 Выбранное существо получает 'Опыт в атаке и Рывок'.", 0,
					28);
		case "Эндор Флем":
			return new Card(0, name, "", 2, 0, 0, 7, "ТАПТ:3 Ранить выбранное существо на 1, Взять карт 1.", 0, 26);
		case "Руах":
			return new Card(0, name, "", 2, 0, 0, 1, "ТАПТ:1 Ранить на половину жизней выбранное существо.", 0, 25);
		case "Раскат грома":
			return new Card(1, "Раскат грома", "", 3, 1, 1, 0, "Ранить выбранное существо на 3.", 0, 0);
		case "Выброс силы":
			return new Card(2, name, "", 3, 1, 1, 0, "Ранить выбранное существо на 5.", 0, 0);
		case "Неудача":
			return new Card(1, name, "", 5, 1, 1, 0,
					"Ранить на остаток выбранное существо и своего героя на столько же.", 0, 0);
		case "Возрождение":
			return new Card(1, name, "", 5, 1, 0, 0, "Раскопать (2,0, ,0,0, ).", 0, 0);
		case "Гьерхор":
			return new Card(1, "Гьерхор", "Йордлинг", 3, 2, 0, 0, "", 2, 2);
		case "Алчущие крови":
			return new Card(2, name, "Слуа", 5, 2, 10, 0, "Направленный удар. Наймт: Жажда 1.", 3, 3);
		case "Змееуст":
			return new Card(1, name, "Слуа", 5, 2, 10, 0, "Защита от заклинаний. Наймт: Жажда 2.", 3, 2);
		case "Лики судьбы":
			return new Card(3, name, "Пустой", 6, 2, 0, 0, "Найм: Лики-абилка.", 2, 3);
		case "Найтин":
			return new Card(2, "Найтин", "", 6, 2, 0, 0, "Направленный удар. Рывок.", 2, 2);
		case "Кригторн":
			return new Card(2, "Кригторн", "", 3, 2, 0, 0, "Первый удар. Рывок.", 2, 1);
		case "Гном":
			return new Card(2, name, "Гном", 3, 2, 0, 0, "", 3, 3);
		case "Секретное существо":
			return new Card(2, name, "Гном", 3, 2, 0, 0, "Рывок", 3, 3);
		case "Гном-легионер":
			return new Card(4, name, "Гном", 3, 2, 0, 0, "Направленный удар. Рывок.", 3, 5);
		case "Гном-смертник":
			return new Card(3, name, "Гном", 3, 2, 0, 0, "Защита от заклинаний. Рывок.", 3, 4);
		case "Цепной пес":
			return new Card(1, name, "Зверь", 2, 2, 0, 0,
					"Орда. Статичный эффект. Получает за каждого другого Цепного пса рывок и +1 к удару.", 1, 2);
		case "Нгонасах":
			return new Card(2, name, "Орк", 2, 2, 13, 0,
					"Рывок. Наймт: Выбранное существо получает 'Направленный удар'.", 2, 2);
		case "Орк-мститель":
			return new Card(2, name, "Орк", 2, 2, 0, 0,
					"При гибели другого вашего существа: Поиск (0,0, ,0,0,Орк-мститель).", 3, 2);
		case "Мастер поединка":
			return new Card(6, name, "Орк", 2, 2, 1, 0, "Наймт: Уничтожьте по стоимости 2.", 6, 5);
		case "Раптор":
			return new Card(8, name, "Орк", 2, 2, 0, 0, "Найм: Уничтожьте каждое по стоимости 3.", 5, 9);
		case "Вождь клана":
			return new Card(5, name, "Орк", 2, 2, 0, 0, "Статичный эффект.", 5, 5);
		case "Ожившее пламя":
			return new Card(1, name, "", 2, 1, 1, 0, "Ранить выбранное существо на 1. Получает к атаке + 2.", 0, 0);
		case "Супергипноз":
			return new Card(1, name, "", 1, 1, 1, 0, "Взять под контроль выбранное существо.", 0, 0);
		case "Пылающий исполин":
			return new Card(6, name, "Орк", 2, 2, 0, 0, "Направленный удар. Статичный эффект.", 6, 6);
		case "Огонь прародителя":
			return new Card(7, name, "", 3, 1, 3, 0, "Ранить персонажа на 8.", 0, 0);
		case "Плетение огня":
			return new Card(3, name, "", 3, 1, 3, 0, "Ранить персонажа на 4.", 0, 0);
		case "Рубака клана":
			return new Card(3, name, "Орк", 2, 2, 0, 0, "Направленный удар.", 5, 3);
		case "Багатур":
			return new Card(6, name, "Орк", 2, 2, 0, 0, "Направленный удар. Гнев. Первый удар. Рывок.", 6, 2);
		case "Тарантул":
			return new Card(2, name, "Койар", 6, 2, 0, 0, "Защита от атак стоимость менее 3.", 4, 2);
		case "Огненный щит":
			return new Card(2, name, "", 2, 1, 1, 0,
					"Выбранное существо до конца следующего хода получает 'Не получает от ударов ран'. Взять карт 1.",
					0, 0);
		case "Орк-егерь":
			return new Card(3, name, "Орк", 2, 2, 1, 0, "Наймт: Выбор - открыть/закрыть существо.", 4, 2);
		case "Амбрадор":
			return new Card(1, name, "Зверь", 6, 2, 12, 0,
					"В начале вашего хода: Верните выбранное существо в руку его владельца.", 4, 3);
		case "Трюкач":
			return new Card(1, name, "", 6, 2, 0, 0, "", 4, 5);
		case "Поглощение души":
			return new Card(3, "Поглощение душ", "", 5, 1, 2, 0,
					"Ранить выбранного героя на 3. Излечить вашего героя на 3.", 0, 0);
		case "Эльф-дозорный":
			return new Card(4, "Эльф-дозорный", "", 4, 2, 0, 0, "Найм: Взять карт 1.", 2, 5);
		case "Послушник":
			return new Card(5, "Послушник", "Лингунг", 3, 2, 1, 0, "Наймт: Выстрел по существу на 4.", 2, 3);
		case "Гном-лучник":
			return new Card(3, name, "Гном", 3, 2, 3, 0, "Защита от выстрелов. Наймт: Выстрел на 2.", 2, 3);
		case "Лучник Захры":
			return new Card(4, "Лучник Захры", "Орк", 2, 2, 3, 0, "Защита от заклинаний. Наймт: Выстрел на 2.", 4, 2);
		case "Жрец клана":
			return new Card(2, name, "Орк", 2, 2, 0, 0, "Рывок.", 3, 2);
		case "Молодой орк":
			return new Card(1, name, "Орк", 2, 2, 0, 0, "", 3, 1);
		case "Орк-провокатор":
			return new Card(1, name, "Орк", 2, 2, 0, 0, "Рывок.", 2, 1);
		case "Цверг-заклинатель":
			return new Card(3, name, "Гном", 3, 2, 0, 0,
					"Защита от заклинаний. Защита от выстрелов. Защита от отравления.", 3, 3);
		case "Верцверг":
			return new Card(4, name, "Гном", 3, 2, 1, 0, "Направленный удар. Наймт: Получает к атаке + 3.", 2, 4);
		case "Цепная молния":
			return new Card(6, "Цепная молния", "", 3, 1, 0, 0, "Ранить каждое существо противника на 3.", 0, 0);
		case "Волна огня":// Fix it
			return new Card(3, "Волна огня", "", 2, 1, 0, 0, "Ранить каждое существо на 5.", 0, 0);
		case "Чешуя дракона":
			return new Card(2, "Чешуя дракона", "", 4, 1, 0, 0, "Получите * 1.", 0, 0);
		case "Выслеживание":
			return new Card(0, "Выслеживание", "", 4, 1, 0, 0, "Получите до конца хода * 2.", 0, 0);
		case "Фиал порчи":
			return new Card(2, "Фиал порчи", "", 1, 1, 1, 0, "Отравить выбранное существо на 2.", 0, 0);
		case "Глашатай пустоты":
			return new Card(1, "Глашатай пустоты", "Пустой", 6, 2, 0, 0, "Уникальность. Не получает ран.", 0, 1);
		case "Мастер теней":
			return new Card(1, name, "Наемник", 6, 2, 0, 0,
					"Найм: Посмотрите топдек противника, можете положить его на кладбище.", 2, 1);
		case "Велит":
			return new Card(2, "Велит", "", 2, 2, 0, 3, "ТАПТ: Выстрел на 1.", 1, 3);
		case "Пуф":
			return new Card(2, name, "Гном", 3, 2, 0, 3, "ТАПТ: Выстрел на 1.", 2, 3);
		case "Кьелэрн":
			return new Card(1, "Кьелэрн", "", 6, 2, 0, 0, "Уникальность. Рывок. ТАП: Получите до конца хода * 1.", 0,
					1);
		case "Агент Разана":
			return new Card(2, "Агент Разана", "", 1, 2, 4, 0, "Наймт: Отравить выбранное существо на 1.", 1, 2);
		case "Скованный еретик":
			return new Card(1, "Скованный еретик", "", 5, 2, 0, 0, "Найм: Закрыться.", 3, 2);
		case "Вэлла":
			return new Card(3, "Вэлла", "", 4, 2, 3, 0, "Наймт: Излечить выбранное существо или героя на 2.", 3, 4);
		case "Рыцарь Туллена":
			return new Card(6, "Рыцарь Туллена", "", 2, 2, 0, 0, "Броня 3.", 6, 3);
		case "Орк-лучник":
			return new Card(1, name, "", 2, 2, 3, 0, "Гнев. Наймт: Выстрел на 1.", 1, 1);
		case "Безумие":
			return new Card(3, name, "", 1, 1, 1, 0, "Нанести урон выбранному существу, равный его удару.", 0, 0);
		case "Зельеварение":
			return new Card(1, name, "", 1, 1, 1, 0, "Верните выбранное существо в руку его владельца.", 0, 0);
		case "Дахут":
			return new Card(3, name, "", 1, 2, 1, 0, "Наймт: Верните выбранное существо в руку его владельца.", 2, 3);
		case "Забира":
			return new Card(2, "Забира", "", 1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 3, 4);
		case "Волнорез":
			return new Card(3, name, "", 1, 2, 0, 0, "Если выбрана целью заклинание - погибает.", 4, 5);
		case "Десница Архааля":
			return new Card(4, name, "", 1, 2, 1, 0, "Опыт в защите. Наймт: Уничтожьте отравленное существо.", 1, 4);
		  case "Нойта":
              return new Card(1, name, "Линунг", 3, 2, 1, 0, "Наймт: Ранить существо без ран на 3.", 1, 1);
        	case "Орк-мародер":
			return new Card(5, name, "", 2, 2, 0, 0, "Опыт в атаке. Первый удар. Рывок.", 5, 2);
		case "Менгир Каррефура":
			return new Card(3, name, "", 1, 2, 0, 1, "ТАПТ: Отравить+ выбранное существо на 1.", 0, 10);
		case "Рыцарь реки":
			return new Card(5, name, "", 1, 2, 1, 0,
					"Наймт: Выбранное существо не может атаковать и выступать защитником до конца следующего хода.", 4,
					6);
		case "Поиск кладов":
			return new Card(6, name, "", 1, 1, 0, 0, "Взять карт 4.", 0, 0);
		case "Прозрение":
			return new Card(2, name, "", 1, 1, 0, 0,
					"Взять карт 1. Если у соперника больше существ, чем у вас, взять еще карт 1.", 0, 0);
		case "Плащ Исхара":
			return new Card(1, name, "Броня", 1, 3, 0, 0, "", 0, 6);
		case "Богарт":
			return new Card(4, name, "", 6, 2, 0, 0,
					"Уникальность. Найм: Каждое другое существо погибает в конце хода противника.", 2, 7);
		case "Полевица":
			return new Card(4, name, "", 1, 2, 0, 0, "Гибель: Взять карт 2.", 2, 3);
		case "Смайта":
			return new Card(4, name, "", 6, 2, 3, 0, "Гибельт: Ранить персонажа на 2.", 4, 3);
		case "Вестник смерти":
			return new Card(1, name, "Слуа", 5, 2, 99, 0, "Гибельт: Сбросьте карту.", 3, 3);
		case "Падальщик пустоши":// fix
			return new Card(1, name, "Зверь", 6, 2, 4, 0,
					"При гибели в ваш ход другого вашего существа: Ранить выбранное существо на 2.", 1, 2);
		case "Ядовитое пламя":
			return new Card(0, name, "", 1, 1, 1, 0, "Доплатите Х *. Ранить выбранное существо на ХХХ.", 0, 0);
		case "Вольный воитель":
			return new Card(0, name, "", 6, 2, 0, 0, "Доплатите Х *. Найм: Получает к характеристикам + ХХХ.", 0, 0);
		case "Шар тины":
			return new Card(2, name, "", 1, 1, 0, 0, "Поиск (0,1, ,0,0, ).", 0, 0);
		case "Шар бури":
			return new Card(2, name, "", 2, 1, 0, 0, "Поиск (0,2, ,0,0, ).", 0, 0);
		case "Карта сокровищ":
			return new Card(2, name, "", 6, 1, 0, 0, "Поиск (0,6, ,0,0, ).", 0, 0);
		case "Шар молний":
			return new Card(2, name, "", 3, 1, 0, 0, "Поиск (0,3, ,0,0, ).", 0, 0);
		case "Гном-кузнец":
			return new Card(3, name, "Гном", 1, 2, 0, 0, "Найм: Поиск (3,0, ,0,0, ).", 1, 4);
		case "Гном-кладоискатель":
			return new Card(5, name, "Гном", 3, 2, 0, 0, "Броня 1. Найм: Поиск (2,0,Гном,2,0, ).", 5, 4);
		case "Шаман племени ворона":
			return new Card(1, name, "Наемник", 6, 2, 0, 0, "Найм: Поиск (2,0, ,0,2, ).", 1, 1);
		case "Дух Эллиона":
			return new Card(1, name, "Дух", 6, 2, 0, 0, "Найм: Потеряйте * 1.", 3, 4);
		case "Рунопевец":
			return new Card(3, name, "Гном", 3, 2, 0, 0, "Статичный эффект.", 3, 3);
		case "Гном-каратель":
			return new Card(4, name, "Гном", 3, 2, 0, 3, "Броня 2. ТАПТ: Ранить выбранное существо или героя на 2.", 1,
					3);
		case "Тан гномов":
			return new Card(6, name, "Гном", 3, 2, 0, 0, "Броня 2. Статичный эффект.", 5, 4);
		case "Безумный охотник":
			return new Card(5, name, "", 6, 2, 0, 0,
					"Найм: Получает +Х к удару и Броню Х, где Х - число других ваших существ.", 4, 4);
		case "Браслет подчинения":
			return new Card(3, name, "Амулет", 1, 3, 0, 0, "", 0, 0);
		case "Молот прародителя":
			return new Card(2, name, "Оружие", 3, 3, 0, 1,
					"ТАПТ: Выбранное существо до конца хода получает к атаке + 2.", 0, 0);
		case "Орочий ятаган":
			return new Card(3, name, "Оружие", 2, 3, 0, 0, "Статичный эффект.", 0, 0);
		case "Аккения":
			return new Card(4, name, "Событие", 2, 4, 0, 0, "Статичный эффект.", 0, 0);
		case "Пустошь Тул-Багара": 
			return new Card(1, name, "Событие", 5, 4, 0, 0, "Статичный эффект.", 0, 0);
		case "Гипноз":
			return new Card(7, name, "", 1, 1, 0, 0, "Противник выбирает существо, оно переходит под ваш контроль.", 0,0);
		case "Дурные советы":
			return new Card(5, name, "", 1, 1, 0, 0, "Противник выбирает существо по стоимости, оно переходит под ваш контроль, стоимость не больше 3.", 0,0);
		case "Брат по оружию":
              return new Card(3, name, "Инквизитор", 6, 2, 21, 0, "Наймт: Уничтожить выбранную экипировку.", 3, 3);
		case "Сдерживающий":
               return new Card(3, name, "Драконид", 6, 2, 22, 0, "Наймт: Уничтожить выбранную экипировку.", 5, 5);
		case "Кутила":
              return new Card(10, name, "Пират", 6, 2, 0, 0, "Статичный эффект.", 6, 6);
	    case "Гневный орк":
            return new Card(2, name, "Орк", 2, 2, 0, 0, "Пока у вас есть другое существо, получает +2 к удару и опыт в атаке.", 1, 3);
	    case "Джаггернаут":
            return new Card(7, name, "Зверь", 2, 2, 0, 0, "В конце хода если не имеет ран, вернуть его в руку.", 6, 8);
	    case "Орк-арбалетчик":
               return new Card(5, name, "Орк", 2, 2, 3, 0, "Наймт: Выстрел на 1. Цель не обязательно. Повторить раз 5, без повторов.", 5, 3);
	    case "Солдат дроу":
            return new Card(1, name, "", 6, 2, 0, 0, "Ловкость.", 2, 1);
	    case "Воин дроу":
            return new Card(2, name, "", 6, 2, 0, 0, "Ловкость. Убийство: Стать активным.", 2, 1);
	    case "Удар дроу":
            return new Card(2, name, "", 6, 1, 3, 0, "Физический удар-убийство на 2.", 0, 0);
	    case "Гелрос":
            return new Card(0, name, "", 6, 0, 0, 3, "ТАПТ:3 Нанести физикой ран 1. Убийство: Стать активным.", 0, 24);
        default:
			System.out.println("Ошибка - Неопознанная карта:" + name);
			return null;
		}
	}

	static void ability(Gamer owner, Card _who, Player _whis, Equpiment _eq, String txt) throws IOException {
		if (txt.contains("Уничтожить выбранную экипировку.")) {
			_eq.owner.removeEqupiment(_eq);
		}
	}
	
	static void ability(Gamer owner, Card _who, Player _whis, Creature _whoCr, Creature _cr, Player _pl, String txt) throws IOException {
		// Super function! Do all what do cards text!
		// Which Card player(_who), who player(_whis), on what creature(_cr, may
		// null), on what player(_pl, may null), text to play(txt)
		if (txt.contains("Физический удар-убийство на ") ) {
			int dmg = MyFunction.getNumericAfterText(txt, "Физический удар-убийство на ");
			if (_cr != null) {
				_cr.takeDamage(dmg, _who, Creature.DamageSource.physic);
				if (_cr.isDie()) {
					owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
                    owner.activatedAbility.whatAbility = WhatAbility.spellAbility;
                    owner.activatedAbility.ableAbility=true;
                    //pause until player choice target.
                    owner.sendChoiceForSpell(3, 0, _who.name + " просит выбрать цель.");
                    System.out.println("pause");
                    synchronized (owner.cretureDiedMonitor) {
                        try {
                            owner.cretureDiedMonitor.wait();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                    System.out.println("resume");
                    owner.setPlayerGameStatus(MyFunction.PlayerStatus.MyTurn);
                    owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyTurn);
					ability(owner,_who,_whis,null, owner.choiceCreature, owner.choicePlayer, "Нанести физикой ран "+dmg);
				}
			} else {
				_pl.takeDamage(dmg,DamageSource.physic);
			}
		}
		if (txt.contains("Нанести магией ран ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Нанести магией ран ");
			if (_cr != null) {
				_cr.takeDamage(dmg, _who, Creature.DamageSource.magic);
				if (_cr.isDie()) {
					if (_whoCr!=null) _whoCr.killing();
					else _whis.killing();
				}
			} else {
				_pl.takeDamage(dmg,DamageSource.magic);
			}
		}
		if (txt.contains("Нанести физикой ран ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Нанести физикой ран ");
			if (_cr != null) {
				_cr.takeDamage(dmg, _who, Creature.DamageSource.physic);
				if (_cr.isDie()) {
					if (_whoCr!=null) _whoCr.killing();
					else _whis.killing();
				}
			} else {
				_pl.takeDamage(dmg,DamageSource.physic);
			}
		}
		if (txt.contains("Стать активным")) {
			
			if (_cr!=null)
			_cr.untapCreature();
			else 
				_whis.untap();
		}
		//OLD
		if (txt.contains("Закрыться.")) {// Only here - _cr=_who to get access
			_cr.tapCreature();
			owner.printToView(0, _cr.name + " закрывается.");
		}
		if (txt.contains("Посмотрите топдек противника, можете положить его на кладбище")) {
			owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceYesNo);
			owner.sendChoiceYesNo(_who.name + " предлагает сбросить топдек противника.",
					_whis.owner.opponent.player.deck.topDeck().name, "Сбросить", "Оставить");
			System.out.println("pause");
			synchronized (owner.yesNoChoiceMonitor) {
				try {
					owner.yesNoChoiceMonitor.wait();
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			System.out.println("resume");
			if (owner.choiceYesNo == 1) {
				_whis.owner.opponent.player.addCardToGraveyard(_whis.owner.opponent.player.deck.topDeck());
				_whis.owner.opponent.player.deck.removeTopDeck();
				owner.printToView(0,
						_who.name + " сбрасывает верхнюю карту с колоды " + _whis.owner.opponent.player.playerName);
				owner.opponent.printToView(0,
						_who.name + " сбрасывает верхнюю карту с колоды " + _whis.owner.opponent.player.playerName);
			}
		}
		if (txt.contains("Выбор - открыть/закрыть существо")) {
			owner.setPlayerGameStatus(MyFunction.PlayerStatus.choiceYesNo);
			owner.sendChoiceYesNo(_who.name + " предлагает открыть или закрыть:", _cr.name, "Открыть", "Закрыть");
			System.out.println("pause");
			synchronized (owner.yesNoChoiceMonitor) {
				try {
					owner.yesNoChoiceMonitor.wait();
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			System.out.println("resume choice yes");
			if (owner.choiceYesNo == 1) {
				_cr.untapCreature();
				owner.printToView(0, _who.name + " открывает " + _cr.name);
				owner.opponent.printToView(0, _who.name + " открывает " + _cr.name);
			} else {
				_cr.tapCreature();
				owner.printToView(0, _who.name + " закрывает " + _cr.name);
				owner.opponent.printToView(0, _who.name + " закрывает " + _cr.name);
			}
		}
		if (txt.contains("Получить щит ББ.")) {
			_whis.effect.takeBBShield(true);
		}
		if (txt.contains("Получить плюс к выстрелам на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Получить плюс к выстрелам на ");
			_whis.effect.takeBonusToShoot(true, dmg);
		}
		if (txt.contains("Лики-абилка.")) {// Only for player, who called it.
			if (_whis.playerName.equals(owner.name)) {
				ArrayList<Card> a = new ArrayList<>();
				if (owner.player.deck.topDeck() != null)
					a.add(owner.player.deck.topDeck());
				if (owner.player.deck.topDeck(2) != null)
					a.add(owner.player.deck.topDeck(2));
				if (owner.player.deck.topDeck(3) != null)
					a.add(owner.player.deck.topDeck(3));

				for (Card c : a) {
					owner.printToView(0, "Лики показывают " + c.name);

					if (c.cost <= 1 && c.type == 2) {
						Creature cr = new Creature(c, owner.player);
				
						owner.board.addCreatureToBoard(cr, owner.player);
						owner.gameQueue.push(new GameQueue.QueueEvent("Summon", cr, 0));
						owner.player.deck.cards.remove(c);
					}
				}
				owner.player.deck.suffleDeck(owner.sufflingConst);
				// main.repaint();
			} else {
				ArrayList<Card> a = new ArrayList<>();
				if (owner.player.deck.topDeck() != null)
					a.add(owner.player.deck.topDeck());
				if (owner.player.deck.topDeck(2) != null)
					a.add(owner.player.deck.topDeck(2));
				if (owner.player.deck.topDeck(3) != null)
					a.add(owner.player.deck.topDeck(3));
				for (Card c : a) {
					if (c.cost <= 1 && c.type == 2) {
						Creature cr = new Creature(c, owner.player);
						owner.board.addCreatureToBoard(cr, owner.player);
						owner.gameQueue.push(new GameQueue.QueueEvent("Summon", cr, 0));
						owner.player.deck.cards.remove(c);
						owner.printToView(0, "Лики вызывают " + c.name);
					}
				}
				owner.player.deck.suffleDeck(owner.sufflingConst);
			}
		}
		if (txt.contains("Уничтожьте по стоимости ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Уничтожьте по стоимости ");
			if (_cr.getCost(_cr.owner) <= dmg) {
				owner.printToView(0, _who.name + " уничтожает " + _cr.name + ".");
				_cr.die();
			}
		}
		if (txt.contains("Уничтожьте каждое по стоимости ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Уничтожьте каждое по стоимости ");
			ListIterator<Creature> temp = _whis.owner.opponent.player.creatures.listIterator();
			while (temp.hasNext()) {
				Creature tmp = temp.next();
				if (tmp.getCost(tmp.owner) <= dmg) {
					owner.printToView(0, _who.name + " уничтожает " + tmp.name + ".");
					tmp.die();
				}
			}
			ListIterator<Creature> temp2 = _whis.creatures.listIterator();
			while (temp2.hasNext()) {
				Creature tmp = temp2.next();
				if (tmp.getCost(tmp.owner) <= dmg) {
					owner.printToView(0, _who.name + " уничтожает " + tmp.name + ".");
					tmp.die();
				}
			}
		}
		if (txt.contains("Поиск (")) {// Поиск (type,color,creatureType,cost,costEx,name)
			if (_whis.playerName.equals(owner.name)) {
				ArrayList<String> parameter = MyFunction.getTextBetween(txt);
				owner.choiceXtype = Integer.parseInt(parameter.get(0));
				owner.choiceXcolor = Integer.parseInt(parameter.get(1));
				owner.choiceXcreatureType = parameter.get(2);
				owner.choiceXcost = Integer.parseInt(parameter.get(3));
				owner.choiceXcostExactly = Integer.parseInt(parameter.get(4));
				owner.choiceXname = parameter.get(5);
				owner.setPlayerGameStatus(MyFunction.PlayerStatus.searchX);
				owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
				owner.sendChoiceSearch(false, _whis.name + " ищет в колоде.");
				owner.server.sendMessage("You search "+owner.choiceXtype+","+owner.choiceXcolor+","+owner.choiceXcreatureType+","+owner.choiceXcost+","+owner.choiceXcostExactly+","+owner.choiceXname);
				System.out.println("pause");
				synchronized (owner.cretureDiedMonitor) {
					try {
						owner.cretureDiedMonitor.wait();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
				System.out.println("resume");
			}
		} else if (txt.contains("Раскопать ("))	{// Only for player, who called it.
			if (_whis.playerName.equals(owner.player.playerName)) {
				ArrayList<String> parameter = MyFunction.getTextBetween(txt);
				owner.choiceXtype = Integer.parseInt(parameter.get(0));
				owner.choiceXcolor = Integer.parseInt(parameter.get(1));
				owner.choiceXcreatureType = parameter.get(2);
				owner.choiceXcost = Integer.parseInt(parameter.get(3));
				owner.choiceXcostExactly = Integer.parseInt(parameter.get(4));
				owner.choiceXname = parameter.get(5);
				owner.setPlayerGameStatus(MyFunction.PlayerStatus.digX);
				owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
				owner.sendChoiceSearch(true, _whis.name + " ищет на кладбище.");
				System.out.println("pause");
				synchronized (owner.cretureDiedMonitor) {
					try {
						owner.cretureDiedMonitor.wait();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
				System.out.println("resume");
			}
		}
		if (txt.contains("Получает к характеристикам + "))	{
			int dmg = MyFunction.getNumericAfterText(txt, "Получает к характеристикам + ");
			_cr.effects.takeBonusTougness(dmg);
			_cr.effects.takeBonusPower(dmg);
		}
		if (txt.contains("Выбранное существо до конца хода получает к атаке + ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Выбранное существо до конца хода получает к атаке + ");
			_cr.effects.takeBonusPowerUEOT(dmg);
		}
		if (txt.contains("Получает к броне + ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Получает к броне + ");
			_cr.effects.takeBonusArmor(dmg);
		}
		if (txt.contains("Получает +Х к удару и Броню Х, где Х - число других ваших существ."))	{
			int dmg = _cr.owner.creatures.size() - 1;
			if (dmg > 0) {
				_cr.effects.takeBonusPower(dmg);
				_cr.effects.takeBonusArmor(dmg);
			}
		}
		if (txt.contains("Получает к атаке + "))

		{
			int dmg = MyFunction.getNumericAfterText(txt, "Получает к атаке + ");
			_cr.effects.takeBonusPower(dmg);
		}
		if (txt.contains("Излечить персонажа на "))	{
			int dmg = MyFunction.getNumericAfterText(txt, "Излечить персонажа на ");
			if (_cr != null) {
				_cr.heal(dmg);
			} else {
				_pl.heal(dmg);
			}
		}
		if (txt.contains("Ранить на половину жизней выбранное существо")) {
			if (_cr != null) {
				int dmg = (_cr.getTougness() - _cr.damage) / 2;
				owner.printToView(0, _who.name + " ранит " + _cr.name + " на " + dmg + ".");
				_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
			}
		}
		if (txt.contains("Ранить персонажа на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить персонажа на ");
			if (_cr != null) {
				owner.printToView(0, _who.name + " ранит " + _cr.name + " на " + dmg + ".");
				_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
			} else {
				owner.printToView(0, _who.name + " ранит " + _pl.name + " на " + dmg + ".");
				_pl.takeDamage(dmg,DamageSource.magic);
			}
		}
		if (txt.contains("Жажда ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Жажда ");
			if (_cr != null) {
				owner.printToView(0, _who.name + " жаждит " + _cr.name + " на " + dmg + ".");
				_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
			} else {
				owner.printToView(0, _who.name + " жаждит " + _pl.name + " на " + dmg + ".");
				_pl.takeDamage(dmg,DamageSource.magic);
			}
		}
		if (txt.contains("Ранить выбранного героя на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить выбранного героя на ");
			owner.printToView(0, _pl.playerName + " получил " + dmg + " урона.");
			_pl.takeDamage(dmg,DamageSource.magic);

		}
		if (txt.contains("Ранить героя противника на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить героя противника на ");
			owner.printToView(0, _whis.owner.opponent.player.playerName + " получил " + dmg + " урона.");
			_whis.owner.opponent.player.takeDamage(dmg,DamageSource.magic);

		}
//		if (txt.contains("Уничтожьте отравленное существо.")) {
//			if (_cr.effects.poison > 0) {
//				owner.printToView(0, _who.name + " уничтожает " + _cr.name + ".");
//				_cr.die();
//			}
//		}
		if (txt.contains("Ранить существо без ран на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить существо без ран на ");
			if (_cr != null && _cr.damage == 0) {
				_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
			}
		}
		if (txt.contains("Ранить выбранное существо на "))	{
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить выбранное существо на ");
			_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
		}
		if (txt.contains("Взять под контроль выбранное существо")) {
			if (_cr.owner != _who)
				_cr.changeControll();
		}
		if (txt.contains("Ранить на остаток выбранное существо и своего героя на столько же")) {
			int dmg = _cr.getTougness() - _cr.damage;
			_cr.takeDamage(dmg, _who, Creature.DamageSource.ability);
			_whis.takeDamage(dmg,DamageSource.magic);
		}
		if (txt.contains("Выбранное существо не может атаковать и выступать защитником до конца следующего хода."))	{
			_cr.effects.takeTemporaryAdditionalText("Не может атаковать. Не может блокировать.", 2);
		}
		if (txt.contains("Нанести урон выбранному существу, равный его удару.")) {
			int dmg = _cr.getPower();
			_cr.takeDamage(dmg, _who, Creature.DamageSource.spell);
		}
		if (txt.contains("Выбранное существо получает '")) {
			String s = MyFunction.getTextBetweenSymbol(txt, "Выбранное существо получает '", "'");
			_cr.effects.takeAdditionalText(s);
		}
		if (txt.contains("Выбранное существо до конца следующего хода получает '")) {
			String s = MyFunction.getTextBetweenSymbol(txt, "Выбранное существо до конца следующего хода получает '",
					"'");
			_cr.effects.takeTemporaryAdditionalText(s, 2);
		}
		if (txt.contains("Верните выбранное существо в руку его владельца.")) {
			_cr.returnToHand();
		}
		if (txt.contains("Противник выбирает существо, оно переходит под ваш контроль.")) {
			int c = _whis.owner.opponent.player.creatures.size();
			// Magic protect not worked when opponent choice self creature.
			if (c == 0) {
				owner.printToView(0, "Целей для " + _who.name + " нет.");
			} else if (c == 1) {
				_whis.owner.opponent.player.creatures.get(0).changeControll();
			} else {
				// Send choiceTarget
				owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
				owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
				// pause until player choice target.
				owner.opponent.sendChoiceForSpell(7, 0, _who.name + " просит выбрать цель.");
				System.out.println("pause");
				synchronized (owner.opponent.yesNoChoiceMonitor) {
					try {
						owner.opponent.yesNoChoiceMonitor.wait();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
				System.out.println("resume");
				_whis.owner.opponent.choiceCreature.changeControll();
				owner.opponent.setPlayerGameStatus(PlayerStatus.EnemyTurn);
				owner.setPlayerGameStatus(PlayerStatus.MyTurn);
			}
		}
		if (txt.contains("Противник выбирает существо по стоимости, оно переходит под ваш контроль, стоимость не больше ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Противник выбирает существо по стоимости, оно переходит под ваш контроль, стоимость не больше ");
			int c = 0;
			for (int i=0;i<_whis.owner.opponent.player.creatures.size();i++){
				if (_whis.owner.opponent.player.creatures.get(i).getCost(_whis)<=dmg) c++;
			}
			// Magic protect not worked when opponent choice self creature.
			if (c == 0) {
				owner.printToView(0, "Целей для " + _who.name + " нет.");
			} else if (c == 1) {
				_whis.owner.opponent.player.creatures.get(0).changeControll();
			} else {
				// Send choiceTarget
				owner.setPlayerGameStatus(MyFunction.PlayerStatus.EnemyChoiceTarget);
				owner.opponent.setPlayerGameStatus(MyFunction.PlayerStatus.choiceTarget);
				// pause until player choice target.
				owner.opponent.sendChoiceForSpell(7, dmg, _who.name + " просит выбрать цель.");
				System.out.println("pause");
				synchronized (owner.opponent.yesNoChoiceMonitor) {
					try {
						owner.opponent.yesNoChoiceMonitor.wait();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}
				System.out.println("resume");
				_whis.owner.opponent.choiceCreature.changeControll();
				owner.opponent.setPlayerGameStatus(PlayerStatus.EnemyTurn);
				owner.setPlayerGameStatus(PlayerStatus.MyTurn);
			}
		}
//		if (txt.contains("Отравить+ выбранное существо на ")) {
//			int dmg = MyFunction.getNumericAfterText(txt, "Отравить+ выбранное существо на ");
//			if (_cr.effects.poison != 0) {
//				_cr.effects.takePoison(_cr.effects.poison + dmg);
//			} else {
//				if (_cr.effects.poison <= dmg)
//					_cr.effects.takePoison(dmg);
//			}
//		}
//		if (txt.contains("Отравить выбранное существо на ")) {
//			int dmg = MyFunction.getNumericAfterText(txt, "Отравить выбранное существо на ");
//			_cr.effects.takePoison(dmg);
//		}
		if (txt.contains(("Излечить вашего героя на "))) {
			int dmg = MyFunction.getNumericAfterText(txt, "Излечить вашего героя на ");
			_whis.heal(dmg);
			//owner.printToView(0, _whis.playerName + " излечил " + dmg + " урона.");
		}
		if (txt.contains(("Получите * "))) {
			int dmg = MyFunction.getNumericAfterText(txt, "Получите * ");
			_whis.untappedCoin += dmg;
			_whis.totalCoin += dmg;
			//owner.printToView(0, _whis.playerName + " получил " + dmg + " монет.");
		}
		if (txt.contains(("Потеряйте * ")))	{
			int dmg = MyFunction.getNumericAfterText(txt, "Потеряйте * ");
			_whis.totalCoin -= dmg;
			int tmp = dmg;
			dmg -= _whis.temporaryCoin;
			_whis.temporaryCoin -= tmp;
			if (dmg < 0)
				dmg = 0;
			if (_whis.temporaryCoin < 0)
				_whis.temporaryCoin = 0;
			
			if (_whis.untappedCoin > _whis.totalCoin)
				_whis.untappedCoin = _whis.totalCoin;
			owner.printToView(0, _whis.playerName + " потерял " + dmg + " монет.");
		}
		if (txt.contains(("Получите до конца хода * ")))

		{
			int dmg = MyFunction.getNumericAfterText(txt, "Получите до конца хода * ");
			_whis.untappedCoin += dmg;
			_whis.totalCoin += dmg;
			_whis.temporaryCoin += dmg;
			owner.printToView(0, _whis.playerName + " получил " + dmg + " монет до конца хода.");
		}
		if (txt.contains(("Ранить каждое существо противника на "))) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить каждое существо противника на ");

			ListIterator<Creature> temp = _whis.owner.opponent.player.creatures.listIterator();
			while (temp.hasNext()) {
				Creature tmp = temp.next();
				tmp.takeDamage(dmg, _who, Creature.DamageSource.ability);
			}
		}
		if (txt.contains(("Ранить каждое существо на "))) {
			int dmg = MyFunction.getNumericAfterText(txt, "Ранить каждое существо на ");

			ListIterator<Creature> temp = _whis.owner.opponent.player.creatures.listIterator();
			while (temp.hasNext()) {
				Creature tmp = temp.next();
				tmp.takeDamage(dmg, _who, Creature.DamageSource.ability);
			}
			ListIterator<Creature> temp2 = _whis.creatures.listIterator();
			while (temp2.hasNext()) {
				Creature tmp = temp2.next();
				tmp.takeDamage(dmg, _who, Creature.DamageSource.ability);
			}
		}
		if (txt.contains(("Каждое другое существо погибает в конце хода противника."))) {
			// TODO Fix it with deathrattle
			for (int i = _whis.owner.opponent.player.creatures.size() - 1; i >= 0; i--) {
				_whis.owner.opponent.player.creatures.get(i).effects.takeTurnToDie(2);
			}
			for (int i = _whis.creatures.size() - 1; i >= 0; i--) {
				if (!_whis.creatures.get(i).name.equals("Богарт"))
					_whis.creatures.get(i).effects.takeTurnToDie(2);
			}
			owner.printToView(0, _who.name + " чумит весь стол!");
		}
		if (txt.contains("Взять карт ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Взять карт ");
			owner.printToView(0, _who.name + " берет " + dmg + " карт.");
			for (int i = 0; i < dmg; i++)
				_whis.drawCard();
		}
		if (txt.contains("Если у соперника больше существ, чем у вас, взять еще карт ")) {
			int dmg = MyFunction.getNumericAfterText(txt,
					"Если у соперника больше существ, чем у вас, взять еще карт ");
			int n1 = _whis.creatures.size();
			int n2 = _whis.owner.opponent.player.creatures.size();
			if (n1 < n2) {
				owner.printToView(0, _who.name + " берет " + dmg + " карт.");
				for (int i = 0; i < dmg; i++)
					_whis.drawCard();
			}
		}
		if (txt.contains("Выстрел по существу на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Выстрел по существу на ");
			if (_whoCr!= null) {dmg+=_whoCr.effects.getBonusToShoot();}
			else {dmg+=_whis.effect.getBonusToShoot();}
			
			owner.printToView(0, _who.name + " стреляет на " + dmg + " по " + _cr.name);
			_cr.takeDamage(dmg, _who, Creature.DamageSource.shoot, _who.haveRage());
		}
		if (txt.contains("Выстрел на ")) {
			int dmg = MyFunction.getNumericAfterText(txt, "Выстрел на ");
			if (_whoCr!= null) {dmg+=_whoCr.effects.getBonusToShoot();}
			else {dmg+=_whis.effect.getBonusToShoot();}
			
			if (_cr != null) {
				owner.printToView(0, _who.name + " стреляет на " + dmg + " по " + _cr.name);
				_cr.takeDamage(dmg, _who, Creature.DamageSource.shoot, _who.haveRage());

			} else {
				_pl.effect.takeBBShield(false);
				owner.printToView(0, _who.name + " стреляет на " + dmg + " по " + _pl.name);
				_pl.takeDamage(dmg,DamageSource.magic);
			}
		}
	}

	boolean haveRage() {
		return (text.contains("Гнев."));
	}

	int getCost(Player pl) {
		int effectiveCost = cost;
		// Gnome cost less
		if (creatureType.equals("Гном")) {
			int runopevecFounded = 0;
			for (int i = 0; i < pl.creatures.size(); i++) {
				if (pl.creatures.get(i).name.equals("Рунопевец"))
					runopevecFounded++;
			}
			effectiveCost -= runopevecFounded;
		}

		if (name.equals("Трюкач")) {
			effectiveCost += pl.cardInHand.size() - 1;
		}
		if (name.equals("Кутила")) {
            effectiveCost -= pl.cardInHand.size();
        }
        
		return effectiveCost;
	}
}
