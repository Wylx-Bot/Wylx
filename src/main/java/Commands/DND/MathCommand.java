package Commands.DND;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;

public class MathCommand extends ServerCommand {
    private final DecimalFormat decFormat = new DecimalFormat("0.#");

    enum MathState {
        NumPrefix,
        Num,
        Op,
    }

    enum MathOp {
        Add,
        Sub,
        Mult,
        Div,
        Sqr,
    }

    private static class MathException extends Exception {
        MathException (String message) {
            super (message);
        }
    }

    MathCommand() {
        super("math", CommandPermission.EVERYONE, "Do Math");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        String msg = event.getMessage().getContentRaw();

        msg = msg.substring(args[0].length());

        try {
            double retVal = doMath(msg);
            event.getChannel().sendMessage("Math result: " + decFormat.format(retVal)).queue();
        } catch (MathException exception) {
            event.getChannel().sendMessage("Math Exception: " + exception.getMessage()).queue();
        }
    }

    private double doMath(String toSolve) throws MathException {
        toSolve = toSolve.replace(" ", "");
        Deque<Double> stack = new ArrayDeque<>();
        double curVal = 0;
        double sign = 1;
        MathState state = MathState.NumPrefix;
        MathOp op = MathOp.Add;

        for (int i = 0; i < toSolve.length(); i++) {
            char chr = toSolve.charAt(i);

            switch(state) {
                case NumPrefix:
                    if (chr == '-') {
                        sign *= -1;
                        continue;
                    } else if (chr == '(') {
                        int lastIdx = getEndParen(toSolve, i);

                        if (lastIdx == -1) {
                            throw new MathException("Expected closing parenthesis");
                        }

                        curVal = doMath(toSolve.substring(i + 1, lastIdx));
                        i = lastIdx;
                        chr = toSolve.charAt(i);
                    }

                    state = MathState.Num;

                    /* fall-through */
                case Num:
                    if (Character.isDigit(chr)) {
                        curVal = (curVal * 10) + (chr - '0');
                        if (i != toSolve.length() - 1) continue;
                    }

                    curVal *= sign;
                    switch (op) {
                        case Add -> stack.push(curVal);
                        case Sub -> stack.push(curVal * -1);
                        case Mult -> stack.push(stack.pop() * curVal);
                        case Div -> stack.push(stack.pop() / curVal);
                        case Sqr -> stack.push(Math.pow(stack.pop(), curVal));
                        default -> {}
                    }

                    curVal = 0;
                    sign = 1;

                case Op:
                    state = MathState.NumPrefix;
                    if (i >= toSolve.length() - 1) break;
                    switch (chr) {
                        case '-' -> op = MathOp.Sub;
                        case '+' -> op = MathOp.Add;
                        case '*' -> op = MathOp.Mult;
                        case '/' -> op = MathOp.Div;
                        case '^' -> op = MathOp.Sqr;
                        case ')' -> state = MathState.Op;
                        default -> {
                            throw new MathException("Expected operator (Got character `" + chr + "` instead)");
                        }
                    }
            }
        }

        double result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    private int getEndParen(String val, int startIdx) {
        int depth = 0;
        for (int i = startIdx; i < val.length(); i++) {
            char chr = val.charAt(i);
            switch (chr) {
                case '(' -> depth++;
                case ')' -> depth--;
            }

            if (depth == 0) {
                return i;
            }
        }

        return -1;
    }
}
