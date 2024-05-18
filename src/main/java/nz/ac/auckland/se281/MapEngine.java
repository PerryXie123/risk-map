package nz.ac.auckland.se281;

import java.util.LinkedList;
import java.util.List;

/** This class is the main entry point. */
public class MapEngine {
  List<Country> countryList = new LinkedList<>();

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
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    String country = Utils.scanner.nextLine();
    for (Country countryComp : countryList) {
      if(country.equals(countryComp.getCountry())){
        MessageCli.COUNTRY_INFO.printMessage(countryComp.getCountry(), countryComp.getContinent(), countryComp.getTax());
      }
    }
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {}
}
