package com.logic.feedback.nd;

import com.logic.api.INDProof;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.api.INDProofFeedback;

public class NDProofFeedback implements INDProofFeedback {

    private final INDProof proof;
    private final INDFeedback ndFeedback;
    private final FeedbackLevel feedbackLevel;
    private final boolean hasError;

    public NDProofFeedback(INDProof proof,INDFeedback ndFeedback,  FeedbackLevel feedbackLevel, boolean hasError) {
        this.proof = proof;
        this.ndFeedback = ndFeedback;
        this.feedbackLevel = feedbackLevel;
        this.hasError = hasError;
    }

    @Override
    public INDProof getProof() {
        return proof;
    }

    @Override
    public INDFeedback getFeedback() {
        return ndFeedback;
    }

    @Override
    public FeedbackLevel getFeedbackLevel() {
        return feedbackLevel;
    }

    @Override
    public boolean hasError() {
        return hasError;
    }
}
