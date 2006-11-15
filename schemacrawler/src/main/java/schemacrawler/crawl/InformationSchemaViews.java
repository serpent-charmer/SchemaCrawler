package schemacrawler.crawl;


import java.util.Properties;

import schemacrawler.BaseOptions;
import sf.util.Utilities;

final class InformationSchemaViews
  extends BaseOptions
{

  private static final long serialVersionUID = 3587581365346059044L;

  private Properties informationSchemaViewsSql;

  InformationSchemaViews(final Properties informationSchemaViewsSql)
  {
    if (informationSchemaViewsSql != null)
    {
      this.informationSchemaViewsSql = informationSchemaViewsSql;
    }
    else
    {
      this.informationSchemaViewsSql = new Properties();
    }
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  String getViewsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.VIEWS");
  }

  boolean hasViewsSql()
  {
    return !Utilities.isBlank(getViewsSql());
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  String getTriggersSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.TRIGGERS");
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  String getRoutinesSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.ROUTINES");
  }

  boolean hasRoutinesSql()
  {
    return !Utilities.isBlank(getRoutinesSql());
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  String getTableConstraintsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS");
  }

  boolean hasTableConstraintsSql()
  {
    return !Utilities.isBlank(getTableConstraintsSql());
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  String getCheckConstraintsSql()
  {
    return informationSchemaViewsSql
      .getProperty("select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS");
  }

  boolean hasCheckConstraintsSql()
  {
    return !Utilities.isBlank(getCheckConstraintsSql());
  }

  /**
   * Gets the index info SQL from the additional configuration.
   * 
   * @return Index info constraints SQL.
   */
  String getIndexInfoSql()
  {
    return informationSchemaViewsSql.getProperty("getIndexInfo");
  }

  boolean hasIndexInfoSql()
  {
    return !Utilities.isBlank(getIndexInfoSql());
  }

}
