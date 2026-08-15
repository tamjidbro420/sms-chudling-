package com.example

import com.example.data.utils.SmsParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsParserTest {

    @Test
    fun testBkashSmsParsing() {
        val header = "bKash"
        val body = "You have received Tk 500.00 from 01712345678. Fee Tk 0.00. Balance Tk 10500.00. TrxID BGA7X92M1K at 11/08/2026 14:30."

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("bKash", parsed?.serviceName)
        assertEquals(500.00, parsed?.amount ?: 0.0, 0.01)
        assertEquals("BGA7X92M1K", parsed?.trxId)
        assertEquals("01712345678", parsed?.senderNumber)
    }

    @Test
    fun testNagadSmsParsing() {
        val header = "NAGAD"
        val body = "You have received Tk 1250.50 from 01811223344. Balance: Tk 5000.00. TrxID: NG98213894."

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("Nagad", parsed?.serviceName)
        assertEquals(1250.50, parsed?.amount ?: 0.0, 0.01)
        assertEquals("NG98213894", parsed?.trxId)
        assertEquals("01811223344", parsed?.senderNumber)
    }

    @Test
    fun testRocketSmsParsing() {
        val header = "Rocket"
        val body = "Cash In Tk 2000.00 from 01599887766. TxnID: RKT7712390."

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("Rocket", parsed?.serviceName)
        assertEquals(2000.00, parsed?.amount ?: 0.0, 0.01)
        assertEquals("RKT7712390", parsed?.trxId)
        assertEquals("01599887766", parsed?.senderNumber)
    }

    @Test
    fun testUserProvidedBkashFormat() {
        val header = "bKash"
        val body = "Cash In Tk 500.00 successful. Sender: 01711223344. Fee Tk 0.00. Balance Tk 1,500.00. TrxID BGA7X92M1K"

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("bKash", parsed?.serviceName)
        assertEquals(500.00, parsed?.amount ?: 0.0, 0.01)
        assertEquals("BGA7X92M1K", parsed?.trxId)
        assertEquals("01711223344", parsed?.senderNumber)
    }

    @Test
    fun testUserProvidedNagadFormat() {
        val header = "NAGAD"
        val body = "Cash In Tk 1,000.00 from 01811223344 successful. Ref: X. Fee: Tk 0.00. Balance: Tk 2,500.00. TrxID NG98213894"

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("Nagad", parsed?.serviceName)
        assertEquals(1000.00, parsed?.amount ?: 0.0, 0.01)
        assertEquals("NG98213894", parsed?.trxId)
        assertEquals("01811223344", parsed?.senderNumber)
    }

    @Test
    fun testUserProvidedRocketFormat() {
        val header = "Rocket"
        val body = "RCV Tk 2,000.00 from 01599887766-1 Balance Tk 3,500.00 TxnId RKT7712390 Date 11-08-26"

        assertTrue(SmsParser.isMatchingPaymentSms(header, body))
        val parsed = SmsParser.parseSms(header, body)

        assertNotNull(parsed)
        assertEquals("Rocket", parsed?.serviceName)
        assertEquals(2000.00, parsed?.amount ?: 0.0, 0.01)
        assertEquals("RKT7712390", parsed?.trxId)
        assertEquals("01599887766-1", parsed?.senderNumber)
    }

    @Test
    fun testNonPaymentSmsIsIgnored() {
        val header = "GP-OFFER"
        val body = "Recharge Tk 50 to get 1GB internet bonus for 3 days."

        val parsed = SmsParser.parseSms(header, body)
        assertNull(parsed)
    }
}
