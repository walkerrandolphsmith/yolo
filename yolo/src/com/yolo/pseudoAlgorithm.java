package com.yolo;

/* 
* This strictly a java psuedo example of the annoyance algorithm.
* Not to be used in the main programming of the app.
*/

import java.util.Scanner;

/**
 *
 * @author Ramon Johnson
 */
public class pseudoAlgorithm 
{
    private static boolean driving, stopped;
    public static int speed = 0;
    public static int stopTimer = 0;
    
    public void carStopped()
    {
        //I wouldn't put this at zero since the GPS is inaccurate. 
        //It once said I was going 5 mph even though I was standing still.
        //Need to find a reasonable value to account for errors.
        if(speed <= 5)
        {
            stopped = true;
        }
        else
        {
            stopped = false;
        }
    }
    
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        //Simulates the input from the swipe functionality.
        String input;
        input = scan.next();
        //Stating if user just started driving
        if(speed > 10 && driving == false) 
        {
            //--then prompt user to answer question if driving YES/NO
            if (input.equalsIgnoreCase("y"))
            {
                stopTimer = 0;
                driving = true;
                //lock functionality;
            }
            else
            {
                //nothing should happen.
            	//Ask again if phone detects driving
            	//Have another algorithm to determine
            	//When the phone is in motion.
            }
        }
        //Stating over 10mph and user was driving
        if(speed > 10 && driving == true) 
        {
            //keep phone functionality locked
        }
        if(speed <= 10)
        {
            /*if (!stopped)
            {
                //still lock phone functionality
            }*/
        	if(driving == false || stopped == true)
        	{
        		//unlock phone functionality
        	}
            else
            {
                //activate stopTimer;
                stopTimer++;
            }
        }
        if(stopTimer > 180)
        {
            driving = false;
        }
    }
    
    public boolean getDrivingState()
    {
        return driving;
    }
    
    public boolean getStoppedState()
    {
        return stopped;
    }
    
    public int getRemainingTime()
    {
        return stopTimer;
    }
}