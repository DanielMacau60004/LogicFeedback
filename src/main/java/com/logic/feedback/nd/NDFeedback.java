package com.logic.feedback.nd;

import com.logic.exps.asts.IASTExp;
import com.logic.feedback.AFeedback;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.IExpFeedback;
import com.logic.nd.ERule;
import com.logic.nd.asts.IASTND;
import com.logic.others.Env;
import com.logic.others.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NDFeedback extends AFeedback implements INDFeedback {

    private final IExpFeedback conclusion;
    private final List<String> marks;
    private final ERule rule;
    private final List<NDFeedback> hypotheses;
    private final boolean isFOL;
    private final IASTND nd;

    public NDFeedback(IExpFeedback conclusion, List<String> marks, List<NDFeedback> hypotheses, ERule rule,
                      IASTND nd, boolean isFOL) {
        this.conclusion = conclusion;
        this.marks = marks;
        this.hypotheses = hypotheses;
        this.rule = rule;
        this.nd = nd;
        this.isFOL = isFOL;
    }

    public NDFeedback(IExpFeedback conclusion, ERule rule, IASTND nd, boolean isFOL) {
        this(conclusion, new ArrayList<>(), new ArrayList<>(), rule, nd, isFOL);
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
        previews.add(NDFeedbackVisitor.parse(preview, isFOL, FeedbackLevel.NONE));
    }

    @Override
    public Map<String, String> getEnv() {
        if (nd.getEnv() == null) return null;
        return nd.getEnv().mapParent().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Utils.getToken(entry.getValue().toString())
                ));
    }

    @Override
    public boolean isFOL() {
        return isFOL;
    }
}
