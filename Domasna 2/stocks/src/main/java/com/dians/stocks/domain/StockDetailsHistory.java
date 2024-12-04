package com.dians.stocks.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Setter
@Getter
public class StockDetailsHistory {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private BigDecimal lastTransactionPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal averagePrice;
    private BigDecimal averagePercentage;
    private Integer quantity;
    private BigDecimal turnoverInBestDenars;
    private BigDecimal totalTurnoverInDenars;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public String getDateAsString() {
        return this.date != null ? this.date.format(FORMATTER) : "";
    }

    public String getPriceFormatted(BigDecimal price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();

        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        return decimalFormat.format(price);
    }

}
