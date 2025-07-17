package com.logic.feedback.nd.hints;

import com.logic.exps.asts.others.ASTVariable;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.IFeedback;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;
import com.logic.nd.exceptions.FreeVariableException;

import java.util.Map;

public class FreeVariableHint {

    public static void produceHint(FreeVariableException exception, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        if (level.ordinal() > 1)
            for (IASTND h : exception.getFreeHypotheses())
                mapper.get(h).getConclusion().setGenHints(true);
    }
}
