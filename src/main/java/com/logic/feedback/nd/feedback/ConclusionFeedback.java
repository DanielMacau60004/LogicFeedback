package com.logic.feedback.nd.feedback;

import com.logic.api.IFormula;
import com.logic.exps.asts.IASTExp;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.ConclusionException;
import com.logic.others.Env;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConclusionFeedback {

    public static void produceFeedback(ConclusionException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        NDFeedback feedback = mapper.get(exception.getRule());

        feedback.getConclusion().setFeedback(switch (level) {
            case NONE -> "";
            case LOW, MEDIUM -> "This tree doesn't solve the problem!";
            case HIGH, SOLUTION -> {
                Set<IFormula> premises = exception.getProvedPremises();

                for (Map.Entry<ASTHypothesis, Env<String, IASTExp>> entry : exception.getRules().entrySet())
                    produceFeedback(mapper.get(entry.getKey()), level, entry.getKey(), entry.getValue());

                yield "This tree doesn't solve the problem!\n" +
                        "You proved:\n" +
                        (premises != null && !premises.isEmpty()
                                ? "{" + premises.stream().map(Object::toString).collect(Collectors.joining(", ")) + "} "
                                : "") +
                        "⊢ " + exception.getProvedConclusion().toString();
            }
        });
    }


    private static void produceFeedback(NDFeedback feedback, FeedbackLevel level,
                                        ASTHypothesis hypothesis, Env<String, IASTExp> env) {
        String error = "Not a premise.\n" + (hypothesis.getM() == null ? "A mark must be assigned!" : "That mark cannot be closed by any rule!");

        feedback.getConclusion().setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Not a premise!";
            case MEDIUM -> error;
            case HIGH -> {
                Map<String, IASTExp> available = env.mapParent();
                if (!available.isEmpty()) {
                    error += "\nAvailable marks:";

                    available.entrySet().stream()
                            .filter(k -> k.getKey() != null)
                            .forEach(k -> feedback.getConclusion().addPreview(new ASTHypothesis(k.getValue(), k.getKey())));
                }
                yield error;
            }
            case SOLUTION -> {
                Set<String> possibleMarks = env.getMatchingParent(hypothesis.getConclusion())
                        .stream().filter(Objects::nonNull).collect(Collectors.toSet());
                if (!possibleMarks.isEmpty()) {
                    error += "\nConsider:";

                    feedback.getConclusion().addPreview(new ASTHypothesis(hypothesis.getConclusion(),
                            possibleMarks.stream().findFirst().get()));
                } else {
                    Map<String, IASTExp> available = env.mapParent();
                    if (!available.isEmpty()) {
                        error += "\nAvailable marks:";

                        available.entrySet().stream()
                                .filter(k -> k.getKey() != null)
                                .forEach(k -> feedback.getConclusion().addPreview(new ASTHypothesis(k.getValue(), k.getKey())));
                    }
                }
                yield error;
            }
        });
    }

}
