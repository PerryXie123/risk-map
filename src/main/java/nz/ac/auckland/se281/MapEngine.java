package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    // Loops through the country list, and splits up the information by comma
    for (String country : countries) {
      String[] countryInfo = country.split(",");
      Country countryInstance = new Country(countryInfo[0], countryInfo[1], countryInfo[2]);
      // Adds country to an arraylist
      countryList.add(countryInstance);
    }
    // Loads the country adjacencies
    graph.loadCountries(adjacencies, countryList);
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    // Prints input message
    // Initialises the neede variables
    MessageCli.INSERT_COUNTRY.printMessage();

    // Gets the input, and capitalises the firse letter
    String country = Utils.scanner.nextLine();
    country = Utils.capitalizeFirstLetterOfEachWord(country);

    // While the country is invalid, keeps asking for another input
    while (!validCountry) {
      try {
        country = Utils.capitalizeFirstLetterOfEachWord(country);
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
    while (!validCountry) {
      try {
        countryStart = Utils.capitalizeFirstLetterOfEachWord(countryStart);
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
    while (!validCountry) {
      try {
        countryEnd = Utils.capitalizeFirstLetterOfEachWord(countryEnd);
        findCountry(countryEnd);
        validCountry = true;
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(countryEnd);
        countryEnd = Utils.scanner.nextLine();
      }
    }

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

    // Finds the shortest path between two countries using BFS
    List<Country> path = findingShortestPath(startCountry, endCountry);
    for (Country country : path) {
      // Adds the fee up
      fee += Integer.valueOf(country.getTax());
      hashSet.add(country.getContinent());
    }

    // Removes the fee of the starting country
    fee -= Integer.valueOf(startCountry.getTax());

    // Uses stringbuilder to append all the countries and continents visited
    StringBuilder stringCountry = new StringBuilder();
    StringBuilder stringContinent = new StringBuilder();
    stringCountry.append("[");
    stringContinent.append("[");

    // Loops through the past list with all countries visited
    for (int i = 0; i < path.size() - 1; i++) {
      stringCountry.append(path.get(i).getCountry());
      stringCountry.append(", ");
    }

    // Loops through the list of all continents visited
    int count = 0;
    for (String continent : hashSet) {
      if (count < hashSet.size() - 1) {
        stringContinent.append(continent);
        stringContinent.append(", ");
      }
      count++;
    }

    // Appends the final country and continent
    stringCountry.append(path.get(path.size() - 1).getCountry());
    stringCountry.append("]");

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
   * @param countryInput the name of a country which needs to be validified
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
   * This method uses BFS to search through the adjacent countries list and finds the shortest path
   * between two countries.
   *
   * @param root the starting country
   * @param target the ending country
   * @return a list of path of the countries
   */
  public List<Country> findingShortestPath(Country root, Country target) {
    // Initialises starting variables
    List<Country> visited = new ArrayList<>();
    Queue<Country> queue = new LinkedList<>();
    Map<Country, Country> previous = new HashMap<>();

    // Adds the starting country to all lists
    queue.add(root);
    visited.add(root);
    previous.put(root, null);

    // Uses BFS to find the shortest path between two countries
    // Loops while the queue of countries to check is not empty
    while (!queue.isEmpty()) {
      Country current = queue.poll();

      if (current.equals(target)) {
        List<Country> path = new LinkedList<>();
        for (Country currentCountry = target;
            currentCountry != null;
            currentCountry = previous.get(currentCountry)) {
          path.add(0, currentCountry);
        }
        // Returns the shortest path
        return path;
      }

      // Gets the current shortest path
      for (Country neighbor : graph.getAdj().get(current)) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
          previous.put(neighbor, current);
        }
      }
    }
    // Returns an empty array to prevent compiling errors
    return new ArrayList<>();
  }
}
