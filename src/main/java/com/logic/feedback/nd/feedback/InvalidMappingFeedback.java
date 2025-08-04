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
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;


public class InvalidMappingFeedback {

    public static void produceFeedback(InvalidMappingException exception, NDFeedback feedback, FeedbackLevel level) {
        boolean isEUni = exception.getRule() instanceof ASTEUni;

        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> ERROR_GENERIC;
            case MEDIUM -> {
                if (isEUni)
                    feedback.getConclusion().setFeedback(INVALID_MAPPING);
                else
                    feedback.getHypotheses().get(0).getConclusion().setFeedback(INVALID_MAPPING);
                yield ERROR_GENERIC;
            }
            case HIGH -> {
                String hypError = String.format(NO_MAPPING, exception.getVariable(), exception.getFrom(), exception.getTo());
                if (isEUni)
                    feedback.getConclusion().setFeedback(hypError);
                else
                    feedback.getHypotheses().get(0).getConclusion().setFeedback(hypError);
                yield INVALID_RULE;
            }
            case SOLUTION -> {
                String hypError = String.format(NO_MAPPING, exception.getVariable(), exception.getFrom(), exception.getTo());
                if (isEUni)
                    feedback.getConclusion().setFeedback(hypError);
                else
                    feedback.getHypotheses().get(0).getConclusion().setFeedback(hypError);

                String exp = Levenshtein.findMostSimilar(
                        exception.getOutcomes().stream().map(Object::toString).collect(Collectors.toSet()),
                        exception.getTo().toString()
                );

                if (exp != null) {
                    if (exception.getRule() instanceof ASTEUni rule)
                        feedback.addPreview(new ASTEUni(new ASTHypothesis(rule.getHyp().getConclusion(), null), new ASTLiteral(exp)));
                    else if (exception.getRule() instanceof ASTIExist rule)
                        feedback.addPreview(new ASTIExist(new ASTHypothesis(new ASTLiteral(exp), null), rule.getConclusion()));
                    else if (exception.getRule() instanceof ASTIUni rule)
                        feedback.addPreview(new ASTIUni(new ASTHypothesis(new ASTLiteral(exp), null), rule.getConclusion()));

                    yield INVALID_RULE + DID_YOU_MEAN;
                }

                yield INVALID_RULE;
            }
        });
    }


}
