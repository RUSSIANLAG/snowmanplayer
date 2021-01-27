package pole;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SnowmanPlayer
    {
        //Letters most likely to follow E in order of frequency
        //    R,S,N,D

        //The most common digraphs on order of frequency
        //    TH, HE, AN, IN, ER, ON, RE, ED, ND, HA, AT, EN, ES, OF, NT, EA, TI, TO, IO, LE, IS, OU, AR, AS, DE, RT, VE

        //The most common trigraphs in order of frequency
        //    THE, AND, THA, ENT, ION, TIO, FOR, NDE, HAS, NCE, TIS, OFT, MEN


        private static final String MOST_FREQUENT = "aei";
        private static final String FREQUENCY_ORDER = "tonsrhldcumfpgwybvkxjqz";
        private static final String MOST_COMMON_LETTER_PAIRS = "th,he,an,in,er,on,re,nd,ha,at,es,en,ed,of,nt,ea,ti,to,io,le,is,ou,ar,as,de,rt,ve";

        private static String[] allWords;
        private static String[] filteredWords;
        private static String lastGuess;

        public static String getAuthor()
        {
            return "Golovin, Ivan";
        }

        public static void startGame(String[] words, int minLength, int maxLength, String allowedChars)
        {
            allWords = words;
            filteredWords = allWords;
        }

        public static void startNewWord(int length)
        {
            filteredWords = allWords;
        }

        public static char guessLetter(String pattern, String previousGuesses)
        {            
            boolean bGuessCorrect = isLastGuessCorrect(pattern, previousGuesses);
            String[] similarWords = filterWordsBasedOnPattern(pattern, previousGuesses);
            filteredWords = similarWords;
            if (filteredWords.length == 1) {
                lastGuess = getNextLetterFromFoundWord(similarWords[0], pattern, previousGuesses);
                return lastGuess.charAt(0);                
            }

            if (filteredWords.length <= 1000) {
                lastGuess = getMostFrequentLetterFromSimilarWords(similarWords, previousGuesses);
                return lastGuess.charAt(0);
            }

            if (bGuessCorrect)
                lastGuess = findPairForLetter(lastGuess, previousGuesses);
            else
                lastGuess = getMostFrequent(pattern, previousGuesses);

            if (lastGuess != null)
                return lastGuess.charAt(0);

            lastGuess = getNextMostFrequent(pattern, previousGuesses);
            return lastGuess.charAt(0);
        }

        private static String[] filterWordsBasedOnPattern(String pattern, String previousGuesses)
        {
            List<String> missedLetters = getMissedLetters(pattern, previousGuesses);
            List<String> similarWords = new ArrayList<>();
            for (int i = 0; i < filteredWords.length; i++)
            {
                String word = filteredWords[i];
                if (word.length() != pattern.length())
                    continue;

                boolean match = true;
                String missed = "";
                for(int h = 0; h < missedLetters.size(); h++) {
                    missed += missedLetters.get(h);
                }
                
                for (int l = 0; l < word.length(); l++)
                {                                        
                    if (StringUtils.isNotEmpty(missed))
                        if(missed.contains(String.valueOf(word.charAt(l))))
                        {
                            match = false;
                            break;
                        }
                }

                if (!match)
                    continue;

                //Filetr out words where letters are not in the same positions
                for(int j = 0; j < pattern.length(); j++) {
                    if (pattern.charAt(j) != '*') {
                        if (word.charAt(j) != pattern.charAt(j))
                        {
                            match = false;
                            break;
                        }
                    }
                }

                if (match)
                    //Filter out wordswheref number of each letter in pattern and found match is not equal
                    for (int k = 0; k < pattern.length(); k++) {
                        int numCharsInPattern = 0;
                        int numCharsInWord = 0;
                        if (pattern.charAt(k) != '*') {
                            numCharsInPattern = countLettersInPattern(pattern, pattern.charAt(k));
                            numCharsInWord = countLettersInPattern(word, pattern.charAt(k));
                            if (numCharsInPattern != numCharsInWord) {
                                match = false;
                                break;
                            }
                        }

                    }

                if (match)
                    similarWords.add(word);
            }
            
            String[] a = new String[similarWords.size()];
            return similarWords.toArray(a);
        }

        private static List<String> getMissedLetters(String pattern, String previousGuesses)
        {
            List<String> missedLetters = new ArrayList<>();
            for (int i = 0; i < previousGuesses.length(); i++) {
                if (!pattern.contains(String.valueOf(previousGuesses.charAt(i))))
                    missedLetters.add(String.valueOf(previousGuesses.charAt(i)));
            }
            return missedLetters;
        }

        private static String findLastGuess(String previousGuesses)
        {
            if (StringUtils.isEmpty((previousGuesses)))
                return "";
            return String.valueOf(previousGuesses.charAt(previousGuesses.length() - 1));
        }

        private static boolean isLastGuessCorrect(String pattern, String previousGuesses)
        {
            if (StringUtils.isEmpty(previousGuesses))
                return false;
            return pattern.contains(String.valueOf(previousGuesses.charAt(previousGuesses.length() - 1)));
        }

        private static String getMostFrequent(String pattern, String previousGuesses)
        {
            for (int i = 0; i < MOST_FREQUENT.length(); i++) {
                if (!previousGuesses.contains(String.valueOf(MOST_FREQUENT.charAt(i))))
                    return String.valueOf(MOST_FREQUENT.charAt(i));
            }
            return null;
        }

        private static String getNextMostFrequent(String pattern, String previousGuesses)
        {
            for (int i = 0; i < FREQUENCY_ORDER.length(); i++) {
                if (!previousGuesses.contains(String.valueOf(FREQUENCY_ORDER.charAt(i))))
                    return String.valueOf(FREQUENCY_ORDER.charAt(i));
            }
            return null;
        }

        private static String getNextLetterFromFoundWord(String word, String pattern, String previousGuesses)
        {
            for (int i = 0; i < word.length(); i++) {
                if (!previousGuesses.contains(String.valueOf(word.charAt(i))))
                    return String.valueOf(word.charAt(i));
            }
            return null;
        }

        private static String findPairForLetter(String letter, String previousGuesses)
        {
            String[] pairs = MOST_COMMON_LETTER_PAIRS.split(",");
            for(int i = 0; i < pairs.length; i++) {
                String pair = String.valueOf(pairs[i]).toLowerCase();
                if(pair.contains(letter))
                {
                    if (letter.equals(String.valueOf(pair.charAt(0)))) {
                        if (previousGuesses.contains(String.valueOf(pair.charAt(1))))
                            continue;
                    }
                    else {
                        if (previousGuesses.contains(String.valueOf(pair.charAt(0))))
                            continue;
                    }
                }

                if (pair.contains(letter)) {
                    if (String.valueOf(pair.charAt(0)).equals(letter))
                        return String.valueOf(pair.charAt(1)).toLowerCase();
                    return String.valueOf(pair.charAt(0)).toLowerCase();
                }
            }
            return null;
        }

        private static int countLettersInPattern(String pattern, char letter) {
            int count = 0;
            for (int k = 0; k < pattern.length(); k++) {
                if (pattern.charAt(k) != '*')
                    if (letter == pattern.charAt(k))
                        count++;
            }
            return count;
        }

        private static String getMostFrequentLetterFromSimilarWords(String[] similarWords, String previousGuesses)
        {
            String mostCommonLetter = "";
            String letter = "";
            int frequency = 0;
            int maxFrequency = 0;
            List<String> calculatedLetters = new ArrayList<>();

            String allSimilarWordsLetters = "";
            for (int i = 0; i < similarWords.length; i++) {
                allSimilarWordsLetters += similarWords[i];
            }

            for (int j = 0; j < allSimilarWordsLetters.length(); j++) {
                letter = String.valueOf(allSimilarWordsLetters.charAt(j));
                if (previousGuesses.contains(letter))
                    continue;
                frequency = countLettersInPattern(allSimilarWordsLetters, letter.charAt(0));
                if (frequency > maxFrequency) {
                    maxFrequency = frequency;
                    mostCommonLetter = letter;
                }
            }
            return mostCommonLetter;
        }
    }