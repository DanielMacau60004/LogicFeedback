package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.AmbiguousException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;

public class AmbiguousFeedback  {

    public static void produceFeedback(AmbiguousException exception, ExpFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM -> "Ambiguous expression!";
            case HIGH -> "Ambiguous expression, consider adding parenthesis!";
            case SOLUTION -> "Ambiguous expression, consider adding parentheses around " + exception.getBefore() + "!";
        });
    }
}
