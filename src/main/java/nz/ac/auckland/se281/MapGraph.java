package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapGraph {
  public Map<Country, List<Country>> adjCountries;

  public MapGraph() {
    this.adjCountries = new HashMap<>();
  }

  public void addNode(Country node) {
    adjCountries.putIfAbsent(node, new ArrayList<>());
  }

  public void addEdge(Country node1, Country node2) {
    addNode(node1);
    addNode(node2);
    adjCountries.get(node1).add(node2);
    adjCountries.get(node2).add(node1);
  }

  public void loadCountries(List<String> countryAdj, List<Country> countryData) {
    Country countryOriginal = null;
    Country adjacentCountry = null;
    for (String data : countryAdj) {
      String[] parts = data.split(",");
      for (int i = 1; i < parts.length; i++) {
        for (Country country : countryData) {
          if (country.getCountry().equals(parts[0])) {
            countryOriginal = country;
          }
        }
        for (Country country : countryData) {
          if (country.getCountry().equals(parts[i])) {
            adjacentCountry = country;
          }
        }
        addEdge(countryOriginal, adjacentCountry);
      }
    }
  }
}
