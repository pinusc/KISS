package fr.neamar.kiss.dataprovider;

import android.util.Log;

import java.util.ArrayList;

import fr.neamar.kiss.loader.LoadMathPojos;
import fr.neamar.kiss.pojo.MathPojo;
import fr.neamar.kiss.pojo.Pojo;

public class MathProvider extends Provider<MathPojo> {
    @Override
    public void onCreate () {
        super.onCreate();
    }

    @Override
    public ArrayList<Pojo> getResults(String s) {
        ArrayList<Pojo> pojos = new ArrayList<>();
        if (!s.matches("([0-9()\\-+\\*/\\^\\.]|sqrt|sin|cos|tan)+")) return pojos;
        MathPojo pojo = new MathPojo();
        try {
            double result = eval(s);
            if ((int) result == result)  // eval always returns a double, even if the result is an integer.
                pojo.result = Integer.toString((int) result);
            else
                pojo.result = Double.toString(result);
        } catch (RuntimeException e) {} //invalid string, do nothing
        pojos.add(pojo);
        return pojos;
    }

    @Override
    public void reload() {
        this.initialize(new LoadMathPojos(this));
    }


    /**
     * Code taken from http://stackoverflow.com/questions/3422673
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
