package com.logic.feedback.api;

import com.logic.api.IFormula;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.IExpFeedback;

public interface IFormulaFeedback {

    IFormula getFormula();
    IExpFeedback getExpFeedback();
    FeedbackLevel getFeedbackLevel();

    boolean hasError();

}
