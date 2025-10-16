package com.logic.feedback.nd.algorithm.tests;

import com.logic.api.IFormula;
import com.logic.exps.ExpUtils;
import com.logic.exps.asts.IASTExp;
import com.logic.exps.asts.binary.ASTAnd;
import com.logic.exps.asts.binary.ASTConditional;
import com.logic.exps.asts.binary.ASTOr;
import com.logic.exps.asts.unary.ASTNot;
import com.logic.exps.interpreters.PLWFFInterpreter;
import com.logic.nd.ERule;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

import java.util.*;

public class BinaryTransitionGraph {

    private final BinaryMap map;
    protected final Set<ERule> forbiddenRules;

    protected final Short2ObjectMap<Set<BinaryTransitionEdge>> graph;
    protected final Short2ObjectMap<Set<BinaryTransitionEdge>> graphInverted;
    protected final Short2BooleanMap explored;

    protected final Set<ASTOr> disjunctions;

    public BinaryTransitionGraph(BinaryMap map, Set<ERule> forbiddenRules) {
        this.map = map;
        this.forbiddenRules = forbiddenRules;
        this.explored = new Short2BooleanOpenHashMap();
        this.graph = new Short2ObjectOpenHashMap<>();
        this.graphInverted = new Short2ObjectOpenHashMap<>();
        this.disjunctions = new HashSet<>();
    }

    public void build(Set<IFormula> expressions) {
        //Add all nodes necessary to generate the sub nodes
        addNode(ExpUtils.BOT, true);
        expressions.forEach(p -> addNode(p.getAST(), true));

        //Add the disjunction rules to each node
        if (!forbiddenRules.contains(ERule.ELIM_DISJUNCTION))
            graph.forEach((e, ts) -> ts.addAll(disjunctions.stream().map(d -> disjunctionERule(map.getFormulas(e).getAST(), d)).toList()));

        //Build inverted
        graph.forEach((k, v) -> v.forEach(this::addInvertedEdge));


    }

    protected short getFormula(IASTExp exp) {
        IFormula formula = PLWFFInterpreter.check(exp);
        map.storeFormula(formula);
        return map.storeFormula(formula);
    }

    protected void addNode(IASTExp node, boolean canGen) {
        short index = getFormula(node);
        if (explored.containsKey(index) && explored.get(index)) return;

        explored.put(index, canGen);
        graph.put(index, new TreeSet<>());

        if (node instanceof ASTOr or) disjunctions.add(or);

        if (canGen) {
            genBottomUp(node);
            genTopDown(node);
        }
    }

    protected void addEdge(BinaryTransitionEdge edge, boolean canGen) {
        if (forbiddenRules.contains(edge.getRule())) return;

        addNode(map.getFormulas(edge.getFrom()).getAST(), canGen);
        edge.getTransitions().forEach(t -> addNode(map.getFormulas(t.getTo()).getAST(), canGen));
        graph.get(edge.getFrom()).add(edge);
    }

    private void addInvertedEdge(BinaryTransitionEdge edge) {
        edge.getTransitions().forEach(t -> {
            Set<BinaryTransitionEdge> edges = graphInverted.get(t.getTo());
            if (edges == null) edges = new LinkedHashSet<>();
            edges.add(edge);
            graphInverted.put(t.getTo(), edges);
        });
    }

    private void absurdityRule(IASTExp exp) {
        if (exp.equals(ExpUtils.BOT)) return;

        Short neg = getFormula(ExpUtils.negate(exp));
        addEdge(new BinaryTransitionEdge(ERule.ABSURDITY, getFormula(exp), (short) 0, neg), true);

        addEdge(new BinaryTransitionEdge(ERule.ELIM_NEGATION, (short) 0).addTransition(getFormula(exp)).addTransition(neg), false);
        addEdge(new BinaryTransitionEdge(ERule.INTRO_NEGATION, neg, (short) 0, getFormula(exp)), false);
    }

    private void negationIRule(ASTNot not) {
        Short notNeg = getFormula(ExpUtils.invert(not));

        addEdge(new BinaryTransitionEdge(ERule.INTRO_NEGATION, getFormula(not), (short) 0, notNeg), true);
        addEdge(new BinaryTransitionEdge(ERule.ELIM_NEGATION, (short) 0).addTransition(notNeg).addTransition(getFormula(not)), true);
    }

