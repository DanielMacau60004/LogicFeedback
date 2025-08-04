package com.logic.feedback.nd.feedback;

import com.logic.feedback.others.Utils;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.exceptions.CloseMarkException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;
public class CloseMarkFeedback {

    public static void produceFeedback(CloseMarkException exception, NDFeedback feedback, FeedbackLevel level) {
        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> INVALID_MARK;
            case MEDIUM -> String.format(RULE_CANNOT_CLOSE_MARK, exception.getMark());
            case HIGH -> {
                String error = String.format(RULE_CANNOT_CLOSE_MARK, exception.getMark());
                if (exception.getExpected() != null) {
                    error += String.format(ONLY_MARKS_ASSIGNED_TO, exception.getExpected());
                }
                yield error;
            }
            case SOLUTION -> {
                String error = String.format(RULE_CANNOT_CLOSE_MARK, exception.getMark());
                if (exception.getExpected() != null) {
                    error += String.format(ONLY_MARKS_ASSIGNED_TO, exception.getExpected());
                }

                Set<String> possibleMarks = exception.getEnv()
                        .getMatchingChild(exception.getExpected()).stream()
                        .filter(Utils::isInteger)
                        .collect(Collectors.toSet());

                if (!possibleMarks.isEmpty()) {
                    error += CONSIDER;
                    feedback.addPreview(new ASTHypothesis(exception.getExpected(), possibleMarks.stream().findFirst().get()));
                }

                yield error;
            }
        });
    }



}
