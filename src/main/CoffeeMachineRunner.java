package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CoffeeMachineRunner {

	public static void main(String[] args) {

		JSONParser parser = new JSONParser();

		try {
			// Parsing JSON file for machine configuration //
			Object obj = parser.parse(new FileReader("input.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONObject machine = (JSONObject) jsonObject.get("machine");
			JSONObject ingredients = (JSONObject) machine.get("total_items_quantity");
			JSONObject beverages = (JSONObject) machine.get("beverages");
			JSONObject outlets = (JSONObject) machine.get("outlets");
			long numberOfOutlets = (long) outlets.get("count_n");

			CoffeeMachine cm = new CoffeeMachine(ingredients, beverages); // Instance of machine created
			cm.showMenu();

			// Reading order list from text file //
			File file = new File("testcases.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));

			List<String> orders = new ArrayList<String>();
			List<Future<?>> temp = new ArrayList<Future<?>>();

			String st;
			try {
				while ((st = br.readLine()) != null) {
					orders.add(st); // Adding all orders in an array list
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println();

			ExecutorService executor = Executors.newFixedThreadPool((int) numberOfOutlets);

			for (String item : orders) { // Splitting orders among 'count_n' threads/outlets
				temp.add(executor.submit(new Runnable() {
					public void run() {
						long threadId = Thread.currentThread().getId();
						cm.order(item, threadId % numberOfOutlets + 1); // Prepare beverage from machine
					}
				}));
			}

			executor.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
