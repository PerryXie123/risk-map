package nz.ac.auckland.se281;

/** Country class which stores country data. */
public class Country {
  private String country;
  private String continent;
  private String tax;

  /**
   * Constructor method for country.
   *
   * @param country string of the country
   * @param continent string of the continent
   * @param tax string of the tax
   */
  public Country(String country, String continent, String tax) {
    // Sets the country, continent, and tax of a country
    this.country = country;
    this.continent = continent;
    this.tax = tax;
  }

  /**
   * Getter method for the country string.
   *
   * @return the string of the country
   */
  public String getCountry() {
    // Returns country
    return country;
  }

  /**
   * Getter method for the continent string.
   *
   * @return the string of the continent
   */
  public String getContinent() {
    // Returns continent
    return continent;
  }

  /**
   * Getter method for the tax string.
   *
   * @return the string of the tax
   */
  public String getTax() {
    // Returns tax
    return tax;
  }
}
