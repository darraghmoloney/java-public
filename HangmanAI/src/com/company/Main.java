package com.company;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        FileIO reader = new FileIO();

        String[] inputs = reader.load("dictionary.txt");

        String word = inputs[(int) (Math.random() * inputs.length)];
        System.out.println(word);
        String shownWord = "";

        for(int i = 0; i<word.length(); i++) {
            shownWord += "_";
        }

        System.out.println(shownWord);

        int remaining = shownWord.length();
        int rounds = 0;
        int correct = 0;

        while(remaining > 0) {

            rounds++;

            char guess = guessLetter(inputs, shownWord);

            System.out.println(guess);

            String temp = "";
            boolean oneGuessed = false;
            for(int i=0; i<shownWord.length(); i++) {
                if(guess == word.charAt(i)) {
                    temp += guess;
                    remaining--;
                    if(!oneGuessed) {
                        oneGuessed = true;
                        correct++;
                    }
                }
                else {
                    temp += shownWord.charAt(i);


                }
            }
            shownWord = temp;
            System.out.println(shownWord);
        }

        System.out.println("Found the word " + shownWord);
        System.out.println("Took " + rounds + " rounds with " + (rounds-correct) + " wrong guesses");

    }


    /*  Store the location of letters that have been found or guessed
     *   by marking that spot as "true"
     *   26 locations for each lowercase alphabet letter
     * */
    private static boolean[] skipCheckLocs = new boolean[26];

    /*  Array of English alphabet char frequencies based on stats.
        Used to choose the best letter to guess if there is a tiebreak
        between chars of equal frequency.

        Based on:
        http://pi.math.cornell.edu/~mec/2003-2004/cryptography/subs/frequencies.html
        Updated with: http://www.datagenetics.com/blog/april12012/
        Each location represents the alphabet character minus the
        integer ASCII value of the lowercase 'a' char. So 'a' is at position 0,
        'e' position 5, etc as in the real alphabet
     */
    private static final int[] engFreqWeight = {
        23, //812,    //a
        10, //149,    //b
        17, //271,    //c
        16, //432,    //d
        26, //1202,   //e
        8, //230,    //f
        12, //203,    //g
        11, //592,    //h
        24, //731,    //i
        1, //10,     //j
        6, //69,     //k
        18, //398,    //l
        13, //261,    //m
        21, //695,    //n
        19, //768,    //o
        14, //182,    //p
        2, //11,     //q
        22, //602,    //r
        25, //628,    //s
        20, //910,    //t
        15, //288,    //u
        7, //111,    //v
        5, //209,    //w
        3, //17,     //x
        9, //211,    //y
        4 //7       //z
    };

    private static ArrayList<String> potentialWords = new ArrayList<>();

    /*  Stastically modelled best starting guesses based on letter frequency for given word length
        Based on http://www.datagenetics.com/blog/april12012/
        As there are generally 8 lives in hangman, most likely most characters in the string will never
        be guessed, but they are here for completeness
    */
    private static final String[] bestGuesses = {
            "aiesrntolcdupmghbyfvkwzxqj",
            "aoeiumbhsrntlcdpgyfvkwzxqj",
            "aeoiuyhbcksrntldpmgfvwzxqj",
            "aeoiuysbfrntlcdpmghvkwzxqj",
            "seaoiuyhrntlcdpmgbfvkwzxqj",
            "eaiousyrntlcdpmghbfvkwzxqj",
            "eiaousrntlcdpmghbyfvkwzxqj",
            "eiaousrntlcdpmghbyfvkwzxqj",
            "eiaousrntlcdpmghbyfvkwzxqj",
            "eioausrntlcdpmghbyfvkwzxqj",
            "eioadsrntlcupmghbyfvkwzxqj",
            "eioafsrntlcdupmghbyvkwzxqj",
            "ieoasrntlcdupmghbyfvkwzxqj",
            "ieosarntlcdupmghbyfvkwzxqj",
            "ieasrntolcdupmghbyfvkwzxqj",
            "iehsarntolcdupmgbyfvkwzxqj",
            "iersantolcdupmghbyfvkwzxqj",
            "ieasrntolcdupmghbyfvkwzxqj",
            "ieasrntolcdupmghbyfvkwzxqj",
            "iesarntolcdupmghbyfvkwzxqj"
    };

    public static char guessLetter(String[] dictionary, String shownWord) {

        int numFound = 0;

        /*  Create the regular expression to search the dictionary for
        *   potential correct word matches
        *   NB: originally used direct regex method, but better to loop here
        *   because we are also writing to the boolean array of characters
        *   that have been found so that they don't count as a letter to guess
        *   later
        * */
        String regPattern = new String();
        for(int i=0; i<shownWord.length(); i++) {
            /*  Letter characters are non-underscores */
            if(shownWord.charAt(i) != '_') {
                regPattern += shownWord.charAt(i);
                numFound++;
                /* Array index is ASCII char value - 'a' (to start at 0) */
                skipCheckLocs[ (char) (shownWord.charAt(i) - 'a') ] = true;
            }
            else {
                regPattern += "."; //Means any character in regex
            }
        }


        /*  Guess a random character if the number of found letters is
        *   too small to optimize the search
        *   Random + weighting (range is frequency of char in English)
        * */
        if(numFound < 2) {
            return guessRandomLetter(shownWord);
        }



        /*  Use an int array to represent alphabet char frequencies */
        int[] letterBucket = new int[26];

        /*  If a list of possible words was already found, use that as
        *   the dictionary, else search the entire dictionary for matches
        * */
        if(potentialWords.size() > 0) {
            dictionary = potentialWords.stream().toArray(String[]::new);
        }

        /*  Use built-in regex matching to find potential words */
        for(String s: dictionary) {


            if(Pattern.matches(regPattern, s)) {
                potentialWords.add(s);
                for(int i=0; i<s.length(); i++) {
                    /*  Take the char value of "a" (65) from the letter,
                    *   so that the array index starts at 0
                    * */
                    int charLoc = (int) s.charAt(i) - 'a';
                    letterBucket[charLoc]++; //Update freq. for that char
                }
            }
        }

        int bestLetterLoc = 0;
        int bestLetterFreq = 0;

        for(int i=0; i<letterBucket.length; i++) {

            if(skipCheckLocs[i]) {
                continue;
            }
            else {
                int currentFreq = letterBucket[i];
                if (currentFreq > bestLetterFreq) {
                    bestLetterLoc = i;
                    bestLetterFreq = currentFreq;
                } else if (currentFreq == bestLetterFreq && engFreqWeight[i] > engFreqWeight[bestLetterLoc]) {
                    bestLetterLoc = i;
                    bestLetterFreq = currentFreq;
                }

            }
        }

        skipCheckLocs[bestLetterLoc] = true;

        return (char) (bestLetterLoc + 'a');

    }

    public static char guessRandomLetter(String shownWord) {

        String freqs = "esiarntolcdupmghbyfvkwzxqj";

        if(shownWord.length() <= 20) {
            freqs = bestGuesses[shownWord.length()-1];
        }

        int i = 0;

        while(skipCheckLocs[freqs.charAt(i) - 'a']) {
            i++;
        }

        skipCheckLocs[freqs.charAt(i) - 'a'] = true;

        return freqs.charAt(i);

    }
}
