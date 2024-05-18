package nz.ac.auckland.se281;

import java.util.LinkedList;
import java.util.List;

/** This class is the main entry point. */
public class MapEngine {
  List<Country> countryList = new LinkedList<>();
  boolean validCountry = false;

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
    MapGraph graph = new MapGraph();
    graph.loadCountries(adjacencies, countryList);
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
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

    for (Country countryComp : countryList) {
      if (country.equals(countryComp.getCountry())) {
        MessageCli.COUNTRY_INFO.printMessage(
            countryComp.getCountry(), countryComp.getContinent(), countryComp.getTax());
      }
    }
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {}

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
}
