package cs.ut.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadTestProperties {

	
	Properties props;
	
	public LoadTestProperties() {
		
		props = new Properties();
		InputStream in = getClass().getResourceAsStream("/test.properties");
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
