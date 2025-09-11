package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.ExpSyntaxException;
import com.logic.parser.ParserConstants;
import com.logic.parser.Token;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;

import java.util.Map;

import static com.logic.parser.ParserConstants.EOF;
import static com.logic.parser.ParserConstants.tokenImage;

public class ExpSyntaxFeedback {

    private static final Map<Integer, String> tokensMapping = Map.of(
            ParserConstants.LITERAL, "literal",
            ParserConstants.GENERIC, "generic",
            ParserConstants.VARIABLE, "variable",
            ParserConstants.PREDICATE, "predicate",
            ParserConstants.FUNCTION, "function");

    public static void produceFeedback(ExpSyntaxException exception, ExpFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM -> "Syntax error!";
            case HIGH -> expected(false, exception);
            case SOLUTION -> expected(true, exception);
        });
    }

    private static String expected(boolean showExpected, ExpSyntaxException exception) {
        Token currentToken = exception.getException().currentToken;
        int[][] array = exception.getException().expectedTokenSequences;
        StringBuilder expected = new StringBuilder();
        String currentType = tokensMapping.get(exception.getException().currentToken.next.kind);
        String currentValue = exception.getException().currentToken.next.image;

        int maxSize = 0;
        for (int[] cArray : array) {
            if (maxSize < cArray.length)
                maxSize = cArray.length;

            for (int token : cArray) {
                if (token != ParserConstants.DOT && token != EOF) {
                    if (!expected.isEmpty()) expected.append(", ");
                    if (tokensMapping.containsKey(token)) expected.append(tokensMapping.get(token));
                    else expected.append(tokenImage[token]);
                }
            }
        }

        StringBuilder retVal = new StringBuilder("Syntax error at column ")
                .append(currentToken.next.beginColumn).append(".");

        if (showExpected)
            retVal.append("\nExpected type: ").append(expected)
                    .append(", but found ").append(currentValue)
                    .append(", which is a ").append(currentType).append(".");

        return retVal.toString();
    }


}
