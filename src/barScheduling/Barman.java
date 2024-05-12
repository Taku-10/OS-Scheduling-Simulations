package barScheduling;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class represents a Barman who serves drink orders. The Barman is a thread
 * that waits for a start signal from the main thread, then continuously
 * retrieves and processes drink orders from the order queue. The order queue
 * can be configured to use either First Come First Served (FCFS) or Shortest
 * Job First (SJF) scheduling algorithms.
 * @author M. M. Kuttel mkuttel@gmail.com
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
    // Constructor with scheduling algorithm selector
    Barman(CountDownLatch startSignal, int schedAlg) 
    {
        this.startSignal = startSignal;
        if (schedAlg == 0) 
        {
            // First Come First Served - use a normal LinkedBlockingQueue
            this.orderQueue = new LinkedBlockingQueue<>();
        } else if (schedAlg == 1) 
        {
            // Shortest Job First - use a PriorityBlockingQueue with a custom comparator
            this.orderQueue = new PriorityBlockingQueue<>(10, new Comparator<DrinkOrder>() 
            {
                @Override
                public int compare(DrinkOrder o1, DrinkOrder o2) {
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
 * Places a drink order into the queue.
 * Also sets the queue entry time of the order to the current time.
 * @param order The drink order to be placed in the queue.
 * @throws InterruptedException If the thread is interrupted while waiting to place the order.
 */
    public void placeDrinkOrder(DrinkOrder order) throws InterruptedException 
    {
        orderQueue.put(order);
        order.setQueueEntryTime(System.currentTimeMillis());  // Timestamp when order is queued
    }

/**
 * The run method is the entry point for the Barman thread.
 * It waits for the start signal from the main thread, then continuously
 * retrieves and processes drink orders from the order queue.
 * @throws InterruptedException If the thread is interrupted while waiting to place the order.
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
                nextOrder.setStartProcessingTime(System.currentTimeMillis());
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
