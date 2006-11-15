/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Trigger;

/**
 * Represents a trigger.
 */
class MutableTrigger
  extends AbstractDependantNamedObject
  implements Trigger
{

  private static final long serialVersionUID = -1619291073229701764L;

  private EventManipulationType eventManipulationType;
  private int actionOrder;
  private String actionCondition;
  private String actionStatement;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;

  MutableTrigger(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   */
  public String getActionCondition()
  {
    return actionCondition;
  }

  void setActionCondition(final String actionCondition)
  {
    this.actionCondition = actionCondition;
  }

  /**
   * {@inheritDoc}
   */
  public int getActionOrder()
  {
    return actionOrder;
  }

  void setActionOrder(final int actionOrder)
  {
    this.actionOrder = actionOrder;
  }

  /**
   * {@inheritDoc}
   */
  public ActionOrientationType getActionOrientation()
  {
    return actionOrientation;
  }

  void setActionOrientation(final ActionOrientationType actionOrientation)
  {
    this.actionOrientation = actionOrientation;
  }

  /**
   * {@inheritDoc}
   */
  public String getActionStatement()
  {
    return actionStatement;
  }

  void setActionStatement(final String actionStatement)
  {
    this.actionStatement = actionStatement;
  }

  /**
   * {@inheritDoc}
   */
  public ConditionTimingType getConditionTiming()
  {
    return conditionTiming;
  }

  void setConditionTiming(final ConditionTimingType conditionTiming)
  {
    this.conditionTiming = conditionTiming;
  }

  /**
   * {@inheritDoc}
   */
  public EventManipulationType getEventManipulationType()
  {
    return eventManipulationType;
  }

  void setEventManipulationType(final EventManipulationType eventManipulationType)
  {
    this.eventManipulationType = eventManipulationType;
  }

}
