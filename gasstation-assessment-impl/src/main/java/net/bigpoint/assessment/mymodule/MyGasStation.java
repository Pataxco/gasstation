package net.bigpoint.assessment.mymodule;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * The Class MyGasStation. Thread safe implementation of the {@link GasStation}
 * interface.
 */
public class MyGasStation implements GasStation {

	/** The {@link GasPump} collection. */
	private Collection<GasPump> pumps;

	/**
	 * The collection of the gas prices. A ConcurrentHashMap is used in order to
	 * obtain thread safety and in order to have for every {@link GasType} one
	 * price only.
	 */
	private ConcurrentHashMap<GasType, Double> prices;

	/** The total revenue of this {@link GasStation}. */
	private double revenue;

	/** The total count of successful sales. */
	private int sales;

	/** The total count of cancelations because not enough gas was available. */
	private int cancelationsNoGas;

	/** The total count of cancelations because gas was too expensive. */
	private int cancelationsTooExpensive;

	/**
	 * Instantiates a new {@link GasStation} object.
	 */
	public MyGasStation() {
		super();
		// For the {@link GasPump} collection a HashSet is used in order to
		// prevent duplicate objects.
		this.pumps = new HashSet<GasPump>();
		this.prices = new ConcurrentHashMap<GasType, Double>();

		// Initialization of prices with all available {@link GasType} values.
		for (GasType gt : GasType.values()) {
			this.prices.put(gt, 20.0);
		}

		// Initializations of counters and revenue.
		this.revenue = 0.0;
		this.sales = 0;
		this.cancelationsNoGas = 0;
		this.cancelationsTooExpensive = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.bigpoint.assessment.gasstation.GasStation#addGasPump(net.bigpoint
	 * .assessment.gasstation.GasPump)
	 */
	@Override
	public void addGasPump(GasPump pump) {
		// Add a {@link GasPump} to the internal collection.
		this.pumps.add(pump);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.bigpoint.assessment.gasstation.GasStation#buyGas(net.bigpoint.assessment
	 * .gasstation.GasType, double, double)
	 */
	@Override
	public double buyGas(GasType type, double amountInLiters,
			double maxPricePerLiter) throws NotEnoughGasException,
			GasTooExpensiveException {
		// return code is a price
		double price;

		// First check the price conditions ... cheapest operation
		if (this.getPrice(type) > maxPricePerLiter) {
			// Increment relevant counter
			this.cancelationsTooExpensive++;
			// Throw the {@link GasTooExpensiveException}
			throw new GasTooExpensiveException();
			// If the price is ok, we have the options to sell or to throw a
			// {@link NotEnoughGasException}. For both options we have to
			// iterate through the internal {@link GasPump} collection.
		} else {
			// Provide thread safety for our collection object. Although it is
			// not the strategy with the best performance it is sufficient for
			// the
			// goal of thread safety.
			synchronized (this.pumps) {
				// Variable to store an appropriate {@link GasPump} object
				GasPump chosenPump = null;
				// Loop through all of our {@link GasPump} objects
				for (GasPump gp : this.pumps) {
					// When we have found a pump with corresponding type and gas
					// amount we store it in the local variable and step out of
					// the loop.
					if (gp.getGasType().equals(type)
							&& gp.getRemainingAmount() >= amountInLiters) {
						chosenPump = gp;
						break;
					}
				}

				// If we haven't found an appropriate {@link GasPump} we have to
				// increment the specific counter and throw the corresponding
				// exception.
				if (chosenPump == null) {
					// Increment the cancelationsNoGas counter
					this.cancelationsNoGas++;
					// Throw the {@link NotEnoughGasException}
					throw new NotEnoughGasException();
					// Everything is fine. Now we can pump the gas from the
					// previously chosen {@link GasPump}
				} else {
					// Let it flow
					chosenPump.pumpGas(amountInLiters);
					// Calculate the price since it is the return code
					price = amountInLiters * this.getPrice(type);
					// Add the cash to the total revenue
					this.revenue += price;
					// Increment the total sales counter
					this.sales++;
				}
			}
		}

		return price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getGasPumps()
	 */
	@Override
	public Collection<GasPump> getGasPumps() {
		// In order to prevent outer modifications of the internal collection we
		// return a copy. Although it is not a full clone ({@link GasPump} does
		// not provide a clone method) it is sufficient for the task.
		Collection<GasPump> rc = new HashSet<GasPump>();
		for (GasPump gp : this.pumps) {
			rc.add(gp);
		}

		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.bigpoint.assessment.gasstation.GasStation#getNumberOfCancellationsNoGas
	 * ()
	 */
	@Override
	public int getNumberOfCancellationsNoGas() {
		// Return the value of the cancelationNoGas counter
		return this.cancelationsNoGas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#
	 * getNumberOfCancellationsTooExpensive()
	 */
	@Override
	public int getNumberOfCancellationsTooExpensive() {
		// Return the value of the cancelationTooExpensive counter
		return this.cancelationsTooExpensive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getNumberOfSales()
	 */
	@Override
	public int getNumberOfSales() {
		// Return the value of the sales counter
		return this.sales;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getPrice(net.bigpoint.
	 * assessment.gasstation.GasType)
	 */
	@Override
	public double getPrice(GasType type) {
		// Return the price of the provided GasType
		return this.prices.get(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#getRevenue()
	 */
	@Override
	public double getRevenue() {
		// Return the total revenue
		return this.revenue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.bigpoint.assessment.gasstation.GasStation#setPrice(net.bigpoint.
	 * assessment.gasstation.GasType, double)
	 */
	@Override
	public void setPrice(GasType type, double price) {
		// Set the price for the provided GasType
		this.prices.put(type, price);
	}

}
