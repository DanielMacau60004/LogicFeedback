package com.logic.feedback.nd.hints;

import com.logic.exps.asts.IASTExp;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.ConclusionException;
import com.logic.others.Env;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConclusionHint {

    public static void produceHint(ConclusionException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        if (level.ordinal() > 1)
            for (ASTHypothesis h : exception.getUnclosed()) {
                mapper.get(h).getConclusion().setGenHints(true);
                produceFeedback(mapper.get(h), level, h, h.getEnv());
            }
    }

    private static void produceFeedback(NDFeedback feedback, FeedbackLevel level,
                                        ASTHypothesis hypothesis, Env<String, IASTExp> env) {

        Set<String> possibleMarks = env.getMatchingParent(hypothesis.getConclusion())
                .stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (!possibleMarks.isEmpty())
            feedback.getConclusion().setGenHints(false);

    }

}