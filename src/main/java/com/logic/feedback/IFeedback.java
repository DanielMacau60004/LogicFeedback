package com.logic.feedback;

import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;

import java.util.List;


public interface IFeedback {

    void setFeedback(String feedback);
    boolean hasFeedback();
    String getFeedback();
    List<NDFeedback> getPreviews();
    boolean canGenHints();
    void addPreview(IASTND preview);
    void setGenHints(boolean genHints);

}
