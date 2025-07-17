package com.logic.feedback.nd.hints;

import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.exceptions.MarkAssignException;


public class MarkAssignHint {

    public static void produceHint(MarkAssignException exception, NDFeedback feedback, FeedbackLevel level) {
        if(level.ordinal() > 1)
            feedback.getConclusion().setGenHints(true);
    }

}
