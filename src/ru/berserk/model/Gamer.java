package ru.berserk.model;

import static ru.berserk.model.Main.CLIENT_VERSION;
import static ru.berserk.model.Main.COIN_START;
import static ru.berserk.model.Main.randomNum;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.DatatypeConverter;

import ru.berserk.model.MyFunction.ActivatedAbility;
import ru.berserk.model.ServerEndpointDemo;

public class Gamer {
	Board board = new Board();
	String name;
	int idCount = 0;
	Player player = new Player(this, "", "", 0);
	GameQueue gameQueue = new GameQueue(this);
	int creatureWhoAttack;
	int creatureWhoAttackTarget;
	public final Object cretureDiedMonitor = new Object();
	public final Object monitor = new Object();
	public final Object yesNoChoiceMonitor = new Object();
	int sufflingConst;
	MyFunction.PlayerStatus status;
	MyFunction.PlayerStatus memPlayerStatus;
	Gamer opponent;
	String deckName;
	ArrayList<String> deckList = new ArrayList<>();
	boolean endMuligan = false;
	boolean ready = true;
	//For search
	int choiceXcolor = 0;
	int choiceXtype = 0;
	String choiceXcreatureType = "";
	int choiceXcost = 0;
	int choiceXcostExactly = 0;
	int choiceYesNo = 0;
	String choiceXname;
    //For spell what aks to choice creature
	Creature choiceCreature;
	ServerEndpointDemo server;

	Gamer(ServerEndpointDemo server) {
		this.server = server;
	}

	void printToView(int n, String txt) throws IOException {
		server.sendMessage("#Message(" + n + "," + txt + ")");
		System.out.println(txt);
	}

	void printToView(int n, Color c, String txt) throws IOException {// Depricated
		server.sendMessage("#Message(" + n + "," + txt + ")");
		System.out.println(txt);
	}

	void setPlayerGameStatus(MyFunction.PlayerStatus _status) throws IOException {
		status = _status;
		server.sendMessage("#PlayerStatus(" + status.getValue() + ")");
	}

	void sendBoth(String message) throws IOException {
		System.out.println("Send both:" + message);
		server.sendMessage(message);
		opponent.server.sendMessage(message);
	}

	boolean isFirstPlayer(String name1, String name2) {
		byte[] b = (name1 + randomNum).getBytes();
		byte[] b2 = (name2 + randomNum).getBytes();
		try {
			byte[] hash = MessageDigest.getInstance("MD5").digest(b);
			String a = DatatypeConverter.printHexBinary(hash);
			byte[] hash2 = MessageDigest.getInstance("MD5").digest(b2);
			String a2 = DatatypeConverter.printHexBinary(hash2);
			return a.compareTo(a2) >= 0;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Deck getDeckList(String[] commands) throws IOException {
		ArrayList<Card> result = new ArrayList<>();
		int i = 1;
		while (!commands[i].equals("$ENDDECK")) {
		 System.out.println("Card = "+commands[i]);
			Card tmp = new Card(Card.createCardWithID(this,commands[i]));
			result.add(tmp);
			i++;
		}
		Deck r = new Deck(result); 
		return r;
	}

	public void removePlayer() throws IOException {
		System.out.println("Player " + name + " disconnected.");
		Main.removeFreePlayer(this);
		Main.removeName(this.name);
		this.server.disconnect();
	}

	private void addNewPlayer(String[] commands, ArrayList<String> parameter) throws IOException {
		name = parameter.get(0);
		System.out.println(name + " connected.");

		boolean nameCorrect = false;
		// TODO synchronized
		if (!Main.containsName(name)) {
			Main.addName(name);
			nameCorrect = true;
		} else {
			System.out.println("Name already exist.");
			// Other name?
		}
		deckName = parameter.get(1);
		Deck tmp  = getDeckList(commands);
		player = new Player(this, tmp.cards.get(0), name);
		player.deck=tmp;
		player.creatures = new ArrayList<>(2);
		
		System.out.println("Card ID= "+player.deck.cards.get(3).id );
		
		if (nameCorrect){
			server.sendMessage("Server version is "+Main.SERVER_VERSION);
		server.sendMessage("Hello, " + name + ", you going to play " + deckName + " deck.");
		server.sendMessage("Waiting for opponent to connect");
		// Const for shuffle
		server.sendMessage("$YOUAREOK(" + Main.randomNum + ")");
		Main.addFreePlayer(this);

		boolean pairFounded = Main.findFreePlayerFor(this);

		if (pairFounded) {
			server.sendMessage("wait");
			server.sendMessage("ok");

			// Get shuffled deck and send to opponent
			startGame();
			opponent.startGame();
		}
		}

	}

	private void startGame() throws IOException {
		// Begin game
		player.deck.cards.remove(0);// Remove hero from deck
		Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);// reroll
																			// for
																			// next
		player.deck.suffleDeck(Main.randomNum);
		Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);// reroll
																			// for
																			// next
		player.drawCard();
		player.drawCard();
		player.drawCard();
		player.drawCard();

		player.untappedCoin = Main.COIN_START;
		player.totalCoin = Main.COIN_START;
		status = MyFunction.PlayerStatus.MuliganPhase;
		sendStatus();
	}

