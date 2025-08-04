package com.logic.feedback.nd.feedback;

import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.MarkAssignException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;


public class MarkAssignFeedback {

    public static void produceFeedback(MarkAssignException exception, NDFeedback feedback, FeedbackLevel level) {
        feedback.getConclusion().setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> INVALID_MARK;
            case MEDIUM -> String.format(MARK_ALREADY_ASSIGNED, exception.getMark());
            case HIGH -> String.format(MARK_ASSIGNED_TO, exception.getMark(), exception.getExpected());
            case SOLUTION -> String.format(MARK_ASSIGNED_TO, exception.getMark(), exception.getExpected()) + CONSIDER_DIFFERENT_MARK;
        });
    }


}
