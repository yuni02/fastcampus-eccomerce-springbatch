package fastcampus.ecommerce.batch.dto;

import fastcampus.ecommerce.batch.domain.product.ProductStatus;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUploadCsvRow {
    private Long sellerId;

    private String category;
    private String productName;
    private String salesStartDate;
    private String productStatus;
    private String brand;
    private String manufacturer;

    private int salesPrice;
    private int stockQuantity;
}
