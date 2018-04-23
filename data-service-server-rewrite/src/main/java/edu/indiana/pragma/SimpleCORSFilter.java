package edu.indiana.pragma;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.MultivaluedMap;

public class SimpleCORSFilter implements ContainerResponseFilter {

        @Override
        public ContainerResponse filter(ContainerRequest containerRequest,
                                        ContainerResponse containerResponse) {
                MultivaluedMap<String, Object> headers = containerResponse.getHttpHeaders();
                // add CORS header to allow accesses from other domains
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                headers.add("Access-Control-Max-Age", "3600");
                headers.add("Access-Control-Allow-Headers", "x-requested-with");
                return containerResponse;
        }

}

