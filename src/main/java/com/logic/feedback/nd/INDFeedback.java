package com.logic.feedback.nd;

import com.logic.feedback.exp.IExpFeedback;
import com.logic.nd.ERule;
import com.logic.nd.asts.IASTND;

import java.util.List;

public interface INDFeedback {

    void setFeedback(String feedback);

    IExpFeedback getConclusion();

    List<String> getMarks();

    ERule getRule();

    List<NDFeedback> getHypotheses();

    List<NDFeedback> getPreviews();

    void addPreview(IASTND preview);

    boolean hasFeedback();
    String getFeedback();

    boolean isFOL();

}
