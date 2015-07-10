package uk.co.automatictester.lightning;

import org.testng.annotations.Test;
import uk.co.automatictester.lightning.exceptions.XMLFileLoadingException;
import uk.co.automatictester.lightning.exceptions.XMLFileNumberFormatException;
import uk.co.automatictester.lightning.tests.PassedTransactionsTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.co.automatictester.lightning.data.TestData.*;

public class TestSetTest {

    @Test
    public void verifyLoadMethod() {
        PassedTransactionsTest passedTransactionsTestA = new PassedTransactionsTest("Test #1", "Verify number of passed tests", "Login", 0);
        PassedTransactionsTest passedTransactionsTestB = new PassedTransactionsTest("Test #2", "Verify number of passed tests", null, 0);

        TestSet testSet = new TestSet();
        testSet.load(TEST_SET_2_0_0);

        assertThat(testSet.getTests(), hasSize(2));
        assertThat(testSet.getTests(), hasItem(passedTransactionsTestA));
        assertThat(testSet.getTests(), hasItem(passedTransactionsTestB));
    }

    @Test(expectedExceptions = XMLFileLoadingException.class)
    public void verifyLoadMethodLoadingException() {
        // suppress error output - coming NOT from own code
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));

        TestSet testSet = new TestSet();
        testSet.load(TEST_SET_NOT_WELL_FORMED);

        System.setErr(null);
    }

    @Test(expectedExceptions = XMLFileNumberFormatException.class)
    public void verifyLoadMethodNumbefFormatException() {
        TestSet testSet = new TestSet();
        testSet.load(TEST_SET_NOT_VALID);
    }

    @Test
    public void verifyExecuteMethod() {
        JMeterTransactions jmeterTranactions = new JMeterCSVFileReader().read(CSV_2_TRANSACTIONS);

        TestSet testSet = new TestSet();
        testSet.load(TEST_SET_2_0_0);
        testSet.execute(jmeterTranactions);

        assertThat(testSet.getPassCount(), is(2));
        assertThat(testSet.getFailCount(), is(0));
        assertThat(testSet.getErrorCount(), is(0));
    }
}