package uk.co.automatictester.lightning.tests;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import uk.co.automatictester.lightning.data.JMeterTransactions;
import uk.co.automatictester.lightning.enums.TestResult;

import java.util.ArrayList;

import static uk.co.automatictester.lightning.constants.JMeterColumns.TRANSACTION_DURATION_INDEX;

public class RespTimeMaxTest extends RespTimeBasedTest {

    private static final String EXPECTED_RESULT_MESSAGE = "Max response time <= %s";
    private static final String ACTUAL_RESULT_MESSAGE = "Max response time = %s";

    private final long maxRespTime;

    private RespTimeMaxTest(String testName, long maxRespTime) {
        super("maxRespTimeTest", testName);
        this.maxRespTime = maxRespTime;
        this.expectedResultDescription = String.format(EXPECTED_RESULT_MESSAGE, maxRespTime);
    }

    @Override
    public void execute(ArrayList<String[]> originalJMeterTransactions) {
        try {
            JMeterTransactions transactions = filterTransactions((JMeterTransactions) originalJMeterTransactions);
            transactionCount = transactions.size();
            calculateActualResult(transactions);
            longestTransactions = transactions.getLongestTransactions();
            actualResultDescription = String.format(ACTUAL_RESULT_MESSAGE, actualResult);
            calculateTestResult();
        } catch (Exception e) {
            result = TestResult.ERROR;
            actualResultDescription = e.getMessage();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    private void calculateActualResult(JMeterTransactions jmeterTransactions) {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        for (String[] transaction : jmeterTransactions) {
            String elapsed = transaction[TRANSACTION_DURATION_INDEX];
            ds.addValue(Double.parseDouble(elapsed));
        }
        actualResult = (int) ds.getMax();
    }

    private void calculateTestResult() {
        if ((long) actualResult > this.maxRespTime) {
            result = TestResult.FAIL;
        } else {
            result = TestResult.PASS;
        }
    }

    public static class Builder {
        private String testName;
        private long maxRespTime;
        private String description;
        private String transactionName;
        private boolean regexp = false;

        public Builder(String testName, long maxRespTime) {
            this.testName = testName;
            this.maxRespTime = maxRespTime;
        }

        public RespTimeMaxTest.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public RespTimeMaxTest.Builder withTransactionName(String transactionName) {
            this.transactionName = transactionName;
            return this;
        }

        public RespTimeMaxTest.Builder withRegexp() {
            this.regexp = true;
            return this;
        }

        public RespTimeMaxTest build() {
            RespTimeMaxTest test = new RespTimeMaxTest(testName, maxRespTime);
            test.description = this.description;
            test.transactionName = this.transactionName;
            test.regexp = this.regexp;
            return test;
        }
    }
}
