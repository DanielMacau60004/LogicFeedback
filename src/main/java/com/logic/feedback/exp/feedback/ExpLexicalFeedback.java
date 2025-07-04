package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.ExpLexicalException;

import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;

public class ExpLexicalFeedback {

    public static void produceFeedback(ExpLexicalException exception, ExpFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM, HIGH, SOLUTION -> "Lexical error!";
        });
    }
}
