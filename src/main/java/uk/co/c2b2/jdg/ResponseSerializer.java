package uk.co.c2b2.jdg;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logging.Logger;

public class ResponseSerializer {

	private static final Logger LOG = Logger.getLogger(ResponseSerializer.class);
	
	private final ObjectMapper mapper;

    private ResponseSerializer() {
		JsonFactory factory = new JsonFactory();
		mapper = new ObjectMapper(factory);
    }
    
    private static class ResponseSerializerHolder {
            private static final ResponseSerializer INSTANCE = new ResponseSerializer();
    }

    /**
     * 
     * @return
     */
    public static ResponseSerializer getInstance() {
            return ResponseSerializerHolder.INSTANCE;
    }

    /**
     * 
     * @param response
     * @return
     */
	public String serialize(Object response) {
		try {
			return mapper.writeValueAsString(response);
		} catch (Exception e) {
			LOG.error("Unable to serialize Object: [" + response + "]", e);
			return null;
		}

	}

}
