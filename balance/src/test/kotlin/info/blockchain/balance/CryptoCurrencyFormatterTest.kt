package info.blockchain.balance

import org.amshove.kluent.`should equal`
import org.junit.Before
import org.junit.Test
import java.util.Locale

class CryptoCurrencyFormatterTest {

    val locale = Locale.US
    @Before
    fun setLocale() {
        Locale.setDefault(locale)
    }

    @Test
    fun `format BTC from Crypto Value`() {
        CryptoValue.ZeroBtc.format(locale) `should equal` "0"
        CryptoValue.bitcoinFromMajor(1).format(locale) `should equal` "1.0"
        CryptoValue.bitcoinFromMajor(10_000).format(locale) `should equal` "10,000.0"
        CryptoValue.bitcoinFromMajor(21_000_000).format(locale) `should equal` "21,000,000.0"
    }

    @Test
    fun `format BCH from Crypto Value`() {
        CryptoValue.ZeroBch.format(locale) `should equal` "0"
        CryptoValue.bitcoinCashFromMajor(1).format(locale) `should equal` "1.0"
        CryptoValue.bitcoinCashFromMajor(10_000).format(locale) `should equal` "10,000.0"
        CryptoValue.bitcoinCashFromMajor(21_000_000).format(locale) `should equal` "21,000,000.0"
    }

    @Test
    fun `format Ether from Crypto Value`() {
        CryptoValue.ZeroEth.format(locale) `should equal` "0"
        1.ether().format(locale) `should equal` "1.0"
        10_000.ether().format(locale) `should equal` "10,000.0"
        100_000_000.ether().format(locale) `should equal` "100,000,000.0"
    }

    @Test
    fun `formatWithUnit 0 BTC`() {
        CryptoValue.ZeroBtc.formatWithUnit(
            locale = locale,
            precision = FormatPrecision.Short
        ) `should equal` "0 BTC"
    }

    @Test
    fun `formatWithUnit BTC`() {
        CryptoValue.bitcoinFromMajor(1).formatWithUnit(locale) `should equal` "1.0 BTC"
        CryptoValue.bitcoinFromMajor(10_000).formatWithUnit(locale) `should equal` "10,000.0 BTC"
        CryptoValue.bitcoinFromMajor(21_000_000).formatWithUnit(locale) `should equal` "21,000,000.0 BTC"
    }

    @Test
    fun `formatWithUnit BTC fractions`() {
        CryptoValue.bitcoinFromSatoshis(1L).formatWithUnit(locale) `should equal` "0.00000001 BTC"
        CryptoValue.bitcoinFromSatoshis(10L).formatWithUnit(locale) `should equal` "0.0000001 BTC"
        CryptoValue.bitcoinFromSatoshis(100L).formatWithUnit(locale) `should equal` "0.000001 BTC"
        CryptoValue.bitcoinFromSatoshis(1000L).formatWithUnit(locale) `should equal` "0.00001 BTC"
        CryptoValue.bitcoinFromSatoshis(10000L).formatWithUnit(locale) `should equal` "0.0001 BTC"
        CryptoValue.bitcoinFromSatoshis(100000L).formatWithUnit(locale) `should equal` "0.001 BTC"
        CryptoValue.bitcoinFromSatoshis(1000000L).formatWithUnit(locale) `should equal` "0.01 BTC"
        CryptoValue.bitcoinFromSatoshis(10000000L).formatWithUnit(locale) `should equal` "0.1 BTC"
        CryptoValue.bitcoinFromSatoshis(120000000L).formatWithUnit(locale) `should equal` "1.2 BTC"
    }

    @Test
    fun `formatWithUnit 0 BCH`() {
        CryptoValue.ZeroBch.formatWithUnit(locale) `should equal` "0 BCH"
    }

    @Test
    fun `formatWithUnit BCH`() {
        CryptoValue.bitcoinCashFromMajor(1).formatWithUnit(locale) `should equal` "1.0 BCH"
        CryptoValue.bitcoinCashFromMajor(10_000).formatWithUnit(locale) `should equal` "10,000.0 BCH"
        CryptoValue.bitcoinCashFromMajor(21_000_000).formatWithUnit(locale) `should equal` "21,000,000.0 BCH"
    }

    @Test
    fun `formatWithUnit BCH fractions`() {
        CryptoValue.bitcoinCashFromSatoshis(1L).formatWithUnit(locale) `should equal` "0.00000001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(10L).formatWithUnit(locale) `should equal` "0.0000001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(100L).formatWithUnit(locale) `should equal` "0.000001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(1000L).formatWithUnit(locale) `should equal` "0.00001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(10000L).formatWithUnit(locale) `should equal` "0.0001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(100000L).formatWithUnit(locale) `should equal` "0.001 BCH"
        CryptoValue.bitcoinCashFromSatoshis(1000000L).formatWithUnit(locale) `should equal` "0.01 BCH"
        CryptoValue.bitcoinCashFromSatoshis(10000000L).formatWithUnit(locale) `should equal` "0.1 BCH"
        CryptoValue.bitcoinCashFromSatoshis(120000000L).formatWithUnit(locale) `should equal` "1.2 BCH"
    }

    @Test
    fun `formatWithUnit 0 ETH`() {
        CryptoValue.ZeroEth.formatWithUnit(locale) `should equal` "0 ETH"
    }

