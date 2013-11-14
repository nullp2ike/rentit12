package cs.ut.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadProperties {

	
	Properties props;
	
	public LoadProperties() {
		
		props = new Properties();
		InputStream in = getClass().getResourceAsStream("/local.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String loadProperty(String key){
		
		return props.getProperty(key);
		
	}
}
