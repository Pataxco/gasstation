package net.bigpoint.assessment.mymodule;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * The Class MyGasStationTest.
 */
public class MyGasStationTest {

	/** The {@link MyGasStation} object used for the tests. */
	private MyGasStation mgs;

	/**
	 * Sets up the test environment.
	 */
	@Before
	public void setUp() {
		mgs = new MyGasStation();
	}

	/**
	 * Tests addGasPump method. Three {@link GasPump} objects are added. The
	 * number of {@link GasPump} objects is compared to the size of the return
	 * code of the addGasPump method.
	 */
	@Test
	public void testAddGasPumps() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);
		GasPump gp2 = new GasPump(GasType.SUPER, 10.0);
		GasPump gp3 = new GasPump(GasType.REGULAR, 10.0);

		mgs.addGasPump(gp1);
		mgs.addGasPump(gp2);
		mgs.addGasPump(gp3);

		assertEquals(3, mgs.getGasPumps().size());
	}

	/**
	 * Tests the setPrice method. Three prices are set and compared via the
	 * getPrice method.
	 */
	@Test
	public void testSetPrices() {
		mgs.setPrice(GasType.DIESEL, 2.0);
		mgs.setPrice(GasType.REGULAR, 3.0);
		mgs.setPrice(GasType.SUPER, 4.0);

		assertEquals(2.0, mgs.getPrice(GasType.DIESEL), 0.0);
		assertEquals(3.0, mgs.getPrice(GasType.REGULAR), 0.0);
		assertEquals(4.0, mgs.getPrice(GasType.SUPER), 0.0);
	}

	/**
	 * Tests the getGasPump method. Thre {@link GasPump} objects are added and
	 * returned via getGasPump method. After the modification of the returned
	 * collection the internal collection of the {@link MyGasStation} object
	 * should not have been modified at all.
	 */
	@Test
	public void testGetGasPumps() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);
		GasPump gp2 = new GasPump(GasType.SUPER, 10.0);
		GasPump gp3 = new GasPump(GasType.REGULAR, 10.0);

		mgs.addGasPump(gp1);
		mgs.addGasPump(gp2);
		mgs.addGasPump(gp3);

		int intitialCount = mgs.getGasPumps().size();

		Collection<GasPump> gasPumps = mgs.getGasPumps();

		gasPumps.remove(gp1);

		assertEquals(intitialCount, mgs.getGasPumps().size());
	}

	/**
	 * Tests the successful purchase of gas via the buyGas method. The rest
	 * amount of corresponding {@link GasPump} is checked via the
	 * getRemainingAmount method.
	 */
	@Test
	public void testBuyGasSuccess() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 145.0);

		try {
			mgs.buyGas(GasType.DIESEL, 10.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(0.0, gp1.getRemainingAmount(), 0.0);
	}

	/**
	 * Tests the correct sales count. Two sales are performed and the count is
	 * compared with the return code of the getNumberOfSales method.
	 */
	@Test
	public void testBuyGasSuccessSales() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 145.0);

		try {
			mgs.buyGas(GasType.DIESEL, 5.0, 145.0);
			mgs.buyGas(GasType.DIESEL, 5.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(2, mgs.getNumberOfSales(), 0);
	}

	/**
	 * Tests the successful purchase of gas when there are two {@link GasPump} objects and only one of them has sufficient gas available.
	 */
	@Test
	public void testBuyGasTwoPumpsSuccess() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 5.0);
		GasPump gp2 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.addGasPump(gp2);
		mgs.setPrice(GasType.DIESEL, 145.0);

		try {
			mgs.buyGas(GasType.DIESEL, 10.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(0.0, gp2.getRemainingAmount(), 0.0);
	}

	/**
	 * Tests the correct revenue calculation.
	 * On purchase ist performed and the return code of the getRevenue method is compared to the correct value.
	 */
	@Test
	public void testBuyGasRevenue() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);
		double price = 145.0;
		double amount = 10.0;

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, price);

		try {
			mgs.buyGas(GasType.DIESEL, amount, price);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(amount * price, mgs.getRevenue(), 0.0);
	}

	/**
	 * Tests the correct throw of the GasTooExpensiveException.
	 * 
	 * @throws GasTooExpensiveException
	 *             the gas too expensive exception
	 */
	@Test(expected = GasTooExpensiveException.class)
	public void testBuyGasTooExpensive() throws GasTooExpensiveException {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 150.0);

		try {
			mgs.buyGas(GasType.DIESEL, 10.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		}
	}

	/**
	 * Tests the counter of the GasTooExpensiveException.
	 */
	@Test
	public void testBuyGasTooExpensiveCount() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 150.0);

		try {
			mgs.buyGas(GasType.DIESEL, 10.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(1, mgs.getNumberOfCancellationsTooExpensive());
	}

	/**
	 * Tests the correct throw of the NotEnoughGasException.
	 * 
	 * @throws NotEnoughGasException
	 *             the not enough gas exception
	 */
	@Test(expected = NotEnoughGasException.class)
	public void testBuyGasNotEnoughGas() throws NotEnoughGasException {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 145.0);

		try {
			mgs.buyGas(GasType.DIESEL, 120.0, 145.0);
		} catch (GasTooExpensiveException e) {
			// Ignored
		}
	}

	/**
	 * Tests the counter of the NotEnoughGasException.
	 */
	@Test
	public void testBuyGasNotEnoughGasCount() {
		GasPump gp1 = new GasPump(GasType.DIESEL, 10.0);

		mgs.addGasPump(gp1);
		mgs.setPrice(GasType.DIESEL, 145.0);

		try {
			mgs.buyGas(GasType.DIESEL, 150.0, 145.0);
		} catch (NotEnoughGasException e) {
			// Ignored
		} catch (GasTooExpensiveException e) {
			// Ignored
		}

		assertEquals(1, mgs.getNumberOfCancellationsNoGas());
	}
}
