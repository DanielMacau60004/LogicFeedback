package com.logic.feedback.nd;

import com.logic.feedback.IFeedback;
import com.logic.feedback.exp.IExpFeedback;
import com.logic.nd.ERule;
import com.logic.nd.asts.IASTND;

import java.util.List;
import java.util.Map;

public interface INDFeedback extends IFeedback {

    IExpFeedback getConclusion();

    List<String> getMarks();

    ERule getRule();

    List<NDFeedback> getHypotheses();

    void addPreview(IASTND preview);


    Map<String, String> getEnv();

    boolean isFOL();

}
