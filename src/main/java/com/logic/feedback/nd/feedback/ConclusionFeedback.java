package com.logic.feedback.nd.feedback;

import com.logic.api.IFormula;
import com.logic.exps.asts.IASTExp;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.feedback.others.Utils;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.ConclusionException;
import com.logic.others.Env;

import java.util.List;
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

                for (ASTHypothesis hypothesis : exception.getUnclosed())
                    produceFeedback(mapper.get(hypothesis), level, hypothesis, hypothesis.getEnv());

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
        String error = "Incomplete proof.\n" + (hypothesis.getM() == null ? "Did you forget to assign a mark?" : "That mark cannot be closed by any rule!");
        feedback.getConclusion().setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Incomplete proof!";
            case MEDIUM -> error;
            case HIGH -> {
                error = getAvailable(feedback, env, error);
                yield error;
            }
            case SOLUTION -> {
                Set<String> possibleMarks = env.getMatchingParent(hypothesis.getConclusion())
                        .stream().filter(Utils::isInteger).collect(Collectors.toSet());
                if (!possibleMarks.isEmpty()) {
                    feedback.getConclusion().setGenHints(false);
                    error += "\nConsider:";

                    feedback.getConclusion().addPreview(new ASTHypothesis(hypothesis.getConclusion(),
                            possibleMarks.stream().findFirst().get()));
                } else
                    error = getAvailable(feedback, env, error);
                yield error;
            }
        });
    }

    private static String getAvailable(NDFeedback feedback, Env<String, IASTExp> env, String error) {
        List<Map.Entry<String, IASTExp>> available =
                env.mapParent().entrySet().stream()
                        .filter(k -> k.getKey() != null && Utils.isInteger(k.getKey()))
                        .toList();

        if (!available.isEmpty()) {
            error += "\nAvailable marks:";

            for (Map.Entry<String, IASTExp> entry : available) {
                feedback.getConclusion().addPreview(new ASTHypothesis(entry.getValue(), entry.getKey()));
            }
        }
        return error;
    }

}