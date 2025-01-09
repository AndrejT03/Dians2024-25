package com.dians.stocks.mapper;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.helper_models.microservice.StockValues;
import com.dians.stocks.helper_models.dto.CompanyDTO;
import com.dians.stocks.helper_models.dto.StockDTO;
import com.dians.stocks.helper_models.dto.StockGraphDTO;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {
    // These methods are pretty much self-explanatory

    public CompanyDTO convertToCompanyDTO(Company company) {
        return new CompanyDTO()
                .builder()
                .companyId(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .latestTurnoverDate(company.getLatestWrittenDate())
                .build();
    }

    public StockDTO convertToStockDTO(StockDetailsHistory stock) {
        return new StockDTO()
                .builder()
                .date(stock.getDateAsString())
                .originalDate(stock.getDate())
                .lastTransactionPrice(stock.getPriceFormatted(stock.getLastTransactionPrice()))
                .maxPrice(stock.getPriceFormatted(stock.getMaxPrice()))
                .minPrice(stock.getPriceFormatted(stock.getMinPrice()))
                .averagePrice(stock.getPriceFormatted(stock.getAveragePercentage()))
                .averagePercentage(stock.getPriceFormatted(stock.getAveragePercentage()))
                .quantity(stock.getQuantity())
                .turnoverInBestDenars(stock.getPriceFormatted(stock.getTurnoverInBestDenars()))
                .totalTurnoverInDenars(stock.getPriceFormatted(stock.getTotalTurnoverInDenars()))
                .build();
    }

    public StockGraphDTO convertToStockGraphDTO(StockDetailsHistory stock) {
        return new StockGraphDTO()
                .builder()
                .date(stock.getDate())
                .price(stock.getLastTransactionPrice())
                .build();
    }

    public StockValues convertToStockValues(StockDetailsHistory stock) {
        return new StockValues()
            .builder()
            .lastTransactionPrice(stock.getLastTransactionPrice())
            .maxPrice(stock.getMaxPrice())
            .minPrice(stock.getMinPrice())
            .quantity(stock.getQuantity())
            .build();
    }
}