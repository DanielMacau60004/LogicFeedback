package com.logic.feedback.nd.feedback;

import com.logic.feedback.others.Utils;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.CloseMarkException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CloseMarkFeedback {

    public static void produceFeedback(CloseMarkException exception, NDFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> "Invalid mark!";
            case MEDIUM -> "This rule cannot close mark " + exception.getMark() + "!";
            case HIGH -> {
                String error = "This rule cannot close mark " + exception.getMark() + "!";
                if (exception.getExpected() != null) error += "\nOnly marks assigned to " + exception.getExpected() + "!";
                yield error;
            }
            case SOLUTION -> {
                String error = "This rule cannot close mark " + exception.getMark() + "!";
                if (exception.getExpected() != null) error += "\nOnly marks assigned to " + exception.getExpected() + "!";

                Set<String> possibleMarks = exception.getEnv()
                        .getMatchingChild(exception.getExpected()).stream()
                        .filter(Utils::isInteger)
                        .collect(Collectors.toSet());
                if (!possibleMarks.isEmpty()) {
                    error += "\nConsider:";
                    feedback.addPreview(new ASTHypothesis(exception.getExpected(), possibleMarks.stream().findFirst().get()));
                }

                yield error;
            }
        });
    }


}
