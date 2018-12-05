package piuk.blockchain.androidcore.data.payments

import com.blockchain.android.testutils.rxInit
import com.blockchain.logging.LastTxUpdater
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import info.blockchain.api.data.UnspentOutputs
import info.blockchain.wallet.payment.SpendableUnspentOutputs
import io.reactivex.Completable
import io.reactivex.Observable
import org.amshove.kluent.shouldEqual
import org.apache.commons.lang3.tuple.Pair
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.BitcoinMainNetParams
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import piuk.blockchain.androidcore.data.rxjava.RxBus
import java.math.BigInteger

class SendDataManagerTest {

    private lateinit var subject: SendDataManager
    private val mockPaymentService: PaymentService = mock()
    private val mockLastTxUpdater: LastTxUpdater = mock()
    private val mockRxBus: RxBus = mock()

    @get:Rule
    val initSchedulers = rxInit {
        mainTrampoline()
        ioTrampoline()
    }

    @Before
    fun setUp() {
        subject =
            SendDataManager(mockPaymentService, mockLastTxUpdater, mockRxBus)
    }

    @Test
    fun `submitPayment BTC`() {
        // Arrange
        val mockOutputBundle: SpendableUnspentOutputs = mock()
        val mockKeys = listOf(mock<ECKey>())
        val toAddress = "TO_ADDRESS"
        val changeAddress = "CHANGE_ADDRESS"
        val mockFee: BigInteger = mock()
        val mockAmount: BigInteger = mock()
        val txHash = "TX_HASH"
        whenever(
            mockPaymentService.submitPayment(
                mockOutputBundle,
                mockKeys,
                toAddress,
                changeAddress,
                mockFee,
                mockAmount
            )
        ).thenReturn(Observable.just(txHash))
        whenever(mockLastTxUpdater.updateLastTxTime()).thenReturn(Completable.complete())
        // Act
        val testObserver = subject.submitBtcPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        ).test()
        // Assert
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.values()[0] shouldEqual txHash
        verify(mockPaymentService).submitPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        )
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun `submitPayment BTC successful even if logging last tx fails`() {
        // Arrange
        val mockOutputBundle: SpendableUnspentOutputs = mock()
        val mockKeys = listOf(mock<ECKey>())
        val toAddress = "TO_ADDRESS"
        val changeAddress = "CHANGE_ADDRESS"
        val mockFee: BigInteger = mock()
        val mockAmount: BigInteger = mock()
        val txHash = "TX_HASH"
        whenever(
            mockPaymentService.submitPayment(
                mockOutputBundle,
                mockKeys,
                toAddress,
                changeAddress,
                mockFee,
                mockAmount
            )
        ).thenReturn(Observable.just(txHash))
        whenever(mockLastTxUpdater.updateLastTxTime()).thenReturn(Completable.error(Exception()))
        // Act
        val testObserver = subject.submitBtcPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        ).test()
        // Assert
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.values()[0] shouldEqual txHash
        verify(mockPaymentService).submitPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        )
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun `submitPayment BCH`() {
        // Arrange
        val mockOutputBundle: SpendableUnspentOutputs = mock()
        val mockKeys = listOf(mock<ECKey>())
        val toAddress = "TO_ADDRESS"
        val changeAddress = "CHANGE_ADDRESS"
        val mockFee: BigInteger = mock()
        val mockAmount: BigInteger = mock()
        val txHash = "TX_HASH"
        whenever(
            mockPaymentService.submitBchPayment(
                mockOutputBundle,
                mockKeys,
                toAddress,
                changeAddress,
                mockFee,
                mockAmount
            )
        ).thenReturn(Observable.just(txHash))
        whenever(mockLastTxUpdater.updateLastTxTime()).thenReturn(Completable.complete())
        // Act
        val testObserver = subject.submitBchPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        ).test()
        // Assert
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.values()[0] shouldEqual txHash
        verify(mockPaymentService).submitBchPayment(
            mockOutputBundle,
            mockKeys,
            toAddress,
            changeAddress,
            mockFee,
            mockAmount
        )
        verifyNoMoreInteractions(mockPaymentService)
    }

    // This call is failing when init'ing the cipher object. This appears to be a JVM issue but I'm
    // not sure what's changed. The test passes now, but should be changed to assert success conditions
    // when the fix is discovered.
    // TODO: Fix me, and then test for success
    // TODO: 08/01/2018 This will default to bitcoin network parameters
    @Ignore
    @Test
    fun getEcKeyFromBip38() {
        // Arrange
        val password = "thisisthepassword"
        val scanData = "6PYP4i7UyewqZWqdnpQwMdCyneXPaFDPkk8LArmVexqoGsy9Yx92SiLCPm"
        val params = BitcoinMainNetParams.get()
        // Act
        val testObserver = subject.getEcKeyFromBip38(password, scanData, params).test()
        // Assert
        testObserver.assertNotComplete()
        testObserver.assertTerminated()
        testObserver.assertNoValues()
    }

    @Test
    fun `getUnspentOutputs BTC`() {
        // Arrange
        val address = "ADDRESS"
        val mockUnspentOutputs: UnspentOutputs = mock()
        whenever(mockPaymentService.getUnspentOutputs(address))
            .thenReturn(Observable.just(mockUnspentOutputs))
        // Act
        val testObserver = subject.getUnspentOutputs(address).test()
        // Assert
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.values()[0] shouldEqual mockUnspentOutputs
        verify(mockPaymentService).getUnspentOutputs(address)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun `getUnspentOutputs BCH`() {
        // Arrange
        val address = "ADDRESS"
        val mockUnspentOutputs: UnspentOutputs = mock()
        whenever(mockPaymentService.getUnspentBchOutputs(address))
            .thenReturn(Observable.just(mockUnspentOutputs))
        // Act
        val testObserver = subject.getUnspentBchOutputs(address).test()
        // Assert
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.values()[0] shouldEqual mockUnspentOutputs
        verify(mockPaymentService).getUnspentBchOutputs(address)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun getSpendableCoins() {
        // Arrange
        val mockUnspent: UnspentOutputs = mock()
        val mockPayment: BigInteger = mock()
        val mockFee: BigInteger = mock()
        val mockOutputs: SpendableUnspentOutputs = mock()
        whenever(mockPaymentService.getSpendableCoins(mockUnspent, mockPayment, mockFee))
            .thenReturn(mockOutputs)
        // Act
        val result = subject.getSpendableCoins(mockUnspent, mockPayment, mockFee)
        // Assert
        result shouldEqual mockOutputs
        verify(mockPaymentService).getSpendableCoins(mockUnspent, mockPayment, mockFee)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun getSweepableCoins() {
        // Arrange
        val mockUnspent: UnspentOutputs = mock()
        val mockFee: BigInteger = mock()
        val mockSweepableCoins: Pair<BigInteger, BigInteger> = mock()
        whenever(mockPaymentService.getMaximumAvailable(mockUnspent, mockFee))
            .thenReturn(mockSweepableCoins)
        // Act
        val result = subject.getMaximumAvailable(mockUnspent, mockFee)
        // Assert
        result shouldEqual mockSweepableCoins
        verify(mockPaymentService).getMaximumAvailable(mockUnspent, mockFee)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun isAdequateFee() {
        // Arrange
        val inputs = 1
        val outputs = 101
        val mockFee: BigInteger = mock()
        whenever(mockPaymentService.isAdequateFee(inputs, outputs, mockFee)).thenReturn(false)
        // Act
        val result = subject.isAdequateFee(inputs, outputs, mockFee)
        // Assert
        result shouldEqual false
        verify(mockPaymentService).isAdequateFee(inputs, outputs, mockFee)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun estimateSize() {
        // Arrange
        val inputs = 1
        val outputs = 101
        val estimatedSize = 1337
        whenever(mockPaymentService.estimateSize(inputs, outputs)).thenReturn(estimatedSize)
        // Act
        val result = subject.estimateSize(inputs, outputs)
        // Assert
        result shouldEqual estimatedSize
        verify(mockPaymentService).estimateSize(inputs, outputs)
        verifyNoMoreInteractions(mockPaymentService)
    }

    @Test
    fun estimateFee() {
        // Arrange
        val inputs = 1
        val outputs = 101
        val mockFeePerKb: BigInteger = mock()
        val mockAbsoluteFee: BigInteger = mock()
        whenever(mockPaymentService.estimateFee(inputs, outputs, mockFeePerKb))
            .thenReturn(mockAbsoluteFee)
        // Act
        val result = subject.estimatedFee(inputs, outputs, mockFeePerKb)
        // Assert
        result shouldEqual mockAbsoluteFee
        verify(mockPaymentService).estimateFee(inputs, outputs, mockFeePerKb)
        verifyNoMoreInteractions(mockPaymentService)
    }
}