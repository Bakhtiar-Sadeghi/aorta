/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.ts.rules;

import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import aorta.AgentState;
import aorta.kr.KBType;
import aorta.kr.MentalState;
import aorta.kr.QueryEngine;
import aorta.kr.language.MetaLanguage;
import aorta.kr.util.FormulaQualifier;
import aorta.kr.util.TermQualifier;
import aorta.logging.Logger;
import aorta.tracer.Tracer;
import aorta.ts.Transition;
import java.util.List;

/**
 *
 * @author Andreas Schmidt Jensen <ascje at dtu.dk>
 */
public class ObjectiveRule extends Transition {

	private static final Logger logger = Logger.getLogger(ObjectiveRule.class.getName());

	@Override
	protected AgentState execute(QueryEngine engine, AgentState state) {
		AgentState newState = state;
		MentalState ms = newState.getMentalState();

		MetaLanguage language = new MetaLanguage();
		Struct obl = language.obligation(new Var("A"), new Var("R"), new Var("O"), new Var("D"));
		Struct rea = language.rea(new Var("A"), new Var("R"));

		Struct orgObl = FormulaQualifier.qualifyStruct(obl, KBType.ORGANIZATION);
		Struct orgRea = FormulaQualifier.qualifyStruct(rea, KBType.ORGANIZATION);

		Term test = Term.createTerm(orgObl.toString() + ", " + orgRea.toString() + ", bel(me(A))");
		List<SolveInfo> conditionals = engine.findAll(ms, test);
		for (SolveInfo conditional : conditionals) {
			if (conditional.isSuccess()) {
				Struct obj = language.obj(new Var("O"));
				Struct optObj = FormulaQualifier.qualifyStruct(obj, KBType.OPTION);
				
				engine.unify(ms, optObj, conditional);
				
				Struct result = optObj;
				Term objectiveArg = obj.getArg(0);
				if (objectiveArg instanceof Var && ((Var)objectiveArg).getTerm() instanceof Struct) {
					objectiveArg = ((Var)objectiveArg).getTerm();
				}
				if (objectiveArg instanceof Struct) {
					if (TermQualifier.isQualified((Struct)objectiveArg)) {
						result = (Struct) TermQualifier.qualifyTerm(language.obj(FormulaQualifier.getQualified((Struct)objectiveArg)), KBType.OPTION.getType());
					}
				}
				
				if (!engine.exists(ms, result)) {
				
					newState.insertTerm(engine, result);

					logger.info("[" + state.getAgent().getName() + "/" + state.getAgent().getCycle() + "] Adding option: " + result);
					Tracer.trace(state.getAgent().getName(), "(" + getName() + ") " + result + "\n");

					break;
				
				}
			}
		}
		return newState;
	}

	@Override
	public String getName() {
		return "Objective";
	}
}