	public void run(String command) throws IOException {
		try {
			String[] commands = command.split("\n");
			if (commands[0].contains("$IAM")) {
				ArrayList<String> parameter = getTextBetween(commands[0]);
				String ver = parameter.get(2);
				if (ver.equals(CLIENT_VERSION)) {
					this.addNewPlayer(commands, parameter);
				} else {
					server.sendMessage("Your client version is depricated! Update it.");
					server.sendMessage("$YOUARENOTOK(" + "Your client version is depricated! Update it." + ")");
					// Do something with it!
				}
				return;
			}

			// Repeatedly get commands from the client and process them.
			System.out.println(name + ":" + command);
			if (command.contains("$MULLIGANEND")) {//0,1,0,1
				endMuligan = true;
				int nc=0;
				ArrayList<String> parameter = MyFunction.getTextBetween(command);
				status = MyFunction.PlayerStatus.waitingMulligan;
				for (int i=3;i>=0;i--){
					if (Integer.parseInt(parameter.get(i))==1){
						nc++;
						player.deck.putOnBottomDeck(player.cardInHand.get(i));
						player.removeCardFromHand(player.cardInHand.get(i));
						//It may be not good. You may remove card with same name, but other position.
					}
				}
				
				for (int i = 0; i < nc; i++)
					player.drawCard();
				
				sendStatus();
				if (opponent.endMuligan) {
					// START
					System.out.println("Game for " + name + " and " + opponent.name + " started.");
					// Choice, who first. Today at random
					if (isFirstPlayer(name, opponent.name)) {
						status = MyFunction.PlayerStatus.MyTurn;
						opponent.status = MyFunction.PlayerStatus.EnemyTurn;
						player.setNumberPlayer(0);
						opponent.player.setNumberPlayer(1);
						player.newTurn();
					} else {
						status = MyFunction.PlayerStatus.EnemyTurn;
						opponent.status = MyFunction.PlayerStatus.MyTurn;
						player.setNumberPlayer(1);
						opponent.player.setNumberPlayer(0);
						opponent.player.newTurn();
					}
					sendStatus();
					opponent.sendStatus();
					Main.randomNum = ThreadLocalRandom.current().nextInt(100, 999 + 1);// reroll
																						// for
																						// next
				}
			} else {
				ResponseClientMessage responseClientMessage = new ResponseClientMessage(this, command);
				responseClientMessage.start();
			}

		} catch (IOException e) {
			System.out.println("Player disconnected: " + name);
			// Reconnect?
		} finally {
			// System.out.println("Finaly " + name);
			// Main.freePlayer.remove(this);
			// if (opponent != null) opponent.server.sendMessage("$DISCONNECT");
			// // This client is going down! Remove it.
			// if (name != null) {
			// Main.names.remove(name);
			// }
			// if (output != null) {
			// Main.writers.remove(output);
			// }
			// try {
			// socket.close();
			// } catch (IOException e) {
			// }
		}

	}

