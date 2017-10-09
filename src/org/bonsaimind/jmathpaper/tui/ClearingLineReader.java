/*
 * Copyright 2017, Robert 'Bobby' Zenz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonsaimind.jmathpaper.tui;

import java.io.IOException;

import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.InfoCmp.Capability;

/**
 * The {@link ClearingLineReader} is a {@link LineReaderImpl} extension which
 * clears the input line.
 */
public class ClearingLineReader extends LineReaderImpl {
	public ClearingLineReader(Terminal terminal) throws IOException {
		super(terminal);
	}
	
	@Override
	protected void cleanup() {
		// Origin: https://github.com/jline/jline3/issues/181
		buf.clear();
		post = null;
		prompt = new AttributedString("");
		
		redisplay(false);
		
		terminal.puts(Capability.keypad_local);
		terminal.trackMouse(Terminal.MouseTracking.Off);
		
		if (isSet(Option.BRACKETED_PASTE)) {
			terminal.writer().write(BRACKETED_PASTE_OFF);
		}
		
		flush();
		
		history.moveToEnd();
	}
}
