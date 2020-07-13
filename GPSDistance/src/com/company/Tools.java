package com.company;

import java.util.Random;

public class Tools {

    /*  For a plane travelling at an given speed,
     *   get the time it takes in seconds
     *   to travel a certain distance
     *  */
    public static int getTimeForPlaneTravel(double speed, double distance) {


        return  (int) (distance / speed * 60.0 * 60.0);
    }

    public static String getTimeString(int seconds) {

        int hours = seconds / 60 / 60; //Of course this could be /3600, but it's more readable like this
        String hourString = (hours > 9) ? (hours + "") : ("0" + hours);
        seconds = seconds - (hours * 60 * 60);

        int minutes = seconds / 60;
        String minString = (minutes > 9) ? (minutes + "") : ("0" + minutes); //Padding 0 - always displaying 2 digits
        seconds = seconds - (minutes * 60);

        String secString = (seconds > 9) ? (seconds + "") : ("0" + seconds);

        return hourString + ":" + minString + ":" + secString;
    }

    public static int roundNearest100(double num) {
        int rounded = (int) num;
        int tensPart = rounded % 100;

        rounded -= tensPart;

        if(tensPart >= 50) {
            rounded += 100;
        }
        return rounded;
    }



    /*  Return a random number that is more likely to give a number close
    *   to single digits, so that random removals from the Priority queue
    *   work, but tend to be closer distances.
    *   Note that this number might be greater than the number of items
    *   remaining in the priority queue - so a solution would be to
    *   check whether the queue is empty and make sure to store the last
    *   successfully polled item in a variable.
    *
    *  */
    public static int weightedRandom() {
        Random rand = new Random();

        int randNum = rand.nextInt(1000);

        //85% - 0 - 5
        if(randNum < 850) {
            return rand.nextInt(6);
        }

        //12.5% - 6-11
        else if(randNum < 970) {
            return rand.nextInt( 6 ) + 5;
        }
        //2% - 12-17

        else if(randNum < 995) {
            return  rand.nextInt(6) + 12;
        }

        //0.5% - 18
        else {
            return 18;
        }

    }




}
