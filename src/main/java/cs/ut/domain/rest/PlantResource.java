package cs.ut.domain.rest;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import cs.ut.domain.Plant;

@RooJavaBean
@RooToString
@XmlRootElement(name = "plant")
public class PlantResource {
	
	private String plantName;
    private BigDecimal pricePerDay;
    private String description;
    
}
