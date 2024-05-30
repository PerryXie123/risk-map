package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/** This class is the main entry point. */
public class MapEngine {
  private List<Country> countryList = new LinkedList<>();
  private boolean validCountry = false;
  private MapGraph graph = new MapGraph();

  public MapEngine() {
    loadMap(); // keep this method invocation
  }

  /** invoked one time only when constracting the MapEngine class. */
  private void loadMap() {
    // Initialises the lists and whatnot needed
    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();
    // Seperated each line in the list by its comma
    for (String country : countries) {
      String[] countryInfo = country.split(",");
      Country countryInstance = new Country(countryInfo[0], countryInfo[1], countryInfo[2]);
      // Adds country to an arraylist
      countryList.add(countryInstance);
    }
    // Loads the country adjacencies
    graph.loadCountries(adjacencies, countryList);
  }

  /** Outputs country information using messagecli and whatnot. */
  public void showInfoCountry() {
    // Prints input message
    // Initialises the neede variables
    MessageCli.INSERT_COUNTRY.printMessage();

    // Gets the input, and capitalises the firse letter
    String country = Utils.scanner.nextLine();
    country = Utils.capitalizeFirstLetterOfEachWord(country);

    // Keeps asking for input
    StringBuilder countryBuilder = new StringBuilder();
    while (!validCountry) {
      try {
        // Use StringBuilder to build the string
        countryBuilder.setLength(0); // Clear the builder
        countryBuilder.append(Utils.capitalizeFirstLetterOfEachWord(country));
        country = countryBuilder.toString();

        // Finds the country
        findCountry(country);
        validCountry = true;
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(country);
        country = Utils.scanner.nextLine();
      }
    }
    validCountry = false;

    // Matches input country to a country inside the list, prints relevant info
    for (Country countryComp : countryList) {
      if (country.equals(countryComp.getCountry())) {
        MessageCli.COUNTRY_INFO.printMessage(
            countryComp.getCountry(), countryComp.getContinent(), countryComp.getTax());
      }
    }
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {
    // Initialises the needed variables
    Integer fee = 0;
    Set<String> hashSet = new LinkedHashSet<>();

    String countryStart;
    String countryEnd;

    Country startCountry = null;
    Country endCountry = null;

    // Prints input message for start country
    MessageCli.INSERT_SOURCE.printMessage();
    countryStart = Utils.scanner.nextLine();
    countryStart = Utils.capitalizeFirstLetterOfEachWord(countryStart);

    // Keeps asking for inputs while the start country is invalid
    // Keeps asking for input
    StringBuilder countryBuilder = new StringBuilder();
    while (!validCountry) {
      try {
        // Use StringBuilder to build the string
        countryBuilder.setLength(0); // Clear the builder
        countryBuilder.append(Utils.capitalizeFirstLetterOfEachWord(countryStart));
        countryStart = countryBuilder.toString();

        // Finds the country
        findCountry(countryStart);
        validCountry = true;
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(countryStart);
        countryStart = Utils.scanner.nextLine();
      }
    }

    validCountry = false;

    // Asks for the end country
    MessageCli.INSERT_DESTINATION.printMessage();
    countryEnd = Utils.scanner.nextLine();
    countryEnd = Utils.capitalizeFirstLetterOfEachWord(countryEnd);

    // Keeps asking for inputs while the end country is invalid
    StringBuilder countryBuilderEnd = new StringBuilder();
    while (!validCountry) {
      try {
        // Use StringBuilder to build the string
        countryBuilderEnd.setLength(0); // Clear the builder
        countryBuilderEnd.append(Utils.capitalizeFirstLetterOfEachWord(countryEnd));
        countryEnd = countryBuilderEnd.toString();

        // Finds the country
        findCountry(countryEnd);
        validCountry = true;
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(countryEnd);
        countryEnd = Utils.scanner.nextLine();
      }
    }

    validCountry = false;

    // Sets the country variables as the starting and ending country
    for (Country country : countryList) {
      if (countryStart.equals(country.getCountry())) {
        startCountry = country;
      }
      if (countryEnd.equals(country.getCountry())) {
        endCountry = country;
      }
    }

    // Prints custom message if the start and end country are the same
    if (countryStart.equals(countryEnd)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }

    // Calls the BFS method
    List<Country> path = findingShortestPath(startCountry, endCountry);

    // Uses pathfinder to construct shortest path using the BFS output
    Stack<Country> stackUnorder = pathFinding(startCountry, endCountry, path);

    // Orders the path
    Stack<Country> stack = new Stack<>();
    while (!stackUnorder.isEmpty()) {
      stack.push(stackUnorder.pop());
    }

    // Updates the tax fee total
    for (Country country : stack) {
      fee += Integer.valueOf(country.getTax());
      hashSet.add(country.getContinent());
    }

    fee -= Integer.valueOf(startCountry.getTax());

    // Creates strings for the output message of continent and country
    StringBuilder stringCountry = new StringBuilder();
    StringBuilder stringContinent = new StringBuilder();

    // Appends the initial square bracket
    stringCountry.append("[");
    stringContinent.append("[");

    // Loops through the path, and appends the countries and continents
    for (int i = 0; i < stack.size() - 1; i++) {
      stringCountry.append(stack.get(i).getCountry());
      stringCountry.append(", ");
    }

    int count = 0;
    for (String continent : hashSet) {
      if (count < hashSet.size() - 1) {
        stringContinent.append(continent);
        stringContinent.append(", ");
      }
      count++;
    }

    // Appends the last country
    stringCountry.append(stack.get(stack.size() - 1).getCountry());
    stringCountry.append("]");

    // Appends the last continent
    String lastContinent = null;
    for (String continent : hashSet) {
      lastContinent = continent;
    }

    stringContinent.append(lastContinent);
    stringContinent.append("]");

    // Prints information for the countries, continents, and fee
    MessageCli.ROUTE_INFO.printMessage(stringCountry.toString());
    MessageCli.CONTINENT_INFO.printMessage(stringContinent.toString());
    MessageCli.TAX_INFO.printMessage(fee.toString());
  }

  /**
   * This method loops through the valid country list to check if the user input is valid.
   *
   * @param countryInput the name of a country which needs to be validified.
   */
  public void findCountry(String countryInput) {
    // Sees if the country is a valid country and is in the list
    int count = 0;
    for (Country country : countryList) {
      if (countryInput.equals(country.getCountry())) {
        count++;
      }
    }
    // Throws and exception if not
    if (count == 0) {
      validCountry = false;
      throw new InvalidCountryException();
    } else {
      validCountry = true;
    }
  }

  /**
   * This method implements the breadth first search algorithm to find all countries from the root
   * to the end.
   *
   * @param root the starting country
   * @param target the ending country
   * @return a list containing all countries visited by BFS
   */
  public List<Country> findingShortestPath(Country root, Country target) {
    // Initialises all needed variables
    List<Country> visited = new ArrayList<>();
    Queue<Country> queue = new LinkedList<>();
    Country targ = null;

    // Executes the BFS algorithm
    queue.add(root);
    visited.add(root);
    // Loops through while the queue isnt empty
    while (!queue.isEmpty() && targ != target) {
      Country node = queue.poll();
      for (Country n : graph.getAdj().get(node)) {
        if (!visited.contains(n)) {
          targ = n;
          visited.add(n);
          queue.add(n);
        }
      }
    }
    // Returns the visited countries
    return visited;
  }

  /**
   * The method uses the list generated by BFS to find the path between two countries using the
   * adjacencies list.
   *
   * @param root the starting country
   * @param end the ending country
   * @param path the path generated by the BFS algorithm
   * @return a stack containing the final path
   */
  public Stack<Country> pathFinding(Country root, Country end, List<Country> path) {
    // Initialises all needed variables
    Stack<Country> stack = new Stack<>();
    Country tempCountry = end;

    // Adds the ending country to the stack
    stack.push(end);

    // Loops through the path list, and compaares it to the current country's adjacencies
    while (tempCountry != root) {
      for (Country country : path) {
        if (graph.hasEdge(country, end)) {
          stack.push(country);
          tempCountry = country;
          break;
        }
      }
      // Sets the end country as a temporary country variable
      end = tempCountry;
    }
    return stack;
  }
}
