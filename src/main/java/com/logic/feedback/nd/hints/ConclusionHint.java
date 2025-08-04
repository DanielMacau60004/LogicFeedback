package com.logic.feedback.nd.hints;

import com.logic.api.IFOLFormula;
import com.logic.api.LogicAPI;
import com.logic.exps.asts.IASTExp;
import com.logic.exps.asts.others.ASTVariable;
import com.logic.exps.interpreters.FOLReplaceExps;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.ConclusionException;
import com.logic.others.Env;
import com.logic.others.Utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConclusionHint {

    public static void produceHint(ConclusionException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level,
                                   boolean isFOL) {
        if (level.ordinal() > 1)
            for (ASTHypothesis h : exception.getUnclosed())
                produceFeedback(mapper.get(h), level, h, h.getEnv(), isFOL);
    }

    private static void produceFeedback(NDFeedback feedback, FeedbackLevel level,
                                        ASTHypothesis hypothesis, Env<String, IASTExp> env, boolean isFOL) {

        Set<String> possibleMarks = env.getMatchingParent(hypothesis.getConclusion())
                .stream().filter(Objects::nonNull).collect(Collectors.toSet());

        IASTExp exp = hypothesis.getConclusion();
        if (isFOL && possibleMarks.isEmpty()) {
            IFOLFormula fol = LogicAPI.parseFOL(exp.toString());
            ASTVariable placeholder = new ASTVariable("?");
            Iterator<ASTVariable> it = fol.iterateVariables();

            while (it.hasNext() && possibleMarks.isEmpty()) {
                IASTExp var = FOLReplaceExps.replace(exp, it.next(), placeholder);
                possibleMarks = env.getMatchingParent(var)
                        .stream().filter(Objects::nonNull).collect(Collectors.toSet());
            }
        }

        if (possibleMarks.isEmpty())
            feedback.getConclusion().setGenHints(true);

    }

}