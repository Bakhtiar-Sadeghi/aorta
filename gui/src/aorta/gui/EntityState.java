/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aorta.gui;

import alice.tuprolog.NoSolutionException;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Term;
import aorta.State;
import aorta.kr.MentalState;
import aorta.tracer.StateListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Andreas Schmidt Jensen <ascje at dtu.dk>
 */
public abstract class EntityState implements StateListener {

	protected final Pattern anon = Pattern.compile("_\\d+");
	
	public abstract void update();

	protected void update(MentalState ms, JList list, Term struct, String var) {
		List<String> values = new ArrayList<>();
		List<SolveInfo> solutions = ms.findAll(struct);
		for (SolveInfo solution : solutions) {
			if (solution.isSuccess()) {
				try {
					String value = solution.getVarValue(var).toString();
					values.add(anon.matcher(value).replaceAll("_"));
				} catch (NoSolutionException ex) {
					// ignore because of isSuccess()
				}
			}
		}
		DefaultListModel model = new DefaultListModel();
		Collections.sort(values);
		for (String v : values) {
			model.addElement(v);
		}
		list.setModel(model);
	}

	@Override
	public void newState(State state) {
		update();
	}
	
	@Override
	public void termAdded(String name, Term term) {
	}

	@Override
	public void termRemoved(String name, Term term) {
	}
	
}
