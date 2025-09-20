package com.logic.feedback.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.logic.api.LogicAPI;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.api.FeedbackAPI;
import com.logic.feedback.api.INDProofFeedback;
import com.logic.feedback.nd.INDFeedback;

import java.io.IOException;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) throws Exception {
        //System.out.println(FeedbackAPI.parseFOL("x", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());
        //System.out.println(FeedbackAPI.parseFOL("∀ Daniel", FeedbackLevel.SOLUTION).getExpFeedback().getFeedback());

        String ndFolString = "[¬I,1] [¬∀x P(x).\n" +
                "    [∃E,2][⊥.\n" +
                "        [H,][∃x ¬P(x).]\n" +
                "        [¬E][⊥.\n" +
                "            [∀E][P(a).\n" +
                "                [H,1][∀x P(x).]\n" +
                "            ]\n" +
                "            [H,2][¬P(a).]\n" +
                "        ]\n" +
                "    ]\n" +
                "]";
        //LogicAPI.parseNDFOLProof(ndFolString);

        // Parse the NDFOL string
        INDProofFeedback parsedResult = FeedbackAPI.parseNDFOL(ndFolString,
                FeedbackLevel.SOLUTION);

        // Serialize to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
        String jsonOutput = mapper.writeValueAsString(parsedResult.getFeedback());

        // Print JSON
        System.out.println(jsonOutput);

    }
}
