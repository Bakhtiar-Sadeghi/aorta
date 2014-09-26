package aorta.reasoning.action;

import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import aorta.AORTAException;
import aorta.AgentState;
import aorta.kr.KBType;
import aorta.kr.MentalState;
import aorta.kr.util.FormulaQualifier;
import aorta.kr.QueryEngine;
import aorta.kr.language.MetaLanguage;
import aorta.tracer.Tracer;
import aorta.ts.TransitionNotPossibleException;
import aorta.logging.Logger;

public class DeactAction extends Action {
	public static final Logger logger = Logger.getLogger(DeactAction.class.getName());
	
	private Term role;
	
	public DeactAction(Term role) {
		this.role = role;
	}

	public Term getRole() {
		return role;
	}

	@Override
	protected AgentState executeAction(QueryEngine engine, Term option, AgentState state)
			throws AORTAException {
		AgentState newState = state;
		
		final Term agent = Term.createTerm(state.getAgent().getName());
		final Term clonedRoleTerm = Term.createTerm(role.toString());
		
		MetaLanguage ml = new MetaLanguage();
				
		Term reaDef = ml.rea(agent, clonedRoleTerm);
		
		Term term = FormulaQualifier.qualifyTerm(reaDef, KBType.ORGANIZATION.getType());
		MentalState ms = state.getMentalState();

		SolveInfo result = engine.solve(ms, term);
		
		logger.finest("Attempting to deact: " + result.isSuccess());
		
		if (result.isSuccess()) {
			state.addBindings(result);
			
			Term qualified = FormulaQualifier.qualifyTerm(reaDef, KBType.ORGANIZATION.getType());
			engine.unify(ms, qualified, state.getBindings());
			
			if (!qualified.isGround()) {
				throw new AORTAException("Cannot execute action: term '" + qualified + "' is not ground.");
			} else if (qualified instanceof Struct) {
				//XXX: newState = state.clone();;
				newState.removeTerm(engine, (Struct) qualified);
				
				logger.fine("[" + state.getAgent().getName() + "] Executing action: deact(" + qualified + ")");
				Tracer.queue(state.getAgent().getName(), "deact(" + qualified + ")");
			} else {
				throw new AORTAException("X in deact(X) must be a Struct (was " + qualified.getClass() + ")");
			}
		} else {
			throw new TransitionNotPossibleException();
		}
		
		return newState;
	}
	
	@Override
	public String toString() {
		return "deact(" + role + ")";
	}

}
