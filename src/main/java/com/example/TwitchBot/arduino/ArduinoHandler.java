package com.example.TwitchBot.arduino;

import java.io.IOException;

import arduino.Arduino;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Simple application that is part of an tutorial.
 * The tutorial shows how to establish a serial connection between a Java and Arduino program.
 * @author Michael Schoeffler (www.mschoeffler.de)
 *
 */
@Component
public class ArduinoHandler {

//    Arduino arduino;
//    @PostConstruct
//    public void init(){
//        arduino = new Arduino("COM4", 9600);
//
//        try {
//            boolean connected = arduino.openConnection();
//            System.out.println("Соединение установлено");
//        }
//        catch (Exception e){
//            System.out.println("No connection with arduino");
//        }
//
//
//    }
//
//    public void changeColor() throws InterruptedException{
//        System.out.println("Изменяю цвет");
//        arduino.serialWrite('1');
//    }

}