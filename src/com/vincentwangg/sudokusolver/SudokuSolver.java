package com.vincentwangg.sudokusolver;

import java.util.Scanner;

public class SudokuSolver {

	public static void main(String[] args) {
		solve(toSudokuMap(
				"0 0 0 0 0 8 0 9 0\n" +
						"4 0 9 5 0 0 0 0 1\n" +
						"0 0 5 0 0 0 8 0 0\n" +
						"0 0 0 0 7 0 0 0 2\n" +
						"0 1 0 6 4 2 0 8 0\n" +
						"8 0 0 0 1 0 0 0 0\n" +
						"0 0 3 0 0 0 9 0 0\n" +
						"5 0 0 0 0 6 2 0 7\n" +
						"0 6 0 4 0 0 0 0 0"));
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

			System.out.println(map);
			counter++;
		}
		System.out.println(map);
//		System.exit(0);
		if (map.isCompleted()) {
			System.out.println("Completed!");
		}
		else {
//			System.out.println(map.toStringWithNotes());
//			System.exit(0);
			map.performGuessPhase();
		}

		if (map.isValid() && map.isCompleted()) {
			System.out.println(map);
			System.out.println("Act phase performed " + counter + " times.");
		}
		return map;
	}
}
