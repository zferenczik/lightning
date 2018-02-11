package uk.co.deliverymind.lightning.lambda.readers;

import uk.co.deliverymind.lightning.data.PerfMonDataEntries;
import uk.co.deliverymind.lightning.exceptions.CSVFileIOException;
import uk.co.deliverymind.lightning.exceptions.CSVFileNoTransactionsException;
import uk.co.deliverymind.lightning.lambda.s3.S3Client;
import uk.co.deliverymind.lightning.readers.PerfMonDataReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PerfMonS3ObjectDataReader extends PerfMonDataReader {

    private static S3Client s3Client;

    public PerfMonS3ObjectDataReader(String region, String bucket) {
        s3Client = new S3Client(region, bucket);
    }

    public PerfMonDataEntries getDataEntires(String csvObject) {
        PerfMonDataEntries perfMonDataEntries = new PerfMonDataEntries();
        String csvObjectContent = s3Client.getS3ObjectContent(csvObject);
        try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(csvObjectContent.getBytes()))) {
            perfMonDataEntries.addAll(getParser().parseAll(isr));
        } catch (IOException e) {
            throw new CSVFileIOException(e);
        }
        if (perfMonDataEntries.isEmpty()) {
            throw new CSVFileNoTransactionsException();
        }
        return perfMonDataEntries;
    }
}
