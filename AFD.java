

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class AFD {
	private Set<String> Stari;
	private Set<Character> Sigma;
	private Map<String, Map<Character, Set<String>>> Delta; 
	private String StareInitiala;
	private Set<String> StariFinale;

	public AFD() {
		Stari = new HashSet<>();
		Sigma = new HashSet<>();
		Delta = new HashMap<>();
		StareInitiala = "";
		StariFinale = new HashSet<>();
	}

	public void citire() {
		String currentDirectory = new File("").getAbsolutePath();
		String grammarFilePath = currentDirectory + "/automat2.txt";

		try (BufferedReader br = new BufferedReader(new FileReader(grammarFilePath))) {
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("Q:")) {
					Stari.addAll(Arrays.asList(line.substring(3).split(",\\s*")));
				} else if (line.startsWith("Sigma:")) {
					for (String symbol : line.substring(6).split(",\\s*")) {
						Sigma.add(symbol.charAt(0));
					}
				} else if (line.startsWith("delta:")) {
					while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
						line = line.trim();
						String[] transitionParts = line.split("\\s+");
						if (transitionParts.length == 3) {
							String sourceState = transitionParts[0];
							char symbol = transitionParts[1].charAt(0);
							String targetState = transitionParts[2];

							Delta.computeIfAbsent(sourceState, k -> new HashMap<>())
									.computeIfAbsent(symbol, k -> new HashSet<>()).add(targetState);
						} else {
							System.err.println("Invalid transition format: " + line);
							return;
						}
					}
				} else if (line.startsWith("q0:")) {
					StareInitiala = line.substring(3).trim();
				} else if (line.startsWith("F:")) {
					StariFinale.addAll(Arrays.asList(line.substring(2).split(",\\s*")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void afisare() {
		System.out.println("Stari: " + Stari);
		System.out.println("Sigma: " + Sigma);
		System.out.println("Delta: " + Delta);
		System.out.println("Stare Initiala: " + StareInitiala);
		System.out.println("Stari Finale: " + StariFinale);
	}
	
	public void verificare() {
		if (!validare()) {
			System.out.println("Automatul nu este definit corect.");
		}}
	
	public boolean validare() {

		if (Sigma.isEmpty()) {
			System.err.println("Alfabetul nu este definit.");
			return false;
		}
	
		if (Delta.isEmpty()) {
			System.err.println("Delta nu este definita.");
			return false;
		}
	
		if (StareInitiala.isEmpty() || !Stari.contains(StareInitiala)) {
			System.err.println("Stare initiala inexistenta sau invalida.");
			return false;
		}
	
		for (String stareFinala : StariFinale) {
			if (!Stari.contains(stareFinala)) {
				System.err.println("Stare finala invalida: " + stareFinala);
				return false;
			}
		}
	
		Set<Character> uniqueSymbols = new HashSet<>(Sigma);
		if (Sigma.size() != uniqueSymbols.size()) {
			System.err.println("Alfabetul contine caractere duplicate.");
			return false;
		}
	
		for (Map.Entry<String, Map<Character, Set<String>>> entry : Delta.entrySet()) {
			String sourceState = entry.getKey();
			Map<Character, Set<String>> transitions = entry.getValue();
	
			if (!Stari.contains(sourceState)) {
				System.err.println("Stare inexistenta pentru tranzitiile din " + sourceState);
				return false;
			}
	
			for (Map.Entry<Character, Set<String>> transitionEntry : transitions.entrySet()) {
				char symbol = transitionEntry.getKey();
				Set<String> targetStates = transitionEntry.getValue();
	
				if (!Sigma.contains(symbol)) {
					System.err.println("Simbol invalid in tranzitiile din " + sourceState + ": " + symbol);
					return false;
				}
	
				for (String targetState : targetStates) {
					if (!Stari.contains(targetState)) {
						System.err.println("Stare invalida in tranzitiile din " + sourceState + ": " + targetState);
						return false;
					}
				}
			}
		}
	
		return true;
	}
	
	public void findUnreachableStates() {
	    Set<String> reachableStates = new HashSet<>();
	    dfs(StareInitiala, Delta, reachableStates);

	    Set<String> unreachableStates = new HashSet<>(Stari);
	    unreachableStates.removeAll(reachableStates);
	    unreachableStates.remove(StareInitiala);

	    System.out.println("Stari inaccesibile: " + unreachableStates);

	    Stari.removeAll(unreachableStates);

	    for (String state : unreachableStates) {
	        if (Delta.containsKey(state)) {
	            Delta.remove(state);
	        }
	    }

	    for (Map<Character, Set<String>> transitions : Delta.values()) {
	        for (Set<String> states : transitions.values()) {
	            states.removeAll(unreachableStates);
	        }
	    }

	    StariFinale.removeAll(unreachableStates);
	}

	 

	    private void dfs(String currentState, Map<String, Map<Character, Set<String>>> transitions, Set<String> visitedStates) {
	        visitedStates.add(currentState);

	        if (transitions.containsKey(currentState)) {
	            for (Set<String> nextStates : transitions.get(currentState).values()) {
	                for (String nextState : nextStates) {
	                    if (!visitedStates.contains(nextState)) {
	                        dfs(nextState, transitions, visitedStates);
	                    }
	                }
	            }
	        }
	    }
	    
	    public void buildPartialMatrix() {
	        int n = Stari.size();
	        String[] statesArray = Stari.toArray(new String[0]);

	        
	        String[][] matrix = new String[n][n];

	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j < n; j++) {
	                matrix[i][j] = "-";
	            }
	        }
	        for (int i = 0; i < n; i++) {
	            matrix[i][i] = statesArray[i];
	        }

	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j < n; j++) {
	                System.out.print(matrix[i][j] + "\t");
	            }
	            System.out.println();
	        }
	    }
	    
	    public void build1Matrix() {
	        int n = Stari.size();
	        String[] statesArray = Stari.toArray(new String[0]);

	        String[][] matrix = new String[n][n];

	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j < n; j++) {
	                matrix[i][j] = "-";
	            }
	        }
	        for (int i = 0; i < n; i++) {
	            matrix[i][i] = statesArray[i];
	        }

	        for (String finalState : StariFinale) {
	            int finalIndex = Arrays.asList(statesArray).indexOf(finalState);

	            for (int i = 0; i < n; i++) {
	                if (!StariFinale.contains(statesArray[i])) {
	                    matrix[i][finalIndex] = "x";
	                }
	            }
	        }

	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j < n; j++) {
	                System.out.print(matrix[i][j] + "\t");
	            }
	            System.out.println();
	        }
	    }

	    

}
