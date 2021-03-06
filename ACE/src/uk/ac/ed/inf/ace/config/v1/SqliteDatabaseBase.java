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
 * <p>Java class for sqliteDatabaseBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sqliteDatabaseBase">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://uk/ac/ed/inf/ace/config/v1}databaseBase">
 *       &lt;attribute name="pathname" use="required" type="{urn://uk/ac/ed/inf/ace/config/v1}nonEmptyString" />
 *       &lt;attribute name="readOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="cacheSize" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveInt" default="2000" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sqliteDatabaseBase")
@XmlSeeAlso({
    SqliteDatabase.class
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
public abstract class SqliteDatabaseBase
    extends DatabaseBase
{

    @XmlAttribute(name = "pathname", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    protected String pathname;
    @XmlAttribute(name = "readOnly")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    protected Boolean readOnly;
    @XmlAttribute(name = "cacheSize")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer cacheSize;

    /**
     * Gets the value of the pathname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public String getPathname() {
        return pathname;
    }

    /**
     * Sets the value of the pathname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public void setPathname(String value) {
        this.pathname = value;
    }

    /**
     * Gets the value of the readOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isReadOnly() {
        if (readOnly == null) {
            return true;
        } else {
            return readOnly;
        }
    }

    /**
     * Sets the value of the readOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    /**
     * Gets the value of the cacheSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public int getCacheSize() {
        if (cacheSize == null) {
            return  2000;
        } else {
            return cacheSize;
        }
    }

    /**
     * Sets the value of the cacheSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:52+01:00", comments = "JAXB RI v2.2.4-2")
    public void setCacheSize(Integer value) {
        this.cacheSize = value;
    }

}
