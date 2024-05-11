package barScheduling;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/*
 Barman Thread class.
 */

public class Barman extends Thread 
{

    private CountDownLatch startSignal;
    private BlockingQueue<DrinkOrder> orderQueue;

	/**
     * Constructor for the Barman class.
     * Initializes the Barman with a start signal and a scheduling algorithm.
     * @param startSignal The CountDownLatch that signals the Barman to start working.
     * @param schedAlg The scheduling algorithm to use for processing drink orders.
     *                   Accepted values are 0 for FCFS and 1 for SJF.
     */
    Barman(CountDownLatch startSignal, int schedAlg)
	 {
        this.startSignal = startSignal;
        if (schedAlg == 0) // FCFS
		 {
            this.orderQueue = new LinkedBlockingQueue<>();

        } 
		else if (schedAlg == 1) // SJF
		{
            this.orderQueue = new PriorityBlockingQueue<>(10, new Comparator<DrinkOrder>()
			 {
                @Override
                public int compare(DrinkOrder o1, DrinkOrder o2) 
				{
                    return Integer.compare(o1.getExecutionTime(), o2.getExecutionTime());
                }
            });
        } 
		else 
		{
			throw new IllegalArgumentException("Unsupported scheduling algorithm");
		}
    }

/**
 * This method places a drink order in the order queue.
 * It is a blocking operation, meaning it will wait until there is space available in the queue.
 * @param order The DrinkOrder object representing the drink order to be placed.
 * @throws InterruptedException If the current thread is interrupted while waiting for space in the queue.
 */
    public void placeDrinkOrder(DrinkOrder order) throws InterruptedException
	{
        orderQueue.put(order);
    }

/**
 * This method is the main execution thread for a barman.
 * It continuously retrieves drink orders from the order queue,
 * processes them, and marks them as done once completed.
 * The barman will wait until it receives a signal to start working.
 * @throws InterruptedException If the current thread is interrupted while waiting for space in the queue.
 */
    public void run() 
	{
        try 
		{
            DrinkOrder nextOrder;
            startSignal.countDown(); // Barman ready
            startSignal.await(); // Check latch - don't start until told to do so

            while (true) 
			{
                nextOrder = orderQueue.take();
                System.out.println("---Barman preparing order for patron " + nextOrder.toString());
                sleep(nextOrder.getExecutionTime()); // Processing order
                System.out.println("---Barman has made order for patron " + nextOrder.toString());
                nextOrder.orderDone();
            }

        } catch (InterruptedException e1) 
		{
            System.out.println("---Barman is packing up ");
        }
    }
}
