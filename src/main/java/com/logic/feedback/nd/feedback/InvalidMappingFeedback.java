package com.logic.feedback.nd.feedback;

import com.logic.exps.asts.others.ASTLiteral;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.asts.unary.ASTEUni;
import com.logic.nd.asts.unary.ASTIExist;
import com.logic.nd.asts.unary.ASTIUni;
import com.logic.nd.exceptions.InvalidMappingException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.feedback.others.Levenshtein;

import java.util.stream.Collectors;

public class InvalidMappingFeedback {

    public static void produceFeedback(InvalidMappingException exception, NDFeedback feedback, FeedbackLevel level) {
        String error = "Error in this rule!";

        boolean isEUni = exception.getRule() instanceof ASTEUni;

        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid rule application!";
            case MEDIUM -> {
                if (isEUni) feedback.getConclusion().setFeedback("Invalid mapping!");
                else feedback.getHypotheses().get(0).getConclusion().setFeedback("Invalid mapping!");
                yield error;
            }
            case HIGH -> {
                String hypError = "No mapping of " + exception.getVariable() + " in " + exception.getFrom() +
                        " that can produce " + exception.getTo() + "!";
                if (isEUni) feedback.getConclusion().setFeedback(hypError);
                else feedback.getHypotheses().get(0).getConclusion().setFeedback(hypError);

                yield error;
            }
            case SOLUTION -> {
                String hypError = "No mapping of " + exception.getVariable() + " in " + exception.getFrom() +
                        " that can produce " + exception.getTo() + "!";
                if (isEUni) feedback.getConclusion().setFeedback(hypError);
                else feedback.getHypotheses().get(0).getConclusion().setFeedback(hypError);

                String exp = Levenshtein.findMostSimilar(exception.getOutcomes().stream()
                        .map(Object::toString).collect(Collectors.toSet()), exception.getTo().toString());

                if (exp != null) {
                    if (exception.getRule() instanceof ASTEUni rule)
                        feedback.addPreview(new ASTEUni(new ASTHypothesis(rule.getHyp().getConclusion(), null), new ASTLiteral(exp)));
                    else if (exception.getRule() instanceof ASTIExist rule)
                        feedback.addPreview(new ASTIExist(new ASTHypothesis(new ASTLiteral(exp), null), rule.getConclusion()));
                    else if (exception.getRule() instanceof ASTIUni rule)
                        feedback.addPreview(new ASTIUni(new ASTHypothesis(new ASTLiteral(exp), null), rule.getConclusion()));

                    yield error + "\nDid you mean:";
                }
                yield error;
            }
        });
    }

}
