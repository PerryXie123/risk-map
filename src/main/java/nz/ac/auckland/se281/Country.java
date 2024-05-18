package nz.ac.auckland.se281;

public class Country {
  private String country;
  private String continent;
  private String tax;

  public Country(String country, String continent, String tax) {
    this.country = country;
    this.continent = continent;
    this.tax = tax;
  }

  public String getCountry() {
    return country;
  }

  public String getContinent() {
    return continent;
  }

  public String getTax() {
    return tax;
  }
}
