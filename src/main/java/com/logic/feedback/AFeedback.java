package com.logic.feedback;

import com.logic.feedback.nd.NDFeedback;
import com.logic.others.Utils;

import java.util.ArrayList;
import java.util.List;


public abstract class AFeedback implements IFeedback {

    protected String feedback;
    protected boolean genHints;
    protected final List<NDFeedback> previews;

    protected AFeedback() {
        this.previews = new ArrayList<>();
        this.genHints = false;
    }

    @Override
    public void setFeedback(String feedback) {
        this.feedback = Utils.getToken(feedback);
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
    public boolean canGenHints() {
        return genHints;
    }

    @Override
    public void setGenHints(boolean genHints) {
        this.genHints = genHints;
    }

}
