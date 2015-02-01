/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.ts.rules.og;

import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Var;
import aorta.AgentState;
import aorta.kr.KBType;
import aorta.kr.MentalState;
import aorta.kr.QueryEngine;
import aorta.kr.language.MetaLanguage;
import aorta.kr.util.FormulaQualifier;
import aorta.logging.Logger;
import aorta.tracer.Tracer;
import aorta.ts.Transition;
import java.util.List;

/**
 *
 * @author Andreas Schmidt Jensen <ascje at dtu.dk>
 */
public class NormRule extends Transition<AgentState> {

	private static final Logger logger = Logger.getLogger(NormRule.class.getName());

	@Override
	protected AgentState execute(QueryEngine engine, AgentState state) {
		AgentState newState = state;
		MentalState ms = newState.getMentalState();

		MetaLanguage language = new MetaLanguage();
		Struct obl = language.norm(new Struct(state.getAgent().getName()), new Var("R"), new Var("Deon"), new Var("O"), new Var("D"));
		Struct orgObl = FormulaQualifier.qualifyStruct(obl, KBType.ORGANIZATION);

		List<SolveInfo> conditionals = engine.findAll(ms, orgObl);
		for (SolveInfo conditional : conditionals) {
			if (conditional.isSuccess()) {
				Struct opt = language.norm(new Var("R"), new Var("Deon"), new Var("O"), new Var("D"));
				Struct optObl = FormulaQualifier.qualifyStruct(opt, KBType.OPTION);
				
				engine.unify(ms, optObl, conditional);
				
				Struct result = optObl;
				
				if (!engine.exists(ms, result)) {
					add(newState, engine, result);

					logger.fine("[" + state.getAgent().getName() + "/" + state.getAgent().getCycle() + "] Adding option: " + result);
					Tracer.trace(state.getAgent().getName(), getName(), result.toString());

					break;
				
				}
			}
		}
		return newState;
	}

	@Override
	public String getName() {
		return "Norm";
	}
}
