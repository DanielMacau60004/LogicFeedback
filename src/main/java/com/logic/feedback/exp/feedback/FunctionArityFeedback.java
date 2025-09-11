package com.logic.feedback.exp.feedback;

import com.logic.exps.exceptions.FunctionArityException;
import com.logic.feedback.FeedbackLevel;

import com.logic.feedback.exp.ExpFeedback;

public class FunctionArityFeedback  {

    public static void produceFeedback(FunctionArityException exception, ExpFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid expression!";
            case MEDIUM -> "Invalid function arity!";
            case HIGH -> "Invalid arity for \"" + exception.getFunctionName() + "\"!";
            case SOLUTION -> "Function \"" + exception.getFunctionName() + "\" appears with conflicting arities: "
                    + exception.getFoundArity() + " and " + exception.getExpectedArity() + "."
            ;
        });
    }
}
