/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pennywise.checkers.core.logic;

import java.io.*;
import java.util.Vector;

import com.pennywise.checkers.core.logic.enums.Player;

/**
 * @author ASHISH
 */
public class UserInteractions {

    public static String GameChoice() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // Ask for user input
        PrintSeparator('#');
        System.out.println("Welcome To The Checkers Game developed by:");
        System.out.println(" \t1. Ashish Prasad ashishp@iitrpr.ac.in");
        System.out.println(" \t2. Apurv Verma   apurvverma@iitrpr.ac.in");
        PrintSeparator('-');
        System.out.println("Enter 'w/W' if you want to play as white.");
        System.out.println("Enter 'b/B' if you want to play as black.");
        System.out.println("Enter 'a/A' for two player game.");
        System.out.println("Enter 'n/N' for auto play.");
        PrintSeparator('#');

        String choice = new String();

        while (true) {
            try {
                System.out.print("Enter your Choice (w/W/b/B/a/A/n/N): ");
                choice = br.readLine().toLowerCase();

                if (choice.equals("w") || choice.equals("b") || choice.equals("a") || choice.equals("n")) {
                    break;
                }
            } catch (Exception ex) {
            }

            System.out.println("\nWrong Choice...Type again(0-7): ");
        }

        return choice;
    }

    public static Step getNextMove(Player player, Board board) {
        return TakeUserInput(-1, -1, board);
    }

    // Pass r1 to be -1 and c1 to be -1 if we want to take r1 and c1 as an input from user
    public static Step TakeUserInput(int r1, int c1, Board board) {
        // Display the game board        
        board.Display();
        PrintSeparator('-');

        // Ask for user input
        System.out.println("Enter your Step.");
        System.out.println("Piece To Step:");

        System.out.print("\tRow(0-7): ");
        if (r1 == -1) {
            r1 = TakeInput();
        } else {
            System.out.println(r1);
        }

        System.out.print("\tCol(0-7): ");
        if (c1 == -1) {
            c1 = TakeInput();
        } else {
            System.out.println(c1);
        }


        System.out.println("Where To Step:");
        System.out.print("\tRow(0-7): ");
        int r2 = TakeInput();

        System.out.print("\tCol(0-7): ");
        int c2 = TakeInput();

        return new Step(r1, c1, r2, c2);
    }

    private static int TakeInput() {

        int num = -1;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                num = Integer.parseInt(br.readLine());

                if (num >= 0 && num < Board.rows) {
                    break;
                }
            } catch (Exception ex) {
            }

            System.out.print("Wrong Choice...Type again(0-7): ");
        }

        return num;
    }

    public static void PrintSeparator(char ch) {
        switch (ch) {
            case '_':
                System.out.println("___________________________________________________________________________");
                break;
            case '-':
                System.out.println("---------------------------------------------------------------------------");
                break;
            case '#':
                System.out.println("###########################################################################");
                break;
        }
    }

    public static void DisplayGreetings(Player color,Board board) {

        board.Display();
        PrintSeparator('_');

        if (color.equals(Player.white)) {
            System.out.println("Congrats!!!!!!!!!! White has Won.");
        } else {
            System.out.println("Congrats!!!!!!!!!! Black has Won.");
        }
    }

    public static void DisplayMoveSeq(Vector<Step> stepSeq) {
        for (Step m : stepSeq) {
            m.display();
            System.out.print(", ");
        }

        System.out.println();
    }
}