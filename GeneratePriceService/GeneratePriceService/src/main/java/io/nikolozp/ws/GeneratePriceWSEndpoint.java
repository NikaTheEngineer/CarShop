package io.nikolozp.ws;

import io.nikolozp.generatepriceservice.GeneratePriceRequest;
import io.nikolozp.generatepriceservice.GeneratePriceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class GeneratePriceWSEndpoint {
    private static final String NAMESPACE_URI = "http://www.nikolozp.io/GeneratePriceService";

    private GeneratePriceService generatePriceService;

    @Autowired
    public GeneratePriceWSEndpoint(GeneratePriceService generatePriceService) throws Exception{
        this.generatePriceService = generatePriceService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GeneratePriceRequest")
    @ResponsePayload
    public GeneratePriceResponse GeneratePriceApplication(@RequestPayload GeneratePriceRequest request) throws Exception{
        return generatePriceService.getPrice(request);
    }

}
