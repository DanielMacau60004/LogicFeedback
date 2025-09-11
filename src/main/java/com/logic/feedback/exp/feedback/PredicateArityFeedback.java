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
            case HIGH -> "Invalid arity for \"" + exception.getPredicateName() + "\"!";
            case SOLUTION -> "Predicate \"" + exception.getPredicateName() + "\" was found with conflicting arities: "
                    + exception.getFoundArity() + " and " + exception.getExpectedArity() + ".";
        });
    }
}
