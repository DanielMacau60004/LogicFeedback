package com.logic.feedback.nd.feedback;

import com.logic.exps.ExpUtils;
import com.logic.exps.asts.IASTExp;
import com.logic.exps.asts.binary.ASTAnd;
import com.logic.exps.asts.binary.ASTConditional;
import com.logic.exps.asts.binary.ASTExistential;
import com.logic.exps.asts.binary.ASTOr;
import com.logic.exps.asts.unary.ASTNot;
import com.logic.exps.asts.unary.ASTParenthesis;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.nd.asts.INDVisitor;
import com.logic.nd.asts.binary.ASTEExist;
import com.logic.nd.asts.binary.ASTEImp;
import com.logic.nd.asts.binary.ASTENeg;
import com.logic.nd.asts.binary.ASTIConj;
import com.logic.nd.asts.others.ASTEDis;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.asts.unary.*;
import com.logic.nd.exceptions.NDRuleException;
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;

public class RuleFeedback implements INDVisitor<Void, Void> {

    private final FeedbackLevel level;
    private final NDFeedback feedback;

    RuleFeedback(NDFeedback feedback, FeedbackLevel level) {
        this.level = level;
        this.feedback = feedback;
    }

    public static void produceFeedback(NDRuleException exception, NDFeedback feedback, FeedbackLevel level) {
        if (level.equals(FeedbackLevel.NONE))
            return;

        feedback.setFeedback(INVALID_RULE);
        if (level.ordinal() > FeedbackLevel.LOW.ordinal())
            exception.getRule().accept(new RuleFeedback(feedback, level), null);
    }

    @Override
    public Void visit(ASTHypothesis rule, Void unused) {
        return null;
    }

    @Override
    public Void visit(ASTIImp rule, Void unused) {
        if (rule.getConclusion() instanceof ASTConditional imp) {
            if (level == FeedbackLevel.SOLUTION) {
                feedback.addPreview(new ASTIImp(
                        new ASTHypothesis(ExpUtils.removeParenthesis(imp.getRight()), null),
                        imp, rule.getM()));
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            }
        } else
            feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_IMP_REQUIRED : ERROR_GENERIC);

