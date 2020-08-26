package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CoffeeMachine {

	private static final int threshold = 20; // Threshold below which ingredients need to get refilled
	private static final int processingTime = 500; // Time to prepare a beverage

	private Map<String, Long> ingredients = new HashMap<String, Long>(); // Available ingredients and their quantity
	private final Map<String, Long> maxIngredients = new HashMap<String, Long>(); // Ingredients and their max quantity
	private Map<String, Map<String, Long>> beverages = new HashMap<String, Map<String, Long>>(); // Beverages and their required ingredients

	public CoffeeMachine(Map<String, Long> ingredients, Map<String, Map<String, Long>> beverages) {

		this.ingredients = ingredients;
		this.maxIngredients.putAll(ingredients);
		this.beverages = beverages;

	}

	private void processOrder() {
		try {
			Thread.sleep(processingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void refill(String ingredient) {
		System.out.println(".......... Refilling " + ingredient);
		ingredients.put(ingredient, maxIngredients.get(ingredient));
	}

	private boolean checkAvailabilty(Map<String, Long> mp, String drink, long machineId) {

		for (Entry<String, Long> item : mp.entrySet()) {

			String ingredient = item.getKey();

			if (!ingredients.containsKey(ingredient)) { // The ingredient is not available
				System.out.println("[Outlet-" + machineId + "] " + drink + " can not be prepared as " + ingredient
						+ " is not available.");
				return false; // Order can not be processed
			}

			if (ingredients.get(ingredient) < item.getValue()) { // The ingredient is not sufficient
				System.out.println("[Outlet-" + machineId + "] " + drink + " can not be prepared as " + ingredient
						+ " is not sufficient.");

				refill(ingredient); // Refilling ingredient which is not sufficient

				if (ingredients.get(ingredient) < item.getValue()) { // The ingredient is not sufficient even after refilling
					return false; // Order can not be processed
				}

				return true; // All required ingredients are present after refilling
			}

		}

		return true; // All required ingredients are present
	}

	public void order(String drink, long machineId) {

		Map<String, Long> mp = beverages.get(drink);

		synchronized (this) {

			if (checkAvailabilty(mp, drink, machineId)) { // All required ingredients are available

				System.out.println("[Outlet-" + machineId + "] " + drink + " is getting prepared.");

				for (Entry<String, Long> item : mp.entrySet()) {

					String ingredient = item.getKey();
					ingredients.put(ingredient, ingredients.get(ingredient) - item.getValue()); // Utilize each required ingredient

					if (ingredients.get(ingredient) <= threshold) { //
						System.out.println(".......... " + ingredient + " is running low!");
						refill(ingredient); // Refill ingredients if its quantity falls below threshold
					}

				}

				processOrder(); // Processing time for making beverage

				System.out.println("[Outlet-" + machineId + "] " + drink + " is prepared.");

			}

		}
	}

	public void showMenu() {
		int i = 1;

		System.out.println("____________________");
		System.out.println("|_______MENU_______|");
		System.out.println();

		for (Entry<String, Map<String, Long>> item : beverages.entrySet()) {
			System.out.println("   " + i + ") " + item.getKey());
			i++;
		}

		System.out.println("___________________");
		System.out.println();
	}
}
