package com.example.boggle_solver;

import java.util.*;

// Add Input, dictionary from file, clean output

class BoggleCompare implements Comparator<String> {
	public int compare(String a, String b){
		if (a.length() != b.length())
			return (Integer.compare(a.length(),b.length()));
		else
			return a.compareTo(b);
	}
}
// Java program for Boggle game
public class Boggle {

	// Alphabet size
	static final int SIZE = 26;

	final int M;
	final int N;
	static int LIMIT = 3;
	String board;

	public Boggle (String board) {
		this.M = (int)Math.sqrt(board.length());
		this.N = this.M;
		this.board = board;
	}

	// trie Node
	class TrieNode {
		TrieNode[] Child = new TrieNode[SIZE];

		// isLeaf is true if the node represents
		// end of a word
		boolean leaf;

		// constructor
		public TrieNode()
		{
			leaf = false;
			for (int i = 0; i < SIZE; i++)
				Child[i] = null;
		}
	}

	// If not present, inserts a key into the trie
	// If the key is a prefix of trie node, just
	// marks leaf node
	void insert(TrieNode root, String Key)
	{
		int n = Key.length();
		TrieNode pChild = root;

		for (int i = 0; i < n; i++) {
			int index = Key.charAt(i) - 'A';

			if (pChild.Child[index] == null)
				pChild.Child[index] = new TrieNode();

			pChild = pChild.Child[index];
		}

		// make last node as leaf node
		pChild.leaf = true;
	}

	// function to check that current location
	// (i and j) is in matrix range
	boolean isSafe(int i, int j, boolean visited[][])
	{
		return (i >= 0 && i < M && j >= 0
				&& j < N && !visited[i][j]);
	}

	// A recursive function to print
	// all words present on boggle
	void searchWord(TrieNode root, char boggle[][], int i,
						int j, boolean visited[][], String str, SortedSet<String> set)
	{
		// if we found word in trie / dictionary
		if ((root.leaf == true) && str.length() > LIMIT)
			set.add(str);

		// If both I and j in range and we visited
		// that element of matrix first time
		if (isSafe(i, j, visited)) {
			// make it visited
			visited[i][j] = true;

			// traverse all child of current root
			for (int K = 0; K < SIZE; K++) {
				if (root.Child[K] != null) {
					// current character
					char ch = (char)(K + 'A');

					for (int a = -1; a < 2; a++){
						for(int b = -1; b < 2; b++){
							if (isSafe(i + a, j + b, visited)
								&& boggle[i + a][j + b] == ch)
								searchWord(root.Child[K], boggle,
										i + a, j + b,
										visited, str + ch, set);
						}
					}
				}
			}

			// make current element unvisited
			visited[i][j] = false;
		}
	}

	// Prints all words present in dictionary.
	void findWords(char boggle[][], TrieNode root, SortedSet<String> set)
	{
		// Mark all characters as not visited
		boolean[][] visited = new boolean[M][N];
		TrieNode pChild = root;

		String str = "";

		// traverse all matrix elements
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				// we start searching for word in dictionary
				// if we found a character which is child
				// of Trie root
				if (pChild.Child[(boggle[i][j]) - 'A'] != null) {

					str = str + boggle[i][j];
					searchWord(pChild.Child[(boggle[i][j]) - 'A'],
							boggle, i, j, visited, str, set);
					str = "";
				}
			}
		}
	}

	// Driver program to test above function
	// Input string guaranteed to be a valid board
	public String[] solveBoggle(String[] dictionary)
	{

		// Let the given dictionary be following

		// root Node of trie
		TrieNode root = new TrieNode();

		// insert all words of dictionary into trie
		int n = dictionary.length;
		for (int i = 0; i < n; i++)
			insert(root, dictionary[i]);

		this.board=this.board.toUpperCase();
		char[][] boggle = new char[M][N];

		for (int i = 0; i < this.board.length(); i++){
			boggle[i / M][i % N] = this.board.charAt(i);
			// Debug
//			System.out.print(this.board.charAt(i) + " ");
//			if ((i % N) == 5) System.out.println();
		}

		// char boggle[][] = { { 'G', 'I', 'Z' },
		// 					{ 'U', 'E', 'K' },
		// 					{ 'Q', 'S', 'E' } };

		SortedSet<String> set = new TreeSet<String>(new BoggleCompare());

		findWords(boggle, root, set);
//		Debug
//		for (String s : set){
//			System.out.println(s);
//		}
		return set.toArray(new String[set.size()]);
	}
}
// This code is contributed by Sumit Ghosh
