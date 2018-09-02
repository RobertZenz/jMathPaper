
package org.bonsaimind.jmathpaper.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jmathpaper.core.Evaluator;
import org.bonsaimind.jmathpaper.core.Paper;
import org.bonsaimind.jmathpaper.core.units.UnitConverter;

public class Definitions {
	protected List<String> aliasDefinitions = new ArrayList<>();
	protected List<String> conversionDefinitions = new ArrayList<>();
	protected List<String> prefixDefinitions = new ArrayList<>();
	protected List<String> unitDefinitions = new ArrayList<>();
	private List<String> readonlyAliasDefinitions = null;
	private List<String> readonlyConversionDefinitions = null;
	private List<String> readonlyPrefixDefinitions = null;
	private List<String> readonlyUnitDefinitions = null;
	
	public Definitions() {
		super();
	}
	
	public void addAliasDefinition(String aliasDefinition) {
		aliasDefinitions.add(aliasDefinition);
	}
	
	public void addConversionDefinition(String conversionDefinition) {
		conversionDefinitions.add(conversionDefinition);
	}
	
	public void addPrefixDefinition(String prefixDefinition) {
		prefixDefinitions.add(prefixDefinition);
	}
	
	public void addUnitDefinition(String unitDefinition) {
		unitDefinitions.add(unitDefinition);
	}
	
	public void apply(Paper paper) {
		Evaluator evaluator = paper.getEvaluator();
		
		aliasDefinitions.forEach(evaluator::loadAlias);
		
		UnitConverter unitConverter = evaluator.getUnitConverter();
		
		prefixDefinitions.forEach(unitConverter::loadPrefix);
		unitDefinitions.forEach(unitConverter::loadUnit);
		conversionDefinitions.forEach(unitConverter::loadConversion);
	}
	
	public List<String> getAliasDefinitions() {
		if (readonlyAliasDefinitions == null) {
			readonlyAliasDefinitions = Collections.unmodifiableList(aliasDefinitions);
		}
		
		return readonlyAliasDefinitions;
	}
	
	public List<String> getConversionDefinitions() {
		if (readonlyConversionDefinitions == null) {
			readonlyConversionDefinitions = Collections.unmodifiableList(aliasDefinitions);
		}
		
		return readonlyConversionDefinitions;
	}
	
	public List<String> getPrefixDefinitions() {
		if (readonlyPrefixDefinitions == null) {
			readonlyPrefixDefinitions = Collections.unmodifiableList(aliasDefinitions);
		}
		
		return readonlyPrefixDefinitions;
	}
	
	public List<String> getUnitDefinitions() {
		if (readonlyUnitDefinitions == null) {
			readonlyUnitDefinitions = Collections.unmodifiableList(aliasDefinitions);
		}
		
		return readonlyUnitDefinitions;
	}
}
