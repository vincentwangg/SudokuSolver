package com.vincentwangg.sudokusolver;

import java.util.Scanner;

public class SudokuSolver {

	public static void main(String[] args) {
		solve(toSudokuMap(
				"0 0 0 0 0 9 8 3 0\n" +
						"0 0 0 3 0 0 5 0 6\n" +
						"0 0 0 5 6 0 0 0 0\n" +
						"3 4 0 0 0 0 0 0 7\n" +
						"0 0 1 0 5 0 9 0 0\n" +
						"9 0 0 0 0 0 0 5 8\n" +
						"0 0 0 0 1 8 0 0 0\n" +
						"1 0 9 0 0 4 0 0 0\n" +
						"0 6 7 9 0 0 0 0 0"));
//		solve(toSudokuMap(
//				"0 0 0 0 4 0 8 0 0\n" +
//						"0 9 2 3 0 0 0 0 4\n" +
//						"4 0 0 0 7 0 0 0 0\n" +
//						"0 5 0 0 0 0 7 2 0\n" +
//						"2 0 0 6 3 7 0 0 8\n" +
//						"0 7 4 0 0 0 0 1 0\n" +
//						"0 0 0 0 9 0 0 0 3\n" +
//						"8 0 0 0 0 3 9 7 0\n" +
//						"0 0 5 0 8 0 0 0 0"));
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
			throw new AssertionError("Map is not valid. Map: \n" + map);
		}

		// Phase NOTES - mark every SudokuValue's notes
		map.performNotesPhase();

		int counter = 1;
		while (map.performActPhase()) {
			counter++;
		}
		if (map.isCompleted()) {
			System.out.println("Completed!");
		}
		else {
			System.out.println(map.toStringWithNotes());
			System.exit(0);
			map.performGuessPhase();
		}

		if (map.isValid() && map.isCompleted()) {
			System.out.println(map);
			System.out.println("Act phase performed " + counter + " times.");
		}
		return map;
	}
}
