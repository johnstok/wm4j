/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
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