        return null;
    }

    @Override
    public Void visit(ASTINeg rule, Void unused) {
        if (!rule.getHyp().getConclusion().equals(ExpUtils.BOT)) {
            feedback.getHypotheses().get(0).getConclusion()
                    .setFeedback(level.ordinal() > 2 ? RULE_BOT_REQUIRED : ERROR_GENERIC);
        }

        if (!(rule.getConclusion() instanceof ASTNot neg)) {
            feedback.getConclusion().setFeedback(level == FeedbackLevel.MEDIUM ?
                    ERROR_GENERIC : RULE_NEG_REQUIRED);
        } else if (level == FeedbackLevel.SOLUTION) {
            feedback.addPreview(new ASTINeg(
                    new ASTHypothesis(ExpUtils.BOT, null),
                    neg, rule.getM()));
            feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
        }
        return null;
    }

    @Override
    public Void visit(ASTERConj rule, Void unused) {
        if (!(rule.getHyp().getConclusion() instanceof ASTAnd and)) {
            feedback.getHypotheses().get(0).getConclusion().setFeedback(level.ordinal() > 2 ? RULE_CONJ_REQUIRED : ERROR_GENERIC);
        } else {
            IASTExp left = ExpUtils.removeParenthesis(and.getLeft());

            if (level.equals(FeedbackLevel.SOLUTION)) {
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
                feedback.addPreview(new ASTERConj(
                        new ASTHypothesis(and, null),
                        left
                ));
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTELConj rule, Void unused) {
        if (!(rule.getHyp().getConclusion() instanceof ASTAnd and)) {
            feedback.getHypotheses().get(0).getConclusion().setFeedback(
                    level.ordinal() > 2 ? RULE_CONJ_REQUIRED : ERROR_GENERIC);
        } else {
            IASTExp right = ExpUtils.removeParenthesis(and.getRight());

            if (level.equals(FeedbackLevel.SOLUTION)) {
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
                feedback.addPreview(new ASTERConj(
                        new ASTHypothesis(and, null),
                        right
                ));
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTIRDis rule, Void unused) {
        IASTExp hyp = rule.getHyp().getConclusion();
        if (!ExpUtils.isLiteral(hyp))
            hyp = new ASTParenthesis(hyp);

        if (!(rule.getConclusion() instanceof ASTOr or)) {
            feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_DIS_REQUIRED : ERROR_GENERIC);
        } else if (!or.getLeft().equals(rule.getHyp().getConclusion())) {

            if (level == FeedbackLevel.SOLUTION) {
                feedback.addPreview(new ASTIRDis(new ASTHypothesis(rule.getHyp().getConclusion(), null),
                        new ASTOr(hyp, or.getRight())
                ));
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            }
        }
        return null;
    }

    @Override
    public Void visit(ASTILDis rule, Void unused) {
        IASTExp hyp = rule.getHyp().getConclusion();
        if (!ExpUtils.isLiteral(hyp))
            hyp = new ASTParenthesis(hyp);

        if (!(rule.getConclusion() instanceof ASTOr or)) {
            feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_DIS_REQUIRED : ERROR_GENERIC);
        } else if (!or.getRight().equals(rule.getHyp().getConclusion())) {

            if (level == FeedbackLevel.SOLUTION) {
                feedback.addPreview(new ASTIRDis(
                        new ASTHypothesis(rule.getHyp().getConclusion(), null),
                        new ASTOr(or.getLeft(), hyp)
                ));
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTAbsurdity rule, Void unused) {
        if (level.equals(FeedbackLevel.MEDIUM)) {
            feedback.getHypotheses().get(0).setFeedback(ERROR_GENERIC);
        } else if (level.equals(FeedbackLevel.HIGH)) {
            feedback.getHypotheses().get(0).getConclusion().setFeedback(RULE_BOT_REQUIRED);
        } else if (level.equals(FeedbackLevel.SOLUTION)) {
            feedback.getHypotheses().get(0).getConclusion().setFeedback(RULE_BOT_REQUIRED);
            feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            feedback.addPreview(new ASTAbsurdity(new ASTHypothesis(ExpUtils.BOT, null),
                    rule.getConclusion(), rule.getM()));
        }

        return null;
    }

    @Override
    public Void visit(ASTIConj rule, Void unused) {
        if (level == FeedbackLevel.MEDIUM) {
            if (!(rule.getConclusion() instanceof ASTAnd and) ||
                    !and.getLeft().equals(rule.getHyp1().getConclusion()) ||
                    !and.getRight().equals(rule.getHyp2().getConclusion())) {
                feedback.getConclusion().setFeedback(ERROR_GENERIC);
            }
        } else if (level == FeedbackLevel.HIGH || level == FeedbackLevel.SOLUTION) {
            if (!(rule.getConclusion() instanceof ASTAnd and)) {
                feedback.getConclusion().setFeedback(RULE_CONJ_REQUIRED);
            } else if (!and.getLeft().equals(rule.getHyp1().getConclusion()) ||
                    !and.getRight().equals(rule.getHyp2().getConclusion())) {
                if (level == FeedbackLevel.SOLUTION) {
                    feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
                    feedback.addPreview(new ASTIConj(
                            new ASTHypothesis(ExpUtils.removeParenthesis(and.getLeft()), null),
                            new ASTHypothesis(ExpUtils.removeParenthesis(and.getRight()), null),
                            and));
                }
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTEDis rule, Void unused) {
        if (rule.getHyp1().getConclusion() instanceof ASTOr && level.equals(FeedbackLevel.SOLUTION)) {
            feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            feedback.addPreview(new ASTEDis(
                    new ASTHypothesis(rule.getHyp1().getConclusion(), null),
                    new ASTHypothesis(rule.getConclusion(), null),
                    new ASTHypothesis(rule.getConclusion(), null),
                    rule.getConclusion(), rule.getM(), rule.getN()));
        }

        if (!(rule.getHyp1().getConclusion() instanceof ASTOr))
            feedback.getHypotheses().get(0).getConclusion().setFeedback(
                    level.ordinal() > 2 ? RULE_DIS_REQUIRED : ERROR_GENERIC
            );


        return null;
    }

    @Override
    public Void visit(ASTEImp rule, Void unused) {
        IASTExp hyp2 = rule.getHyp2().getConclusion();

        if (!(hyp2 instanceof ASTConditional imp)) {
            feedback.getHypotheses().get(1).getConclusion().setFeedback(
                    level.ordinal() > 2 ? RULE_IMP_REQUIRED : ERROR_GENERIC);
        } else {
            IASTExp left = ExpUtils.removeParenthesis(imp.getLeft());
            IASTExp right = ExpUtils.removeParenthesis(imp.getRight());

            if (level.equals(FeedbackLevel.SOLUTION)) {
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
                feedback.addPreview(new ASTEImp(
                        new ASTHypothesis(left, null),
                        new ASTHypothesis(imp, null),
                        right
                ));
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTENeg rule, Void unused) {
        IASTExp h1 = rule.getHyp1().getConclusion();
        IASTExp h2 = rule.getHyp2().getConclusion();

        IASTExp exp = ExpUtils.negate(h1).equals(h2) ? h1 :
                ExpUtils.negate(h2).equals(h1) ? h2 : null;

        if (!rule.getConclusion().equals(ExpUtils.BOT))
            feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_BOT_REQUIRED : ERROR_GENERIC);

        if (exp != null) {
            if (level.equals(FeedbackLevel.SOLUTION)) {
                feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
                feedback.addPreview(new ASTENeg(
                        new ASTHypothesis(exp, null),
                        new ASTHypothesis(ExpUtils.negate(exp), null),
                        ExpUtils.BOT
                ));
            }
        }
        return null;
    }

    @Override
    public Void visit(ASTEUni rule, Void unused) {
        feedback.getHypotheses().get(0).getConclusion().setFeedback(level.ordinal() > 2 ? RULE_UNI_REQUIRED : ERROR_GENERIC);
        return null;
    }

    @Override
    public Void visit(ASTIExist rule, Void unused) {
        feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_EXIST_REQUIRED : ERROR_GENERIC);
        return null;
    }

    @Override
    public Void visit(ASTIUni rule, Void unused) {
        feedback.getConclusion().setFeedback(level.ordinal() > 2 ? RULE_UNI_REQUIRED : ERROR_GENERIC);
        return null;
    }

    @Override
    public Void visit(ASTEExist rule, Void unused) {
        if (!(rule.getHyp1().getConclusion() instanceof ASTExistential))
            feedback.getHypotheses().get(0).getConclusion().setFeedback(
                    level.ordinal() > 2 ? RULE_EXIST_REQUIRED : ERROR_GENERIC);

        if (level.equals(FeedbackLevel.SOLUTION) && rule.getHyp1().getConclusion() instanceof ASTExistential) {
            feedback.setFeedback(feedback.getFeedback() + SOLUTION_SUFFIX);
            feedback.addPreview(new ASTEExist(
                    new ASTHypothesis(rule.getHyp1().getConclusion(), null),
                    new ASTHypothesis(rule.getConclusion(), null),
                    rule.getConclusion(),
                    rule.getM()
            ));
        }

        return null;
    }
}
