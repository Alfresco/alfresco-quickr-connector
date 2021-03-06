
package org.alfresco.repo.lotus.ws;

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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getApproveDraftsReturn" type="{http://webservices.clb.content.ibm.com}ClbDraftsResponse"/>
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
    "getApproveDraftsReturn"
})
@XmlRootElement(name = "getApproveDraftsResponse")
public class GetApproveDraftsResponse {

    @XmlElement(required = true)
    protected ClbDraftsResponse getApproveDraftsReturn;

    /**
     * Gets the value of the getApproveDraftsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraftsResponse }
     *     
     */
    public ClbDraftsResponse getGetApproveDraftsReturn() {
        return getApproveDraftsReturn;
    }

    /**
     * Sets the value of the getApproveDraftsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraftsResponse }
     *     
     */
    public void setGetApproveDraftsReturn(ClbDraftsResponse value) {
        this.getApproveDraftsReturn = value;
    }

}
