package uk.co.automatictester.lightning.tests;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.co.automatictester.lightning.data.JMeterTransactions;
import uk.co.automatictester.lightning.enums.TestResult;
import uk.co.automatictester.lightning.utils.Percent;

public class PassedTransactionsRelativeTest extends ClientSideTest {

    private static final String EXPECTED_RESULT_MESSAGE = "Percent of failed transactions <= %s";
    private static final String ACTUAL_RESULT_MESSAGE = "Percent of failed transactions = %s";

    private Percent allowedPercentOfFailedTransactions;
    private int failureCount;

    private PassedTransactionsRelativeTest(String testName, Percent percent) {
        super("passedTransactionsTest", testName);
        this.allowedPercentOfFailedTransactions = percent;
        this.expectedResultDescription = String.format(EXPECTED_RESULT_MESSAGE, allowedPercentOfFailedTransactions.getValue());
    }

    protected void calculateActualResult(JMeterTransactions jmeterTransactions) {
        failureCount = getFailureCount(jmeterTransactions);
        actualResult = (int) (((float) failureCount / transactionCount) * 100);
    }

    protected void calculateActualResultDescription() {
        actualResultDescription = String.format(ACTUAL_RESULT_MESSAGE, actualResult);
    }

    protected void calculateTestResult() {
        if (actualResult > (float) allowedPercentOfFailedTransactions.getValue()) {
            result = TestResult.FAIL;
        } else {
            result = TestResult.PASS;
        }
    }

    private void calculateResultForRelativeTreshold(int failureCount) {
        int percentOfFailedTransactions = (int) (((float) failureCount / transactionCount) * 100);
        if (percentOfFailedTransactions > (float) allowedPercentOfFailedTransactions.getValue()) {
            result = TestResult.FAIL;
        } else {
            result = TestResult.PASS;
        }
        actualResult = percentOfFailedTransactions;
        actualResultDescription = String.format(ACTUAL_RESULT_MESSAGE, percentOfFailedTransactions);
    }


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static class Builder {
        private String testName;
        private Percent allowedPercentOfFailedTransactions;
        private String description;
        private String transactionName;
        private boolean regexp = false;

        public Builder(String testName, int percent) {
            this.testName = testName;
            this.allowedPercentOfFailedTransactions = new Percent(percent);
        }

        public PassedTransactionsRelativeTest.Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public PassedTransactionsRelativeTest.Builder withTransactionName(String transactionName) {
            this.transactionName = transactionName;
            return this;
        }

        public PassedTransactionsRelativeTest.Builder withRegexp() {
            this.regexp = true;
            return this;
        }

        public PassedTransactionsRelativeTest build() {
            PassedTransactionsRelativeTest test;
            test = new PassedTransactionsRelativeTest(testName, allowedPercentOfFailedTransactions);
            test.description = this.description;
            test.transactionName = this.transactionName;
            test.regexp = this.regexp;
            return test;
        }
    }
}
