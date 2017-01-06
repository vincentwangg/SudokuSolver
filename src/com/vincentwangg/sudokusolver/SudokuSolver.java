package com.vincentwangg.sudokusolver;

import java.util.Scanner;

public class SudokuSolver {

	public static void main(String[] args) {
		solve(toSudokuMap("1 7 9 0 0 5 0 0 6 \n" +
				"5 0 2 0 0 0 0 0 0 \n" +
				"3 8 4 0 6 0 5 0 0 \n" +
				"0 2 0 7 0 9 3 6 4 \n" +
				"7 3 0 0 0 0 0 9 0 \n" +
				"4 9 0 1 0 3 0 0 0 \n" +
				"0 0 3 0 7 0 9 5 0 \n" +
				"9 0 7 0 0 0 8 0 0 \n" +
				"2 0 0 4 9 0 6 0 7 \n"));
	}

	public static SudokuMap toSudokuMap(String input) {
		Scanner in = new Scanner(input);
		SudokuMap map = new SudokuMap();

		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				int value = in.nextInt();

				if (value != 0) {
					map.setValue(row, col, value);
				}
			}
		}

		return map;
	}

	public static SudokuMap solve(SudokuMap map) {
		if (!map.isValid()) {
			return null;
		}

		// Phase NOTES - mark every SudokuValue's notes
		map.performNotesPhase();
		System.out.println(map);

		int counter = 1;
		while (map.performActPhase()) {
			System.out.println("Change " + counter);
			System.out.println(map);
			counter++;
		}
		if (map.isCompleted()) {
			System.out.println("Completed!");
		}
		else {
			map.performGuessPhase();
		}
		System.out.println("Act phase performed " + counter + " times.");
		return map;
	}
}
