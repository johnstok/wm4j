/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 * Modified by   $Author$
 * Modified on   $Date$
 *
 * Changes: see version control.
 *-----------------------------------------------------------------------------
 */
package wm;


/**
 * A value with a relative weighting.
 *
 * @author Keith Webster Johnston.
 */
public class WeightedValue
    implements
        Comparable<WeightedValue> {

    private static final String QUALITY_DELIMITER = ";q=";         //$NON-NLS-1$

    private final String _value;
    private final float  _weight;


    /**
     * Constructor.
     *
     * @param value  The value.
     * @param weight The value's relative weighting.
     */
    public WeightedValue(final String value, final float weight) {
        super();
        _value = value;
        _weight = weight;
    }


    /** {@inheritDoc} */
    @Override
    public int compareTo(final WeightedValue o) {
        if (null == o) {
            return -1;
        } else if (o.getWeight() < getWeight()) {
            return -1;
        } else if (o.getWeight() == getWeight()) {
            return 0;
        } else {
            return 1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final WeightedValue other = (WeightedValue) obj;
        if (_value == null) {
            if (other._value != null) { return false; }
        } else if (!_value.equals(other._value)) { return false; }
        return true;
    }


    /**
       * Accessor.
       *
       * @return Returns the value.
       */
    public String getValue() {
        return _value;
    }


    /**
       * Accessor.
       *
       * @return Returns the weight.
       */
    public float getWeight() {
        return _weight;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getValue() + QUALITY_DELIMITER + getWeight();
    }
}
