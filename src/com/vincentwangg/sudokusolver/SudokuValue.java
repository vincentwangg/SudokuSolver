package com.vincentwangg.sudokusolver;

import java.util.ArrayList;

public class SudokuValue {

	private int actualValue;
	private ArrayList<Integer> notes = new ArrayList<>(9);

	public SudokuValue() {}

	/**
	 * Copy Constructor
	 */
	public SudokuValue(SudokuValue sudokuValue) {
		copyValue(sudokuValue);
	}

	public void copyValue(SudokuValue sudokuValue) {
		actualValue = sudokuValue.actualValue;
		notes = sudokuValue.getNotes();
	}

	public boolean hasActualValue() {
		return actualValue != 0;
	}

	public int getActualValue() {
		return actualValue;
	}

	public void addNote(int note) {
		if (note <= 9 && note > 0) {
			notes.add(note);
		}
	}

	public boolean containsNote(int note) {
		return notes.contains(note);
	}

	public void addNotes(ArrayList<Integer> notes) {
		this.notes.addAll(notes);
	}

	public ArrayList<Integer> getNotes() {
		ArrayList<Integer> newNotes = new ArrayList<>(notes.size());
		newNotes.addAll(notes);
		return newNotes;
	}

	public boolean setActualValue() {
		if (notes.size() == 1) {
			actualValue = notes.get(0);
			notes.clear();
			return true;
		}
		return false;
	}

	public void setActualValue(int value) {
		if (!hasActualValue()) {
			actualValue = value;
			notes.clear();
		}
	}

	public boolean removeNote(int note) {
		return notes.remove((Integer) note);
	}

	public void removeActualValue() {
		actualValue = 0;
	}

	public void clearNotes() {
		notes.clear();
	}
}
