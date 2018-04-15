/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jmathpaper.core.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Unit} is a representation of a unit of measurement, which allows to
 * quantify things.
 * <p>
 * Two {@link Unit}s are {@link #equals(Object) equal} to each other if they
 * have the same {@link #getName() name} (case-insensitive) and the same
 * {@link #getExponent()}.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Units_of_measurement">Wikipedia:
 *      Units of measurement</a>
 */
public class Unit {
	/** An instance which denotes no unit. */
	public static final Unit ONE = new Unit("1", 1);
	
	protected List<String> aliases = new ArrayList<>();
	protected boolean derived = false;
	protected int exponent = 0;
	protected String name = null;
	private List<String> readonlyAliases = null;
	
	/**
	 * Creates a new instance of {@link Unit}.
	 *
	 * @param name The name (case-sensitive), cannot be {@code null}.
	 * @param exponent The exponent, must be {@code 1} or greater.
	 * @param aliases The aliases (if any, case-insensitive).
	 * @throws IllegalArgumentException If the name is {@code null} or can be
	 *         considered empty, or if the exponent is zero or negative.
	 */
	public Unit(String name, int exponent, List<String> aliases) {
		super();
		
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("name cannot be null or empty.");
		}
		
		if (exponent <= 0) {
			throw new IllegalArgumentException("exponent cannot be zero or negative.");
		}
		
		this.name = name.trim();
		this.exponent = exponent;
		
		if (aliases != null && !aliases.isEmpty()) {
			this.aliases.addAll(aliases);
		}
	}
	
	/**
	 * Creates a new instance of {@link Unit}.
	 *
	 * @param name The name (case-sensitive), cannot be {@code null}.
	 * @param exponent The exponent, must be {@code 1} or greater.
	 * @param aliases The aliases (if any, case-insensitive).
	 * @throws IllegalArgumentException If the name is {@code null} or can be
	 *         considered empty, or if the exponent is zero or negative.
	 */
	public Unit(String name, int exponent, String... aliases) {
		super();
		
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("name cannot be null or empty.");
		}
		
		if (exponent <= 0) {
			throw new IllegalArgumentException("exponent cannot be zero or negative.");
		}
		
		this.name = name;
		this.exponent = exponent;
		
		if (aliases != null && aliases.length > 0) {
			this.aliases.addAll(Arrays.asList(aliases));
		}
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
		Unit other = (Unit)obj;
		if (exponent != other.exponent) {
			return false;
		}
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
	 * Gets the {@link List} of aliases of this {@link Unit}.
	 * 
	 * @return The {@link List} of aliases of this {@link Unit}.
	 */
	public List<String> getAliases() {
		if (readonlyAliases == null) {
			readonlyAliases = Collections.unmodifiableList(aliases);
		}
		
		return readonlyAliases;
	}
	
	/**
	 * Gets the exponent of this {@link Unit}.
	 * <p>
	 * Aliases should be treated case-sensitive.
	 * 
	 * @return The exponent of this {@link Unit}.
	 */
	public int getExponent() {
		return exponent;
	}
	
	/**
	 * Gets the name of this {@link Unit}.
	 * <p>
	 * Names should be treated case-insensitive.
	 * 
	 * @return The name of this {@link Unit}.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + exponent;
		// Create the hash with the lower-case name to make sure that "UNIT" and
		// "unit" are the same hashcode.
		result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		return result;
	}
	
	/**
	 * Gets whether this {@link Unit} has been derived from a base {@link Unit}.
	 * <p>
	 * Derived in this context means that it has a different exponent than the
	 * base {@link Unit}.
	 * 
	 * @return Whether this {@link Unit} has been derived from a base
	 *         {@link Unit}.
	 */
	public boolean isDerived() {
		return derived;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (exponent > 1 && derived) {
			return name.toLowerCase() + "^" + Integer.toString(exponent);
		} else {
			return name.toLowerCase();
		}
	}
	
	/**
	 * Derives a new {@link Unit} with the given exponent from this one.
	 * 
	 * @param exponent The exponent for the new {@link Unit}.
	 * @return A new {@link Unit} with the given exponent from this one.
	 * @throws UnsupportedOperationException If this {@link Unit} cannot have a
	 *         different exponent.
	 */
	public Unit withExponent(int exponent) {
		if (this.exponent > 1 && !derived) {
			throw new UnsupportedOperationException(name + " is of exponent " + Integer.toString(this.exponent) + " and cannot be assigned a different one.");
		}
		
		Unit derivedUnit = new Unit(name, exponent, aliases);
		derivedUnit.derived = true;
		
		return derivedUnit;
	}
}
