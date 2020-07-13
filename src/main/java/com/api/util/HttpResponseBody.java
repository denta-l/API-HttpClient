package com.api.util;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseBody {
	
	
	public String BrandName;
	public String Id;
	public String LaptopName;
	public Features Features;
	
	

}

class Features{
	public List<String> Feature = new ArrayList<String>();
}
