package com.logic.feedback.exp;

import com.logic.api.IFormula;
import com.logic.exps.ExpUtils;
import com.logic.exps.asts.IASTExp;
import com.logic.feedback.AFeedback;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedbackVisitor;
import com.logic.nd.asts.IASTND;


public class ExpFeedback extends AFeedback implements IExpFeedback {

    private final String exp;
    private final Boolean isFOL;

    public ExpFeedback(String exp, boolean isFOL) {
        this.exp = exp;
        this.isFOL = isFOL;
    }

    public ExpFeedback(IFormula formula, boolean isFOL) {
        this(formula.getAST(), isFOL);
    }

    public ExpFeedback(IASTExp exp, boolean isFOL) {
        this.exp = ExpUtils.removeParenthesis(exp).toString();
        this.isFOL = isFOL;
    }

    @Override
    public void addPreview(IASTND preview) {
        previews.add(NDFeedbackVisitor.parse(preview, isFOL, FeedbackLevel.NONE));
    }

    @Override
    public String getExp() {
        return exp;
    }

    @Override
    public boolean isFOL() {
        return isFOL;
    }
}
