package cs.ut.domain.soap;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@XmlRootElement
public class PlantResource {

	
	private String plantName;
    private BigDecimal pricePerDay;
    private String description;
    private long identifier;
	
}
