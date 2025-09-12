package com.logic.feedback.nd.feedback;

import com.logic.exps.asts.others.ASTVariable;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.IFeedback;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;
import com.logic.nd.exceptions.FreeVariableException;

import java.util.Map;
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;

public class FreeVariableFeedback {

    public static void produceFeedback(FreeVariableException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        NDFeedback feedback = mapper.get(exception.getRule());

        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> ERROR_GENERIC;
            case MEDIUM -> {
                for (IASTND h : exception.getFreeHypotheses()) {
                    IFeedback f = mapper.get(h).getConclusion();
                    f.setFeedback(ERROR_GENERIC);
                }
                yield MISSING_SIDE_CONDITION;
            }
            case HIGH, SOLUTION -> {
                ASTVariable from = exception.getFrom();
                ASTVariable variable = exception.getVariable();
                for (IASTND h : exception.getFreeHypotheses()) {
                    IFeedback f = mapper.get(h).getConclusion();

                    StringBuilder msg = new StringBuilder();
                    if (from != null && variable != null) {
                        msg.append(String.format(VARIABLES_NOT_EQUAL, from, variable));
                    }
                    if (variable != null) {
                        msg.append(String.format(VARIABLE_APPEARS_FREE, variable.getName()));
                    } else {
                        msg.append(FREE_VARIABLE);
                    }

                    f.setFeedback(msg.toString());
                }
                yield MISSING_SIDE_CONDITION;
            }
        });
    }

}
