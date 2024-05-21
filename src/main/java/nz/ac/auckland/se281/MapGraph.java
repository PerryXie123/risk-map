package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** MapGraph class which stores the adjacent data. */
public class MapGraph {
  private Map<Country, List<Country>> adjCountries;

  /** The constructor method for the adjacent map. */
  public MapGraph() {
    // Sets the adjacent countries map
    this.adjCountries = new HashMap<>();
  }

  /**
   * This method adds a country as a node.
   *
   * @param node the country to be added.
   */
  public void addNode(Country node) {
    // Adds countries as nodes
    adjCountries.putIfAbsent(node, new ArrayList<>());
  }

  /**
   * This method adds an adjacency between two countries.
   *
   * @param node1 the first country.
   * @param node2 the second country.
   */
  public void addEdge(Country node1, Country node2) {
    // Adds an edge between two different countries
    addNode(node1);
    addNode(node2);
    adjCountries.get(node1).add(node2);
    adjCountries.get(node2).add(node1);
  }

  /**
   * This method loads the data into a hashmap of its adjacencies.
   *
   * @param countryAdj list of adjacent countries.
   * @param countryData list of the country data.
   */
  public void loadCountries(List<String> countryAdj, List<Country> countryData) {
    // Initialises the original and adjacent countries
    Country countryOriginal = null;
    Country adjacentCountry = null;
    // Loops through each line in the adjacent countries list
    for (String data : countryAdj) {
      // Splits the data by commas
      String[] parts = data.split(",");
      // Loops through the adjacent countries
      for (int i = 1; i < parts.length; i++) {
        for (Country country : countryData) {
          if (country.getCountry().equals(parts[0])) {
            // Initialises the original country
            countryOriginal = country;
          }
        }
        for (Country country : countryData) {
          if (country.getCountry().equals(parts[i])) {
            // Initialises the adjacent country
            adjacentCountry = country;
          }
        }
        // Adds a border between the two countries
        addEdge(countryOriginal, adjacentCountry);
      }
    }
  }

  /**
   * Getter method for the adjacent countries.
   *
   * @return the map of the adjacent countries.
   */
  public Map<Country, List<Country>> getAdj() {
    // Returns the adjacent countries map
    return adjCountries;
  }
}
