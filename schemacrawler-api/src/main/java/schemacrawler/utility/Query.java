/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.utility;


import static sf.util.DatabaseUtility.executeSql;
import static sf.util.DatabaseUtility.executeSqlForLong;
import static sf.util.DatabaseUtility.executeSqlForScalar;
import static sf.util.TemplatingUtility.expandTemplate;
import static sf.util.TemplatingUtility.extractTemplateVariables;
import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.JavaSqlType.JavaSqlTypeGroup;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.StringFormat;

/**
 * A SQL query. May be parameterized with ant-like variable references.
 *
 * @author sfatehi
 */
public final class Query
  implements Serializable
{

  private static final Logger LOGGER = Logger.getLogger(Query.class.getName());

  private static final long serialVersionUID = 2820769346069413473L;

  private static String getColumnsListAsString(final List<Column> columns,
                                               final boolean omitLargeObjectColumns)
  {
    final StringBuilder buffer = new StringBuilder(1024);
    for (int i = 0; i < columns.size(); i++)
    {
      final Column column = columns.get(i);
      final JavaSqlTypeGroup javaSqlTypeGroup = column.getColumnDataType()
        .getJavaSqlType().getJavaSqlTypeGroup();
      if (!(omitLargeObjectColumns
            && javaSqlTypeGroup == JavaSqlTypeGroup.large_object))
      {
        if (i > 0)
        {
          buffer.append(", ");
        }
        buffer.append(column.getName());
      }
    }
    return buffer.toString();
  }

  private final String name;
  private final String query;

  /**
   * Definition of a query, including a name, and parameterized or
   * regular SQL.
   *
   * @param name
   *        Query name.
   * @param query
   *        Query SQL.
   */
  public Query(final String name, final String query)
  {
    final boolean isNameProvided = !isBlank(name);
    final boolean isQueryProvided = !isBlank(query);
    if (isNameProvided && isQueryProvided)
    {
      this.name = name;
      this.query = query;
    }
    else if (isNameProvided && !isQueryProvided)
    {
      this.name = this.query = name;
    }
    else
    {
      throw new IllegalArgumentException("No SQL found for query");
    }
  }

  public ResultSet execute(final Statement statement,
                           final InclusionRule schemaInclusionRule)
    throws SQLException
  {
    final String sql = getQuery(schemaInclusionRule);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", name, sql));
    return executeSql(statement, sql);
  }

  public ResultSet execute(final Statement statement,
                           final Table table,
                           final boolean isAlphabeticalSortForTableColumns)
    throws SQLException
  {
    final String sql = getQuery(table, isAlphabeticalSortForTableColumns);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", name, sql));
    return executeSql(statement, sql);
  }

  public long executeForLong(final Connection connection, final Table table)
    throws SchemaCrawlerException
  {
    final String sql = getQuery(table, true);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", name, sql));
    return executeSqlForLong(connection, sql);
  }

  public Object executeForScalar(final Connection connection)
    throws SchemaCrawlerException
  {
    final String sql = getQuery();
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", name, sql));
    return executeSqlForScalar(connection, sql);
  }

  public Object executeForScalar(final Connection connection, final Table table)
    throws SchemaCrawlerException
  {
    final String sql = getQuery(table, true);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", name, sql));
    return executeSqlForScalar(connection, sql);
  }

  /**
   * Gets the query name.
   *
   * @return Query name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Gets the query SQL.
   *
   * @return Query SQL
   */
  public String getQuery()
  {
    return expandTemplate(query);
  }

  /**
   * Determines if this query has substitutable parameters, and whether
   * it should be run once for each table.
   *
   * @return If the query is to be run over each table
   */
  public boolean isQueryOver()
  {
    final Set<String> keys = extractTemplateVariables(query);
    return keys.contains("table");
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return name + ":" + query;
  }

  /**
   * Gets the query with parameters substituted.
   *
   * @param schemaInclusionRule
   *        Schema inclusion rule
   * @return Ready-to-execute query
   */
  private String getQuery(final InclusionRule schemaInclusionRule)
  {
    final Map<String, String> properties = new HashMap<>();
    if (schemaInclusionRule != null)
    {
      final String schemaInclusionPattern = schemaInclusionRule
        .getInclusionPattern().pattern();
      if (!isBlank(schemaInclusionPattern))
      {
        properties.put("schemas", schemaInclusionPattern);
      }
    }

    String sql = query;
    sql = expandTemplate(sql, properties);
    sql = expandTemplate(sql);

    return sql;
  }

  /**
   * Gets the query with parameters substituted.
   *
   * @param table
   *        Table information
   * @return Ready-to-execute query
   */
  private String getQuery(final Table table,
                          final boolean isAlphabeticalSortForTableColumns)
  {
    final Map<String, String> tableProperties = new HashMap<>();
    if (table != null)
    {
      final NamedObjectSort columnsSort = NamedObjectSort
        .getNamedObjectSort(isAlphabeticalSortForTableColumns);
      final List<Column> columns = table.getColumns();
      Collections.sort(columns, columnsSort);

      if (table.getSchema() != null)
      {
        tableProperties.put("schema", table.getSchema().getFullName());
      }
      tableProperties.put("table", table.getFullName());
      tableProperties.put("tablename", table.getName());
      tableProperties.put("columns", getColumnsListAsString(columns, false));
      tableProperties.put("orderbycolumns",
                          getColumnsListAsString(columns, true));
      tableProperties.put("tabletype", table.getTableType().toString());
    }

    String sql = query;
    sql = expandTemplate(sql, tableProperties);
    sql = expandTemplate(sql);

    return sql;
  }

}
