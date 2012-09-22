//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.09.22 at 11:20:53 AM BST 
//


package uk.ac.ed.inf.setmac.config.v1;

import java.math.BigDecimal;
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
 *     &lt;extension base="{urn://uk/ac/ed/inf/setmac/config/v1}ldaSvmBase">
 *       &lt;attribute name="alpha" type="{http://www.w3.org/2001/XMLSchema}decimal" default="50.0" />
 *       &lt;attribute name="beta" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.01" />
 *       &lt;attribute name="useSymmetricAlpha" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="numThreads" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveNonZeroInt" default="1" />
 *       &lt;attribute name="printLogLikelihood" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="numInferencerIterations" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveInt" default="100" />
 *       &lt;attribute name="inferencerThinning" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveNonZeroInt" default="10" />
 *       &lt;attribute name="inferencerBurnIn" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveNonZeroInt" default="10" />
 *       &lt;attribute name="svmType" default="CSvc">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="CSvc"/>
 *             &lt;enumeration value="NuSvc"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="svmKernelType" default="RBF">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Linear"/>
 *             &lt;enumeration value="Polynomial"/>
 *             &lt;enumeration value="RBF"/>
 *             &lt;enumeration value="Sigmoid"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="svmDegree" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveNonZeroInt" default="3" />
 *       &lt;attribute name="svmGamma" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.5" />
 *       &lt;attribute name="svmCoef0" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.0" />
 *       &lt;attribute name="svmCost" type="{http://www.w3.org/2001/XMLSchema}decimal" default="100.0" />
 *       &lt;attribute name="svmNu" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.5" />
 *       &lt;attribute name="svmLossFunctionEpsilon" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.1" />
 *       &lt;attribute name="svmCacheSize" type="{urn://uk/ac/ed/inf/ace/config/v1}positiveNonZeroInt" default="40" />
 *       &lt;attribute name="svmTerminationToleranceEpsilon" type="{http://www.w3.org/2001/XMLSchema}decimal" default="0.001" />
 *       &lt;attribute name="svmShrinking" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="svmProbabilityEstimates" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
