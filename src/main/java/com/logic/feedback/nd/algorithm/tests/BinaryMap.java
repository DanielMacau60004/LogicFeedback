package com.logic.feedback.nd.algorithm.tests;

import com.logic.api.IFormula;
import com.logic.exps.ExpUtils;
import com.logic.exps.interpreters.PLWFFInterpreter;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class BinaryMap {

    //TODO maybe consider guava BiMap to do this...
    public final Map<Short, IFormula> formulas;
    private final Map<IFormula, Short> formulasIndexes;

    public final Int2ObjectOpenHashMap<BinarySet> assumptions;
    public final Object2IntMap<BinarySet> assumptionsIndexes;

    public BinaryMap() {
        this.formulas = new HashMap<>();
        this.formulasIndexes = new HashMap<>();
        this.assumptions = new Int2ObjectOpenHashMap<>();
        this.assumptionsIndexes = new Object2IntOpenHashMap<>();

        storeFormula(PLWFFInterpreter.check(ExpUtils.BOT));
        storeAssumption(new BinarySet());
    }

    public Short storeFormula(IFormula formula) {
        Short value = formulasIndexes.get(formula);
        if (value != null) return value;

        value = (short) formulas.size();
        formulas.put(value, formula);
        formulasIndexes.put(formula, value);

        return value;
    }

    public IFormula getFormulas(Short index) {
        if (index == null) return null;
        return formulas.get(index);
    }

    public int storeAssumption(BinarySet set) {
        int value = assumptionsIndexes.getInt(set);
        if (value != 0 || assumptionsIndexes.containsKey(set))
            return value; // key already present

        value = assumptions.size();
        assumptions.put(value, set);
        assumptionsIndexes.put(set, value);

        return value;
    }


    public BinarySet getAssumptions(int index) {
        //if (index == null) return null;
        return assumptions.get(index);
    }

}
