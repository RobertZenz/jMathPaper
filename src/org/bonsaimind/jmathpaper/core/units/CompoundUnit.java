/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.core.units;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link CompoundUnit} is a combination of two or more unit of measurements.
 * For example all units of speed are a compound unit of length and time.
 */
public class CompoundUnit {
	protected List<Token> tokens = new ArrayList<>();
	private String cachedStringValue = null;
	private List<Token> readonlyTokens = null;
	
	/**
	 * Creates a new instance of {@link CompoundUnit}.
	 *
	 * @param tokens The {@link List} of {@link Token}s which makes up this
	 *        {@link CompoundUnit}.
	 */
	public CompoundUnit(List<Token> tokens) {
		super();
		
		this.tokens.addAll(tokens);
	}
	
	/**
	 * Gets the {@link List} of {@link Token}s which makes up this
	 * {@link CompoundUnit}.
	 * 
	 * @return The {@link List} of {@link Token}s which makes up this
	 *         {@link CompoundUnit}.
	 */
	public List<Token> getTokens() {
		if (readonlyTokens == null) {
			readonlyTokens = Collections.unmodifiableList(tokens);
		}
		
		return readonlyTokens;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (cachedStringValue == null) {
			StringBuilder stringValue = new StringBuilder();
			
			for (Token token : tokens) {
				if (token.getTokenType() == TokenType.UNIT) {
					stringValue.append(token.getUnit().toString());
				} else {
					stringValue.append(token.getValue());
				}
			}
			
			cachedStringValue = stringValue.toString();
		}
		
		return cachedStringValue;
	}
	
	/**
	 * The {@link Token} represents a single part of a compound unit.
	 */
	public static class Token {
		protected TokenType tokenType = null;
		protected PrefixedUnit unit = null;
		protected String value = null;
		
		/**
		 * Creates a new instance of {@link Token}.
		 *
		 * @param value The original {@link String} value.
		 * @param tokenType The {@link TokenType}.
		 * @param unit The {@link Unit} this {@link Token} represents, if any.
		 */
		public Token(String value, TokenType tokenType, PrefixedUnit unit) {
			super();
			
			this.value = value;
			this.tokenType = tokenType;
			this.unit = unit;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Token other = (Token)obj;
			if (tokenType != other.tokenType) {
				return false;
			}
			if (unit == null) {
				if (other.unit != null) {
					return false;
				}
			} else if (!unit.equals(other.unit)) {
				return false;
			}
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}
		
		/**
		 * Gets the {@link TokenType}.
		 *
		 * @return The {@link TokenType}
		 */
		public TokenType getTokenType() {
			return tokenType;
		}
		
		/**
		 * Gets the {@link PrefixedUnit}.
		 *
		 * @return The {@link PrefixedUnit}
		 */
		public PrefixedUnit getUnit() {
			return unit;
		}
		
		/**
		 * Gets the actual {@link String} value.
		 *
		 * @return The actual {@link String} value.
		 */
		public String getValue() {
			return value;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
			result = prime * result + ((unit == null) ? 0 : unit.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if (tokenType == TokenType.OPERATOR) {
				return tokenType.name() + ": " + value;
			} else {
				return tokenType.name() + ": " + value + " (" + unit + ")";
			}
		}
	}
	
	/**
	 * The {@link TokenType} represents the type of a single token.
	 */
	public static enum TokenType {
		/** The token is an operator, for example a dash "{@code /}". */
		OPERATOR,
		
		/** The token represents a unit. */
		UNIT;
	}
}
