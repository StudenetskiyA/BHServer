package ru.studenetskiy.model;

import java.io.IOException;
import java.util.ArrayList;

public class ClientMessageHandler {
ArrayList<String> parameter = new ArrayList<String>();
TwilightServerEndpoint server;

ClientMessageHandler(TwilightServerEndpoint server, String message){
	this.server=server;
	parameter=Commons.getTextBetween(message);
		
}

void proceed() throws IOException{
	//TODO Move this
	Commons.sql.connect();
	 //If user exist and password correct
    if (Commons.sql.isUserPasswordCorrect(parameter.get(0), parameter.get(1))){
        server.sendMessage("Password correct");
        //Change coordinates and last connect
        Commons.sql.writeUserCoordinates(parameter.get(0),Integer.parseInt(parameter.get(2)),Integer.parseInt(parameter.get(3)));
        Commons.sql.writeUserLastConneted(parameter.get(0));
        // and check enter to zone
        //Zone currentZone = sqlZone.isInZone(commandList[2].toMyInt(),commandList[3].toMyInt())
//        if (currentZone!=null){
//            println("Вы в зоне "+currentZone.name)
//            //check powerside
//            val side = sql.getUserPowerside(commandList[0])
//            if (side==PowerSide.Human && currentZone.textForHuman!="") {
//                println(currentZone.textForHuman)
//            }
//            else if (side==PowerSide.Light && currentZone.textForLight!="") {
//                println(currentZone.textForLight)
//            }
//            else if (side==PowerSide.Dark && currentZone.textForDark!="") {
//                println(currentZone.textForDark)
//            }
//            else {
//                println("Все спокойно")
//            }
//        }
//        else {
//            server.sendMessage("Все спокойно");
//        }
    }
    else server.sendMessage("Incorrect password");

	//TODO Move this
	Commons.sql.disconnect();
}
}
