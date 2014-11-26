/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.ts.rules;

import alice.tuprolog.Struct;
import aorta.AgentState;
import aorta.ExternalAgent;
import aorta.kr.KBType;
import aorta.kr.QueryEngine;
import aorta.kr.util.FormulaQualifier;
import aorta.tracer.Tracer;
import aorta.ts.Transition;
import aorta.logging.Logger;

/**
 *
 * @author asj
 */
public class Ext extends Transition<AgentState> {

	private static final Logger logger = Logger.getLogger(Ext.class.getName());

	@Override
	public AgentState execute(QueryEngine engine, AgentState state) {

		AgentState newState = state;
		if (state.getExternalAgent().containsBBChanges() 
                || state.getExternalAgent().containsGBChanges() 
                || state.getExternalAgent().containsCapChanges()) {
			Tracer.trace(state.getAgent().getName(), "(Ext)");
			
			//XXX: newState = state.clone();;
			ExternalAgent ext = newState.getExternalAgent();

			int newBeliefs = 0, remBeliefs = 0, 
					newGoals = 0, remGoals = 0, 
					newCaps = 0, remCaps = 0;
			
			Struct struct;
			while ((struct = ext.getRemovedBelief()) != null) {
				Struct qualified = FormulaQualifier.qualifyStruct(struct, KBType.BELIEF.getType());
				newState.removeFromMentalState(engine, qualified);
				remBeliefs++;
				
				Tracer.trace(state.getAgent().getName(), "-" + qualified + ";");
			}
			while ((struct = ext.getNewBelief()) != null) {
				Struct qualified = FormulaQualifier.qualifyStruct(struct, KBType.BELIEF.getType());
				newState.insertInMentalState(engine, qualified);
				newBeliefs++;

				Tracer.trace(state.getAgent().getName(), "+" + qualified + ";");
			}
			while ((struct = ext.getRemovedGoal()) != null) {
				Struct qualified = FormulaQualifier.qualifyStruct(struct, KBType.GOAL.getType());
				newState.removeFromMentalState(engine, qualified);
				remGoals++;
				
				Tracer.trace(state.getAgent().getName(), "-" + qualified + ";");
			}
			while ((struct = ext.getNewGoal()) != null) {
				Struct qualified = FormulaQualifier.qualifyStruct(struct, KBType.GOAL.getType());
				newState.insertInMentalState(engine, qualified);
				newGoals++;
				
				Tracer.trace(state.getAgent().getName(), "+" + qualified + ";");
			}
			while ((struct = ext.getRemovedCapability()) != null) {
				Struct cap = new Struct("cap", struct);
				newState.removeFromMentalState(engine, cap);
				remCaps++;
				
				Tracer.trace(state.getAgent().getName(), "-" + cap + ";");
			}
			while ((struct = ext.getNewCapability()) != null) {
				Struct cap = new Struct("cap", struct);
				newState.insertInMentalState(engine, cap);
				newCaps++;
				
				Tracer.trace(state.getAgent().getName(), "+" + cap + ";");
			}
			
			Tracer.trace(state.getAgent().getName(), "\n");
			Tracer.trace(state.getAgent().getName());
		
			logger.finest(
				"[" + state.getAgent().getName() + "/" + state.getAgent().getCycle() + "] New beliefs: " + newBeliefs + 
					", removed beliefs: " + remBeliefs + 
					", new goals: " + newGoals +
					", removed goals: " + remGoals + 
					", new caps: " + newCaps +
					", removed caps: " + remCaps);
		}

		return newState;
	}

	@Override
	public String getName() {
		return "Ext";
	}
}
