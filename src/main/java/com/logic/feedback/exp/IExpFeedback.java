package com.logic.feedback.exp;

import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.IASTND;

import java.util.List;

public interface IExpFeedback {

    void setFeedback(String feedback);

    String getExp();

    boolean hasFeedback();

    String getFeedback();

    boolean isFOL();

    List<NDFeedback> getPreviews();

    void addPreview(IASTND preview);
}
