package uk.co.divyatej.ittoolkit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SubnetCalculatorTest {
    @Test
    fun calculatesTypicalSlash24() {
        val result = calculateSubnet("192.168.1.10", "24")!!
        assertEquals("192.168.1.0", result.network)
        assertEquals("255.255.255.0", result.mask)
        assertEquals("192.168.1.255", result.broadcast)
        assertEquals("192.168.1.1", result.firstHost)
        assertEquals("192.168.1.254", result.lastHost)
        assertEquals(254L, result.hosts)
    }

    @Test
    fun handlesPointToPointSlash31() {
        val result = calculateSubnet("10.0.0.1", "31")!!
        assertEquals("10.0.0.0", result.network)
        assertEquals("10.0.0.1", result.broadcast)
        assertEquals(2L, result.hosts)
    }

    @Test
    fun handlesSingleAddressSlash32() {
        val result = calculateSubnet("203.0.113.9", "32")!!
        assertEquals("203.0.113.9", result.network)
        assertEquals("203.0.113.9", result.broadcast)
        assertEquals(1L, result.hosts)
    }

    @Test
    fun handlesZeroPrefix() {
        val result = calculateSubnet("192.168.1.10", "0")!!
        assertEquals("0.0.0.0", result.network)
        assertEquals("255.255.255.255", result.broadcast)
        assertEquals(4294967294L, result.hosts)
    }

    @Test
    fun rejectsInvalidAddressesAndPrefixes() {
        assertNull(calculateSubnet("256.1.1.1", "24"))
        assertNull(calculateSubnet("192.168.1", "24"))
        assertNull(calculateSubnet("192.168.1.1", "33"))
        assertNull(calculateSubnet("192.168.1.1", "-1"))
        assertNull(calculateSubnet("192.168.001.1", "24"))
    }
}
