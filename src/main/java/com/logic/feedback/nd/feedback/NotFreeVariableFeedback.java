package com.logic.feedback.nd.feedback;

import com.logic.exps.asts.IASTExp;
import com.logic.exps.asts.others.ASTVariable;
import com.logic.exps.interpreters.FOLReplaceExps;
import com.logic.nd.asts.others.ASTHypothesis;
import com.logic.nd.asts.unary.ASTEUni;
import com.logic.nd.asts.unary.ASTIExist;
import com.logic.nd.exceptions.NotFreeVariableException;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.NDFeedback;
import com.logic.feedback.others.AlphabetSequenceIterator;
import static com.logic.feedback.nd.feedback.FeedbackMessages.*;

public class NotFreeVariableFeedback {

    public static void produceFeedback(NotFreeVariableException exception, NDFeedback feedback, FeedbackLevel level) {
        boolean isEUni = exception.getRule() instanceof ASTEUni;

        feedback.setFeedback(switch (level) {
            case NONE -> "";
            case LOW -> ERROR_GENERIC;
            case MEDIUM -> MISSING_SIDE_CONDITION;
            case HIGH -> {
                String errorHyp = String.format(TERM_NOT_FREE, exception.getTerm(), exception.getFrom(), exception.getTo());
                if (isEUni)
                    feedback.getConclusion().setFeedback(errorHyp);
                else
                    feedback.getHypotheses().get(0).setFeedback(errorHyp);

                yield MISSING_SIDE_CONDITION;
            }
            case SOLUTION -> {
                String errorHyp = String.format(TERM_NOT_FREE, exception.getTerm(), exception.getFrom(), exception.getTo());
                if (isEUni)
                    feedback.getConclusion().setFeedback(errorHyp);
                else
                    feedback.getHypotheses().get(0).setFeedback(errorHyp);

                ASTVariable var;
                IASTExp formula = null;
                AlphabetSequenceIterator it = new AlphabetSequenceIterator('a', 9);
                while (it.hasNext()) {
                    var = new ASTVariable(it.next());
                    if (!exception.getTo().isABoundedVariable(var)) {
                        formula = FOLReplaceExps.replace(exception.getTo().getAST(), exception.getFrom(), var);
                        break;
                    }
                }

                if (exception.getRule() instanceof ASTEUni rule)
                    feedback.addPreview(new ASTEUni(new ASTHypothesis(rule.getHyp().getConclusion(), null), formula));
                else if (exception.getRule() instanceof ASTIExist rule)
                    feedback.addPreview(new ASTIExist(new ASTHypothesis(formula, null), rule.getConclusion()));

                yield MISSING_SIDE_CONDITION + SOLUTION_SUFFIX;
            }
        });
    }


}

