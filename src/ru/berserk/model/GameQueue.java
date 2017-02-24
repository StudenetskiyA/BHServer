package ru.berserk.model;


import java.io.IOException;

import ru.berserk.model.MyFunction.ActivatedAbility;

/**
 * Created by StudenetskiyA on 23.01.2017.
 */

public class GameQueue
{
    Gamer gamer;
    Gamer opponent;

    GameQueue(Gamer _gamer){
        gamer=_gamer;
        opponent=gamer.opponent;
    }

    static class QueueEvent {

        String whatToDo;
        Creature targetCr;
        int howMany;

        public QueueEvent(String _what, Creature _tc,int _howMany) {
            whatToDo = _what;
            targetCr=_tc;
            howMany=_howMany;
        }
    }

    // Указатель на первый элемент
    private ObjectBox head = null;
    // Указатель на последний элемент
    private ObjectBox tail = null;
    // Поле для хранения размера очереди
    private int size = 0;

    public void push(QueueEvent obj) {
        // Сразу создаем вспомогательный объект и помещаем новый элемент в него
        ObjectBox ob = new ObjectBox();
        ob.setObject(obj);
        // Если очередь пустая - в ней еще нет элементов
        if (head == null) {
            // Теперь наша голова указывает на наш первый элемент
            head = ob;
        } else {
            // Если это не первый элемент, то надо, чтобы последний элемент в очереди
            // указывал на вновь прибывший элемент
            tail.setNext(ob);
        }
        // И в любом случае нам надо наш "хвост" переместить на новый элемент
        // Если это первый элемент, то "голова" и "хвост" будут указывать на один и тот же элемент
        tail = ob;
        // Увеличиваем размер нашей очереди
        size++;
    }

    public void responseAllQueue() throws IOException {
       // boolean someFound=false;
       gamer.memPlayerStatus=gamer.status;
       gamer.opponent.memPlayerStatus=gamer.opponent.status;
       System.out.println("Queue save status for "+gamer.name+" at "+gamer.status.toString());
        while (size() != 0) {
         //   someFound=true;
            GameQueue.QueueEvent event = pull();
            System.out.println("next queue response");
            if (event.whatToDo.equals("Die")) {
                if (event.targetCr.owner.creatures.contains(event.targetCr)) {
                    event.targetCr.owner.owner.printToView(0, event.targetCr.name + " умирает.");

                    event.targetCr.owner.massDieCheckNeededTarget();

                    System.out.println(event.targetCr.name + " удален/" + event.targetCr.owner.playerName);

                    event.targetCr.owner.removeCreatureFromList(event.targetCr);
                }
            }
            else if (event.whatToDo.equals("Upkeep")) {
                if (event.targetCr.owner.creatures.contains(event.targetCr)) {
                    event.targetCr.owner.massUpkeepCheckNeededTarget();
                }
            } 
            else if (event.whatToDo.equals("Summon")) {
                if (event.targetCr.owner.creatures.contains(event.targetCr)) {
                	if (event.targetCr.text.contains("Повторить раз ")) {
                    	gamer.activatedAbility.battlecryPlayedTimes = MyFunction.getNumericAfterText(event.targetCr.text, "Повторить раз ");
                    }
                    event.targetCr.owner.massSummonCheckNeededTarget();
                }
            }
        }
        System.out.println("complite queue response");
      //  if (someFound) {
            gamer.status = gamer.memPlayerStatus;
            gamer.opponent.status = gamer.opponent.memPlayerStatus;
       // }
    }

    public QueueEvent pull() {
        // Если у нас нет элементов, то возвращаем null
        if (size == 0) {
            return null;
        }
        // Получаем наш объект из вспомогательного класса из "головы"
        QueueEvent obj = head.getObject();
        // Перемещаем "голову" на следующий элемент
        head = head.getNext();
        // Если это был единственный элемент, то head станет равен null
        // и тогда tail (хвост) тоже дожен указать на null.
        if (head == null) {
            tail = null;
        }
        // Уменьшаем размер очереди
        size--;
        // Возвращаем значение
        return obj;
    }

    public Object get(int index) {
        // Если нет элементов или индекс больше размера или индекс меньше 0
        if(size == 0 || index >= size || index < 0) {
            return null;
        }
        // Устанавлваем указатель, который будем перемещать на "голову"
        ObjectBox current = head;
        // В этом случае позиция равну 0
        int pos = 0;
        // Пока позиция не достигла нужного индекса
        while(pos < index) {
            // Перемещаемся на следующий элемент
            current = current.getNext();
            // И увеличиваем позицию
            pos++;
        }
        // Мы дошли до нужной позиции и теперь можем вернуть элемент
        QueueEvent obj = current.getObject();
        return obj;
    }

    public int size() {
        return size;
    }

    // Наш вспомогательный класс будет закрыт от посторонних глаз
    private class ObjectBox
    {
        // Поле для хранения объекта
        private QueueEvent object;
        // Поле для указания на следующий элемент в цепочке.
        // Если оно равно NULL - значит это последний элемент
        private ObjectBox next;

        public QueueEvent getObject() {
            return object;
        }

        public void setObject(QueueEvent object) {
            this.object = object;
        }

        public ObjectBox getNext() {
            return next;
        }

        public void setNext(ObjectBox next) {
            this.next = next;
        }
    }
}
