package utility;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ArrayPermuter {

	private static ArrayList<ArrayList<String>> permutations;

	private ArrayList<String> inputList;

	private static Logger logger;

	public ArrayPermuter() {
		permutations = new ArrayList<ArrayList<String>>();
		logger = Logger.getLogger(ArrayPermuter.class);
	}

	public ArrayList<ArrayList<String>> performPermutations(ArrayList<String> stringList) {
		inputList = stringList;
		String[] inputArray = new String[stringList.size()];

		for (int i = 0; i < inputArray.length; i++) {
			inputArray[i] = stringList.get(i);
		}
		generatePermutation(inputArray);

		return permutations;
	}

	public ArrayList<ArrayList<String>> performNWayPermutations(ArrayList<String> stringList, int r) {
		inputList = stringList;
		String[] inputArray = new String[stringList.size()];

		for (int i = 0; i < inputArray.length; i++) {
			inputArray[i] = stringList.get(i);
		}
		generateNWayPermutation(inputArray, r);

		return permutations;
	}

	public static void generateNWayPermutation(String[] str, int r) {
		String[] prefix = {};
		nWayPermutation(prefix, str, r);
	}

	private static void nWayPermutation(String[] prefix, String[] str, int r) {
		int n = str.length;
		if (prefix.length == r)
			printArray(prefix);
		else {
			for (int i = 0; i < n; i++) {
				String[] newElement = new String[1];
				newElement[0] = str[i];
				String[] newPrefixArray = new String[prefix.length + 1];
				newPrefixArray = combineArrays(prefix, newElement);

				String[] secondArgFirstPart = getArrayElements(str, 0, i);
				String[] secondArgSecondPart = getArrayElements(str, i + 1, n);

				String[] secondArg = combineArrays(secondArgFirstPart, secondArgSecondPart);

				nWayPermutation(newPrefixArray, secondArg, r);
			}
		}
	}

	public static void generatePermutation(String[] str) {
		String[] prefix = {};
		permutation(prefix, str);
	}

	private static void permutation(String[] prefix, String[] str) {
		int n = str.length;
		if (n == 0)
			printArray(prefix);
		else {
			for (int i = 0; i < n; i++) {
				int k = 0;
				String[] newElement = new String[1];
				newElement[0] = str[i];
				String[] newPrefixArray = new String[prefix.length + 1];
				newPrefixArray = combineArrays(prefix, newElement);

				String[] secondArgFirstPart = getArrayElements(str, 0, i);
				String[] secondArgSecondPart = getArrayElements(str, i + 1, n);

				String[] secondArg = combineArrays(secondArgFirstPart, secondArgSecondPart);

				permutation(newPrefixArray, secondArg);
			}
		}
	}

	public static void printArray(String[] arr) {
		if (arr != null) {
			String currSeq = "";
			ArrayList<String> tempList = new ArrayList<String>();
			for (int i = 0; i < arr.length; i++) {
				currSeq += arr[i] + " ";
				tempList.add(arr[i]);
			}
			permutations.add(tempList);

			logger.warn("qq: " + currSeq);

		}
	}

	public static String[] getArrayElements(String[] arr, int i, int k) {
		int len = k - i;
		if (len > 0) {
			String[] returnArray = new String[len];
			for (int j = i, m = 0; j < k; j++) {
				returnArray[m] = arr[j];
				m++;
			}
			return returnArray;
		} else
			return null;
	}

	public static String[] combineArrays(String[] arr1, String[] arr2) {
		int size = 0;
		if (arr1 != null)
			size += arr1.length;

		if (arr2 != null)
			size += arr2.length;

		String[] combinedArray = new String[size];
		int i = 0;
		if (arr1 != null) {
			for (; i < arr1.length; i++) {
				combinedArray[i] = arr1[i];
			}
		}
		int k = 0;

		if (arr2 != null) {
			for (; i < size; i++) {
				combinedArray[i] = arr2[k];
				k++;
			}
		}
		return combinedArray;
	}

	public static ArrayList<ArrayList<String>> getPermutations() {
		return permutations;
	}

	public static void setPermutations(ArrayList<ArrayList<String>> permutations) {
		ArrayPermuter.permutations = permutations;
	}

	public ArrayList<String> getInputList() {
		return inputList;
	}

	public void setInputList(ArrayList<String> inputList) {
		this.inputList = inputList;
	}

}
