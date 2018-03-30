package uk.co.automatictester.lightning.tests;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import uk.co.automatictester.lightning.data.JMeterTransactions;
import uk.co.automatictester.lightning.shared.TestData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

public class LightningTestTest {

    @Test
    public void testFilterTransactionsSome() {
        JMeterTransactions jmeterTransactions = new JMeterTransactions();
        jmeterTransactions.add(TestData.LOGIN_1000_SUCCESS);
        jmeterTransactions.add(TestData.SEARCH_800_SUCCESS);

        ClientSideTest test = Mockito.mock(ClientSideTest.class, Mockito.CALLS_REAL_METHODS);
        when(test.getTransactionName()).thenReturn("Search");

        JMeterTransactions filteredTransactions = test.filterTransactions(jmeterTransactions);
        assertThat(filteredTransactions.getTransactionCount(), is(equalTo((1))));
    }

    @Test
    public void testFilterTransactionsAll() {
        JMeterTransactions jmeterTransactions = new JMeterTransactions();
        jmeterTransactions.add(TestData.LOGIN_1000_SUCCESS);
        jmeterTransactions.add(TestData.SEARCH_800_SUCCESS);

        ClientSideTest test = Mockito.mock(ClientSideTest.class, Mockito.CALLS_REAL_METHODS);
        when(test.getTransactionName()).thenReturn(null);

        JMeterTransactions filteredTransactions = test.filterTransactions(jmeterTransactions);
        assertThat(filteredTransactions.getTransactionCount(), is(equalTo((2))));
    }
}