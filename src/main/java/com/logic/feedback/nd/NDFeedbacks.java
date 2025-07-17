package com.logic.feedback.nd;

import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.api.LogicAPI;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.feedback.*;
import com.logic.feedback.nd.hints.ConclusionHint;
import com.logic.feedback.nd.hints.FreeVariableHint;
import com.logic.feedback.nd.hints.MarkAssignHint;
import com.logic.nd.NDProofs;
import com.logic.nd.asts.IASTND;
import com.logic.nd.exceptions.*;
import com.logic.others.Utils;
import com.logic.parser.Parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NDFeedbacks {

    public static NDProofFeedback parseNDPLFeedback(String nd, FeedbackLevel level) {
        return parseNDFeedback(nd, level, false, null, null);
    }

    public static NDProofFeedback parseNDFOLFeedback(String nd, FeedbackLevel level) {
        return parseNDFeedback(nd, level, true, null, null);
    }

    public static NDProofFeedback parseNDPLFeedback(IASTND nd, FeedbackLevel level) {
        return parseNDFeedback(nd, level, false, null, null);
    }

    public static NDProofFeedback parseNDFOLFeedback(IASTND nd, FeedbackLevel level) {
        return parseNDFeedback(nd, level, true, null, null);
    }

    public static NDProofFeedback parseNDPLFeedback(String nd, FeedbackLevel level,
                                                    Set<IFormula> premises, IFormula conclusion) {
        return parseNDFeedback(nd, level, false, premises, conclusion);
    }

    public static NDProofFeedback parseNDFOLFeedback(String nd, FeedbackLevel level,
                                                     Set<IFormula> premises, IFormula conclusion) {
        return parseNDFeedback(nd, level, true, premises, conclusion);
    }

    public static NDProofFeedback parseNDPLFeedback(IASTND nd, FeedbackLevel level,
                                                    Set<IFormula> premises, IFormula conclusion) {
        return parseNDFeedback(nd, level, false, premises, conclusion);
    }

    public static NDProofFeedback parseNDFOLFeedback(IASTND nd, FeedbackLevel level,
                                                     Set<IFormula> premises, IFormula conclusion) {
        return parseNDFeedback(nd, level, true, premises, conclusion);
    }

    private static NDProofFeedback parseNDFeedback(String nd, FeedbackLevel level, boolean isFOL,
                                                   Set<IFormula> premises, IFormula conclusion) {
        try {
            Parser parser = new Parser(new ByteArrayInputStream(nd.getBytes()));
            ASTParser astParser = isFOL ? Parser::parseNDFOL : Parser::parseNDPL;
            IASTND proof = astParser.parse(parser);
            return parseNDFeedback(proof, level, isFOL, premises, conclusion);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong! " + e.getMessage(), e);
        }
    }


    private static NDProofFeedback parseNDFeedback(IASTND proof, FeedbackLevel level, boolean isFOL,
                                                   Set<IFormula> premises, IFormula conclusion) {
        try {
            Map<IASTND, NDFeedback> mapper = new HashMap<>();
            NDFeedback feedback = NDFeedbackVisitor.parse(proof, isFOL, mapper, level);

            INDProof ndProof = null;
            boolean error = false;
            boolean hasProblem = premises != null && conclusion != null;

            try {
                ndProof = isFOL ? NDProofs.verifyNDFOLProof(proof) : NDProofs.verifyNDPLProof(proof);
                if (hasProblem) {
                    ndProof = LogicAPI.checkNDProblem(ndProof, premises, conclusion);
                }
            } catch (Exception e) {
                error = true;
                handleException(e, mapper, level, hasProblem);
            }

            return new NDProofFeedback(ndProof, feedback, level, error);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong! " + e.getMessage(), e);
        }
    }

    private static void handleException(Exception e, Map<IASTND, NDFeedback> mapper, FeedbackLevel level,
                                        boolean hasProblem) {
        if (e instanceof CloseMarkException cm) {
            CloseMarkFeedback.produceFeedback(cm, mapper.get(cm.getRule()), level);
        } else if (e instanceof MarkAssignException ma) {
            MarkAssignFeedback.produceFeedback(ma, mapper.get(ma.getRule()), level);
            if(hasProblem) MarkAssignHint.produceHint(ma, mapper.get(ma.getRule()), level);
        } else if (e instanceof FreeVariableException fv) {
            FreeVariableFeedback.produceFeedback(fv, mapper, level);
            if(hasProblem) FreeVariableHint.produceHint(fv, mapper, level);
        } else if (e instanceof InvalidMappingException im) {
            InvalidMappingFeedback.produceFeedback(im, mapper.get(im.getRule()), level);
        } else if (e instanceof NotFreeVariableException nf) {
            NotFreeVariableFeedback.produceFeedback(nf, mapper.get(nf.getRule()), level);
        } else if (e instanceof ConclusionException c) {
            ConclusionFeedback.produceFeedback(c, mapper, level);
            if(hasProblem) ConclusionHint.produceHint(c, mapper, level);
        } else if (e instanceof NDRuleException nr) {
            RuleFeedback.produceFeedback(nr, mapper.get(nr.getRule()), level);
        } else {
            throw new RuntimeException("Unhandled exception: " + e.getClass().getSimpleName(), e);
        }
    }

    @FunctionalInterface
    private interface ASTParser {
        IASTND parse(Parser parser) throws Exception;
    }
}
