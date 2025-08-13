package com.logic.feedback.nd.hints;

import com.logic.api.IFOLFormula;
import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.api.IPLFormula;
import com.logic.exps.asts.others.ASTVariable;
import com.logic.exps.interpreters.FOLReplaceExps;
import com.logic.exps.interpreters.FOLWFFInterpreter;
import com.logic.feedback.FeedbackLevel;
import com.logic.feedback.nd.algorithm.*;
import com.logic.feedback.nd.algorithm.proofs.strategies.SizeTrimStrategy;
import com.logic.feedback.others.AlphabetSequenceIterator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.logic.feedback.nd.hints.HintsMessages.*;

public class Hints {

    public static String generateHint(IFormula mainConclusion, Set<IFormula> mainPremises,
                                      IFormula goalConclusion, Set<IFormula> goalPremises,
                                      FeedbackLevel level, boolean isFOL) {

        String error = NO_HINT_FOUND;
        INDProof genProof = getAlgoProof(mainConclusion, mainPremises, goalConclusion, goalPremises, isFOL);

        if (genProof != null && genProof.numberOfRules() > 0) {
            error = String.format(RULES_AWAY, genProof.numberOfRules());
            if (level.ordinal() > 2)
                error += String.format(TRY_TO_APPLY_RULE, genProof.getAST().getRule());
        }

        return error;
    }


    private static INDProof getAlgoProof(IFormula mainConclusion, Set<IFormula> mainPremises,
                                         IFormula goalConclusion, Set<IFormula> goalPremises, boolean isFOL) {
        INDProof genProof;
        if (!isFOL) {
            genProof = generateHintPL(
                    (IPLFormula) mainConclusion, mainPremises.stream().map(p -> (IPLFormula) p).collect(Collectors.toSet()),
                    (IPLFormula) goalConclusion, goalPremises.stream().map(p -> (IPLFormula) p).collect(Collectors.toSet()));
        } else {
            genProof = generateHintFOL(
                    (IFOLFormula) mainConclusion, mainPremises.stream().map(p -> (IFOLFormula) p).collect(Collectors.toSet()),
                    (IFOLFormula) goalConclusion, goalPremises.stream().map(p -> (IFOLFormula) p).collect(Collectors.toSet()));
        }
        return genProof;
    }

    private static INDProof generateHintPL(IPLFormula mainConclusion, Set<IPLFormula> mainPremises,
                                           IPLFormula goalConclusion, Set<IPLFormula> goalPremises) {

        try {
            return new AlgoProofPLBuilder(
                    new AlgoProofPLMainGoalBuilder(mainConclusion)
                            .addPremises(mainPremises))
                    .setGoal(new AlgoProofPLGoalBuilder(goalConclusion)
                            .addHypotheses(goalPremises))
                    .setAlgoSettingsBuilder(
                            new AlgoSettingsBuilder()
                                    //TODO This will depend on the exercise
                                    .setTrimStrategy(new SizeTrimStrategy()))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private static Set<ASTVariable> collectVariables(IFOLFormula mainConclusion, Set<IFOLFormula> mainPremises,
                                                     IFOLFormula goalConclusion, Set<IFOLFormula> goalPremises) {
        Set<ASTVariable> variables = new HashSet<>();
        mainConclusion.iterateVariables().forEachRemaining(variables::add);
        mainPremises.forEach(p->p.iterateVariables().forEachRemaining(variables::add));
        goalConclusion.iterateVariables().forEachRemaining(variables::add);
        goalPremises.forEach(p->p.iterateVariables().forEachRemaining(variables::add));
        return variables;
    }

    private static INDProof generateHintFOL(IFOLFormula mainConclusion, Set<IFOLFormula> mainPremises,
                                            IFOLFormula goalConclusion, Set<IFOLFormula> goalPremises) {

        Set<ASTVariable> variables = collectVariables(mainConclusion, mainPremises, goalConclusion, goalPremises);
        ASTVariable fresh = createFreshVariable(variables);

        ASTVariable replaceable = new ASTVariable("?");
        variables.remove(replaceable);
        Set<IFOLFormula> placeableExps    = new HashSet<>();
        Set<IFOLFormula> notPlaceableExps = new HashSet<>();

        for (IFOLFormula f : goalPremises) {
            if (f.isAVariable(replaceable)) placeableExps.add(f);
            else notPlaceableExps.add(f);
        }

        if (placeableExps.isEmpty())
            return generateAuxHintFOL(mainConclusion, mainPremises, goalConclusion, goalPremises, fresh);

        for(ASTVariable v : variables) {
            Set<IFOLFormula> newGoalPremises = placeableExps.stream()
                    .map(p -> FOLWFFInterpreter.check(FOLReplaceExps.replace(p.getAST(), replaceable, v)))
                    .collect(Collectors.toSet());
            newGoalPremises.addAll(notPlaceableExps);

            INDProof proof = generateAuxHintFOL(mainConclusion, mainPremises, goalConclusion, newGoalPremises, fresh);
            if (proof != null)
                return proof;
        }

        return null;
    }

    private static ASTVariable createFreshVariable(Set<ASTVariable> variables) {
        AlphabetSequenceIterator it = new AlphabetSequenceIterator('x',Integer.MAX_VALUE);
        while (it.hasNext()) {
            ASTVariable var = new ASTVariable(it.next());
            if(!variables.contains(var)) return var;
        }

        throw new RuntimeException("Failed to create a fresh variable!");
    }


    private static INDProof generateAuxHintFOL(IFOLFormula mainConclusion, Set<IFOLFormula> mainPremises,
                                               IFOLFormula goalConclusion, Set<IFOLFormula> goalPremises,
                                               ASTVariable fresh) {
        try {
            return new AlgoProofFOLBuilder(
                    new AlgoProofFOLMainGoalBuilder(mainConclusion)
                            .addPremises(mainPremises))
                    .setGoal(new AlgoProofFOLGoalBuilder(goalConclusion)
                            .addHypotheses(goalPremises)
                            //TODO hardcoded, some may require one variable others more..
                            //TODO does not keep track of variables that cannot appear open
                            .addTerm(fresh)) //A fresh variable!
                    .setAlgoSettingsBuilder(
                            new AlgoSettingsBuilder()
                                    //TODO This will depend on the exercise
                                    .setTotalClosedNodes(10000)
                                    //.setHypothesesPerGoal(5)
                                    .setTimeout(500)
                                    .setTrimStrategy(new SizeTrimStrategy()))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }


}
