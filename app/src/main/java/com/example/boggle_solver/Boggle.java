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

class MutableInt { //non-thread safe class for incrementing score
	int value = 1;
	public void increment() { ++value;}
	public int get() { return value; }
}
// Java program for Boggle game
public class Boggle {

	// Alphabet size
	static final int SIZE = 26;

	final int M;
	final int N;
	int LIMIT;
	String board;
	int error; //error code

	Map<Integer,MutableInt> wordCounts = new HashMap<>();
	public Boggle (String board
					, int limit) { //lowest word count limit
		this.M = (int)Math.sqrt(board.length());
		this.N = this.M;

		this.board = board;
		this.LIMIT = limit;
		this.error = 0; //
		checkSize(board.length());
	}

	// trie Node
	static class TrieNode {
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

	// Driver program to test above function
	// Input string guaranteed to be a valid board
	public String[] solveBoggle(String[] dictionary)
	{

		// Let the given dictionary be following

		// root Node of trie
		TrieNode root = new TrieNode();

		// insert all words of dictionary into trie
		for (String s : dictionary) {
			insert(root, s);
		}

		this.board=this.board.toUpperCase();
		char[][] boggle = new char[M][N];

		for (int i = 0; i < this.board.length(); i++){
			boggle[i / M][i % N] = this.board.charAt(i);
			// Debug print board
			// System.out.print(this.board.charAt(i) + " "); if ((i % N) == 5) System.out.println();
		}

		// char boggle[][] = { { 'G', 'I', 'Z' },
		// 					{ 'U', 'E', 'K' },
		// 					{ 'Q', 'S', 'E' } };

		SortedSet<String> set = new TreeSet<>(new BoggleCompare());

		findWords(boggle, root, set);
//		Debug print solved board
//		for (String s : set)  System.out.println(s);
		return set.toArray(new String[0]);
	}
	// 0 - no error, 1 - boardLength
	public int isError() {
		return this.error;
	}
	//Output functions
	// Gets wordcount of an m-length set of words
	public int getCount(int len){
		MutableInt i = wordCounts.get(len);
		return ( i==null ) ? -1 : i.get();
	}

	

	void checkSize(int boardLength){
		if(boardLength >=0){
			int sr = (int)Math.sqrt(boardLength);
			this.error = ((sr * sr)==boardLength) ? this.error : 1;
		}
		this.error = 0;
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
	boolean isSafe(int i, int j, boolean[][] visited)
	{
		return (i >= 0 && i < M && j >= 0
				&& j < N && !visited[i][j]);
	}

	// A recursive function to print
	// all words present on boggle
	void searchWord(TrieNode root, char[][] boggle, int i,
					int j, boolean[][] visited, String str, SortedSet<String> wordList)
	{
		// if we found word in trie / dictionary
		if ((root.leaf) && str.length() >= LIMIT) {
			MutableInt count = wordCounts.get(str.length());
			boolean isContained = wordList.add(str);
			if(count == null)
				wordCounts.put(str.length(), new MutableInt());
			else if (isContained)
				count.increment();

		}


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
										visited, str + ch, wordList);
						}
					}
				}
			}

			// make current element unvisited
			visited[i][j] = false;
		}
	}

	// Prints all words present in dictionary.
	void findWords(char[][] boggle, TrieNode root, SortedSet<String> set)
	{
		// Mark all characters as not visited
		boolean[][] visited = new boolean[M][N];

		StringBuilder str = new StringBuilder();

		// traverse all matrix elements
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				// we start searching for word in dictionary
				// if we found a character which is child
				// of Trie root
				if (root.Child[(boggle[i][j]) - 'A'] != null) {

					str.append(boggle[i][j]);
					searchWord(root.Child[(boggle[i][j]) - 'A'],
							boggle, i, j, visited, str.toString(), set);
					str = new StringBuilder();
				}
			}
		}
	}
}
//This code is contributed by Sumit Ghosh