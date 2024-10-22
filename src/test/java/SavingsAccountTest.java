import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class SavingsAccountTest {

    @SuppressWarnings("deprecation")
    @Test
    public void givenSavingsAccount_whenNoArgConstructorCalled_thenDefaultsSet() {
        // Test that an empty object is created with default values
        SavingsAccount s = new SavingsAccount();
        assertThat("Balance should be zero", s.getBalance(), is(0.0d));
        assertThat("Name should be empty", s.getName(), is(""));
        assertThat("Rate should be 0", s.getInterestRate(), is(0.0d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenSavingsAccount_whenNegativeInterestRate_thenExceptionIsThrown() {
        // Interest rate should never be negative, expect an exception
        SavingsAccount s = new SavingsAccount("name", -1, 0, -1, -1);
        // We should never reach this point
        System.out.println(s);
    }

	@SuppressWarnings("deprecation")
    @Test
    public void givenSavingsAccount_whenMonthEndCalled_thenInterestApplied() {
        // Test if monthEnd applies interest correctly
        SavingsAccount s = new SavingsAccount("Test Account", 1, 1000.0, 0.05, 1L);
        s.monthEnd();
        assertThat("Balance should include monthly interest", s.getBalance(), is(1004.17)); // Approx 5% annual rate applied monthly
    }

    @SuppressWarnings("deprecation")
	@Test
    public void givenSavingsAccount_whenBelowMinimumBalance_thenFeeAppliedAtMonthEnd() {
        SavingsAccount s = new SavingsAccount("Test Account", 1, 500.0, 0.05, 1L);
        s.setMinimumBalance(600);  
        s.setBelowMinimumFee(10);  
        s.monthEnd();
        assertThat("Balance should include below minimum fee deduction", s.getBalance(), is(490.0)); // 500 - 10 fee
    }

	@SuppressWarnings("deprecation")
    @Test
    public void givenSavingsAccount_whenToCSVCalled_thenCorrectCSVGenerated() {
        SavingsAccount s = new SavingsAccount("Test Account", 123456L, 1000.0, 0.05, 1L);
        String expectedCsv = "123456, Test Account, 1000.0, 0.05, 1, v1";
        assertThat("CSV output should match the expected format", s.toCSV(), is(expectedCsv));
    }
}
