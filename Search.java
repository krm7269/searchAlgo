import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Collections;
import java.lang.Math;

public class Search {

	private static ArrayList<String> getInput(String fileArg){
		ArrayList<String> result = new ArrayList<String>();
		if(fileArg.equals("-")){
			Scanner in = new Scanner(System.in);
			System.out.println("Enter Starting City:");
			String start = in.nextLine().strip();
			result.add(start);
			System.out.println("Enter Ending City:");
			String goal = in.nextLine().strip();
			result.add(goal);
			in.close();
		}else{
			try{
				File file = new File(fileArg);
				Scanner scanner = new Scanner(file);
				while(scanner.hasNextLine()){
					result.add(scanner.nextLine().strip());
				}
				scanner.close();
			}catch(FileNotFoundException e){
				System.err.println("File not found: " + fileArg);
				System.exit(0);
			}
		}
		return result;
	}

	private static HashMap<String, ArrayList<String>> generateCityConnections(){
		HashMap<String, ArrayList<String>> connections = new HashMap<>();
		try{
			File file = new File("./edge.dat");
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] cityPoints = line.strip().split("\\s+");
				String start = cityPoints[0].strip();
				String end = cityPoints[1].strip();
				if(!connections.containsKey(start)){
					connections.put(start, new ArrayList<String>());
				}
				if(!connections.get(start).contains(end)){
					connections.get(start).add(end);
				}
				if(!connections.containsKey(end)){
					connections.put(end, new ArrayList<String>());
				}
				if(!connections.get(end).contains(start)){
					connections.get(end).add(start);
				}
			}
			scanner.close();
		}catch(FileNotFoundException e){
			System.err.println("File not found: edge.dat");
			System.exit(0);
		}
		return connections;
	}

	private static HashMap<String, ArrayList<Double>> generateDistanceMap(){
		HashMap<String, ArrayList<Double>> distances = new HashMap<>();
		try{
			File file = new File("./city.dat");
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				String[] cityInfo = line.strip().split("\\s+");
				String city = cityInfo[0].strip();
				double latitute = Double.parseDouble(cityInfo[2].strip());
				double longitude = Double.parseDouble(cityInfo[3].strip());
				ArrayList<Double> coords = new ArrayList<Double>();
				coords.add(latitute);
				coords.add(longitude);
				distances.put(city, coords);
			}
			scanner.close();
		}catch(FileNotFoundException e){
			System.err.println("File not found: city.dat");
			System.exit(0);
		}
		return distances;
	}

	private static double calculateDistance(String start, String end, HashMap<String, ArrayList<Double>> distanceMap){
		return Math.sqrt( (distanceMap.get(start).get(0)-distanceMap.get(end).get(0))*(distanceMap.get(start).get(0)-distanceMap.get(end).get(0)) + (distanceMap.get(start).get(1)-distanceMap.get(end).get(1))*(distanceMap.get(start).get(1)-distanceMap.get(end).get(1)) ) * 100;
	}

	private static double calculatePathDistance(ArrayList<String> path, HashMap<String, ArrayList<Double>> distanceMap){
		double distance = 0.0;
		for (int edge = 0; edge < path.size()-1; edge++) {
			distance += calculateDistance(path.get(edge), path.get(edge+1), distanceMap);
		}
		return distance;
	}

	private static String calculateBFS(ArrayList<String> startAndGoal, HashMap<String, ArrayList<String>> cityConnections, HashMap<String, ArrayList<Double>> distanceMap){
		double totalMiles = 0.0;
		ArrayList<String> answerPath = null;
		ArrayList<ArrayList<String>> paths = new ArrayList<ArrayList<String>>();
		ArrayList<String> alreadyVisited = new ArrayList<String>();
		String start = startAndGoal.get(0);
		ArrayList<String> nodeQueue = new ArrayList<String>();
		nodeQueue.add(start);
		ArrayList<String> starter = new ArrayList<String>();
		starter.add(start);
		paths.add(starter);
		String currentNode = nodeQueue.get(0);
		while(!currentNode.equals(startAndGoal.get(1))){	
			ArrayList<String> connections = cityConnections.get(currentNode);
			Collections.sort(connections);
			ArrayList<String> currentPath = paths.get(0);
			for (String node : connections) {
				if(!alreadyVisited.contains(node) && !nodeQueue.contains(node)){
					nodeQueue.add(node);
					ArrayList<ArrayList<String>> currentPaths = new ArrayList<ArrayList<String>>();
					currentPaths.addAll(paths);
					for (ArrayList<String> path : currentPaths) {
						if(path.get(path.size()-1).equals(currentNode)){
							ArrayList<String> newPath = new ArrayList<String>();
							newPath.addAll(path);
							newPath.add(node);
							paths.add(newPath);
						}
					}
				}
			}
			paths.remove(currentPath);
			alreadyVisited.add(currentNode);
			nodeQueue.remove(currentNode);
			currentNode = nodeQueue.get(0);
		}
		for (ArrayList<String> path : paths) {
			if(path.contains(startAndGoal.get(0)) && path.contains(startAndGoal.get(1))){
				answerPath = path;
			}
		}
		String output = "\nBreadth-First Search Results:\n";
		for (String city : answerPath) {
			output = output + city + "\n";
		}
		totalMiles = calculatePathDistance(answerPath, distanceMap);
		output = output + "That took " + (answerPath.size()-1) + " hops to find.\nTotal distance = " + (int)Math.round(totalMiles) + " miles.\n\n";
		return output;
	}

	private static String calculateDFS(ArrayList<String> startAndGoal, HashMap<String, ArrayList<String>> cityConnections, HashMap<String, ArrayList<Double>> distanceMap){
		double totalMiles = 0.0;
		ArrayList<String> answerPath = null;
		ArrayList<ArrayList<String>> paths = new ArrayList<ArrayList<String>>();
		ArrayList<String> alreadyVisited = new ArrayList<String>();
		String start = startAndGoal.get(0);
		ArrayList<String> nodeQueue = new ArrayList<String>();
		nodeQueue.add(start);
		ArrayList<String> starter = new ArrayList<String>();
		starter.add(start);
		paths.add(starter);
		String currentNode = nodeQueue.get(0);
		while(!currentNode.equals(startAndGoal.get(1))){	
			ArrayList<String> connections = cityConnections.get(currentNode);
			Collections.sort(connections, Collections.reverseOrder());
			ArrayList<String> currentPath = paths.get(0);
			int index = 0;
			for (String node : connections) {
				if(!alreadyVisited.contains(node) && !nodeQueue.contains(node)){
					nodeQueue.add(0, node);
					index++;
					ArrayList<ArrayList<String>> currentPaths = new ArrayList<ArrayList<String>>();
					currentPaths.addAll(paths);
					for (ArrayList<String> path : currentPaths) {
						if(path.get(path.size()-1).equals(currentNode)){
							ArrayList<String> newPath = new ArrayList<String>();
							newPath.addAll(path);
							newPath.add(node);
							paths.add(0, newPath);
						}
					}
				}
			}
			paths.remove(currentPath);
			alreadyVisited.add(currentNode);
			nodeQueue.remove(currentNode);
			currentNode = nodeQueue.get(0);
		}
		for (ArrayList<String> path : paths) {
			if(path.contains(startAndGoal.get(0)) && path.contains(startAndGoal.get(1))){
				answerPath = path;
			}
		}
		String output = "\nDepth-First Search Results:\n";
		for (String city : answerPath) {
			output = output + city + "\n";
		}
		totalMiles = calculatePathDistance(answerPath, distanceMap);
		output = output + "That took " + (answerPath.size()-1) + " hops to find.\nTotal distance = " + (int)Math.round(totalMiles) + " miles.\n\n";
		return output;
	}

	private static ArrayList<String> getShortestPath(HashMap<ArrayList<String>, Double> paths){
		double min = Collections.min(paths.values());
		for (ArrayList<String> path : paths.keySet()) {
			if(paths.get(path)==min){
				return path;
			}
		}
		return null;
	}

	private static String calculateAstar(ArrayList<String> startAndGoal, HashMap<String, ArrayList<String>> cityConnections, HashMap<String, ArrayList<Double>> distanceMap){
		double totalMiles = 0.0;
		ArrayList<String> answerPath = null;
		HashMap<ArrayList<String>, Double> paths = new HashMap<ArrayList<String>, Double>();
		ArrayList<String> alreadyVisited = new ArrayList<String>();
		String start = startAndGoal.get(0);
		ArrayList<String> starter = new ArrayList<String>();
		starter.add(start);
		paths.put(starter, calculateDistance(start, startAndGoal.get(1), distanceMap));
		String currentNode = start;
		while(!currentNode.equals(startAndGoal.get(1))){	
			ArrayList<String> currentPath = getShortestPath(paths);
			currentNode = currentPath.get(currentPath.size()-1);
			ArrayList<String> connections = cityConnections.get(currentNode);
			for (String node : connections) {
				if(node.equals(startAndGoal.get(1))){
					currentPath.add(node);
					answerPath = currentPath;
					currentNode = node;
					break;
				}
				if(!alreadyVisited.contains(node)){
					HashMap<ArrayList<String>, Double> currentPaths = new HashMap<ArrayList<String>, Double>();
					currentPaths.putAll(paths);
					for (ArrayList<String> path : currentPaths.keySet()) {
						if(path.get(path.size()-1).equals(currentNode)){
							ArrayList<String> newPath = new ArrayList<String>();
							newPath.addAll(path);
							newPath.add(node);
							paths.put(newPath, calculatePathDistance(newPath, distanceMap) + calculateDistance(node, startAndGoal.get(1), distanceMap));
						}
					}
				}
			}
			paths.remove(currentPath);
			alreadyVisited.add(currentNode);
		}
		String output = "\nA* Search Results:\n";
		for (String city : answerPath) {
			output = output + city + "\n";
		}
		totalMiles = calculatePathDistance(answerPath, distanceMap);
		output = output + "That took " + (answerPath.size()-1) + " hops to find.\nTotal distance = " + (int)Math.round(totalMiles) + " miles.\n\n";
		return output;
	}

	public static void main(String[] args){
		if(args.length != 2){
			System.err.println("Usage: java Search inputFile outputFile");
			System.exit(0);
		}else{
			HashMap<String, ArrayList<String>> cityConnections = generateCityConnections();
			ArrayList<String> startAndGoal = getInput(args[0].strip());
			for (String city : startAndGoal) {
				if(!cityConnections.containsKey(city)){
					System.err.println("No such city: " + city);
					System.exit(0);
				}
			}
			HashMap<String, ArrayList<Double>> distanceMap = generateDistanceMap();
			String outputBFS = calculateBFS(startAndGoal, cityConnections, distanceMap);
			String outputDFS = calculateDFS(startAndGoal, cityConnections, distanceMap);
			String outputAstar = calculateAstar(startAndGoal, cityConnections, distanceMap);
			if(args[1].strip().equals("-")){
				System.out.println(outputBFS + outputDFS + outputAstar);
			}else{
				try{
					File outputFile = new File(args[1].strip());
					outputFile.createNewFile();
					FileWriter fileWriter = new FileWriter(outputFile, false);
					fileWriter.write(outputBFS + outputDFS + outputAstar);
					fileWriter.close();
				}catch(IOException e){
					System.err.println("Could not write to: " + args[1].strip());
					System.exit(0);
				}
			}
		}
	}
}