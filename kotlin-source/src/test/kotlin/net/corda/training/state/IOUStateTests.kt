
package net.corda.training.state

import net.corda.core.contracts.*
import net.corda.core.identity.Party
import net.corda.finance.*
import net.corda.training.ALICE
import net.corda.training.BOB
import net.corda.training.MEGACORP
import net.corda.training.MINICORP
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class IOUStateTests {

    @Test
    fun hasIOUAmountFieldOfCorrectType() {
        IOUState::class.java.getDeclaredField("amount")
        assertEquals(
            IOUState::class.java.getDeclaredField("amount").type,
            Amount::class.java
        )
    }

    @Test
    fun hasLenderFieldOfCorrectType() {
        IOUState::class.java.getDeclaredField("lender")
        assertEquals(
            IOUState::class.java.getDeclaredField("lender").type,
            Party::class.java
        )
    }

    @Test
    fun hasBorrowerFieldOfCorrectType() {
        IOUState::class.java.getDeclaredField("borrower")
        assertEquals(
            IOUState::class.java.getDeclaredField("borrower").type,
            Party::class.java
        )
    }

    @Test
    fun hasPaidFieldOfCorrectType() {
        IOUState::class.java.getDeclaredField("paid")
        assertEquals(
            IOUState::class.java.getDeclaredField("paid").type,
            Amount::class.java
        )
    }

    @Test
    fun lenderIsParticipant() {
        val iouState = IOUState(1.POUNDS, ALICE.party, BOB.party)
        assertNotEquals(-1, iouState.participants.indexOf(ALICE.party))
    }

    @Test
    fun borrowerIsParticipant() {
        val iouState = IOUState(1.POUNDS, ALICE.party, BOB.party)
        assertNotEquals(-1, iouState.participants.indexOf(BOB.party))
    }

    @Test
    fun isLinearState() {
        assert(
            LinearState::class.java.isAssignableFrom(IOUState::class.java)
        )
    }

    @Test
    fun hasLinearIdFieldOfCorrectType() {
        IOUState::class.java.getDeclaredField("linearId")
        assertEquals(
            IOUState::class.java.getDeclaredField("linearId").type,
            UniqueIdentifier::class.java
        )

        val iouState = IOUState(1.POUNDS, ALICE.party, BOB.party)
        assertNotNull(iouState.linearId)
    }

    @Test
    fun checkIOUStateParameterOrdering() {
        val fields = IOUState::class.java.declaredFields
        val amountIdx =
            fields.indexOf(IOUState::class.java.getDeclaredField("amount"))
        val lenderIdx =
            fields.indexOf(IOUState::class.java.getDeclaredField("lender"))
        val borrowerIdx =
            fields.indexOf(IOUState::class.java.getDeclaredField("borrower"))
        val paidIdx =
            fields.indexOf(IOUState::class.java.getDeclaredField("paid"))
        val linearIdIdx =
            fields.indexOf(IOUState::class.java.getDeclaredField("linearId"))

        assert(amountIdx < lenderIdx)
        assert(lenderIdx < borrowerIdx)
        assert(borrowerIdx < paidIdx)
        assert(paidIdx < linearIdIdx)
    }

    @Test
    fun checkPayHelperMethod() {
        val iou = IOUState(10.DOLLARS, ALICE.party, BOB.party)

        assertEquals(5.DOLLARS, iou.pay(5.DOLLARS).paid)
        assertEquals(3.DOLLARS, iou.pay(1.DOLLARS).pay(2.DOLLARS).paid)
        assertEquals(
            10.5.DOLLARS,
            iou.pay(5.DOLLARS).pay(3.DOLLARS).pay(2.5.DOLLARS).paid
        )
    }

    @Test
    fun checkWithNewLenderHelperMethod() {
        val iou = IOUState(10.DOLLARS, ALICE.party, BOB.party)

        assertEquals(MINICORP.party, iou.withNewLender(MINICORP.party).lender)
        assertEquals(
            MEGACORP.party,
            iou.withNewLender(MINICORP.party)
                .withNewLender(MEGACORP.party).lender
        )
    }
}
