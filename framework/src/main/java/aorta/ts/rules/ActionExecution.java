/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.ts.rules;

import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import aorta.AORTAException;
import aorta.AgentState;
import aorta.kr.KBType;
import aorta.kr.MentalState;
import aorta.kr.QueryEngine;
import aorta.kr.util.Qualifier;
import aorta.logging.Logger;
import aorta.reasoning.ActionRule;
import aorta.reasoning.fml.Formula;
import aorta.reasoning.fml.TrueFormula;
import aorta.tracer.Tracer;
import aorta.ts.Transition;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author asj
 */
public class ActionExecution extends Transition {

	private static final Logger logger = Logger.getLogger(ActionExecution.class.getName());

	@Override
	protected AgentState execute(QueryEngine engine, AgentState state) {
		AgentState newState = state;

		for (ActionRule ar : state.getActionRules()) {
			try {
				Term option = Term.createTerm(ar.getOption().toString());

				MentalState ms = state.getMentalState();

				List<SolveInfo> solutions = engine.findAll(ms, Qualifier.qualifyTerm(option, KBType.OPTION));
				for (SolveInfo info : solutions) {
//				SolveInfo info = engine.solve(ms, Qualifier.qualifyTerm(option, KBType.OPTION));
					if (info.isSuccess()) {
						//XXX: newState = newState.clone();
//						newState.addBindings(info);

						Formula context = ar.getContext();
						Term qualified = Qualifier.qualifyGoal(ms, context);

						System.out.println("Option: " + option + " is success: " + info);

						engine.unify(ms, qualified, info);
						SolveInfo contextSolution = engine.solve(ms, qualified);

						System.out.println("Context: " + qualified + " (success=" + contextSolution + ")");

						if (context instanceof TrueFormula || contextSolution.isSuccess()) {
							newState.addBindings(info);
							newState.addBindings(contextSolution);

							Tracer.queue(state.getAgent().getName(), "(" + getName() + ") ");
							Tracer.queue(state.getAgent().getName(), option + " : " + qualified + " => ");
							engine.unify(ms, option, newState.getBindings());
							newState = ar.getAction().execute(engine, option, newState);

							Tracer.queue(state.getAgent().getName(), "\n");
							Tracer.trace(state.getAgent().getName());

							break;
						}

					}
				}
			} catch (AORTAException ex) {
				Tracer.clearQueue(state.getAgent().getName());
				logger.log(Level.FINE, "Transition was not possible (" + ex.getMessage() + ")");

				newState = state;
			}

		}

		return newState;
	}

	@Override
	public String getName() {
		return "Act-Exec";
	}
}
