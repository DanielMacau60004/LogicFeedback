package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.MissingParenthesisException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;

public class MissingParenthesisFeedback {

    public static void produceFeedback(MissingParenthesisException exception, ExpFeedback feedback,FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM -> "Missing a parenthesis!";
            case HIGH -> "You forgot to close the parentheses!";
            case SOLUTION -> "Consider closing the parenthesis at column " + exception.getColumn() +
                    ", after \"" + exception.getExp() + "\"!";
        });
    }
}
