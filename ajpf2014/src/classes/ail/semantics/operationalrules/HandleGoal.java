// ----------------------------------------------------------------------------
// Copyright (C) 2008-2012 Louise A. Dennis, Berndt Farwer, Michael Fisher and 
// Rafael H. Bordini.
// 
// This file is part of the Agent Infrastructure Layer (AIL)
//
// The AIL is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
// 
// The AIL is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
// To contact the authors:
// http://www.csc.liv.ac.uk/~lad
//----------------------------------------------------------------------------

package ail.semantics.operationalrules;

import ail.semantics.AILAgent;
import ail.syntax.Deed;
import ail.syntax.Goal;

import gov.nasa.jpf.annotation.FilterField;

/**
 * Handle the top deed if it is a add/delete goal.  Once again forces sub-classing but
 * now stories information on the goal and its type.
 * 
 * @author lad
 *
 */
public abstract class HandleGoal extends HandleTopDeed {
	@FilterField
	protected Goal g;
	
	@FilterField
	protected int gt;
	
	/*
	 * (non-Javadoc)
	 * @see ail.semantics.operationalrules.HandleTopDeed#checkPreconditions(ail.semantics.AILAgent)
	 */
	public boolean checkPreconditions(AILAgent a) {
		if (super.checkPreconditions(a) && (topdeed.getCategory() == Deed.AILGoal)) {
			g = topdeed.getGoal();
			gt = g.getGoalType();
			return true;
		}

		return false;
	}
}