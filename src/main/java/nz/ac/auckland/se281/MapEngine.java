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
  List<Country> countryList = new LinkedList<>();
  boolean validCountry = false;
  MapGraph graph = new MapGraph();

  public MapEngine() {
    // add other code here if you want
    loadMap(); // keep this method invocation
  }

  /** invoked one time only when constracting the MapEngine class. */
  private void loadMap() {
    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();
    for (String country : countries) {
      String[] countryInfo = country.split(",");
      Country countryInstance = new Country(countryInfo[0], countryInfo[1], countryInfo[2]);
      countryList.add(countryInstance);
    }
    graph.loadCountries(adjacencies, countryList);
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    MessageCli.INSERT_COUNTRY.printMessage();
    String country = Utils.scanner.nextLine();
    country = Utils.capitalizeFirstLetterOfEachWord(country);

    while (validCountry == false) {
      try {
        findCountry(country);
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(country);
        country = Utils.scanner.nextLine();
        country = Utils.capitalizeFirstLetterOfEachWord(country);
      }
    }
    validCountry = false;
    for (Country countryComp : countryList) {
      if (country.equals(countryComp.getCountry())) {
        MessageCli.COUNTRY_INFO.printMessage(
            countryComp.getCountry(), countryComp.getContinent(), countryComp.getTax());
      }
    }
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {
    List<Country> path = new ArrayList<>();
    Integer fee = 0;
    Set<String> hashSet = new LinkedHashSet<>();

    String countryStart;
    String countryEnd;

    Country startCountry = null;
    Country endCountry = null;

    MessageCli.INSERT_SOURCE.printMessage();
    countryStart = Utils.scanner.nextLine();
    countryStart = Utils.capitalizeFirstLetterOfEachWord(countryStart);

    while (validCountry == false) {
      try {
        findCountry(countryStart);
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(countryStart);
        countryStart = Utils.scanner.nextLine();
        countryStart = Utils.capitalizeFirstLetterOfEachWord(countryStart);
      }
    }

    validCountry = false;

    MessageCli.INSERT_DESTINATION.printMessage();
    countryEnd = Utils.scanner.nextLine();
    countryEnd = Utils.capitalizeFirstLetterOfEachWord(countryEnd);

    while (validCountry == false) {
      try {
        findCountry(countryEnd);
      } catch (InvalidCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(countryEnd);
        countryEnd = Utils.scanner.nextLine();
        countryEnd = Utils.capitalizeFirstLetterOfEachWord(countryEnd);
      }
    }

    for (Country country : countryList) {
      if (countryStart.equals(country.getCountry())) {
        startCountry = country;
      }
      if (countryEnd.equals(country.getCountry())) {
        endCountry = country;
      }
    }

    if (countryStart.equals(countryEnd)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }
    path = breadthFirstTraversal(startCountry, endCountry);
    for (Country country : path) {
      fee += Integer.valueOf(country.getTax());
      hashSet.add(country.getContinent());
    }

    fee -= Integer.valueOf(startCountry.getTax());

    StringBuilder stringCountry = new StringBuilder();
    StringBuilder stringContinent = new StringBuilder();
    stringCountry.append("[");
    stringContinent.append("[");

    for (int i = 0; i < path.size() - 1; i++) {
      stringCountry.append(path.get(i).getCountry());
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

    stringCountry.append(path.get(path.size() - 1).getCountry());
    stringCountry.append("]");

    String lastContinent = null;
    for (String continent : hashSet) {
      lastContinent = continent;
    }

    stringContinent.append(lastContinent);
    stringContinent.append("]");

    MessageCli.ROUTE_INFO.printMessage(stringCountry.toString());
    MessageCli.CONTINENT_INFO.printMessage(stringContinent.toString());
    MessageCli.TAX_INFO.printMessage(fee.toString());
  }

  public void findCountry(String countryInput) {
    int count = 0;
    for (Country country : countryList) {
      if (countryInput.equals(country.getCountry())) {
        count++;
      }
    }
    if (count == 0) {
      validCountry = false;
      throw new InvalidCountryException();
    } else {
      validCountry = true;
    }
  }

  public List<Country> breadthFirstTraversal(Country root, Country target) {
    List<Country> visited = new ArrayList<>();
    Queue<Country> queue = new LinkedList<>();
    Map<Country, Country> previous = new HashMap<>();

    queue.add(root);
    visited.add(root);
    previous.put(root, null);

    while (!queue.isEmpty()) {
      Country current = queue.poll();

      if (current.equals(target)) {
        List<Country> path = new LinkedList<>();
        for (Country currentCountry = target;
            currentCountry != null;
            currentCountry = previous.get(currentCountry)) {
          path.add(0, currentCountry);
        }
        return path;
      }

      for (Country neighbor : graph.adjCountries.get(current)) {
        if (!visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
          previous.put(neighbor, current);
        }
      }
    }

    return new ArrayList<>();
  }
}
