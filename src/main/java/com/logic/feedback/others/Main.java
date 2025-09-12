package com.logic.feedback.others;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.api.FeedbackAPI;

public class Main {

    public static void main(String[] args) throws Exception {
        //System.out.println(FeedbackAPI.parseFOL("x", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());
        //System.out.println(FeedbackAPI.parseFOL("∀ Daniel", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());

        String ndFolString = "[→I,1] [∃x ¬P(x) → ¬∀x P(x). [¬I,2] [¬∀x P(x). [∃E,3] [⊥. [H,1] [∃x ¬P(x).] " +
                "[¬E] [⊥. [H,] [P(x).] [H,3] [¬P(x).]]]]]";

        // Parse the NDFOL string
        Object parsedResult = FeedbackAPI.parseNDFOL(ndFolString, FeedbackLevel.SOLUTION);

        // Serialize to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
        String jsonOutput = mapper.writeValueAsString(parsedResult);

        // Print JSON
        System.out.println(jsonOutput);

    }
}
