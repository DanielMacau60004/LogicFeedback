package com.logic.feedback.api;

import com.logic.api.IFormula;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedbacks;
import com.logic.feedback.nd.NDFeedbacks;

import java.util.Set;

public class FeedbackAPI {

    public static IFormulaFeedback parsePL(String exp, FeedbackLevel level) {
        return ExpFeedbacks.parsePLFormulaFeedback(exp, level);
    }

    public static IFormulaFeedback parseFOL(String exp, FeedbackLevel level) {
        return ExpFeedbacks.parseFOLFormulaFeedback(exp, level);
    }

    public static INDProofFeedback parseNDPL(String nd, FeedbackLevel level) {
        return NDFeedbacks.parseNDPLFeedback(nd, level);
    }

    public static INDProofFeedback parseNDFOL(String nd, FeedbackLevel level) {
        return NDFeedbacks.parseNDFOLFeedback(nd, level);
    }

    public static INDProofFeedback parseNDPLProblem(String nd, FeedbackLevel level, Set<IFormula> premises, IFormula conclusion) {
        return NDFeedbacks.parseNDPLFeedback(nd, level, premises, conclusion);
    }

    public static INDProofFeedback parseNDFOLProblem(String nd, FeedbackLevel level, Set<IFormula> premises, IFormula conclusion) {
        return NDFeedbacks.parseNDFOLFeedback(nd, level, premises, conclusion);
    }
}
