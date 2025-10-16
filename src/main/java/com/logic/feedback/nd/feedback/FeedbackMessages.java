package com.logic.feedback.nd.feedback;

public class FeedbackMessages {

    public static final String ERROR_GENERIC = "Something is wrong!";
    public static final String SOLUTION_SUFFIX = "\nPossible solution:";

    public static final String INVALID_RULE = "Invalid rule application!";

    public static final String RULE_IMP_REQUIRED = "This must be an implication!";
    public static final String RULE_BOT_REQUIRED = "This must be bottom!";
    public static final String RULE_NEG_REQUIRED = "This must be a negation!";
    public static final String RULE_CONJ_REQUIRED = "This must be a conjunction!";
    public static final String RULE_DIS_REQUIRED = "This must be a disjunction!";
    public static final String RULE_UNI_REQUIRED = "This must be a universal!";
    public static final String RULE_EXIST_REQUIRED = "This must be a existential!";

    public static final String INVALID_MARK = "Invalid mark!";
    public static final String RULE_CANNOT_CLOSE_MARK = "This rule cannot close mark %s!";
    public static final String ONLY_MARKS_ASSIGNED_TO = "\nOnly marks assigned to %s!";
    public static final String SHOULD_BE_VARIABLE = "\nWhere ? should be a variable!";
    public static final String CONSIDER = "\nConsider:";
    public static final String MARK_ALREADY_ASSIGNED = "Mark %s already assigned!";
    public static final String MARK_ASSIGNED_TO = "Mark %s assigned to %s!";
    public static final String CONSIDER_DIFFERENT_MARK = "\nConsider assigning a different mark!";

    public static final String MISSING_SIDE_CONDITION = "Missing side condition!";
    public static final String OPEN_HYPOTHESIS = "Open hypothesis!";
    public static final String VARIABLES_NOT_EQUAL = "\nVariables: %s ≠ %s";
    public static final String VARIABLE_APPEARS_FREE = "Variable %s appears free!";
    public static final String FREE_VARIABLE = "\nFree variable!";

    public static final String INVALID_MAPPING = "Invalid mapping!";
    public static final String NO_MAPPING = "No mapping of %s in %s that can produce %s!";
    public static final String DID_YOU_MEAN = "\nDid you mean:";

    public static final String TERM_NOT_FREE = "Term %s is not free to %s in %s!";

    public static final String NOT_A_NEGATION = "Formula %s should be a negation of %s!";

}
