/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.filter;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.ReducibleCollection;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schemacrawler.FilterOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

final class TablesReducer implements Reducer<Table> {

  private final SchemaCrawlerOptions options;
  private final Predicate<Table> tableFilter;

  TablesReducer(final SchemaCrawlerOptions options, final Predicate<Table> tableFilter) {
    this.options = requireNonNull(options, "No SchemaCrawler options provided");
    this.tableFilter = requireNonNull(tableFilter, "No table filter provided");
  }

  @Override
  public void reduce(final ReducibleCollection<? extends Table> allTables) {
    if (allTables == null) {
      return;
    }
    doReduce(allTables);

    removeForeignKeys(allTables);
  }

  private void doReduce(final ReducibleCollection<? extends Table> allTables) {
    // Filter tables, keeping the ones we need
    final Set<Table> reducedTables = new HashSet<>();
    for (final Table table : allTables) {
      if (tableFilter.test(table)) {
        reducedTables.add(table);
      }
    }

    // Add in referenced tables
    final FilterOptions filterOptions = options.getFilterOptions();
    final int childTableFilterDepth = filterOptions.getChildTableFilterDepth();
    final Collection<Table> childTables =
        includeRelatedTables(TableRelationshipType.child, childTableFilterDepth, reducedTables);
    final int parentTableFilterDepth = filterOptions.getParentTableFilterDepth();
    final Collection<Table> parentTables =
        includeRelatedTables(TableRelationshipType.parent, parentTableFilterDepth, reducedTables);

    final Set<Table> keepTables = new HashSet<>();
    keepTables.addAll(reducedTables);
    keepTables.addAll(childTables);
    keepTables.addAll(parentTables);

    // Mark tables as being filtered out
    for (final Table table : allTables) {
      if (isTablePartial(table) || !keepTables.contains(table)) {
        markTableFilteredOut(table);
      }
    }

    allTables.filter(table -> keepTables.contains(table));
  }

  private Collection<Table> includeRelatedTables(
      final TableRelationshipType tableRelationshipType,
      final int depth,
      final Set<Table> greppedTables) {
    final Set<Table> includedTables = new HashSet<>();
    includedTables.addAll(greppedTables);

    for (int i = 0; i < depth; i++) {
      for (final Table table : new HashSet<>(includedTables)) {
        for (final Table relatedTable : table.getRelatedTables(tableRelationshipType)) {
          if (!isTablePartial(relatedTable)) {
            includedTables.add(relatedTable);
          }
        }
      }
    }

    return includedTables;
  }

  private boolean isTablePartial(final Table table) {
    return table instanceof PartialDatabaseObject;
  }

  private void markTableFilteredOut(final Table table) {
    table.setAttribute("schemacrawler.table.filtered_out", true);
    if (options.getGrepOptions().isGrepOnlyMatching()) {
      table.setAttribute("schemacrawler.table.no_grep_match", true);
    }
  }

  private void removeForeignKeys(final ReducibleCollection<? extends Table> allTables) {
    for (final Table table : allTables) {
      for (final ForeignKey foreignKey : table.getExportedForeignKeys()) {
        for (final ColumnReference fkColumnRef : foreignKey) {
          final Table referencedTable = fkColumnRef.getForeignKeyColumn().getParent();
          if (isTablePartial(referencedTable) || allTables.isFiltered(referencedTable)) {
            markTableFilteredOut(referencedTable);
          }
        }
      }
    }
  }
}
