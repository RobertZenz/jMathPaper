
package org.bonsaimind.jmathpaper.cli;

import org.bonsaimind.jmathpaper.Arguments;
import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.Evaluator;

public final class Cli {
	
	private Cli() {
		super();
	}
	
	public final static void run(Arguments arguments) {
		Evaluator evaluator = new Evaluator();
		EvaluatedExpression evaluatedExpression = evaluator.evaluate(arguments.getExpression());
		
		if (evaluatedExpression.getErrorMessage() == null) {
			System.out.print(evaluatedExpression.getId());
			System.out.print("\t");
			System.out.print(evaluatedExpression.getExpression());
			System.out.print("\t");
			System.out.print("= ");
			System.out.print(evaluatedExpression.getResult().toPlainString());
			System.out.println();
		} else {
			System.err.println(evaluatedExpression.getErrorMessage());
		}
	}
}