    private void implicationIRule(ASTConditional imp) {
        Short right = getFormula(ExpUtils.removeParenthesis(imp.getRight()));
        Short left = getFormula(ExpUtils.removeParenthesis(imp.getLeft()));
        addEdge(new BinaryTransitionEdge(ERule.INTRO_IMPLICATION, getFormula(imp), right, left), true);
    }

    private void disjunctionIRule(ASTOr or) {
        Short right = getFormula(ExpUtils.removeParenthesis(or.getRight()));
        Short left = getFormula(ExpUtils.removeParenthesis(or.getLeft()));

        addEdge(new BinaryTransitionEdge(ERule.INTRO_DISJUNCTION_RIGHT, getFormula(or), left), true);
        addEdge(new BinaryTransitionEdge(ERule.INTRO_DISJUNCTION_LEFT, getFormula(or), right), true);
    }

    private BinaryTransitionEdge disjunctionERule(IASTExp exp, ASTOr or) {
        Short orExp = getFormula(ExpUtils.removeParenthesis(or));
        Short right = getFormula(ExpUtils.removeParenthesis(or.getRight()));
        Short left = getFormula(ExpUtils.removeParenthesis(or.getLeft()));

        Short expF = getFormula(exp);

        return new BinaryTransitionEdge(ERule.ELIM_DISJUNCTION, getFormula(exp)).addTransition(orExp).addTransition(expF, left).addTransition(expF, right);
    }

    private void implicationERule(ASTConditional imp) {
        IASTExp right = ExpUtils.removeParenthesis(imp.getRight());
        Short left = getFormula(ExpUtils.removeParenthesis(imp.getLeft()));
        Short expF = getFormula(imp);

        addEdge(new BinaryTransitionEdge(ERule.ELIM_IMPLICATION, getFormula(right)).addTransition(left).addTransition(expF), true);
    }

    private void conjunctionERule(ASTAnd and) {
        IASTExp right = ExpUtils.removeParenthesis(and.getRight());
        IASTExp left = ExpUtils.removeParenthesis(and.getLeft());
        Short expF = getFormula(and);

        addEdge(new BinaryTransitionEdge(ERule.ELIM_CONJUNCTION_RIGHT, getFormula(left), expF), true);
        addEdge(new BinaryTransitionEdge(ERule.ELIM_CONJUNCTION_LEFT, getFormula(right), expF), true);
    }

    private void conjunctionIRule(ASTAnd and) {
        Short right = getFormula(ExpUtils.removeParenthesis(and.getRight()));
        Short left = getFormula(ExpUtils.removeParenthesis(and.getLeft()));

        addEdge(new BinaryTransitionEdge(ERule.INTRO_CONJUNCTION, getFormula(and)).addTransition(left).addTransition(right), true);
    }

    protected void genBottomUp(IASTExp exp) {
        exp = ExpUtils.removeParenthesis(exp);
        absurdityRule(exp);

        if (exp instanceof ASTConditional imp) implicationIRule(imp);
        else if (exp instanceof ASTNot not) negationIRule(not);
    }

    protected void genTopDown(IASTExp exp) {
        exp = ExpUtils.removeParenthesis(exp);

        if (exp instanceof ASTConditional imp) implicationERule(imp);
        else if (exp instanceof ASTAnd and) {
            conjunctionERule(and);
            conjunctionIRule(and);
        } else if (exp instanceof ASTOr or) disjunctionIRule(or);

    }

    public Set<BinaryTransitionEdge> getEdges(short exp) {
        return graph.get(exp);
    }

    public Set<BinaryTransitionEdge> getInvertedEdges(short exp) {
        return graphInverted.get(exp);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Disjunctions[").append(disjunctions.size()).append("]: ").append(disjunctions).append("\n");

        appendGraphInfo(str, "Graph", graph);
        str.append("\n");
        appendGraphInfo(str, "Graph Inverted", graphInverted);

        return str.toString();
    }

    private void appendGraphInfo(StringBuilder str, String label, Map<Short, Set<BinaryTransitionEdge>> graphData) {
        str.append(label).append(":\n");
        str.append("Total nodes: ").append(graphData.size()).append("\n");
        str.append("Total edges: ").append(graphData.values().stream().mapToInt(Set::size).sum()).append("\n");

        for (Map.Entry<Short, Set<BinaryTransitionEdge>> entry : graphData.entrySet()) {
            str.append(map.getFormulas(entry.getKey())).append(": ").append(entry.getValue().size()).append("\n");
            for (BinaryTransitionEdge edge : entry.getValue()) {
                str.append(edge.toString(map)).append("]\n");
            }
        }
    }


}
