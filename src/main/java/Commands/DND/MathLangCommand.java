package Commands.DND;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static Commands.DND.MathLangCommand.NotationType.*;

public class MathLangCommand extends ServerCommand {
    private final DecimalFormat decimalFormat = new DecimalFormat();

    // for checking if a double is close enough to an int
    private static final double DELTA = 0.0001;

    @Override
    public void runCommand(CommandContext ctx) {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        decimalFormat.setMaximumFractionDigits(10);
        MessageReceivedEvent event = ctx.event();
        String msg = event.getMessage().getContentRaw();

        msg = msg.substring(ctx.args()[0].length());
        if (ctx.args().length == 1) {
            event.getChannel().sendMessage(getDescription(ctx.prefix())).queue();
            return;
        }

        //MathValue output = interp(parse(String.join(" ", ctx.args())));
        MathValue output = interp(parse(msg));
        if(output.error.isBlank()) {
            event.getChannel().sendMessage("Math result: " + decimalFormat.format(output.num)).queue();
        } else {
            event.getChannel().sendMessage("Math Exception: " + output.error).queue();
        }
    }

    enum NotationType {
        Prefix,
        Infix,
        PrefixAndInfix,
        All,
    }

    /* Language may include Exprs as such spaces separate expressions and arguments
     * number
     * 0xHexNum
     * 0bBinary
     * Expr args // prefix expressions
     * larg Expr rargs // infix expressions
     * (Expr)
     */
    // constant pi, e and i (imaginary numbers?)
    private static MathExpr parse(String str) {
        str = str.toLowerCase();
        //Pattern number = Pattern.compile("\\d+|\\d*\\.\\d+"); // any integer or float number
        //Pattern hexPattern = Pattern.compile("0x[0-9abcdef]+"); // a hex number
        //Pattern binary = Pattern.compile("0b[0-1]"); // a binary number
        //Pattern isAPrefixExpression = Pattern.compile(String.join("|", MathOp.keysOfType(Prefix)));
        //Pattern parenCapture = Pattern.compile("\\(.*\\)"); // anything in parens or parens()
        try {
            return new MathExpr(Double.parseDouble(str));
        } catch (NumberFormatException e) {
            if (str.startsWith("0x")) {
                return new MathExpr(Double.valueOf(Integer.parseInt(str.substring(2), 16)));
            } else if (str.startsWith("-0x")) {
                return new MathExpr(Double.valueOf(Integer.parseInt("-" + str.substring(3), 16)));
            } else if (str.startsWith("0b")) {
                return new MathExpr(Double.valueOf(Integer.parseInt(str.substring(2), 2)));
            }
        }
        String[] found = str.split(" ");
        MathOp operation = MathOp.Err;
        List<MathExpr> args = new ArrayList<>();
         for (int i = 0; i < found.length; i++) {
            if(i == 0 && (operation = MathOp.getOpFromKey(found[i])) != null) { // checks for a prefix expression
                if (!(operation.notationType.equals(Prefix) || operation.notationType.equals(PrefixAndInfix)))
                    return new MathExpr("Operation: '" + found[0] + "' is not a prefix expression.");
                else
                    continue;
            } else if(i == 1 && (operation = MathOp.getOpFromKey(found[i])) != null) { // checks for a infix expression
                 if (!(operation.notationType.equals(Infix) || operation.notationType.equals(PrefixAndInfix)))
                     return new MathExpr("Operation: '" + found[0] + "' is not a Infix expression.");
                 else
                     continue;
            }
            args.add(parse(found[i]));
        }
        return new MathExpr(args.toArray(MathExpr[]::new), operation);
    }

