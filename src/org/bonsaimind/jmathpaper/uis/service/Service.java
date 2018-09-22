
package org.bonsaimind.jmathpaper.uis.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.bonsaimind.jmathpaper.core.EvaluatedExpression;
import org.bonsaimind.jmathpaper.core.ui.AbstractPapersUi;

public class Service extends AbstractPapersUi {
	private volatile boolean running = true;
	
	public Service() {
		super();
	}
	
	@Override
	public void quit() {
		running = false;
	}
	
	@Override
	public void run() throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (running) {
				process(reader.readLine());
			}
		}
	}
	
	@Override
	protected void currentPaperHasBeenModified() {
		EvaluatedExpression evaluatedExpression = paper.getEvaluatedExpressions().get(paper.getEvaluatedExpressions().size() - 1);
		
		if (!uiParameters.getBoolean("isPrintResultOnly")) {
			System.out.print(evaluatedExpression.getFormattedResult(paper.getNumberFormat()));
		} else {
			System.out.print(paper.format(evaluatedExpression));
		}
		
		if (!uiParameters.getBoolean("isNoNewline")) {
			System.out.println();
		}
	}
}
