// ----------------------------------------------------------------------------
// Copyright (C) 2012 Louise A. Dennis, and  Michael Fisher 
//
// This file is part of Agent JPF (AJPF)
// 
// AJPF is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
// 
// AJPF is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with AJPF; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
// To contact the authors:
// http://www.csc.liv.ac.uk/~lad
//
//----------------------------------------------------------------------------

package ajpf;

import ajpf.psl.MCAPLFormula;

import java.util.List;
import java.util.Set;

/**
 * Interface to be implemented by agents in specific APLs.
 * 
 * @author louiseadennis
 *
 */
public interface MCAPLScheduler {
	public List<MCAPLJobber> getActiveJobbers();
	
	public void notActive(String a);
	
	public void isActive(String a);
	
	public void addJobber(MCAPLJobber a);
	
	public List<String> getActiveJobberNames();
}
