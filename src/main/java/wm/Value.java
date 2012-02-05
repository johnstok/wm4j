/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents a string value with associated properties.
 *
 * @author Keith Webster Johnston.
 */
public class Value {

    private final String              _value;
    private final Map<String, String> _props = new HashMap<String, String>();


    /**
     * Constructor.
     *
     * @param value
     */
    public Value(final String value) {
        this(value, new HashMap<String, String>());
    }


    /**
     * Constructor.
     *
     * @param value
     */
    public Value(final String value, final Map<String, String> properties) {
        // FIXME: Handle null.
        _value = value;
        _props.putAll(properties);
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param lRange
     * @return
     */
    public static Value parse(final String string) {
        final HashMap<String, String> properties = new HashMap<String, String>();

        final String[] parts = string.split(";");
        for (int i=1; i<parts.length; i++) {
            final Matcher m =
                Pattern.compile("("+Syntax.TOKEN+"+)[ ]*=[ ]*("+Syntax.TOKEN+"*)")
                .matcher(parts[i].trim());
            if (m.matches()) {
                final int count = m.groupCount();
                if (m.groupCount()==1) {
                    properties.put(m.group(1), "");
                } else {
                    properties.put(m.group(1), m.group(2));
                }
            }
        }

        return new Value(parts[0].trim(), properties);
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param weightingPropertyName
     * @param defaultWeight
     *
     * @return
     */
    public WeightedValue asWeightedValue(final String weightingPropertyName,
                                         final float defaultWeight) {
        final String weightProperty = _props.get(weightingPropertyName);
        float weight =
            (null==weightProperty || weightProperty.trim().isEmpty()) ? defaultWeight :Float.parseFloat(weightProperty);
        if (weight<0) { weight=0; }
        if (weight>defaultWeight) { weight=defaultWeight; }
        return new WeightedValue(_value, weight);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_props == null) ? 0 : _props.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Value other = (Value) obj;
        if (_props == null) {
            if (other._props != null) {
                return false;
            }
        } else if (!_props.equals(other._props)) {
            return false;
        }
        if (_value == null) {
            if (other._value != null) {
                return false;
            }
        } else if (!_value.equals(other._value)) {
            return false;
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() { return _value+" "+_props; }
}
