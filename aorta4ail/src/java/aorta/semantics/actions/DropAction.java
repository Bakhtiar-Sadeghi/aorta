/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.semantics.actions;

import ail.syntax.Action;
import ail.syntax.Predicate;

/**
 *
 * @author asj
 */
public class DropAction extends Action {
	private final Predicate obj;
	
	public DropAction(Predicate obj) {
		super("commit", normalAction);
		
		this.obj = obj;
	}

	public Predicate getObj() {
		return obj;
	}

	@Override
	public String toString() {
		return "drop(" + obj + ")";
	}
	
}