    private static MathValue interp(MathExpr expr) {
        switch (expr.op) {
            case Add -> {
                if (expr.args.length != 2)
                    new MathValue("There must be 2 argument for addition");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue(left.num + right.num);
                } else {
                    return new MathValue(left.error + right.error + ": errors in addition");
                }
            }
            case Sub -> {
                if (expr.args.length == 1){
                    MathValue left = expr.args[0].getResult();
                    if(left.error.isBlank()) {
                        return new MathValue(left.num * -1);
                    } else
                        return left;
                }

                if (expr.args.length != 2)
                    new MathValue("There must be 2 argument for subtraction");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue(left.num - right.num);
                } else {
                    return new MathValue(left.error + right.error + ": errors in subtraction");
                }
            }
            case Mult -> {
                if (expr.args.length != 2)
                    new MathValue("There must be 2 argument for multiplication");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue(left.num * right.num);
                } else {
                    return new MathValue(left.error + right.error + ": errors in multiplication");
                }
            }
            case Div -> {
                if (expr.args.length != 2)
                    new MathValue("There must be 2 argument for division");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    if (right.num != 0){
                        return new MathValue(left.num / right.num);
                    } else {
                        return new MathValue("Can not divide by zero");
                    }
                } else {
                    return new MathValue(left.error + right.error + ": errors in division");
                }
            }
            case Pow -> {
                if (expr.args.length != 2)
                    new MathValue("There must be 2 argument for powers");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue((Math.pow(left.num, right.num)));
                } else {
                    return new MathValue(left.error + right.error + ": errors in powers");
                }
            }
            case Log -> {
                if (expr.args.length == 1){
                    MathValue left = expr.args[0].getResult();
                    if(left.error.isBlank()) {
                        return new MathValue(Math.log(left.num));
                    } else
                        return left;
                }

                if (expr.args.length != 2)
                    new MathValue("There must be 1-2 argument for a log");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue(Math.log(right.num) / Math.log(left.num));
                } else {
                    return new MathValue(left.error + right.error + ": errors in log");
                }
            }
            case Root -> {
                if (expr.args.length == 1){
                    MathValue left = expr.args[0].getResult();
                    if(left.error.isBlank()) {
                        return new MathValue(Math.sqrt(left.num));
                    } else
                        return left;
                }
                if (expr.args.length != 2)
                    new MathValue("There must be 1-2 argument for root");
                MathValue left = expr.args[0].getResult();
                MathValue right = expr.args[1].getResult();
                if(left.error.isBlank() && right.error.isBlank()) {
                    return new MathValue(Math.pow(left.num, 1.0/ right.num));
                } else {
                    return new MathValue(left.error + right.error + ": errors in root");
                }
            }
        }
        return new MathValue(expr.num);
    }

    enum MathOp {
        /*
        Parens
        Exponents, Pow, Log, Root, Fact
        Mult
        Div
        Add
        Sub
         */
        Num(1, "", All), // a representation of a pure double number
        //Hex(1, "0x", Prefix), // a representation of a hexadecimal number
        //Bin(1, "0b", Prefix), // a representation of a binary number
        Err(1, "error", Prefix), // represents an error
        Add(2, "+", Infix),
        Sub(1,2, "-", PrefixAndInfix), // Represents subtraction and negation (default l-value 0)
        Mult(2, "*", Infix),
        Div(2, "/", Infix),
        Pow(2, "^", Infix),
        Log(1,2, "log", Prefix), // represents all logarithmic expressions (default base 2)
        Root(1,2, "root", Prefix), // represents roots and square root (default root 2)
        /// NEEDS BIG INTEGER MATH Fact(1, "!", Prefix),
//        Sum(1,-1, "sum", Prefix), // sum of any number of numbers
//        Roll(1,2, "d", PrefixAndInfix), // dice roll (default l-value 1)
//        Range(1,2, "range", PrefixAndInfix), // range default at 0 to n but have n-m
//        RandPick(1,-1, "pick", Prefix), // picks a random number from all numbers it's given
//        Rand(0,2, "rand", Prefix), // makes a random number 0 to 1 or
//        round(1, "round", Prefix), // rounds a float to the nearest whole number
//        floor(1, new String[]{"floor", "flr"}, Prefix), // rounds a float down to the nearest whole number
//        ceiling(1, new String[]{"ceiling", "cel"}, Prefix), // rounds a float up to the nearest whole number
//        abs(1, "abs", Prefix),
//        // trig functions and conversion rad/deg (trig functions take deg)
//        deg(1, "deg", Prefix), // representation of degrees
//        rad(1, "rad", Prefix), // representation of radians
//        sin(1, "sin", Prefix),
//        cos(1, "cos", Prefix),
//        tan(1, "tan", Prefix),
//        arcsin(1, new String[]{"arcsin", "asin"}, Prefix),
//        arccos(1, new String[]{"arccos", "acos"}, Prefix),
//        arctan(1, new String[]{"arctan", "atan"}, Prefix),
//        degtorad(1, new String[]{"degtorad", "dtr", "d2r"}, Prefix),
//        radtodeg(1, new String[]{"radtodeg", "rtd", "r2d"}, Prefix),
//        // bin, hex and dec conversion
//        dectobin(1, new String[]{"dectobin", "dtb", "d2b"}, Prefix),
//        dectohex(1, new String[]{"dectohex", "dtx", "d2x"}, Prefix),
//        bintodec(1, new String[]{"bintodec", "btd", "b2d"}, Prefix),
//        hextodec(1, new String[]{"hextodec", "htd", "h2d"}, Prefix),
//        hextobin(1, new String[]{"hextobin", "htb", "h2b"}, Prefix),
//        bintohex(1, new String[]{"bintohex", "btx", "b2x"}, Prefix)
        ;
        // TODO: Add date/time handling and types
        // TODO: Matrix?

        // number of operands for the operator, use -1 to signify any number
        int leastArgs;
        int mostArgs;
        // any number of strings that represent the operator
        String[] keys;
        // if the operator should have arguments after it or before it or both
        NotationType notationType;

        MathOp(int numArgs, String key, NotationType notationType) {
            this.leastArgs = numArgs;
            this.mostArgs = numArgs;
            this.keys = new String[]{key};
            this.notationType = notationType;
        }
        MathOp(int leastArgs, int mostArgs, String key, NotationType notationType) {
            this.leastArgs = leastArgs;
            this.mostArgs = mostArgs;
            this.keys = new String[]{key};
            this.notationType = notationType;
        }
        MathOp(int numArgs, String[] keys, NotationType notationType) {
            this.leastArgs = numArgs;
            this.mostArgs = numArgs;
            this.keys = keys;
            this.notationType = notationType;
        }
        MathOp(int leastArgs, int mostArgs, String[] keys, NotationType notationType) {
            this.leastArgs = leastArgs;
            this.mostArgs = mostArgs;
            this.keys = keys;
            this.notationType = notationType;
        }

        /**
         * Finds all MathOps for a given type this does not include Operations with multiple types ie. Sub
         * @param n the notation type to look for
         * @return a List of MathOp with the notation type n
         */
        public static List<String> keysOfType(NotationType n) {
            return Arrays.stream(MathOp.values())
                    .filter(mathOp -> mathOp.notationType == n)
                    .map(mathOp -> mathOp.keys[0])
                    .collect(Collectors.toList());
        }

        /**
         * Searches the MathOp enum for a MathOp using the key given
         * @param key a String referring to a MathOp
         * @return the MathOp referred to by the key given or null if none exists
         */
        public static MathOp getOpFromKey(String key) {
            for(MathOp m : MathOp.values())
                for(String k : m.keys)
                    if(k.equals(key))
                        return m;
            return null;
        }
    }



    private static class MathExpr {
        MathExpr[] args;
        Double num;
        String str;
        MathOp op;

        public MathExpr(MathExpr[] args, MathOp op) {
            this.args = args;
            this.op = op;
        }

        // creation of an error by passing it a string
        public MathExpr(String arg) {
            this.str = arg;
            this.op = MathOp.Err;
        }

        // creation of a number by passing just a number
        public MathExpr(Double num) {
            this.num = num;
            this.op = MathOp.Num;
        }

        public MathValue getResult() {
            if(op.equals(MathOp.Num))
                return new MathValue(num);
            MathValue out = interp(this);
            return out;
        }
    }

    private static class MathValue {
        Double num;
        String error;

        public MathValue(Double num) {
            this.num = num;
            this.error = "";
        }

        public MathValue(String err) {
            this.error = err;
            this.num = null;
        }


    }
}
