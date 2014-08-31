package graph;

import java.util.ArrayList;

//trieda ktorou vytvaram permutacie pri hladani najkratsej cesty v grafe
//zdroj algoritmu permutacii http://stackoverflow.com/a/20906510
public class Permutation {

	public static ArrayList<int[]> permutations(int[] a) {
		ArrayList<int[]> ret = new ArrayList<int[]>();
		permutation(a, 0, ret);
		return ret;
	}

	private static void permutation(int[] arr, int pos, ArrayList<int[]> list) {
		if (arr.length - pos == 1)
			list.add(arr.clone());
		else
			for (int i = pos; i < arr.length; i++) {
				swap(arr, pos, i);
				permutation(arr, pos + 1, list);
				swap(arr, pos, i);
			}
	}

	private static void swap(int[] arr, int pos1, int pos2) {
		int h = arr[pos1];
		arr[pos1] = arr[pos2];
		arr[pos2] = h;
	}
}
