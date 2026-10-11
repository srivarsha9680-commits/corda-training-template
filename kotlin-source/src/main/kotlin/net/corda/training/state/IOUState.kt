
package net.corda.training.state

import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.finance.POUNDS
import java.util.Currency

data class IOUState(
    val amount: Amount<Currency>,
    val lender: Party,
    val borrower: Party,
    val paid: Amount<Currency> = Amount(
        0L,
        amount.token
    ),
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState {

    override val participants: List<AbstractParty>
        get() = listOf(lender, borrower)

    fun pay(amountToPay: Amount<Currency>): IOUState {
        require(amountToPay.token == amount.token) {
            "Payment must use the same currency as the IOU."
        }
        require(amountToPay.quantity > 0L) {
            "Payment must be positive."
        }
        require(paid.quantity + amountToPay.quantity <= amount.quantity) {
            "Payment cannot exceed the outstanding IOU amount."
        }

        return copy(paid = paid + amountToPay)
    }

    fun withNewLender(newLender: Party): IOUState {
        return copy(lender = newLender)
    }
}
