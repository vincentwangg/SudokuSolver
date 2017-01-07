package com.vincentwangg.sudokusolver;

import java.util.ArrayList;
import java.util.BitSet;

public class SudokuMap {

	private SudokuBox[][] boxes;

	public SudokuMap() {
		boxes = new SudokuBox[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boxes[i][j] = new SudokuBox();
			}
		}
	}

	/**
	 * Copy Constructor
	 */
	public SudokuMap(SudokuMap map) {
		boxes = new SudokuBox[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boxes[i][j] = new SudokuBox(map.boxes[i][j]);
			}
		}
	}

	private void copyMap(SudokuMap map) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				boxes[i][j].copyBox(map.boxes[i][j]);
			}
		}
	}

	public boolean isCompleted() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!getSudokuValue(i, j).hasActualValue()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isValid() {
		// Check all boxes
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (!boxes[i][j].isValid()) {
					return false;
				}
			}
		}

		// Check boxes for repeats
		BitSet bitSet = new BitSet();

		// Check all rows and cols
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					SudokuValue value = getSudokuValue(i == 0 ? j : k, i == 0 ? k : j);
					if (value.hasActualValue()) {
						if (bitSet.get(value.getActualValue())) {
							return false;
						}
						else {
							bitSet.set(value.getActualValue());
						}
					}
				}
				bitSet.clear();
			}
		}
		return true;
	}

	/**
	 * For initially setting up map. 0-based indexing
	 */
	public void setValue(int row, int col, int value) {
		if (row < 0 || row > 8 || col < 0 || col > 8) {
			throw new IndexOutOfBoundsException("SudokuMap: Row and col ranges are " +
					"both [0, 8]. Provided row and col: " + row + ", " + col);
		}
		int boxRow = row / 3;
		int boxCol = col / 3;
		int valRow = row % 3;
		int valCol = col % 3;

		SudokuValue sudokuValue = boxes[boxRow][boxCol].getValue(valRow, valCol);
		sudokuValue.addNote(value);
		sudokuValue.setActualValue();
	}

	public int getValue(int row, int col) {
		if (row < 0 || row > 8 || col < 0 || col > 8) {
			throw new IndexOutOfBoundsException("SudokuMap: Row and col ranges are " +
					"both [0, 8]. Provided row and col: " + row + ", " + col);
		}
		int boxRow = row / 3;
		int boxCol = col / 3;
		int valRow = row % 3;
		int valCol = col % 3;

		return boxes[boxRow][boxCol].getValue(valRow, valCol).getActualValue();
	}

	public SudokuValue getSudokuValue(int row, int col) {
		if (row < 0 || row > 8 || col < 0 || col > 8) {
			throw new IndexOutOfBoundsException("SudokuMap: Row and col ranges are " +
					"both [0, 8]. Provided row and col: " + row + ", " + col);
		}
		int boxRow = row / 3;
		int boxCol = col / 3;
		int valRow = row % 3;
		int valCol = col % 3;

		return boxes[boxRow][boxCol].getValue(valRow, valCol);
	}

	public SudokuBox getSudokuBox(int row, int col) {
		if (row < 0 || row > 2 || col < 0 || col > 2) {
			throw new IndexOutOfBoundsException("SudokuMap: Row and col ranges are " +
					"both [0, 2]. Provided row and col: " + row + ", " + col);
		}
		int boxRow = row / 3;
		int boxCol = col / 3;

		return boxes[boxRow][boxCol];
	}

	public void performNotesPhase() {
		for (int boxRow = 0; boxRow < boxes.length; boxRow++) {
			SudokuBox[] row = boxes[boxRow];
			for (int boxCol = 0; boxCol < row.length; boxCol++) {
				SudokuBox box = row[boxCol];
				// Get list of missing values in box
				ArrayList<Integer> missing = box.getMissingValues();

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						SudokuValue value = box.getValue(i, j);

						// If SudokuValue isn't filled in yet, mark notes
						if (!value.hasActualValue()) {
							value.clearNotes();
							for (int m : missing) {
								// Check column and row
								if (isValueValid(boxRow * 3 + i, boxCol * 3 + j, m)) {
									value.addNote(m);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean performActPhase() {
		boolean anyBoxesFilled = false;
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				SudokuValue value = getSudokuValue(row, col);

				// Fill in value if there's only one note
				if (value.setActualValue()) {
					anyBoxesFilled = true;
					configureNotes(row, col);
				}

				if (!value.hasActualValue()) {
					for (int i : value.getNotes()) {
						// If it's the only blank value in its row/col, make sure the number exists in other
						//   boxes before putting it in
						if (isOnlyViableValueRowWise(row, col, i)
								&& doOtherBoxesContainValue(true, row / 3, col / 3, i)) {
							value.setActualValue(i);
							configureNotes(row, col);
							anyBoxesFilled = true;
						}
						if (isOnlyViableValueColWise(row, col, i)
								&& doOtherBoxesContainValue(false, row / 3, col / 3, i)) {
							value.setActualValue(i);
							configureNotes(row, col);
							anyBoxesFilled = true;
						}
					}
				}
			}
		}
		return anyBoxesFilled;
	}

	public void performGuessPhase() {
		// Find a box where the notes.size is 2 and try to solve one of them. If unsuccessful,
		//	guess the other and solve
		SudokuMap testMap = new SudokuMap(this);
		for (int i = 2; i <= 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					SudokuValue value = testMap.getSudokuValue(j, k);
					if (!value.hasActualValue()) {
						ArrayList<Integer> notes = value.getNotes();
						if (notes.size() == i) {
							for (int l : notes) {
								value.setActualValue(l);
								testMap.configureNotes(j, k);
								SudokuMap attempt = SudokuSolver.solve(testMap);

								if (attempt != null && attempt.isCompleted() && attempt.isValid()) {
									copyMap(attempt);
									return;
								}
								// Recover original map
								testMap.copyMap(this);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Function that should be called whenever an actual value is set. The SudokuValues in the same
	 * row, col, or box that have the recently set actual value in their notes have it removed.
	 *
	 * @param row row where actual value is set
	 * @param col col where actual value is set
	 */
	private void configureNotes(int row, int col) {
		SudokuValue value = getSudokuValue(row, col);
		int rowLowBnd = row / 3; // Row Lower Bound
		rowLowBnd *= 3;
		int colLowBnd = col / 3; // Col Lower Bound
		colLowBnd *= 3;
		// Remove notes in same box
		for (int i = rowLowBnd; i < rowLowBnd + 3; i++) {
			for (int j = colLowBnd; j < colLowBnd + 3; j++) {
				SudokuValue tmp = getSudokuValue(i, j);
				if (!tmp.hasActualValue() && tmp != value) {
					tmp.removeNote(value.getActualValue());
				}
			}
		}
		// Remove notes in rows and columns
		for (int i = 0; i < 9; i++) {
			SudokuValue tmp = getSudokuValue(row, i);
			if (!tmp.hasActualValue() && tmp != value) {
				tmp.removeNote(value.getActualValue());
			}
			tmp = getSudokuValue(i, col);
			if (!tmp.hasActualValue() && tmp != value) {
				tmp.removeNote(value.getActualValue());
			}
		}
	}

	private boolean isValueValid(int valRow, int valCol, int value) {
		// If value is not in the row or col, then it is valid
		return !isValueInCol(valCol, value) && !isValueInRow(valRow, value);
	}

	private boolean doOtherBoxesContainValue(boolean isRow, int boxRow, int boxCol, int value) {
		boolean[] boxesChecked = new boolean[3];
		boxesChecked[isRow ? boxCol : boxRow] = true;
		for (int i = 0; i < boxesChecked.length; i++) {
			if (!boxesChecked[i] && !boxes[isRow ? boxRow : i][isRow ? i : boxCol].containsValue(value)) {
				return false;
			}
		}
		return true;
	}

	private boolean isOnlyViableValueColWise(int row, int col, int value) {
		return isOnlyViableValue(false, row, col, value);
	}

	private boolean isOnlyViableValueRowWise(int row, int col, int value) {
		return isOnlyViableValue(true, row, col, value);
	}

	private boolean isOnlyViableValue(boolean rowWise, int row, int col, int value) {
		int lower = rowWise ? col / 3 : row / 3;
		lower *= 3;
		for (int i = lower; i < lower + 3; i++) {
			boolean isColValid = rowWise
					&& i != col
					&& !getSudokuValue(row, i).hasActualValue()
					&& !isValueInCol(i, value);
			boolean isRowValid = !rowWise
					&& i != row
					&& !getSudokuValue(i, col).hasActualValue()
					&& !isValueInRow(i, value);
			if (isRowValid ^ isColValid) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 0-based index
	 */
	private boolean isValueInRow(int row, int value) {
		for (int i = 0; i < 9; i++) {
			SudokuValue sudokuValue = getSudokuValue(row, i);
			if (sudokuValue.hasActualValue() && sudokuValue.getActualValue() == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 0-based index
	 */
	private boolean isValueInCol(int col, int value) {
		for (int i = 0; i < 9; i++) {
			SudokuValue sudokuValue = getSudokuValue(i, col);
			if (sudokuValue.hasActualValue() && sudokuValue.getActualValue() == value) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (i == 3 || i == 6) {
				bldr.append("-------------------\n");
			}
			for (int j = 0; j < 9; j++) {
				if (j == 3 || j == 6) {
					bldr.append('|');
				}
				int value = getValue(i, j);
				bldr.append(value == 0 ? " " : Integer.toString(value));
				bldr.append(' ');
			}
			bldr.append('\n');
		}
		return bldr.toString();
	}

	public String toStringInput() {
		StringBuilder bldr = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int value = getValue(i, j);
				bldr.append(value == 0 ? '0' : Integer.toString(value));
				bldr.append(' ');
			}
			bldr.append('\n');
		}
		return bldr.toString();
	}

	public String toStringWithNotes() {
		StringBuilder bldr = new StringBuilder();
		StringBuilder notes = new StringBuilder();
		bldr.append("   ");
		notes.append('\n');
		for (int i = 0; i < 9; i++) {
			if (i == 3 || i == 6) {
				bldr.append(" ");
			}
			bldr.append(i).append(' ');
		}
		bldr.append("\n");
		for (int i = 0; i < 9; i++) {
			if (i == 3 || i == 6) {
				bldr.append("  -------------------\n");
			}
			bldr.append(i).append('.').append(' ');
			for (int j = 0; j < 9; j++) {
				if (j == 3 || j == 6) {
					bldr.append('|');
				}
				int value = getValue(i, j);
				bldr.append(value == 0 ? ' ' : Integer.toString(value));
				bldr.append(' ');

				SudokuValue sudokuValue = getSudokuValue(i, j);
				if (!sudokuValue.hasActualValue()) {
					notes.append(i).append(", ").append(j).append(": ");
					boolean notesExist = false;
					for (int note : sudokuValue.getNotes()) {
						notes.append(note).append(", ");
						notesExist = true;
					}
					if (notesExist) {
						notes.deleteCharAt(notes.length() - 1);
						notes.deleteCharAt(notes.length() - 1);
					}
					notes.append('\n');
				}
			}
			bldr.append('\n');
		}
		bldr.append(notes);
		return bldr.toString();
	}
}
