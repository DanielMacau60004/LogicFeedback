package com.logic.feedback.exp;

import com.logic.api.IFormula;
import com.logic.api.LogicAPI;
import com.logic.exps.exceptions.*;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.feedback.*;

public class ExpFeedbacks {

    public static ExpFeedback parsePLFeedback(String exp, FeedbackLevel level) {
        return handleFeedback(exp, level, LogicAPI::parsePL, false);
    }

    public static ExpFeedback parseFOLFeedback(String exp, FeedbackLevel level) {
        return handleFeedback(exp, level, LogicAPI::parseFOL, true);
    }

    public static FormulaFeedback parsePLFormulaFeedback(String exp, FeedbackLevel level) {
        return handleFormulaFeedback(exp, level, LogicAPI::parsePL, false);
    }

    public static FormulaFeedback parseFOLFormulaFeedback(String exp, FeedbackLevel level) {
        return handleFormulaFeedback(exp, level, LogicAPI::parseFOL, true);
    }

    private static ExpFeedback handleFeedback(String exp, FeedbackLevel level, Parser parser, boolean isFOL) {
        try {
            return new ExpFeedback(parser.parse(exp), isFOL);
        } catch (Exception e) {
            return handleException(exp, e, level, isFOL);
        }
    }

    private static FormulaFeedback handleFormulaFeedback(String exp, FeedbackLevel level, Parser parser, boolean isFOL) {
        IFormula formula = null;
        ExpFeedback feedback;
        boolean error = false;

        try {
            formula = parser.parse(exp);
            feedback = new ExpFeedback(formula, isFOL);
        } catch (Exception e) {
            error = true;
            feedback = handleException(exp, e, level, isFOL);
        }

        return new FormulaFeedback(formula, feedback, level, error);
    }

    private static ExpFeedback handleException(String exp, Exception e, FeedbackLevel level, boolean isFOL) {
        ExpFeedback feedback;

        if (e instanceof ExpSyntaxException ex) {
            feedback = new ExpFeedback(exp, isFOL);
            ExpSyntaxFeedback.produceFeedback(ex, feedback, level);
        } else if (e instanceof ExpLexicalException ex) {
            feedback = new ExpFeedback(exp, isFOL);
            ExpLexicalFeedback.produceFeedback(ex, feedback, level);
        } else if (e instanceof MissingParenthesisException ex) {
            feedback = new ExpFeedback(exp, isFOL);
            MissingParenthesisFeedback.produceFeedback(ex, feedback, level);
        } else if (e instanceof AmbiguousException ex) {
            feedback = new ExpFeedback(ex.getExp(), isFOL);
            AmbiguousFeedback.produceFeedback(ex, feedback, level);
        } else if (e instanceof FunctionArityException ex) {
            feedback = new ExpFeedback(ex.getExp(), isFOL);
            FunctionArityFeedback.produceFeedback(ex, feedback, level);
        } else if (e instanceof PredicateArityException ex) {
            feedback = new ExpFeedback(ex.getExp(), isFOL);
            PredicateArityFeedback.produceFeedback(ex, feedback, level);
        } else {
            throw new RuntimeException("Something went wrong", e);
        }

        return feedback;
    }

    @FunctionalInterface
    private interface Parser {
        IFormula parse(String exp) throws Exception;
    }
}
