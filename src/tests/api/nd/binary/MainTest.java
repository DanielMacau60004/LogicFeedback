package api.nd.binary;

import com.logic.api.IFormula;
import com.logic.api.INDProof;
import com.logic.api.LogicAPI;
import com.logic.feedback.nd.algorithm.tests.*;
import com.logic.others.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

public class MainTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "((a → a) ∧ (a → a)) ∧ ((a → a) ∧ (a → a))",
            //"(p ∧ q) → (r ∨ s), (p → r) ∨ (q → s)",
            "((p → q) → (¬p ∨ q)) ∧ ((¬p ∨ q) → (p → q))",
            "(((p ∧ q) ∨ (p ∧ ¬q)) ∨ (¬p ∧ q)) ∨ (¬p ∧ ¬q)",
            //"a → ¬¬ a ",
            "(((p → (q ∨ s)) ∧ ((p ∧ r) → s)) ∧ ((s ∧ t) → (p ∨ ¬q))) → (((p ∧ (q → r)) → s) ∧ (((q ∧ s) ∧ t) → p))",
            "((p ∨ q) ∨ (r ∨ s)) → ((p ∨ s) ∨ (r ∨ q))",
            //"(s ∨ t) → (s → ¬t), (s → ¬t) → (t → k), s ∨ t, s ∨ k",
            "(¬a ∨ ¬b) → ((c → (a ∧ b)) → ¬c)"
    })
    void test(String args) {
        IFormula conclusion = LogicAPI.parsePL(args);
        Set<IFormula> premises = new HashSet<>();
        //for (String part : premisesStr.split(","))
        //    premises.add(LogicAPI.parsePL(part.trim()));

        Set<IFormula> expressions = new HashSet<>(premises);
        expressions.add(conclusion);

        BinaryMap map = new BinaryMap();
        BinaryGoalObj initialGoal = new BinaryGoalObj(map.storeFormula(conclusion), map);

        BinaryTransitionGraph bg = new BinaryTransitionGraph(map, Set.of());
        bg.build(expressions);

        //System.out.println(Utils.getToken(bg.toString()));

        //BinaryProofGraph bpg = new BinaryProofGraph(map, bg);
        BinaryProofGraphv2 bpg = new BinaryProofGraphv2(map, bg);
        bpg.build(initialGoal);

        //System.out.println(Utils.getToken(bpg.toString()));

        //BinarySizeTrim tg = new BinarySizeTrim(bg, bpg, map);
        //BinaryHeightTrim tg = new BinaryHeightTrim(bg, bpg, map);
        //tg.build();

        //System.out.println(Utils.getToken(tg.toString()));

        BinarySolution sol = new BinarySolution(bpg.getGraph(), map);
        INDProof proof = sol.findSolution(initialGoal);
        //System.out.println("Formulas: " + map.formulas.size() + ", Assumptions: " + map.assumptions.size());
        System.out.println("Size: " + proof.size() + " Height: " + proof.height());
        System.out.println(proof);
    }
}