    @Test
    fun `formatWithUnit ETH`() {
        1.ether().formatWithUnit(locale) `should equal` "1.0 ETH"
        10_000.ether().formatWithUnit(locale) `should equal` "10,000.0 ETH"
        1_000_000_000.ether().formatWithUnit(locale) `should equal` "1,000,000,000.0 ETH"
    }

    @Test
    fun `formatWithUnit STX`() {
        CryptoValue.fromMinor(CryptoCurrency.STX,
            1234567123.toBigDecimal()).formatWithUnit(locale) `should equal` "123.4567123 STX"
    }

    @Test
    fun `formatWithUnit 0 STX`() {
        CryptoValue.ZeroStx.formatWithUnit(locale) `should equal` "0 STX"
    }

    @Test
    fun `formatWithUnit ETH fractions too small to display`() {
        1L.formatWeiWithUnit() `should equal` "0 ETH"
        10L.formatWeiWithUnit() `should equal` "0 ETH"
        100L.formatWeiWithUnit() `should equal` "0 ETH"
        1_000L.formatWeiWithUnit() `should equal` "0 ETH"
        10_000L.formatWeiWithUnit() `should equal` "0 ETH"
        100_000L.formatWeiWithUnit() `should equal` "0 ETH"
        1_000_000L.formatWeiWithUnit() `should equal` "0 ETH"
        10_000_000L.formatWeiWithUnit() `should equal` "0 ETH"
        100_000_000L.formatWeiWithUnit() `should equal` "0 ETH"
        1_000_000_000L.formatWeiWithUnit() `should equal` "0 ETH"
    }

    @Test
    fun `formatWithUnit ETH with tiny fractions - full precision`() {
        val formatWithUnit =
            { wei: Long ->
                CryptoValue(
                    CryptoCurrency.ETHER, wei.toBigInteger()
                ).formatWithUnit(locale, precision = FormatPrecision.Full)
            }
        formatWithUnit(1L) `should equal` "0.000000000000000001 ETH"
        formatWithUnit(10L) `should equal` "0.00000000000000001 ETH"
        formatWithUnit(100L) `should equal` "0.0000000000000001 ETH"
        formatWithUnit(1_000L) `should equal` "0.000000000000001 ETH"
        formatWithUnit(10_000L) `should equal` "0.00000000000001 ETH"
        formatWithUnit(100_000L) `should equal` "0.0000000000001 ETH"
        formatWithUnit(1_000_000L) `should equal` "0.000000000001 ETH"
        formatWithUnit(10_000_000L) `should equal` "0.00000000001 ETH"
        formatWithUnit(100_000_000L) `should equal` "0.0000000001 ETH"
        formatWithUnit(1_000_000_000L) `should equal` "0.000000001 ETH"
        formatWithUnit(10_000_000_000L) `should equal` "0.00000001 ETH"
        formatWithUnit(100_000_000_000L) `should equal` "0.0000001 ETH"
    }

    @Test
    fun `formatWithUnit ETH fractions`() {
        10_000_000_000L.formatWeiWithUnit() `should equal` "0.00000001 ETH"
        100_000_000_000L.formatWeiWithUnit() `should equal` "0.0000001 ETH"
        1_000_000_000_000L.formatWeiWithUnit() `should equal` "0.000001 ETH"
        10_000_000_000_000L.formatWeiWithUnit() `should equal` "0.00001 ETH"
        100_000_000_000_000L.formatWeiWithUnit() `should equal` "0.0001 ETH"
        1_000_000_000_000_000L.formatWeiWithUnit() `should equal` "0.001 ETH"
        10_000_000_000_000_000L.formatWeiWithUnit() `should equal` "0.01 ETH"
        100_000_000_000_000_000L.formatWeiWithUnit() `should equal` "0.1 ETH"
        1_200_000_000_000_000_000.formatWeiWithUnit() `should equal` "1.2 ETH"
    }

    @Test
    fun `formatWithUnit ETH rounding`() {
        12_000_000_000L.formatWeiWithUnit() `should equal` "0.00000001 ETH"
        15_000_000_000L.formatWeiWithUnit() `should equal` "0.00000001 ETH"
        17_000_000_000L.formatWeiWithUnit() `should equal` "0.00000001 ETH"
        120_000_000_000L.formatWeiWithUnit() `should equal` "0.00000012 ETH"
        150_000_000_000L.formatWeiWithUnit() `should equal` "0.00000015 ETH"
        170_000_000_000L.formatWeiWithUnit() `should equal` "0.00000017 ETH"
    }

    @Test
    fun `format in another locale`() {
        CryptoValue.ZeroEth.format(Locale.FRANCE) `should equal` "0"
        1.ether().format(Locale.FRANCE) `should equal` "1,0"
        10_000.ether().format(Locale.FRANCE) `should equal` "10\u00a0000,0"
        100_000_000.ether().format(Locale.FRANCE) `should equal` "100\u00a0000\u00a0000,0"
    }

    @Test
    fun `format in another locale, forced to another`() {
        CryptoValue.ZeroEth.format(locale = Locale.US) `should equal` "0"
        1.ether().format(locale = Locale.US) `should equal` "1.0"
        10_000.ether().format(locale = Locale.US) `should equal` "10,000.0"
        100_000_000.ether().format(locale = Locale.US) `should equal` "100,000,000.0"
    }

    private fun Long.formatWeiWithUnit() =
        CryptoValue(CryptoCurrency.ETHER, this.toBigInteger()).formatWithUnit(locale)
}
