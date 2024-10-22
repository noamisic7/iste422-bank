Command line to run for CheckingAccount: ./gradlew runCheckingFixture --args="CheckingAccountTest.csv"

build.gradle had to be added to run runCheckingFixture --> task runCheckingFixture(type: JavaExec) {
    group = "Execution"
    description = "Run CheckingAccountTestFixture class"
    classpath = sourceSets.test.runtimeClasspath
    main = "CheckingAccountTestFixture"
}

initialBalance: The starting balance of the account.
checks: Amount of checks that were written. If multiple checks were written, they are separated by |.
withdrawals: Amount of money that was withdrawn, with multiple withdrawals also separated by |.
deposits: Amount of money that was deposited, again separated by | for multiple deposits.
finalBalance: The expected balance at the end after all transactions are done.

initialBalance, checks, withdrawals, deposits, finalBalance --> format in the CheckingAccountTest.csv
| represents that more of one type of transactions were made. For example if 15|20|10 was in the third spot
or better to say at the withdrawals spot if would withdraw 15 than 20 and lastly 10 dollars.
, is seperating this (initialBalance, checks, withdrawals, deposits, finalBalance) values.

This is for basic transactions, it does not account for more complex scenarios. Tt would not be able to calculate for example international fees, or know the transactions limits.

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Command line to run for SavingsAccount: ./gradlew runSavingsFixture --args="SavingsAccountTest.csv"

build.gradle had to be added to run runSavingsFixture --> task runSavingsFixture(type: JavaExec) {
    group = "Execution"
    description = "Run SavingsAccountTestFixture class"
    classpath = sourceSets.test.runtimeClasspath
    main = "SavingsAccountTestFixture"
}

initialBalance: The starting balance of the account.
interestRate: The interest rate applied to the savings account, for example monthly.
withdrawals: Amount of money that was withdrawn, with multiple withdrawals separated by |.
deposits: Amount of money that was deposited, again separated by | for multiple deposits.
runMonthEndNTimes: Number of times to run the month-end processing, which applies the interest.
finalBalance: The expected balance at the end after all transactions and interest have been applied.

initialBalance, interestRate, withdrawals, deposits, runMonthEndNTimes, finalBalance --> format in the SavingsAccountTest.csv
| same as for the checking examples, seperates multiple transactions of the same type.
, is seperating this (initialBalance, interestRate, withdrawals, deposits, runMonthEndNTimes, finalBalance) values.

This is for basic transactions, it does not account for more complex scenarios. Such as early withdrawal penalties, minimum balance requirements, or compounding interest over long periods.
 is everything good