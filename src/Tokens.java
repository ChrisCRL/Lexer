/*
*  Contains most tokens except for ints, decimals, IDs, strings, whitespace, and comments.
*  Due to the nature of those tokens/rules, we will define them in the Lexer file instead.
*/

public class Tokens {
    public static final String OUTPUT = "output";
    public static final String ON = "on";
    public static final String CREATE = "create";
    public static final String CONSTANT = "constant";
    public static final String ELSE_IF = "elseif";
    public static final String ME = "me";
    public static final String UNTIL = "until";
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    public static final String ALERT = "alert";
    public static final String DETECT = "detect";
    public static final String ALWAYS = "always";
    public static final String CHECK = "check";
    public static final String PARENT = "parent";
    public static final String BLUEPRINT = "blueprint";
    public static final String NATIVE = "system";
    public static final String INHERITS = "is";
    public static final String CAST = "cast";
    public static final String INPUT = "input";
    public static final String SAY = "say";
    public static final String NOW = "now";
    public static final String WHILE = "while";
    public static final String PACKAGE = "package";
    public static final String TIMES = "times";
    public static final String REPEAT = "repeat";
    public static final String ELSE = "else";
    public static final String RETURNS = "returns";
    public static final String RETURN = "return";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String NULL = "undefined";
    public static final String STATIC = "shared";
    public static final String ACTION = "action";
    public static final String COLON = ":";
    public static final String INTEGER_KEYWORD = "integer";
    public static final String NUMBER_KEYWORD = "number";
    public static final String TEXT = "text";
    public static final String BOOLEAN_KEYWORD = "boolean";
    public static final String USE = "use";
    public static final String NOT = "not";
    public static final String NOT_ALT = "Not";
    public static final String NOT_EQUAL = "not=";
    public static final String NOT_EQUAL_ALT = "Not=";
    public static final String PERIOD = ".";
    public static final String COMMA = ",";
    public static final String EQUALITY = "=";
    public static final String GREATER = ">";
    public static final String GREATER_EQUAL = ">=";
    public static final String LESS = "<";
    public static final String LESS_EQUAL = "<=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";
    public static final String MODULO = "mod";
    public static final String LEFT_SQR_BRACE = "[";
    public static final String RIGHT_SQR_BRACE = "]";
    public static final String LEFT_PAREN = "(";
    public static final String RIGHT_PAREN = ")";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String IF = "if";
    public static final String END = "end";
    public static final String CLASS = "class";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    // Ensure the class cannot be instantiated
    private Tokens() {}
}