	void sendChoiceSearch(boolean dig, String message) throws IOException {
		// #ChoiceSearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message).
		System.out.println("Sending choice search to " + player.playerName);
		String s;
		if (!dig)
			s = "#ChoiceSearchInDeck(";
		else
			s = "#ChoiceSearchInGraveyard(";
		s += player.playerName + ",";
		s += choiceXtype + ",";
		s += choiceXcolor + ",";
		s += choiceXcreatureType + ",";
		s += choiceXcost + ",";
		s += choiceXcostExactly + ",";
		s += choiceXname + ",";
		s += message + ")";
		server.sendMessage(s);
	}

	void sendChoiceTarget(String message) throws IOException {
		System.out.println("Sending choice target to " + player.playerName + ", whatAbility= "
				+ MyFunction.ActivatedAbility.whatAbility.getValue());
		String s = "#ChoiceTarget(";
		s += player.playerName + ",";
		s += status.getValue() + ",";
		s += player.creatures.indexOf(MyFunction.ActivatedAbility.creature) + ",";
		s += MyFunction.ActivatedAbility.whatAbility.getValue() + ",";
		s += message + ",";
		String tmp = (MyFunction.ActivatedAbility.ableAbility) ? "1":"0";
		s += tmp+")";
		server.sendMessage(s);
	}

	void sendChoiceYesNo(String message, String card, String yes, String no) {
		// #ChoiceSearchInDeck(PlayerName,CardType,CardColor,CreatureType,CardCost,CardCostExactly,Message).
		System.out.println("Sending choice Yes/no to " + player.playerName);
		String s;
		s = "#ChoiceYesNo(";
		s += player.playerName + ",";
		s += card + ",";
		s += message + ",";
		s += yes + ",";
		s += no + ")";
		try {
			server.sendMessage(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void sendChoiceForSpell(int targetType, int cost, String message) {
		// #ChoiceForSpell(PlayerName,Status,TargetType,costN-,message)
		System.out.println("Sending choice for spell to " + player.playerName);
		String s = "#ChoiceForSpell(";
		s += player.playerName + ",";
		s += status.getValue() + ",";
		s += targetType + ",";
		s += cost + ",";
		s += message + ")";
		try {
			server.sendMessage(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void sendUntapAll() throws IOException {
		server.sendMessage("#UntapAll(" + this.name + ")");
		opponent.server.sendMessage("#UntapAll(" + this.name + ")");
	}

	void sendStatus() throws IOException {
		System.out.println("Sending status to " + player.playerName + ", status= " + status.getValue());
		String s = "#TotalStatusPlayer(";
		s += player.playerName + ",";
		s += status.getValue() + ",";
		s += player.damage + ",";
		s += player.untappedCoin + ",";
		s += player.totalCoin + ",";
		s += player.temporaryCoin + ",";
		s += player.owner.opponent.player.untappedCoin + ",";
		s += player.owner.opponent.player.totalCoin + ",";
		s += player.owner.opponent.player.temporaryCoin + ",";
		s += player.deck.getCardExpiried() + ",";
		s += player.owner.opponent.player.cardInHand.size() + ",";
		s += player.cardInHand.size() + ",";
		for (int i = 0; i < player.cardInHand.size(); i++) {
			s += player.cardInHand.get(i).name + ",";
			s += player.cardInHand.get(i).id + ",";
		}
		s += ")";
		server.sendMessage(s);
	}

	public static ArrayList<String> getTextBetween(String fromText) {
		ArrayList<String> rtrn = new ArrayList<>();
		String beforeText = "(";
		fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.length() - 1);
		String[] par = fromText.split(",");
		for (int i = 0; i < par.length; i++)
			rtrn.add(par[i]);
		return rtrn;
	}
}