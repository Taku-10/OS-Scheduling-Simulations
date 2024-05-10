package barScheduling;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Barman extends Thread 
{

	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;
	private Map<DrinkOrder, Integer> remainingTimes = new HashMap<>();
    private int quantum = 25;  // Quantum time for Round Robin scheduling
    private int schedAlg;  // Scheduling algorithm identifier 0=FCFS, 1=SJF, 2=RR

	Barman(  CountDownLatch startSignal,int schedAlg)
	 {
		if (schedAlg==0) // FCFS
		{
			this.orderQueue = new LinkedBlockingQueue<>();
		} 
		else if (schedAlg == 1) // SJF
		{
			this.orderQueue = new PriorityBlockingQueue<>(11, new Comparator<DrinkOrder>() 
			{
                @Override
                public int compare(DrinkOrder o1, DrinkOrder o2)
				{
                    return Integer.compare(o1.getExecutionTime(), o2.getExecutionTime());
                }
            });
		} 
		else if (schedAlg == 2) // RR
		{
			this.orderQueue = new LinkedBlockingQueue<>();
		} 
		else 
		{
			throw new IllegalArgumentException("Unsupported scheduling algorithm");
		}
			
	    this.startSignal=startSignal;
	}
	
/**
 * The placeDrinkOrder method is responsible for adding a new drink order to the barman's order queue.
 * It takes a DrinkOrder object as a parameter and adds it to the queue.
 * If the scheduling algorithm is set to Round Robin (RR), the method also initializes the remaining time for the order.
 * @param order The drink order to be added to the queue.
 */
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException
	{
        orderQueue.put(order);
		// Initialize remaining time for RR for tracking
		if (schedAlg == 2) 
		{ 
            remainingTimes.put(order, order.getExecutionTime());  
        }
    }
	
/**
 * The run method of the Barman thread.
 * This method is responsible for processing drink orders based on the scheduling algorithm.
 * It waits for the start signal, then continuously takes orders from the order queue,
 * processes them according to the scheduling algorithm, and notifies the patrons when their orders are ready.
 * If an InterruptedException occurs, it prints a message indicating that the barman is packing up.
 */
	public void run() 
	{
		try 
		{
			DrinkOrder nextOrder;
			// Signal that the barman is ready
			startSignal.countDown(); 
			// Wait until the start signal is received
			startSignal.await(); 

			while(true) 
			{
				// Take the next order from the queue
				nextOrder=orderQueue.take();
				// Process the order based on the scheduling algorithm
				if (schedAlg == 2)
				{
					processRoundRobin(nextOrder);
				} 
				else 
				{
					processNonPreemptive(nextOrder);
				}
			}
				
		} catch (InterruptedException e1) 
		{
			System.out.println("---Barman is packing up ");
		}
	}
/**
 * The processRoundRobin method is responsible for processing a drink order based on the Round Robin (RR) scheduling algorithm.
 * It takes a DrinkOrder object as a parameter and processes it according to the RR algorithm.
 * The method first retrieves the remaining time for the order from the remainingTimes map.
 * If the remaining time is greater than the quantum time, the method simulates working on the order for the quantum time,
 * updates the remaining time, and re-inserts the order into the order queue with the updated remaining time.
 * If the remaining time is less than or equal to the quantum time, the method simulates working on the order for the remaining time,
 * marks the order as done, and removes it from the remainingTimes map.
 * @param nextOrder The drink order to be processed based on the RR algorithm.
 */
	private void processRoundRobin(DrinkOrder nextOrder) throws InterruptedException 
	{
		int remainingTime = remainingTimes.get(nextOrder);

        if (remainingTime > quantum) 
		{
            System.out.println("---Barman preparing part of order for patron " + nextOrder.toString());
            sleep(quantum);  // Simulate work for 'quantum' milliseconds
            remainingTime -= quantum;
            remainingTimes.put(nextOrder, remainingTime);
            orderQueue.put(nextOrder); // Reinsert with updated remaining time
        } 
		else 
		{
            System.out.println("---Barman preparing complete order for patron " + nextOrder.toString());
            sleep(remainingTime); // Processing order
            System.out.println("---Barman has made order for patron " + nextOrder.toString());
            nextOrder.orderDone();
            remainingTimes.remove(nextOrder);
        }
	}

/**
 * The processNonPreemptive method is responsible for processing a drink order based on the Non-Preemptive (Non-Preemptive) scheduling algorithm.
 * It takes a DrinkOrder object as a parameter and processes it according to the Non-Preemptive algorithm.
 * It then simulates working on the order for the order's execution time, and updates the order's status to "done", 
 * @param nextOrder The drink order to be processed based on the Non-Preemptive algorithm.
 */
	private void processNonPreemptive(DrinkOrder nextOrder) throws InterruptedException 
	{
		System.out.println("---Barman preparing order for patron " + nextOrder.toString());
        sleep(nextOrder.getExecutionTime()); // Processing order
        System.out.println("---Barman has made order for patron " + nextOrder.toString());
        nextOrder.orderDone();
	}
}


