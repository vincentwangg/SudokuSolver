package com.vincentwangg.sudokusolver;

import java.util.ArrayList;
import java.util.BitSet;

public class SudokuBox {

	private SudokuValue[][] values;

	public SudokuBox() {
		values = new SudokuValue[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				values[i][j] = new SudokuValue();
			}
		}
	}

	public boolean isValid() {
		BitSet bitSet = new BitSet();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				SudokuValue value = values[i][j];
				if (value.hasActualValue()) {
					if (bitSet.get(value.getActualValue())) {
						return false;
					}
					else {
						bitSet.set(value.getActualValue());
					}
				}
			}
		}
		return true;
	}

	public boolean containsValue(int value) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (values[i][j].hasActualValue()
						&& values[i][j].getActualValue() == value) {
					return true;
				}
			}
		}
		return false;
	}

	public SudokuValue getValue(int row, int col) {
		return values[row][col];
	}

	public ArrayList<Integer> getMissingValues() {
		boolean[] existing = new boolean[9];
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (values[row][col].hasActualValue()) {
					existing[values[row][col].getActualValue() - 1] = true;
				}
			}
		}

		ArrayList<Integer> missingInts = new ArrayList<>(9);
		for (int i = 0; i < existing.length; i++) {
			if (!existing[i]) {
				missingInts.add(i + 1);
			}
		}
		return missingInts;
	}

	public void removeNote(int note) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (!values[i][j].hasActualValue()) {
					values[i][j].removeNote(note);
				}
			}
		}
	}
}
