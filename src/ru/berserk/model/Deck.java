package ru.berserk.model;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

// Created by StudenetskiyA on 30.12.2016.

public class Deck {
    ArrayList<Card> cards = new ArrayList<>();
    public String name;

    public Deck(String _name){
        name=_name;
    }

    public int getCardExpiried(){
        return cards.size();
    }

    public Card searchCard(String name){
        for (int i=0;i<cards.size();i++){
            if (cards.get(i).equals(name)) return cards.get(i);
        }
        return null;
    }

    public boolean haveTopDeck(){
        if (cards.size()==0) return false;
        else return true;
    }

    public Card topDeck(int depth){
        if (cards.size()>=depth)
            return cards.get(cards.size()-depth);
        else {
           // Main.printToView(0,"Закончилась колода");
        }
        return null;
    }

    public Card topDeck(){
        return topDeck(1);
    }

    public Card drawTopDeck(){
        Card tmp=cards.get(cards.size()-1);
        cards.remove(cards.size()-1);
        return tmp;
    }

    public Card getTopDeck(){
        Card tmp=cards.get(cards.size()-1);
        return tmp;
    }

    public void removeTopDeck(){
        if (haveTopDeck())
            cards.remove(cards.size()-1);
    }

    public void putOnBottomDeck(String _cardName){
        cards.add(0,Card.getCardByName(_cardName));
    }

    public void putOnBottomDeck(Card _card){
        cards.add(0,_card);
    }

    public void suffleDeck(int n){
        //Until server know nothing
        for (int i=0;i<cards.size();i++){
            byte[] b = (cards.get(i).name+i+n).getBytes();
            try {
                byte[] hash = MessageDigest.getInstance("MD5").digest(b);
                String a=DatatypeConverter.printHexBinary(hash);
                cards.get(i).hash=a;
                // System.out.println(cards.get(i).name+"/"+a);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        Comparator<Card> comparator = new Comparator<Card>() {
            @Override
            public int compare(Card left, Card right) {
                return left.hash.compareTo(right.hash) ; // use your logic
            }
        };

        Collections.sort(cards, comparator);
    }
}
