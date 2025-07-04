package com.logic.feedback.exp;

import com.logic.api.IFormula;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.api.IFormulaFeedback;

public class FormulaFeedback implements IFormulaFeedback {

    private final IFormula formula;
    private final IExpFeedback expFeedback;
    private final FeedbackLevel feedbackLevel;
    private final boolean hasError;

    public FormulaFeedback(IFormula formula, IExpFeedback expFeedback, FeedbackLevel feedbackLevel, boolean hasError) {
        this.formula = formula;
        this.expFeedback = expFeedback;
        this.feedbackLevel = feedbackLevel;
        this.hasError = hasError;
    }

    @Override
    public boolean hasError() {
        return hasError;
    }

    @Override
    public IFormula getFormula() {
        return formula;
    }

    @Override
    public IExpFeedback getExpFeedback() {
        return expFeedback;
    }

    @Override
    public FeedbackLevel getFeedbackLevel() {
        return feedbackLevel;
    }
}
