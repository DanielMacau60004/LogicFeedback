package com.logic.feedback.nd.feedback;

import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.MarkAssignException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;


public class MarkAssignFeedback {

    public static void produceFeedback(MarkAssignException exception, NDFeedback feedback, FeedbackLevel level) {
        feedback.getConclusion().setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid mark!";
            case MEDIUM -> "Mark " + exception.getMark() + " already assigned!";
            case HIGH -> "Mark " + exception.getMark() + " assigned to " + exception.getExpected() + "!";
            case SOLUTION ->
                    "Mark " + exception.getMark() + " assigned to " + exception.getExpected() + "!\nConsider assigning a different mark!";
        });
    }

}
