//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.22 at 11:20:53 AM BST 
//


package uk.ac.ed.inf.setmac.config.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{urn://uk/ac/ed/inf/setmac/config/v1}engineBase">
 *       &lt;sequence>
 *         &lt;element name="sites">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="site" type="{urn://uk/ac/ed/inf/setmac/config/v1}site" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sites"
})
@XmlRootElement(name = "engine")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
public class Engine
    extends EngineBase
{

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Engine.Sites sites;

    /**
     * Gets the value of the sites property.
     * 
     * @return
     *     possible object is
     *     {@link Engine.Sites }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public Engine.Sites getSites() {
        return sites;
    }

    /**
     * Sets the value of the sites property.
     * 
     * @param value
     *     allowed object is
     *     {@link Engine.Sites }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSites(Engine.Sites value) {
        this.sites = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="site" type="{urn://uk/ac/ed/inf/setmac/config/v1}site" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "site"
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public static class Sites {

        @XmlElement(required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
        protected List<Site> site;

        /**
         * Gets the value of the site property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the site property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSite().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Site }
         * 
         * 
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
        public List<Site> getSite() {
            if (site == null) {
                site = new ArrayList<Site>();
            }
            return this.site;
        }

    }

}