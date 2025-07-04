package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.PredicateArityException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;

public class PredicateArityFeedback {

    public static void produceFeedback(PredicateArityException exception, ExpFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM -> "Invalid predicate arity!";
            case HIGH -> "Invalid function predicate in function \"" + exception.getPredicateName() + "\"!";
            case SOLUTION -> "Invalid function predicate in function \"" + exception.getPredicateName() +
                    "\" found " + exception.getFoundArity() + " but expected " + exception.getExpectedArity() + "!"
            ;
        });
    }
}
