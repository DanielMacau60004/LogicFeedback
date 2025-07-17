package com.logic.feedback.nd;

import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.exp.ExpFeedback;
import com.logic.feedback.exp.ExpFeedbacks;
import com.logic.nd.ERule;
import com.logic.nd.asts.AASTND;
import com.logic.nd.asts.IASTND;
import com.logic.nd.asts.INDVisitor;
import com.logic.nd.asts.binary.ASTEExist;
import com.logic.nd.asts.binary.ASTEImp;
import com.logic.nd.asts.binary.ASTENeg;
import com.logic.nd.asts.binary.ASTIConj;
import com.logic.nd.asts.others.ASTEDis;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.asts.unary.*;
import com.logic.others.Utils;

import java.util.*;

public class NDFeedbackVisitor implements INDVisitor<NDFeedback, Void> {

    private final Map<IASTND, NDFeedback> mapper;
    private final boolean isFOL;
    private final FeedbackLevel level;

    NDFeedbackVisitor(boolean isFOL, FeedbackLevel level, Map<IASTND, NDFeedback> mapper) {
        this.isFOL = isFOL;
        this.level = level;
        this.mapper = mapper;
    }

    public static NDFeedback parse(IASTND proof, boolean isFOL, Map<IASTND, NDFeedback> mapper, FeedbackLevel level) {
        return proof.accept(new NDFeedbackVisitor(isFOL, level, mapper), null);
    }

    public static NDFeedback parse(IASTND proof, boolean isFOL, FeedbackLevel level) {
        return proof.accept(new NDFeedbackVisitor(isFOL, level, new HashMap<>()), null);
    }

    public ExpFeedback convertToFeedback(AASTND ast) {
        return isFOL ? ExpFeedbacks.parseFOLFeedback(ast.getConclusion().toString(), level) :
                ExpFeedbacks.parsePLFeedback(ast.getConclusion().toString(), level);
    }

    @Override
    public NDFeedback visit(ASTHypothesis r, Void unused) {
        NDFeedback fb = new NDFeedback(convertToFeedback(r), Collections.singletonList(r.getM()),
                null, ERule.HYPOTHESIS, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTIImp r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(r.getM()),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_IMPLICATION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTINeg r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(r.getM()),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_NEGATION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTERConj r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.ELIM_CONJUNCTION_RIGHT, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTELConj r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.ELIM_CONJUNCTION_LEFT, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTIRDis r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_DISJUNCTION_RIGHT, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTILDis r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_DISJUNCTION_LEFT, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTAbsurdity r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(r.getM()),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.ABSURDITY, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTIConj r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp1().accept(this, unused),
                        r.getHyp2().accept(this, unused)),
                ERule.INTRO_CONJUNCTION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTEDis r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(r.getM(), r.getN()),
                Arrays.asList(r.getHyp1().accept(this, unused),
                        r.getHyp2().accept(this, unused),
                        r.getHyp3().accept(this, unused)),
                ERule.ELIM_DISJUNCTION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTEImp r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp1().accept(this, unused),
                        r.getHyp2().accept(this, unused)),
                ERule.ELIM_IMPLICATION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTENeg r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp1().accept(this, unused),
                        r.getHyp2().accept(this, unused)),
                ERule.ELIM_NEGATION, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTEUni r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.ELIM_UNIVERSAL, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTIExist r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_EXISTENTIAL, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTIUni r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(),
                Arrays.asList(r.getHyp().accept(this, unused)),
                ERule.INTRO_UNIVERSAL, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

    @Override
    public NDFeedback visit(ASTEExist r, Void unused) {
        NDFeedback fb = new NDFeedback(
                convertToFeedback(r),
                Arrays.asList(r.getM()),
                Arrays.asList(r.getHyp1().accept(this, unused),
                        r.getHyp2().accept(this, unused)),
                ERule.ELIM_EXISTENTIAL, r, isFOL);
        mapper.put(r, fb);
        return fb;
    }

}
