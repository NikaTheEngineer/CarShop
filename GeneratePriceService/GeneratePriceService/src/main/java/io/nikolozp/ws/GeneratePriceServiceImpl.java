package io.nikolozp.ws;

import io.nikolozp.generatepriceservice.GeneratePriceRequest;
import io.nikolozp.generatepriceservice.GeneratePriceResponse;
import org.springframework.stereotype.Service;

@Service
public class GeneratePriceServiceImpl implements GeneratePriceService{
    @Override
    public GeneratePriceResponse getPrice(GeneratePriceRequest request) {
        GeneratePriceResponse toRet = new GeneratePriceResponse();
        toRet.setPrice("" + request.getModel().getModelName().length()*1000);
        return toRet;
    }
}
