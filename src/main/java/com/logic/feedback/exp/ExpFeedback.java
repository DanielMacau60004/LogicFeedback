package com.logic.feedback.exp;

import com.logic.api.IFormula;
import com.logic.exps.asts.IASTExp;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.feedback.nd.NDFeedbackVisitor;
import com.logic.nd.asts.IASTND;
import com.logic.others.Utils;

import java.util.ArrayList;
import java.util.List;


public class ExpFeedback implements IExpFeedback {

    private final String exp;
    private String feedback;
    private final Boolean isFOL;
    private final List<NDFeedback> previews;

    public ExpFeedback(String exp, boolean isFOL) {
        this.exp = exp;
        this.isFOL = isFOL;
        this.previews = new ArrayList<>();
    }

    public ExpFeedback(IFormula formula, boolean isFOL) {
        this.exp = formula.getAST().toString();
        this.isFOL = isFOL;
        this.previews = new ArrayList<>();
    }

    public ExpFeedback(IASTExp exp, boolean isFOL) {
        this.exp = exp.toString();
        this.isFOL = isFOL;
        this.previews = new ArrayList<>();
    }

    @Override
    public void setFeedback(String feedback) {
        this.feedback = Utils.getToken(feedback);
    }

    @Override
    public String getExp() {
        return exp;
    }

    @Override
    public boolean hasFeedback() {
        return feedback != null;
    }

    @Override
    public String getFeedback() {
        return feedback;
    }

    @Override
    public List<NDFeedback> getPreviews() {
        return previews;
    }

    @Override
    public void addPreview(IASTND preview) {
        previews.add( NDFeedbackVisitor.parse(preview, isFOL, FeedbackLevel.NONE));
    }

    @Override
    public boolean isFOL() {
        return isFOL;
    }
}
