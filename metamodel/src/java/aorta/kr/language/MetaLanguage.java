/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.kr.language;

import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import aorta.kr.util.TermQualifier;
import gov.nasa.jpf.annotation.FilterField;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 *
 * @author Andreas Schmidt Jensen <ascje at dtu.dk>
 */
public class MetaLanguage {

	@FilterField 
	private static final Term[] qualifiers = new Term[]{
		Term.createTerm("goal(_)"), Term.createTerm("org(_)"), Term.createTerm("opt(_)"), Term.createTerm("bel(_)")
	};

	// Metamodel predicates
	public Struct role(Term roleName, Term objectives) {
		return new Struct("role", roleName, objectives);
	}

	public Struct objective(Term objectiveName, Term subObjectives) {
		return new Struct("obj", objectiveName, subObjectives);
	}

	public Struct dependency(Term role1, Term role2, Term objective) {
		return new Struct("dep", role1, role2, objective);
	}

	public Struct rea(Term agent, Term role) {
		return new Struct("rea", agent, role);
	}

	public Struct condition(Term role, Term deon, Term objective, Term deadline, Term condition) {
		return new Struct("cond", role, deon, objective, deadline, condition);
	}

	public Struct norm(Term agent, Term role, Term deon, Term objective, Term deadline) {
		return new Struct("norm", agent, role, deon, objective, deadline);
	}

	public Struct violation(Term agent, Term role, Term deon, Term objective) {
		return new Struct("viol", agent, role, deon, objective);
	}

	// Options
	public Struct role(Term role) {
		return new Struct("role", role);
	}

	public Struct obj(Term obj) {
		return new Struct("obj", obj);
	}

	public Struct norm(Term role, Term deon, Term obl, Term deadline) {
		return new Struct("norm", role, deon, obl, deadline);
	}

	public Struct viol(Term agent, Term role, Term deon, Term obl) {
		return new Struct("viol", agent, role, deon, obl);
	}

	public Struct send(Term role, Term ilf, Term contents) {
		return new Struct("send", role, ilf, contents);
	}

	// Messages
	public Struct message(Term agents, Term message) {
		return new Struct("msg", agents, message);
	}

	public Struct sent(Term agent, Term message) {
		return new Struct("sent", agent, message);
	}

	public boolean inML(Term term) {
		if (term instanceof Struct && "~".equals(((Struct) term).getName())) {
			term = ((Struct) term).getArg(0);
		}

		// using reflection for convenience
		outer: for (Method method : getClass().getMethods()) {
			for (Class c : method.getParameterTypes()) {
				if (c != Term.class) {
					continue outer;
				}
			}
			if (method.getReturnType() != Struct.class) {
				continue;
			}

			Object[] args = new Object[method.getParameterTypes().length];
			for (int i = 0; i < args.length; i++) {
				args[i] = new Var();
			}
			Struct s;
			try {
				s = (Struct) method.invoke(this, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				ex.printStackTrace(System.err);
				continue;
			}
			if (term.match(s)) {
				return true;
			}
		}

		return false;
	}

	public Term qualify(Term term) {
		return qualify(term, true);
	}
	
	public Term qualify(Term term, boolean qualifyBel) {
		Term qualified = null;

		if (term.match(Term.FALSE) || term.match(Term.TRUE)) {
			qualified = term;
		} else if (term instanceof Struct) {
			boolean matched = false;
			for (Term t : qualifiers) {
				if (term.match(t)) {
					matched = true;
					qualified = term;
					break;
				}
			}

			// Similar to TermQualifier, but this one uses inML to determine which KB to add to.
			if (!matched) {
				Struct struct = (Struct) term;
				if (TermQualifier.isReserved(term)) {
					String functor = struct.getName();
					Struct queryAsList = TermQualifier.toList(struct);
					Struct arguments = queryAsList.listTail();
					Struct qualifiedArguments = (Struct) qualify(arguments, qualifyBel);
					Struct rebuiltList = new Struct();
					rebuiltList.append(new Struct(functor));
					Iterator<? extends Term> it = qualifiedArguments.listIterator();
					while (it.hasNext()) {
						rebuiltList.append(it.next());
					}
					qualified = TermQualifier.fromList(rebuiltList);
				} else if (struct.isList()) {
					Struct newList = new Struct();
					Iterator<? extends Term> it = struct.listIterator();
					while (it.hasNext()) {
						newList.append(qualify(it.next(), qualifyBel));
					}
					qualified = newList;
			
				} else if (struct.getArity() == 2 && ",".equals(struct.getName()) || ";".equals(struct.getName())) {
					qualified = new Struct(struct.getName(), qualify(struct.getArg(0), qualifyBel), qualify(struct.getArg(1), qualifyBel));
				} else if (inML(term)) {
					qualified = new Struct("org", term);
				} else {
					qualified = qualifyBel ? new Struct("bel", term) : term;
				}
			}
		} else {
			qualified = term;
		}

		return qualified;
	}
}
