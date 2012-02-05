/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.negotiation;

import java.util.List;
import wm.WeightedValue;


/**
 * Negotiates a selection from a collection of available options.
 *
 * @author Keith Webster Johnston.
 */
public interface Negotiator<T> {

    /**
     * Negotiate a value based on a specified list of preferences.
     *
     * @param preferences The preferences by which selection is made.
     *
     * @return The most preferable option; null if no option meets the specified
     *  preferences.
     */
    public T select(List<WeightedValue> preferences);
}
