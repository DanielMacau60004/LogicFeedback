package com.logic.feedback.api;

import com.logic.api.INDProof;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.INDFeedback;

public interface INDProofFeedback {

    INDProof getProof();
    INDFeedback getFeedback();
    FeedbackLevel getFeedbackLevel();

    boolean hasError();
}
