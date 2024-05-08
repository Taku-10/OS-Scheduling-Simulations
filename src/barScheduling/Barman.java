package barScheduling;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CountDownLatch;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/*
 Barman Thread class.
 */

public class Barman extends Thread 
{
	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;
	private List<DrinkOrder> roundRobinList;
	private int schedAlg;

	Barman(  CountDownLatch startSignal,int schedAlg) 
	{
		this.schedAlg = schedAlg;
		// First Come First Serve
		if (schedAlg == 0) 
		{
			this.orderQueue = new LinkedBlockingQueue<>();
		}
		// Shortest Job First
		else if (schedAlg == 1)
		{
			this.orderQueue = new PriorityBlockingQueue<>(11, Comparator.comparingInt(DrinkOrder::getExecutionTime));
		} 
		
	    this.startSignal = startSignal;
	}
	
	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException 
	{
		orderQueue.put(order);
    }
	
	public void run()
	 {
		try
		 {

			startSignal.countDown(); //barman ready
			startSignal.await(); //check latch - don't start until told to do so

	        executeStandard(); // FCFS = 0, SJF = 1, RR = 2
		} 
		catch (InterruptedException e1) 

		{
			System.out.println("---Barman is packing up ");
		}
	}

	private void executeStandard() throws InterruptedException 
	{
		DrinkOrder nextOrder;
		while (true) 
		{
			nextOrder = orderQueue.take();
			processOrder(nextOrder);
		}
	}

	public void processOrder(DrinkOrder order) throws InterruptedException 
	{
		System.out.println("---Barman preparing order for patron "+ order.toString());
		sleep(order.getExecutionTime()); //processing order
		System.out.println("---Barman has made order for patron "+ order.toString());
		order.orderDone();
	}
}


