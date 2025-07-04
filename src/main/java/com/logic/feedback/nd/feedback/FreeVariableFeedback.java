package com.logic.feedback.nd.feedback;

import com.logic.exps.asts.others.ASTVariable;
import com.logic.nd.asts.IASTND;
import com.logic.nd.exceptions.FreeVariableException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;

import java.util.Map;

public class FreeVariableFeedback {

    public static void produceFeedback(FreeVariableException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        NDFeedback feedback = mapper.get(exception.getRule());

        String error = "Error in this rule!";
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Something is wrong!";
            case MEDIUM -> {
                for (IASTND h : exception.getFreeHypotheses())
                    mapper.get(h).getConclusion().setFeedback("Something is wrong!");
                yield "Missing side condition!";
            }
            case HIGH, SOLUTION -> {
                ASTVariable from = exception.getFrom();
                ASTVariable variable = exception.getVariable();
                for (IASTND h : exception.getFreeHypotheses())
                    mapper.get(h).getConclusion().setFeedback("Open hypothesis!" +
                            (from != null && variable != null ? "\nVariables: " + from + " ≠ " + variable : "") +
                            (variable != null ? "\nVariable " + variable.getName() + " appears free!" : "\nFree variable!"));

                yield error;
            }
        });
    }
}
