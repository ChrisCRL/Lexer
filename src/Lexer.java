import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.*;

public class Lexer {

    public static void main(String @NotNull [] args) {

        // these DO NOT contain ints, decimals, IDs, strings, whitespace, and comments.
        // These will be added in this function instead
        Map<String, Integer> map = populateMap(); // main map that contains all tokens and their values
        Map<String, String> keywordMap = assignKeyword(); // 2nd map that will contain each token's keywords

        try {
            // trying to open files on Windows
            String folder = new File(".").getCanonicalPath() + "\\" + args[0]; // getting folder
            File folderObj = new File(folder);
            File[] fileList = folderObj.listFiles();

            if (fileList != null) { // if contents are inside folder
                for (File file : fileList) { // grab a file one at a time
                    if (file.isFile()) {
                        System.out.println("\nInput: " + file.getName());
                        System.out.println("Output: ");
                        lexerAlgorithm(file, map, keywordMap);
                    }
                }
            }
            else { // trying to open files on MAC

                folder = new File(".").getCanonicalPath() + "/" + args[0]; // getting folder
                folderObj = new File(folder);
                fileList = folderObj.listFiles();

                if (fileList != null) {
                    for (File file : fileList) {
                        if (file.isFile()) {
                            if (file.getName().equals(".DS_Store")) { // MAC has this weird hidden file, so skip
                                continue;
                            }
                            System.out.println("\nInput: " + file.getName());
                            System.out.println("Output: ");
                            lexerAlgorithm(file, map, keywordMap);
                        }
                    }
                }
                else
                {
                    System.out.println("Error: No files in directory or directory doesn't exits");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\nCongrats! You read all the files!\n");

        System.exit(0);
    }

    public static void lexerAlgorithm(File file, @NotNull Map<String, Integer> map, @NotNull Map<String, String> keywordMap) {

        // MAIN ALGORITHM //
        int CHANNEL;       // "channels" for whitespace and comments (0 for base channel, 1 for comments)
        // reading in files line by line in order to parse tokens
        String compareKey; // used for comparing parsed keywords and tokens
        int character;     // used to get characters from file
        boolean ID_Num = false; // used to determine if we are in an identifier so that we can process integers

        try(FileReader read = new FileReader(file);
            BufferedReader buffRead = new BufferedReader(read)) {

            StringBuilder word = new StringBuilder(); // used to compare the text in the file with tokens in the map
            // to do this, read txt file char by char until word is found on map
            while ((character = buffRead.read()) != -1) {

                char fileChar = (char) character;

                if (fileChar == '\n' || fileChar == '\r' || fileChar == ' ') // skipping whitespace
                    continue;

                // checking for string token
                if (fileChar == '"') {
                    // iterating through entire text to find end quote
                    int nextCharacter;
                    while(true)
                    {
                        nextCharacter = buffRead.read();
                        char fileCharNext = (char) nextCharacter;

                        if (nextCharacter == -1) // if end of file, it's an error since no end quote
                        {
                            greedyErrorFound();
                            word.setLength(0);
                            break;
                        }

                        if (fileCharNext == '"') { // finding end quote
                            CHANNEL = 0;
                            compareKey = word.toString();
                            stringFound(compareKey);
                            word.setLength(0); // setting word equal to nothing
                            break;
                        }
                        else
                            word.append(fileCharNext);
                    }
                    continue; // skipping whitespace after token
                    //fileChar = (char) skipChar(nextCharacter, buffRead);
                }

                // checking for comment token
                if (fileChar == '/') {
                    int nextCharacter;
                    nextCharacter = buffRead.read();
                    char fileCharNext = (char) nextCharacter;

                    if (fileCharNext == '/')
                    {
                        // iterating through entire line to find end of single line comment
                        int i = 0;
                        while(true)
                        {
                            nextCharacter = buffRead.read();
                            fileCharNext = (char) nextCharacter;

                            if(fileCharNext == ' ' && i == 0) // skip the initial space
                                continue;
                            i++;

                            if (fileCharNext == '\r' || nextCharacter == -1) { // end of single line comment
                                CHANNEL = 1;
                                compareKey = word.toString();
                                commentFound(compareKey);
                                word.setLength(0);
                                break;
                            }
                            else
                                word.append(fileCharNext);
                        }
                        continue;
                    }
                    if (fileCharNext == '*') { // checking for multiline comment
                        // iterating through entire text to find end of multiline comment
                        int i = 0;
                        while(true)
                        {
                            nextCharacter = buffRead.read();
                            fileCharNext = (char) nextCharacter;

                            if (fileCharNext == ' ' && i == 0) // skip the initial space
                                continue;
                            i++;

                            if (fileCharNext == ' ') {
                                // looking ahead by 1
                                buffRead.mark(1); // setting current index
                                int nextChar = buffRead.read();
                                char charNext = (char) nextChar;

                                buffRead.reset(); // reset index
                                if (charNext == '*')
                                    continue;
                            }

                            if (nextCharacter == -1) // if end of file, it's an error since no end indicator
                            {
                                greedyErrorFound();
                                word.setLength(0);
                                break;
                            }

                            if (fileCharNext == '*') { // end of single line comment
                                // looking ahead by 1
                                buffRead.mark(1); // setting current index
                                int nextChar = buffRead.read();
                                char charNext = (char) nextChar;

                                if (charNext == '/'){
                                    CHANNEL = 1;
                                    compareKey = word.toString();
                                    commentFound(compareKey);
                                    word.setLength(0);
                                    break;
                                }
                                else{
                                    word.append(fileCharNext);
                                    buffRead.reset(); // reset index
                                }
                            }
                            else
                                word.append(fileCharNext);
                        }
                        continue;
                    }
                    else { // if nothing after, its division
                        CHANNEL = 0;
                        String tmp = Character.toString(fileChar);
                        tokenFound(tmp, map, keywordMap);
                        word.setLength(0);
                        continue;
                    }
                }

                // ints and decimals will also be considered greedy in this algorithm
                if (Character.isDigit(fileChar) && !ID_Num) {

                    word.append(fileChar);
                    int nextCharacter;
                    boolean containDec = false; // checks whether there is a decimal

                    // iterating through entire line to find a non int/dec token
                    while(true)
                    {
                        buffRead.mark(1);
                        nextCharacter = buffRead.read();
                        char fileCharNext = (char) nextCharacter;

                        if (fileCharNext == '.') {

                            buffRead.mark(1);
                            int tmpNextCharacter = buffRead.read();
                            char tmpFileCharNext = (char) tmpNextCharacter;

                            if (tmpFileCharNext == ' ')
                            {
                                DecErrorFound();
                                word.setLength(0);
                                break;
                            }

                            buffRead.reset();
                            containDec = true;
                            word.append(fileCharNext);
                        }
                        else if (!Character.isDigit(fileCharNext)) { // end of integer

                            if (containDec) {
                                CHANNEL = 0;
                                compareKey = word.toString();
                                decimalFound(compareKey);
                                word.setLength(0);
                                buffRead.reset();
                                break;
                            }

                            CHANNEL = 0;
                            compareKey = word.toString();
                            integerFound(compareKey);
                            word.setLength(0);
                            buffRead.reset();
                            break;
                        }
                        else
                            word.append(fileCharNext);
                    }
                    continue;
                }

                // converting char to a String for comparison
                word.append(fileChar);
                compareKey = word.toString();

                if (map.containsKey(compareKey)) // check if word is in map
                {
                    // looking ahead to check if end of word indicated by whitespace or by single char tokens
                    buffRead.mark(1); // setting current index
                    int nextCharacter = buffRead.read();
                    char fileCharNext = (char) nextCharacter;
                    String isNextChar =  String.valueOf(fileCharNext);

                    if (nextCharacter == -1) // indicating end of file
                    {
                        CHANNEL = 0;
                        tokenFound(compareKey, map, keywordMap);
                        word.setLength(0);
                        buffRead.reset(); // if not end of a word, reset index
                        break;
                    }
                    if (map.containsKey(isNextChar) && !isNextChar.equals("=")) // checking if the character ahead is a token (such as (, +, -, ], etc.)
                    {
                        CHANNEL = 0;
                        tokenFound(compareKey, map, keywordMap);
                        word.setLength(0);
                        buffRead.reset();
                        continue;
                    }
                    if (fileCharNext == ' ' || fileCharNext == '\r' || fileCharNext == '\n') // if end of word
                    {
                        CHANNEL = 0;
                        tokenFound(compareKey, map, keywordMap);
                        word.setLength(0);
                        buffRead.reset();
                        continue;
                    }
                    String tmp = String.valueOf(compareKey.charAt(0));
                    if (map.containsKey(tmp)) // checks for the single char tokens (ie ),+,.,/, etc)
                    {
                        if (compareKey.equals("<") || compareKey.equals(">")) {
                            buffRead.reset();
                            continue;
                        }

                        CHANNEL = 0;
                        tokenFound(compareKey, map, keywordMap);
                        word.setLength(0);
                        buffRead.reset();
                        continue;
                    }
                    if (Character.isDigit(fileCharNext)) { // checking if next char is a digit, if so skip
                        ID_Num = true;
                        buffRead.reset();
                        continue;
                    }
                    else
                        buffRead.reset();
                }

                // checks if its identifier
                char tmp = compareKey.charAt(0);
                if (map.containsKey(compareKey))
                    continue;

                if (isLetter(tmp)) { // checking if first letter is ('a'..'z'|'A'..'Z')

                    // looking ahead to check if end of word indicated by whitespace or by single char tokens
                    buffRead.mark(1); // setting current index
                    int nextCharacter = buffRead.read();
                    char fileCharNext = (char) nextCharacter;
                    String isNextChar =  String.valueOf(fileCharNext);

                    if (Character.isDigit(fileCharNext))
                        ID_Num = true;
                    if (nextCharacter == -1) // indicating end of file
                    {
                        for (int i = 1; i < compareKey.length(); i++) { // checking is the rest of word is ('a'..'z'|'A'..'Z'|'0'..'9' | '_')*
                            if (!isLetterOrDigitOrUnderscore(compareKey.charAt(i)))
                                errorFound(String.valueOf(compareKey.charAt(i)));
                        }

                        CHANNEL = 0;
                        identifierFound(compareKey);
                        word.setLength(0);
                        buffRead.reset();
                        break;
                    }
                    if (map.containsKey(isNextChar)) // checking if the character ahead is a token (such as (, +, -, ], etc.)
                    {
                        ID_Num = false;
                        CHANNEL = 0;
                        identifierFound(compareKey);
                        word.setLength(0);
                        buffRead.reset();
                        continue;
                    }
                    if (fileCharNext == ' ' || fileCharNext == '\r' || fileCharNext == '\n') // if end of word
                    {
                        ID_Num = false;
                        for (int i = 1; i < compareKey.length(); i++) { // checking is the rest of word is ('a'..'z'|'A'..'Z'|'0'..'9' | '_')*
                            if (!isLetterOrDigitOrUnderscore(compareKey.charAt(i)))
                                errorFound(String.valueOf(compareKey.charAt(i)));
                        }

                        CHANNEL = 0;
                        identifierFound(compareKey);
                        word.setLength(0);
                        buffRead.reset();
                    }
                    else
                        buffRead.reset();
                }
                else {
                    errorFound(compareKey);
                    word.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Hashmap function that reads in a Java file that consists of tokens.
     * Allocates map with contents inside token file. Contents of file
     * are the keys to the map. The value of the map is an index that
     * starts at 0 and increments by 1 for each token/line read
     */
    public static @NotNull HashMap<String, Integer> populateMap() {

        HashMap<String, Integer> map = new HashMap<>();
        Class<?> tokens = Tokens.class;
        java.lang.reflect.Field[] constants = tokens.getDeclaredFields();   // Get the constants from the Tokens class*
        int value = 0;                                                      // *ChatGPT helped me with this

        // Populating the hashMap
        for (java.lang.reflect.Field temp : constants) {
            String key = null;
            try {
                key = (String) temp.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            map.put(key, value++);
        }
        return map;
    }

    /*
     * Hashmap function that assigns keywords to tokens. part 1
     */
    public static @NotNull HashMap<String, String> assignKeyword() {

        final String[] KEYWORD_ARRAY = {
                "OUTPUT", "ON", "CREATE", "CONSTANT", "ELSE_IF", "ME", "UNTIL", "PUBLIC", "PRIVATE", "ALERT",
                "DETECT", "ALWAYS", "CHECK", "PARENT", "BLUEPRINT", "NATIVE", "INHERITS", "CAST", "INPUT",
                "SAY", "NOW", "WHILE", "PACKAGE", "TIMES", "REPEAT", "ELSE", "RETURNS", "RETURN", "AND", "OR",
                "NULL", "STATIC", "ACTION", "COLON", "INTEGER_KEYWORD", "NUMBER_KEYWORD", "TEXT", "BOOLEAN_KEYWORD",
                "USE", "NOT", "NOT_ALT", "NOT_EQUAL", "NOT_EQUAL_ALT", "PERIOD", "COMMA", "EQUALITY", "GREATER",
                "GREATER_EQUAL", "LESS", "LESS_EQUAL", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "MODULO",
                "LEFT_SQR_BRACE", "RIGHT_SQR_BRACE", "LEFT_PAREN", "RIGHT_PAREN", "DOUBLE_QUOTE", "IF", "END",
                "CLASS", "TRUE", "FALSE"
        };

        return KeywordMap(KEYWORD_ARRAY);
    }

    /*
     * Hashmap function that assigns keywords to tokens. part 2
     */
    @NotNull
    private static HashMap<String, String> KeywordMap(String[] KEYWORD_ARRAY) {

        HashMap<String, String> map = new HashMap<>();
        Class<?> tokens = Tokens.class;
        java.lang.reflect.Field[] constants = tokens.getDeclaredFields();

        // Populating the hashMap
        int i = 0;
        for (java.lang.reflect.Field temp : constants) {
            String key = null;
            try {
                key = (String) temp.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (KEYWORD_ARRAY[i].equals("NOT_ALT") && Objects.equals(key, "Not"))
            {
                map.put(key, "NOT");
                i++;
            } else if (KEYWORD_ARRAY[i].equals("NOT_EQUAL_ALT") && Objects.equals(key, "Not=")) {
                map.put(key, "NOT_EQUAL");
                i++;
            } else if (KEYWORD_ARRAY[i].equals("FALSE") && Objects.equals(key, "false")) {
                map.put(key, "BOOLEAN");
                i++;
            } else if (KEYWORD_ARRAY[i].equals("TRUE") && Objects.equals(key, "true")) {
                map.put(key, "BOOLEAN");
                i++;
            }
            else
            {
                map.put(key, KEYWORD_ARRAY[i]);
                i++;
            }
        }
        return map;
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isLetterOrDigitOrUnderscore(char c) {
        return isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public static void tokenFound(String compareKey, @NotNull Map<String, Integer> map, @NotNull Map<String, String> keywordMap) {
        System.out.println("Token Category: " + map.get(compareKey) + ", " + keywordMap.get(compareKey) + " keyword, value " + "\"" + compareKey + "\"");
    }

    public static void stringFound(String compareKey) {
        System.out.println("Token Category: " + 100 + ", STRING keyword, value " + "\"" + compareKey + "\"");
    }

    public static void commentFound(String compareKey) {
        System.out.println("Token Category: " + 101 + ", COMMENT, value " + "\"" + compareKey + "\"");
    }

    public static void integerFound(String compareKey) {
        System.out.println("Token Category: " + 102 + ", INT, value " + "\"" + compareKey + "\"");
    }

    public static void decimalFound(String compareKey) {
        System.out.println("Token Category: " + 103 + ", DECIMAL, value " + "\"" + compareKey + "\"");
    }

    public static void identifierFound(String compareKey) {
        System.out.println("Token Category: " + 99 + ", IDENTIFIER, value " + "\"" + compareKey + "\"");
    }

    public static void errorFound(String compareKey) {
        System.out.println("Error: " + "\"" + compareKey + "\" not allowed");
    }
    public static void greedyErrorFound() {
        System.out.println("Error: String or Multiline Comment was not enclosed");
    }

    public static void DecErrorFound() {
        System.out.println("Error: Needs values after decimal");
    }
}