/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;



/**
 * Tests objects to confirm they meet specified requirements.
 *
 * @author Keith Webster Johnston.
 */
public class Contract {

    /**
     * Constructor.
     */
    private Contract() { super(); }


    /**
     * Create a contract for input parameters.
     *
     * @return A new contract.
     */
    public static Contract require() { return new Contract(); }


    /**
     * Test that a string matches a given regular expression.
     *
     * @param regex The regular expression.
     * @param value The string to test.
     *
     * @return Returns the 'value' parameter.
     */
    public String matches(final String regex, final String value) {
        if (!value.matches(regex)) {
            throw new IllegalArgumentException(
                "String '"
                + value
                + "' does not match regular expression /"
                + regex
                + "/.");
        }
        return value;
    }


}
