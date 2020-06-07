package io.nikolozp.ws;

import io.nikolozp.generatepriceservice.GeneratePriceRequest;
import io.nikolozp.generatepriceservice.GeneratePriceResponse;
import org.springframework.stereotype.Service;

public interface GeneratePriceService {

    public GeneratePriceResponse getPrice(GeneratePriceRequest request);

}
