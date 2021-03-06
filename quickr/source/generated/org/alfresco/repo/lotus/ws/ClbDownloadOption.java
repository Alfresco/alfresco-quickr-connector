
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDownloadOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbDownloadOption">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbDownloadOption")
@XmlEnum
public enum ClbDownloadOption {

    @XmlEnumValue("None")
    NONE("None");
    private final String value;

    ClbDownloadOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbDownloadOption fromValue(String v) {
        for (ClbDownloadOption c: ClbDownloadOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
