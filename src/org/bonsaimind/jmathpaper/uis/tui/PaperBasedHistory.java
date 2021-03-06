/*
 * Copyright 2017, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jmathpaper.uis.tui;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.ui.Ui;
import org.jline.reader.History;
import org.jline.reader.LineReader;

public class PaperBasedHistory implements History {
	private Entry entry = null;
	private int index = 0;
	private ListIterator<Entry> iterator = null;
	private Paper lastPaper = null;
	private int lastSize = -1;
	private Ui ui = null;
	
	public PaperBasedHistory(Ui ui) {
		super();
		
		this.ui = ui;
		
		entry = new PaperBasedEntry();
		iterator = new PaperBasedIterator();
	}
	
	@Override
	public void add(Instant time, String line) {
		// Not supported.
	}
	
	@Override
	public void attach(LineReader reader) {
		// Not needed.
	}
	
	@Override
	public String current() {
		checkPaperChanged();
		
		return getEvaluatedExpressions().get(index).getExpression();
	}
	
	@Override
	public int first() {
		checkPaperChanged();
		
		if (!getEvaluatedExpressions().isEmpty()) {
			return 0;
		} else {
			return -1;
		}
	}
	
	@Override
	public String get(int index) {
		return getEvaluatedExpressions().get(index).getExpression();
	}
	
	@Override
	public int index() {
		checkPaperChanged();
		
		return index;
	}
	
	@Override
	public ListIterator<Entry> iterator() {
		checkPaperChanged();
		
		return iterator;
	}
	
	@Override
	public ListIterator<Entry> iterator(int index) {
		checkPaperChanged();
		
		return iterator;
	}
	
	@Override
	public int last() {
		checkPaperChanged();
		
		return getEvaluatedExpressions().size() - 1;
	}
	
	@Override
	public void load() throws IOException {
		// Not supported.
	}
	
	@Override
	public boolean moveTo(int index) {
		checkPaperChanged();
		
		if (index >= 0 && index < getEvaluatedExpressions().size()) {
			this.index = index;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void moveToEnd() {
		checkPaperChanged();
		
		index = getEvaluatedExpressions().size();
	}
	
	@Override
	public boolean moveToFirst() {
		checkPaperChanged();
		
		if (getEvaluatedExpressions().size() > 0) {
			index = 0;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean moveToLast() {
		checkPaperChanged();
		
		if (getEvaluatedExpressions().size() > 0) {
			index = getEvaluatedExpressions().size();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean next() {
		checkPaperChanged();
		
		if (index < getEvaluatedExpressions().size() - 1) {
			index = index + 1;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean previous() {
		checkPaperChanged();
		
		if (index > 0) {
			index = index - 1;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void purge() throws IOException {
		// Not supported.
	}
	
	@Override
	public void save() throws IOException {
		// Not supported.
	}
	
	@Override
	public int size() {
		checkPaperChanged();
		
		return getEvaluatedExpressions().size();
	}
	
	private void checkPaperChanged() {
		if (ui.getPaper() != lastPaper || lastSize != getEvaluatedExpressions().size()) {
			lastPaper = ui.getPaper();
			
			index = getEvaluatedExpressions().size();
			lastSize = getEvaluatedExpressions().size();
		}
	}
	
	private List<EvaluatedExpression> getEvaluatedExpressions() {
		if (ui.getPaper() == null) {
			return Collections.emptyList();
		}
		
		return ui.getPaper().getEvaluatedExpressions();
	}
	
	private final class PaperBasedEntry implements Entry {
		public PaperBasedEntry() {
			super();
		}
		
		@Override
		public int index() {
			return index;
		}
		
		@Override
		public String line() {
			return getEvaluatedExpressions().get(index).getExpression();
		}
		
		@Override
		public Instant time() {
			return Instant.now();
		}
	}
	
	private final class PaperBasedIterator implements ListIterator<Entry> {
		public PaperBasedIterator() {
			super();
		}
		
		@Override
		public void add(Entry e) {
			// Not supported.
		}
		
		@Override
		public boolean hasNext() {
			checkPaperChanged();
			
			return index < getEvaluatedExpressions().size() - 1;
		}
		
		@Override
		public boolean hasPrevious() {
			checkPaperChanged();
			
			return index > 0;
		}
		
		@Override
		public Entry next() {
			checkPaperChanged();
			
			index = index + 1;
			
			return entry;
		}
		
		@Override
		public int nextIndex() {
			checkPaperChanged();
			
			return index + 1;
		}
		
		@Override
		public Entry previous() {
			checkPaperChanged();
			
			index = index - 1;
			
			return entry;
		}
		
		@Override
		public int previousIndex() {
			checkPaperChanged();
			
			return index - 1;
		}
		
		@Override
		public void remove() {
			// Not supported.
		}
		
		@Override
		public void set(Entry e) {
			// Not supported.
		}
	}
}
