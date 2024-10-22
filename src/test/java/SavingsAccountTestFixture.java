import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SavingsAccountTestFixture {
    public static Logger logger = LogManager.getLogger(SavingsAccountTestFixture.class);
    // static final String TEST_FILE = "src/test/resources/SavingsAccountTest.csv".replace('/', File.separatorChar);

    record TestScenario(double initBalance,
                        double interestRate,
                        List<Double> withdrawals,
                        List<Double> deposits,
                        int runMonthEndNTimes,
                        double endBalance
    ) { }

    private static List<TestScenario> testScenarios;

    @SuppressWarnings("deprecation")
    @Test
    public static void runTestScenarios(String filename) throws Exception {
        if (testScenarios == null) {
            System.err.println("Starting tests for file: " + filename);
            System.err.println("\n\n");
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println();
            System.err.println("Note: NOT running any Test Scenarios");
            System.err.println("Run main() method to run scenarios!!");
            System.err.println();
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println("\n\n");
            return;
        }

        for (int testNum = 0; testNum < testScenarios.size(); testNum++) {
            TestScenario scenario = testScenarios.get(testNum);
            logger.info("**** Running test for {}", scenario);

            SavingsAccount sa = new SavingsAccount(
                    "test " + testNum, 1, scenario.initBalance, scenario.interestRate, -1);

            for (double withdrawalAmount : scenario.withdrawals) {
                sa.withdraw(withdrawalAmount);
            }
            for (double depositAmount : scenario.deposits) {
                sa.deposit(depositAmount);
            }

            for (int i = 0; i < scenario.runMonthEndNTimes; i++) {
                sa.monthEnd();
            }

            assertThat("Test #" + testNum + ":" + scenario, sa.getBalance(), is(scenario.endBalance));
        }
    }

    private static void runJunitTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(SavingsAccountTestFixture.class);
        System.out.printf("Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t" + f);
        }
    }

    private static List<Double> parseListOfAmounts(String amounts) {
        if (amounts.trim().isEmpty()) {
            return List.of();
        }
        List<Double> ret = new ArrayList<>();
        logger.debug("Amounts to split: {}", amounts);
        for (String amtStr : amounts.trim().split("\\|")) {
            logger.debug("An Amount: {}", amtStr);
            ret.add(Double.parseDouble(amtStr));
        }
        return ret;
    }

    private static TestScenario parseScenarioString(String scenarioAsString) {
        String[] scenarioValues = scenarioAsString.split(",");
        double initialBalance = Double.parseDouble(scenarioValues[0].trim());
        double interestRate = Double.parseDouble(scenarioValues[1].trim());
        List<Double> wds = parseListOfAmounts(scenarioValues[2]);
        List<Double> deps = parseListOfAmounts(scenarioValues[3]);
        int runMonthEndNTimes = Integer.parseInt(scenarioValues[4].trim());
        double finalBalance = Double.parseDouble(scenarioValues[5].trim());

        TestScenario scenario = new TestScenario(
                initialBalance, interestRate, wds, deps, runMonthEndNTimes, finalBalance);
        return scenario;
    }

    private static List<TestScenario> parseScenarioStrings(List<String> scenarioStrings) {
        logger.info("Parsing test scenarios...");
        List<TestScenario> scenarios = new ArrayList<>();
        for (String scenarioAsString : scenarioStrings) {
            if (scenarioAsString.trim().isEmpty()) {
                continue;
            }
            TestScenario scenario = parseScenarioString(scenarioAsString);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("START");
        

        if (args.length < 1) {
            System.err.println("Please provide the filename as a command-line argument.");
            System.exit(1);
        }

        String filename = args[0];
        System.out.println("Running tests with data from: " + filename);

        try {
            List<String> scenarioStringsFromFile = Files.readAllLines(Paths.get(filename));
            testScenarios = parseScenarioStrings(scenarioStringsFromFile);
            runJunitTests();
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(2);
        }
        System.out.println("DONE");
    }
}
