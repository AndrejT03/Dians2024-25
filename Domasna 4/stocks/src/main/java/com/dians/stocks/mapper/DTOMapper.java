package com.dians.stocks.mapper;

import com.dians.stocks.domain.Company;
import com.dians.stocks.domain.StockDetailsHistory;
import com.dians.stocks.dto.CompanyDTO;
import com.dians.stocks.dto.StockDTO;
import com.dians.stocks.dto.StockGraphDTO;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

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
}