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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for docTypeBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="docTypeBase">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://uk/ac/ed/inf/ace/config/v1}typeable">
 *       &lt;attribute name="labelPropertyKey" use="required" type="{urn://uk/ac/ed/inf/ace/config/v1}nonEmptyString" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "docTypeBase")
@XmlSeeAlso({
    DocType.class
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
public abstract class DocTypeBase
    extends Typeable
{

    @XmlAttribute(name = "labelPropertyKey", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    protected String labelPropertyKey;

    /**
     * Gets the value of the labelPropertyKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public String getLabelPropertyKey() {
        return labelPropertyKey;
    }

    /**
     * Sets the value of the labelPropertyKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public void setLabelPropertyKey(String value) {
        this.labelPropertyKey = value;
    }

}
