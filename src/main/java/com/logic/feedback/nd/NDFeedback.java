package com.logic.feedback.nd;

import com.logic.feedback.FeedbackLevel;
import com.logic.nd.ERule;
import com.logic.nd.asts.IASTND;
import com.logic.feedback.exp.IExpFeedback;
import com.logic.others.Utils;

import java.util.ArrayList;
import java.util.List;

public class NDFeedback implements INDFeedback  {

    private final IExpFeedback conclusion;
    private final List<String> marks;
    private final ERule rule;
    private final List<NDFeedback> hypotheses;
    private  String feedback;
    private final List<NDFeedback> previews;
    private final boolean isFOL;

    public NDFeedback(IExpFeedback conclusion, List<String> marks, List<NDFeedback> hypotheses, ERule rule,
                      boolean isFOL) {
        this.conclusion = conclusion;
        this.marks = marks;
        this.hypotheses = hypotheses;
        this.rule = rule;

        this.previews = new ArrayList<>();
        this.isFOL = isFOL;
    }

    public NDFeedback(IExpFeedback conclusion,  ERule rule, boolean isFOL) {
        this(conclusion, new ArrayList<>(), new ArrayList<>(), rule, isFOL);
    }

    @Override
    public void setFeedback(String feedback) {
        this.feedback = Utils.getToken(feedback);
    }

    @Override
    public IExpFeedback getConclusion() {
        return conclusion;
    }

    @Override
    public List<String> getMarks() {
        return marks;
    }

    @Override
    public ERule getRule() {
        return rule;
    }

    @Override
    public List<NDFeedback> getHypotheses() {
        return hypotheses;
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
    public boolean hasFeedback() {
        return feedback != null;
    }

    @Override
    public String getFeedback() {
        return feedback;
    }

    @Override
    public boolean isFOL() {
        return isFOL;
    }
}
