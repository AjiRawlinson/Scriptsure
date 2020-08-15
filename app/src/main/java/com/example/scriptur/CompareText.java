package com.example.scriptur;

import java.util.ArrayList;
import java.util.Arrays;

public class CompareText {

    final String[] stopwords = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you",
            "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her",
            "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs",
            "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am",
            "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do",
            "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as",
            "until", "while", "of", "at", "by", "for", "with", "about", "against", "between",
            "into", "through", "during", "before", "after", "above", "below", "to", "from", "up",
            "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once",
            "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
            "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same",
            "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    public CompareText() {
    }

    public int calculateScore(String actualText, String inputText) {
        actualText = actualText.toLowerCase();
        inputText = inputText.toLowerCase();
        double keyWords = compareKeyWords(getKeyWords(actualText), getKeyWords(inputText));
        double levensteinPercent = (double) levenshteinDistance(actualText, inputText) / (double) actualText.length();
        if(levensteinPercent > 1.0) { levensteinPercent = 1.0; } // if Levenstein Distance is larger than actual dialog length, put value to one, stops possibility of negative score
        int percentage = (int) Math.round((keyWords * 80) + ((1 - levensteinPercent) * 20)); //score formula subject to change

        return percentage;
    }

    public String[] getKeyWords(String text) {
        String[] allWords = text.replaceAll("[^a-zA-Z ]", "").split("\\s+");
        String[] keyWords = removeStopWords(allWords);
        return keyWords;
    }

    public String[] removeStopWords(String[] text) {
        ArrayList<String> stopwordsAL = new ArrayList<>(Arrays.asList(stopwords));
        ArrayList<String> textAL = new ArrayList<>(Arrays.asList(text));
        textAL.removeAll(stopwordsAL); // not working

        String[] textList = new String[textAL.size()];
        for(int i = 0; i < textList.length; i++) {
            textList[i] = textAL.get(i).toString();
        }
        return textList;
    }

    public double compareKeyWords(String[] actual, String[] input) {
        int numActualKeywords = actual.length, numInputKeywards = input.length;
        double score = 0;

        for(int i = 0; i < numActualKeywords; i++) {
            for(int j = 0; j < numInputKeywards; j++) {
                if(actual[i].equalsIgnoreCase(input[j])) {
                    score += 1.0;
                    input[j] = null;//stops repeating words in actual text from artificially inflating the score
                    break;//breaks after finding first match
                }
            }
            actual[i] = null;////stops repeating words in input text from artificially inflating the score
        }
        int delta = numInputKeywards - numActualKeywords; //how many extra words were said
        if(delta > 0) {  score = score - (delta / 2); }
        score = score / numActualKeywords;
        if(score > 0.0) { return score; }
        return 0;
    }

    public int levenshteinDistance (String lhs, String rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for(int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert  = cost[i] + 1;
                int cost_delete  = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost; cost = newcost; newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }
}
