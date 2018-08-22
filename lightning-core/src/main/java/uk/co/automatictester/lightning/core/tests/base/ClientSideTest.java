package uk.co.automatictester.lightning.core.tests.base;

import org.apache.commons.lang3.NotImplementedException;
import uk.co.automatictester.lightning.core.data.JMeterTransactions;
import uk.co.automatictester.lightning.core.enums.TestResult;
import uk.co.automatictester.lightning.core.structures.TestData;

import java.util.List;

import static uk.co.automatictester.lightning.core.enums.JMeterColumns.TRANSACTION_RESULT_INDEX;

public abstract class ClientSideTest extends LightningTest {

    protected String transactionName;
    protected int transactionCount;
    protected boolean regexp = false;

    protected ClientSideTest(String testType, String testName) {
        super(testType, testName);
    }

    public JMeterTransactions filterTransactions(JMeterTransactions originalJMeterTransactions) {
        String transactionName = getTransactionName();
        if (transactionName == null) {
            return originalJMeterTransactions;
        } else {
            if (isRegexp()) {
                return originalJMeterTransactions.getTransactionsMatching(transactionName);
            } else {
                return originalJMeterTransactions.getTransactionsWith(transactionName);
            }
        }
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public boolean isRegexp() {
        return regexp;
    }

    public void execute() {
        try {
            JMeterTransactions originalJMeterTransactions = TestData.getClientSideTestData();
            JMeterTransactions transactions = filterTransactions(originalJMeterTransactions);
            transactionCount = transactions.size();
            calculateActualResult(transactions);
            calculateActualResultDescription();
            calculateTestResult();
        } catch (Exception e) {
            result = TestResult.ERROR;
            actualResultDescription = e.getMessage();
        }
    }

    @Override
    public String getTestExecutionReport() {
        return String.format("Test name:            %s%n" +
                        "Test type:            %s%n" +
                        "%s" +
                        "%s" +
                        "Expected result:      %s%n" +
                        "Actual result:        %s%n" +
                        "Transaction count:    %s%n" +
                        "Test result:          %s%n",
                getName(),
                getType(),
                getDescriptionForReport(),
                getTransactionNameForReport(),
                getExpectedResultDescription(),
                getActualResultDescription(),
                getTransactionCount(),
                getResultForReport());
    }

    protected String getTransactionNameForReport() {
        String message = String.format("Transaction name:     %s%n", getTransactionName());
        return getTransactionName() != null ? message : "";
    }

    public List<Integer> getLongestTransactions() {
        throw new NotImplementedException("Method not implemented for LightningTest which is not RespTimeBasedTest");
    }

    protected int getFailureCount(JMeterTransactions transactions) {
        return (int) transactions.getEntries().stream()
                .filter(t -> !Boolean.parseBoolean(t[TRANSACTION_RESULT_INDEX.getValue()]))
                .count();
    }

    protected abstract void calculateActualResult(JMeterTransactions jmeterTransactions);

    protected abstract void calculateActualResultDescription();

    protected abstract void calculateTestResult();
}
