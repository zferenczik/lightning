package uk.co.automatictester.lightning.core.data;

import com.univocity.parsers.common.processor.ConcurrentRowProcessor;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.automatictester.lightning.core.exceptions.CSVFileNonexistentLabelException;
import uk.co.automatictester.lightning.core.s3.S3Client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.co.automatictester.lightning.core.enums.JMeterColumns.*;

public class JMeterTransactions extends CsvEntries {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected JMeterTransactions() {
    }

    private JMeterTransactions(File csvFile) {
        long start = System.currentTimeMillis();
        log.debug("Reading CSV file - start");

        loadFromFile(csvFile);
        throwExceptionIfEmpty();

        long finish = System.currentTimeMillis();
        long millisecondsBetween = finish - start;
        log.debug("Reading CSV file - finish, read {} rows, took {}ms", entries.size(), millisecondsBetween);
    }

    private JMeterTransactions(String region, String bucket, String csvObject) {
        s3Client = new S3Client(region, bucket);
        long start = System.currentTimeMillis();
        log.debug("Reading CSV file - start");

        loadFromS3Object(csvObject);
        throwExceptionIfEmpty();

        long finish = System.currentTimeMillis();
        long millisecondsBetween = finish - start;
        log.debug("Reading CSV file - finish, read {} rows, took {}ms", entries.size(), millisecondsBetween);
    }

    private JMeterTransactions(List<String[]> entries) {
        super(entries);
    }

    public static JMeterTransactions fromFile(File csvFile) {
        return new JMeterTransactions(csvFile);
    }

    public static JMeterTransactions fromS3Object(String region, String bucket, String csvObject) {
        return new JMeterTransactions(region, bucket, csvObject);
    }

    public static JMeterTransactions fromList(List<String[]> entries) {
        return new JMeterTransactions(entries);
    }

    public JMeterTransactions getTransactionsWith(String label) {
        JMeterTransactions transactions = new JMeterTransactions();
        for (String[] transaction : entries) {
            if (transaction[TRANSACTION_LABEL_INDEX.getValue()].equals(label)) {
                transactions.add(transaction);
            }
        }
        if (transactions.size() == 0) {
            throw new CSVFileNonexistentLabelException(label);
        }
        return transactions;
    }

    public JMeterTransactions getTransactionsMatching(String labelPattern) {
        JMeterTransactions transactions = new JMeterTransactions();
        for (String[] transaction : entries) {
            if (transaction[TRANSACTION_LABEL_INDEX.getValue()].matches(labelPattern)) {
                transactions.add(transaction);
            }
        }
        if (transactions.size() == 0) {
            throw new CSVFileNonexistentLabelException(labelPattern);
        }
        return transactions;
    }

    public List<Integer> getLongestTransactions() {
        List<Integer> transactionDurations = getTransactionDurationsDesc();
        return getLongestTransactionDurations(transactionDurations);
    }

    public int getFailCount() {
        int failCount = 0;
        for (String[] transaction : entries) {
            if ("false".equals(transaction[TRANSACTION_RESULT_INDEX.getValue()])) {
                failCount++;
            }
        }
        return failCount;
    }

    public long getFirstTransactionTimestamp() {
        long minTimestamp = 0;
        for (String[] transaction : entries) {
            long currentTransactionTimestamp = Long.parseLong(transaction[TRANSACTION_TIMESTAMP.getValue()]);
            if (minTimestamp == 0 || currentTransactionTimestamp < minTimestamp) {
                minTimestamp = currentTransactionTimestamp;
            }
        }
        return minTimestamp;
    }

    public long getLastTransactionTimestamp() {
        long maxTimestamp = 0;
        for (String[] transaction : entries) {
            long currentTransactionTimestamp = Long.parseLong(transaction[TRANSACTION_TIMESTAMP.getValue()]);
            if (maxTimestamp == 0 || currentTransactionTimestamp > maxTimestamp) {
                maxTimestamp = currentTransactionTimestamp;
            }
        }
        return maxTimestamp;
    }

    private List<Integer> getTransactionDurationsDesc() {
        List<Integer> transactionDurations = new ArrayList<>();
        for (String[] transaction : entries) {
            int elapsed = Integer.parseInt(transaction[TRANSACTION_DURATION_INDEX.getValue()]);
            transactionDurations.add(elapsed);
        }
        Collections.sort(transactionDurations);
        Collections.reverse(transactionDurations);
        return transactionDurations;
    }

    private List<Integer> getLongestTransactionDurations(List<Integer> transactionDurations) {
        int transactionDurationsCount = (transactionDurations.size() >= MAX_NUMBER_OF_LONGEST_TRANSACTIONS.getValue()) ? MAX_NUMBER_OF_LONGEST_TRANSACTIONS.getValue() : transactionDurations.size();
        return transactionDurations.subList(0, transactionDurationsCount);
    }

    protected CsvParserSettings getCsvParserSettings() {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.selectFields("label", "elapsed", "success", "timeStamp");
        RowListProcessor rowProcessor = new RowListProcessor();
        ConcurrentRowProcessor concurrentRowProcessor = new ConcurrentRowProcessor(rowProcessor);
        parserSettings.setProcessor(concurrentRowProcessor);
        return parserSettings;
    }
}