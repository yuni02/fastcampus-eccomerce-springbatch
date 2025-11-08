package fastcampus.ecommerce.batch.util;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.ProductUploadCsvRow;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class ProductGenerator {

    public static void main(String[] args) {
        String csvFilePath = "data/random_product.csv";
        int record = 10000000;

        try (FileWriter fileWriter = new FileWriter(
            csvFilePath); CSVPrinter csvPrinter = new CSVPrinter(fileWriter,
            CSVFormat.DEFAULT.builder().setHeader(ReflectionUtils.getFieldNames(
                ProductUploadCsvRow.class).toArray(String[]::new)).build())) {

        } catch (IOException e) {
            System.out.println("IO Exception");
        }
    }

}
