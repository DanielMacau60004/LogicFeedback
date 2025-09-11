package com.logic.feedback.others;

import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.api.FeedbackAPI;

public class Main {

    public static void main(String[] args) {
        System.out.println(FeedbackAPI.parseFOL("x", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());
        System.out.println(FeedbackAPI.parseFOL("∀ Daniel", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());
    }
}
