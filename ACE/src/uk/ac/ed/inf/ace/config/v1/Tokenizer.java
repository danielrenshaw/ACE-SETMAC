//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.22 at 11:20:52 AM BST 
//


package uk.ac.ed.inf.ace.config.v1;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn://uk/ac/ed/inf/ace/config/v1}tokenizerBase">
 *       &lt;attribute name="pattern" use="required" type="{urn://uk/ac/ed/inf/ace/config/v1}nonEmptyString" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
public class Tokenizer
    extends TokenizerBase
{

    @XmlAttribute(name = "pattern", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    protected String pattern;

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public void setPattern(String value) {
        this.pattern = value;
    }

}
