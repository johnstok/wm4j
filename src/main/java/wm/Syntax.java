/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;



/**
 * Defines regex character classes for basic HTTP syntax.
 *
 * @author Keith Webster Johnston.
 */
@Specification(name="rfc-2616", section="2.2")
public class Syntax {

    /*
   The following rules are used throughout this specification to
   describe basic parsing constructs. The US-ASCII coded character set
   is defined by ANSI X3.4-1986 [21].

       OCTET          = <any 8-bit sequence of data>
       CHAR           = <any US-ASCII character (octets 0 - 127)>
       UPALPHA        = <any US-ASCII uppercase letter "A".."Z">
       LOALPHA        = <any US-ASCII lowercase letter "a".."z">
       ALPHA          = UPALPHA | LOALPHA
       DIGIT          = <any US-ASCII digit "0".."9">
       CTL            = <any US-ASCII control character
                        (octets 0 - 31) and DEL (127)>
       CR             = <US-ASCII CR, carriage return (13)>
       LF             = <US-ASCII LF, linefeed (10)>
       SP             = <US-ASCII SP, space (32)>
       HT             = <US-ASCII HT, horizontal-tab (9)>
       <">            = <US-ASCII double-quote mark (34)>

   HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all
   protocol elements except the entity-body (see appendix 19.3 for
   tolerant applications). The end-of-line marker within an entity-body
   is defined by its associated media type, as described in section 3.7.

       CRLF           = CR LF

   HTTP/1.1 header field values can be folded onto multiple lines if the
   continuation line begins with a space or horizontal tab. All linear
   white space, including folding, has the same semantics as SP. A
   recipient MAY replace any linear white space with a single SP before
   interpreting the field value or forwarding the message downstream.

       LWS            = [CRLF] 1*( SP | HT )

   The TEXT rule is only used for descriptive field contents and values
   that are not intended to be interpreted by the message parser. Words
   of *TEXT MAY contain characters from character sets other than ISO-
   8859-1 [22] only when encoded according to the rules of RFC 2047
   [14].

       TEXT           = <any OCTET except CTLs,
                        but including LWS>

   A CRLF is allowed in the definition of TEXT only as part of a header
   field continuation. It is expected that the folding LWS will be
   replaced with a single SP before interpretation of the TEXT value.

   Hexadecimal numeric characters are used in several protocol elements.

       HEX            = "A" | "B" | "C" | "D" | "E" | "F"
                      | "a" | "b" | "c" | "d" | "e" | "f" | DIGIT

   Many HTTP/1.1 header field values consist of words separated by LWS
   or special characters. These special characters MUST be in a quoted
   string to be used within a parameter value (as defined in section
   3.6).

       token          = 1*<any CHAR except CTLs or separators>
       separators     = "(" | ")" | "<" | ">" | "@"
                      | "," | ";" | ":" | "\" | <">
                      | "/" | "[" | "]" | "?" | "="
                      | "{" | "}" | SP | HT
    */

    public static final String TOKEN = "[\\p{ASCII}&&[^\\(\\)<>@,;\\:\\\\\"/\\[\\]\\?\\=\\{\\} \t]&&[^\\p{Cntrl}]]";

    /*

   Comments can be included in some HTTP header fields by surrounding
   the comment text with parentheses. Comments are only allowed in
   fields containing "comment" as part of their field value definition.
   In all other fields, parentheses are considered part of the field
   value.

       comment        = "(" *( ctext | quoted-pair | comment ) ")"
       ctext          = <any TEXT excluding "(" and ")">

   A string of text is parsed as a single word if it is quoted using
   double-quote marks.

       quoted-string  = ( <"> *(qdtext | quoted-pair ) <"> )
       qdtext         = <any TEXT except <">>

   The backslash character ("\") MAY be used as a single-character
   quoting mechanism only within quoted-string and comment constructs.

       quoted-pair    = "\" CHAR
     */
}
