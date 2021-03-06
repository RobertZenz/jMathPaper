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

/**
 * A {@link PrefixedUnit} is the combination of an {@link Unit} with a
 * {@link Prefix}.
 * <p>
 * Two {@link PrefixedUnit}s are considered {@link #equals(Object) equal} if the
 * {@link Unit} and {@link Prefix} are equal to each other.
 */
public class PrefixedUnit {
	/** An instance which denotes neither an unit nor a prefix. */
	public static final PrefixedUnit ONE = new PrefixedUnit(Prefix.BASE, Unit.ONE);
	
	protected Prefix prefix = null;
	protected Unit unit = null;
	
	/**
	 * Creates a new instance of {@link PrefixedUnit}.
	 * 
	 * @param prefix The {@link Prefix} for this {@link PrefixedUnit}, cannot be
	 *        {@code null}.
	 * @param unit The {@link Unit} for this {@link PrefixedUnit}, cannot be
	 *        {@code null}.
	 */
	public PrefixedUnit(Prefix prefix, Unit unit) {
		super();
		
		if (prefix == null) {
			throw new IllegalArgumentException("prefix cannot be null.");
		}
		
		if (unit == null) {
			throw new IllegalArgumentException("unit cannot be null.");
		}
		
		this.prefix = prefix;
		this.unit = unit;
	}
	
	/**
	 * A {@link PrefixedUnit} derived from this one with the {@link Prefix#BASE}
	 * 
	 * @return A {@link PrefixedUnit} derived from this one with the
	 *         {@link Prefix#BASE}.
	 */
	public PrefixedUnit atBase() {
		return new PrefixedUnit(Prefix.BASE, unit);
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
		PrefixedUnit other = (PrefixedUnit)obj;
		if (prefix == null) {
			if (other.prefix != null) {
				return false;
			}
		} else if (!prefix.equals(other.prefix)) {
			return false;
		}
		if (unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!unit.equals(other.unit)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the {@link Prefix} of this {@link PrefixedUnit}.
	 * 
	 * @return The {@link Prefix} of this {@link PrefixedUnit}.
	 */
	public Prefix getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the {@link Unit} of this {@link PrefixedUnit}.
	 * 
	 * @return The {@link Unit} of this {@link PrefixedUnit}.
	 */
	public Unit getUnit() {
		return unit;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}
	
	/**
	 * Gets whether this {@link PrefixedUnit} is the SI unit "1" without a
	 * prefix.
	 * 
	 * @return {@code true} if this {@link PrefixedUnit} is the SI unit "1"
	 *         without a prefix.
	 */
	public boolean isOne() {
		return this == ONE
				|| (prefix.isBase() && unit.isOne());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return prefix.toString() + unit.toString();
	}
}