public class LdaSvm
    extends LdaSvmBase
{

    @XmlAttribute(name = "alpha")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal alpha;
    @XmlAttribute(name = "beta")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal beta;
    @XmlAttribute(name = "useSymmetricAlpha")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Boolean useSymmetricAlpha;
    @XmlAttribute(name = "numThreads")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer numThreads;
    @XmlAttribute(name = "printLogLikelihood")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Boolean printLogLikelihood;
    @XmlAttribute(name = "numInferencerIterations")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer numInferencerIterations;
    @XmlAttribute(name = "inferencerThinning")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer inferencerThinning;
    @XmlAttribute(name = "inferencerBurnIn")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer inferencerBurnIn;
    @XmlAttribute(name = "svmType")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected String svmType;
    @XmlAttribute(name = "svmKernelType")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected String svmKernelType;
    @XmlAttribute(name = "svmDegree")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer svmDegree;
    @XmlAttribute(name = "svmGamma")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmGamma;
    @XmlAttribute(name = "svmCoef0")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmCoef0;
    @XmlAttribute(name = "svmCost")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmCost;
    @XmlAttribute(name = "svmNu")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmNu;
    @XmlAttribute(name = "svmLossFunctionEpsilon")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmLossFunctionEpsilon;
    @XmlAttribute(name = "svmCacheSize")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Integer svmCacheSize;
    @XmlAttribute(name = "svmTerminationToleranceEpsilon")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected BigDecimal svmTerminationToleranceEpsilon;
    @XmlAttribute(name = "svmShrinking")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Boolean svmShrinking;
    @XmlAttribute(name = "svmProbabilityEstimates")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    protected Boolean svmProbabilityEstimates;

    /**
     * Gets the value of the alpha property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getAlpha() {
        if (alpha == null) {
            return new BigDecimal("50.0");
        } else {
            return alpha;
        }
    }

    /**
     * Sets the value of the alpha property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setAlpha(BigDecimal value) {
        this.alpha = value;
    }

    /**
     * Gets the value of the beta property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getBeta() {
        if (beta == null) {
            return new BigDecimal("0.01");
        } else {
            return beta;
        }
    }

    /**
     * Sets the value of the beta property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setBeta(BigDecimal value) {
        this.beta = value;
    }

    /**
     * Gets the value of the useSymmetricAlpha property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isUseSymmetricAlpha() {
        if (useSymmetricAlpha == null) {
            return false;
        } else {
            return useSymmetricAlpha;
        }
    }

    /**
     * Sets the value of the useSymmetricAlpha property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setUseSymmetricAlpha(Boolean value) {
        this.useSymmetricAlpha = value;
    }

    /**
     * Gets the value of the numThreads property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getNumThreads() {
        if (numThreads == null) {
            return  1;
        } else {
            return numThreads;
        }
    }

    /**
     * Sets the value of the numThreads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setNumThreads(Integer value) {
        this.numThreads = value;
    }

    /**
     * Gets the value of the printLogLikelihood property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isPrintLogLikelihood() {
        if (printLogLikelihood == null) {
            return true;
        } else {
            return printLogLikelihood;
        }
    }

    /**
     * Sets the value of the printLogLikelihood property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setPrintLogLikelihood(Boolean value) {
        this.printLogLikelihood = value;
    }

    /**
     * Gets the value of the numInferencerIterations property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getNumInferencerIterations() {
        if (numInferencerIterations == null) {
            return  100;
        } else {
            return numInferencerIterations;
        }
    }

    /**
     * Sets the value of the numInferencerIterations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setNumInferencerIterations(Integer value) {
        this.numInferencerIterations = value;
    }

    /**
     * Gets the value of the inferencerThinning property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getInferencerThinning() {
        if (inferencerThinning == null) {
            return  10;
        } else {
            return inferencerThinning;
        }
    }

    /**
     * Sets the value of the inferencerThinning property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setInferencerThinning(Integer value) {
        this.inferencerThinning = value;
    }

    /**
     * Gets the value of the inferencerBurnIn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getInferencerBurnIn() {
        if (inferencerBurnIn == null) {
            return  10;
        } else {
            return inferencerBurnIn;
        }
    }

    /**
     * Sets the value of the inferencerBurnIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setInferencerBurnIn(Integer value) {
        this.inferencerBurnIn = value;
    }

    /**
     * Gets the value of the svmType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public String getSvmType() {
        if (svmType == null) {
            return "CSvc";
        } else {
            return svmType;
        }
    }

    /**
     * Sets the value of the svmType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmType(String value) {
        this.svmType = value;
    }

    /**
     * Gets the value of the svmKernelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public String getSvmKernelType() {
        if (svmKernelType == null) {
            return "RBF";
        } else {
            return svmKernelType;
        }
    }

    /**
     * Sets the value of the svmKernelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmKernelType(String value) {
        this.svmKernelType = value;
    }

    /**
     * Gets the value of the svmDegree property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getSvmDegree() {
        if (svmDegree == null) {
            return  3;
        } else {
            return svmDegree;
        }
    }

    /**
     * Sets the value of the svmDegree property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmDegree(Integer value) {
        this.svmDegree = value;
    }

    /**
     * Gets the value of the svmGamma property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmGamma() {
        if (svmGamma == null) {
            return new BigDecimal("0.5");
        } else {
            return svmGamma;
        }
    }

    /**
     * Sets the value of the svmGamma property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmGamma(BigDecimal value) {
        this.svmGamma = value;
    }

    /**
     * Gets the value of the svmCoef0 property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmCoef0() {
        if (svmCoef0 == null) {
            return new BigDecimal("0.0");
        } else {
            return svmCoef0;
        }
    }

    /**
     * Sets the value of the svmCoef0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmCoef0(BigDecimal value) {
        this.svmCoef0 = value;
    }

    /**
     * Gets the value of the svmCost property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmCost() {
        if (svmCost == null) {
            return new BigDecimal("100.0");
        } else {
            return svmCost;
        }
    }

    /**
     * Sets the value of the svmCost property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmCost(BigDecimal value) {
        this.svmCost = value;
    }

    /**
     * Gets the value of the svmNu property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmNu() {
        if (svmNu == null) {
            return new BigDecimal("0.5");
        } else {
            return svmNu;
        }
    }

    /**
     * Sets the value of the svmNu property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmNu(BigDecimal value) {
        this.svmNu = value;
    }

    /**
     * Gets the value of the svmLossFunctionEpsilon property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmLossFunctionEpsilon() {
        if (svmLossFunctionEpsilon == null) {
            return new BigDecimal("0.1");
        } else {
            return svmLossFunctionEpsilon;
        }
    }

    /**
     * Sets the value of the svmLossFunctionEpsilon property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmLossFunctionEpsilon(BigDecimal value) {
        this.svmLossFunctionEpsilon = value;
    }

    /**
     * Gets the value of the svmCacheSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public int getSvmCacheSize() {
        if (svmCacheSize == null) {
            return  40;
        } else {
            return svmCacheSize;
        }
    }

    /**
     * Sets the value of the svmCacheSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmCacheSize(Integer value) {
        this.svmCacheSize = value;
    }

    /**
     * Gets the value of the svmTerminationToleranceEpsilon property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public BigDecimal getSvmTerminationToleranceEpsilon() {
        if (svmTerminationToleranceEpsilon == null) {
            return new BigDecimal("0.001");
        } else {
            return svmTerminationToleranceEpsilon;
        }
    }

    /**
     * Sets the value of the svmTerminationToleranceEpsilon property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmTerminationToleranceEpsilon(BigDecimal value) {
        this.svmTerminationToleranceEpsilon = value;
    }

    /**
     * Gets the value of the svmShrinking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isSvmShrinking() {
        if (svmShrinking == null) {
            return true;
        } else {
            return svmShrinking;
        }
    }

    /**
     * Sets the value of the svmShrinking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmShrinking(Boolean value) {
        this.svmShrinking = value;
    }

    /**
     * Gets the value of the svmProbabilityEstimates property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public boolean isSvmProbabilityEstimates() {
        if (svmProbabilityEstimates == null) {
            return false;
        } else {
            return svmProbabilityEstimates;
        }
    }

    /**
     * Sets the value of the svmProbabilityEstimates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2012-09-22T11:20:53+01:00", comments = "JAXB RI v2.2.4-2")
    public void setSvmProbabilityEstimates(Boolean value) {
        this.svmProbabilityEstimates = value;
    }

}
