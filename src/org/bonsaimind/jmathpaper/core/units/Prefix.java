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

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * A {@link Prefix} is a representation of a unit prefix which denotes multiples
 * or fractions of a unit.
 * <p>
 * Two {@link Prefix}es are considered {@link #equals(Object) equal} when they
 * have the same {@link #getName()} (case-insensitive).
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Unit_prefix">Wikipedia: Unit
 *      Prefix</a>
 */
public class Prefix {
	/** An instance which denotes no prefix. */
	public static final Prefix BASE = new Prefix("", "", 1, 1);
	
	protected int base = 0;
	protected BigDecimal factor = null;
	protected String name = null;
	protected int power = 0;
	protected String symbol = null;
	
	/**
	 * Creates a new instance of {@link Prefix}.
	 * 
	 * @param name The (unique) name for this {@link Prefix}, case-insensitive.
	 * @param symbol The (unique) symbol for this {@link Prefix},
	 *        case-sensitive.
	 * @param base The base for this {@link Prefix}.
	 * @param power The power for this {@link Prefix}.
	 */
	public Prefix(String name, String symbol, int base, int power) {
		this.name = name;
		this.symbol = symbol;
		this.base = base;
		this.power = power;
		this.factor = new BigDecimal(base).pow(power, MathContext.DECIMAL128).stripTrailingZeros();
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
		Prefix other = (Prefix)obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the base of this {@link Prefix}.
	 * 
	 * @return The base of this {@link Prefix}.
	 */
	public int getBase() {
		return base;
	}
	
	/**
	 * Gets the factor of this {@link Prefix}.
	 * <p>
	 * The factor is the factor which can be directly appled to the value.
	 * 
	 * @return The factor of this {@link Prefix}.
	 */
	public BigDecimal getFactor() {
		return factor;
	}
	
	/**
	 * Gets the name of this {@link Prefix}.
	 * <p>
	 * The name uniquely identifies this {@link Prefix} and should be treated
	 * case insensitive.
	 * 
	 * @return The name of this {@link Prefix}.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the power of this {@link Prefix}.
	 * 
	 * @return The power of this {@link Prefix}.
	 */
	public int getPower() {
		return power;
	}
	
	/**
	 * Gets the symbol that denotes this {@link Prefix}.
	 * <p>
	 * The symbol uniquely identifies this {@link Prefix}, this should be
	 * treated case sensitive.
	 * 
	 * @return The symbol that denotes this {@link Prefix}.
	 */
	public String getSymbol() {
		return symbol;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// Use the lower-case name to make sure that name is treated
		// case-insensitive.
		result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name.toLowerCase();
	}
}
