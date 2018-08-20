package uk.co.automatictester.lightning.core.tests;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import uk.co.automatictester.lightning.core.data.JMeterTransactions;
import uk.co.automatictester.lightning.core.enums.TestResult;
import uk.co.automatictester.lightning.core.tests.base.RespTimeBasedTest;
import uk.co.automatictester.lightning.core.utils.IntToOrdConverter;

import static uk.co.automatictester.lightning.core.enums.JMeterColumns.TRANSACTION_DURATION_INDEX;

public class RespTimeNthPercentileTest extends RespTimeBasedTest {

    private static final String TEST_TYPE = "nthPercRespTimeTest";
    private static final String MESSAGE = "%s percentile of transactions have response time ";
    private static final String EXPECTED_RESULT_MESSAGE = MESSAGE + "<= %s";
    private static final String ACTUAL_RESULT_MESSAGE = MESSAGE + "= %s";

    private final long maxRespTime;
    private final int percentile;

    private RespTimeNthPercentileTest(String testName, long maxRespTime, int percentile) {
        super("nthPercRespTimeTest", testName);
        this.maxRespTime = maxRespTime;
        this.percentile = percentile;
        this.expectedResultDescription = String.format(EXPECTED_RESULT_MESSAGE, new IntToOrdConverter().convert(percentile), maxRespTime);
    }

    public void calculateActualResultDescription() {
        actualResultDescription = String.format(ACTUAL_RESULT_MESSAGE, new IntToOrdConverter().convert(percentile), actualResult);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return TEST_TYPE.hashCode() + name.hashCode() + (int) maxRespTime;
    }

    protected void calculateActualResult(JMeterTransactions jmeterTransactions) {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        ds.setPercentileImpl(new Percentile().withEstimationType(Percentile.EstimationType.R_3));
        jmeterTransactions.getEntries().forEach(transaction -> {
            String elapsed = transaction[TRANSACTION_DURATION_INDEX.getValue()];
            ds.addValue(Double.parseDouble(elapsed));
        });
        actualResult = (int) ds.getPercentile((double) percentile);
    }

    protected void calculateTestResult() {
        if (actualResult > maxRespTime) {
            result = TestResult.FAIL;
        } else {
            result = TestResult.PASS;
        }
    }

    public static class Builder {
        private String testName;
        private long maxRespTime;
        private int percentile;
        private String description;
        private String transactionName;
        private boolean regexp = false;

        public Builder(String testName, long maxRespTime, int percentile) {
            this.testName = testName;
            this.maxRespTime = maxRespTime;
            this.percentile = percentile;
        }

        public RespTimeNthPercentileTest.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public RespTimeNthPercentileTest.Builder withTransactionName(String transactionName) {
            this.transactionName = transactionName;
            return this;
        }

        public RespTimeNthPercentileTest.Builder withRegexp() {
            this.regexp = true;
            return this;
        }

        public RespTimeNthPercentileTest build() {
            RespTimeNthPercentileTest test = new RespTimeNthPercentileTest(testName, maxRespTime, percentile);
            test.description = this.description;
            test.transactionName = this.transactionName;
            test.regexp = this.regexp;
            return test;
        }
    }
}
