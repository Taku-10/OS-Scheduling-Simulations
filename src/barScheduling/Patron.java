package barScheduling;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * This is the basic class, representing the patrons at the bar.
 * Each patron is a thread that arrives at the bar, submits an order,
 * and waits for the order to be processed.
 * @author M. M. Kuttel 2024 mkuttel@gmail.com
 */

public class Patron extends Thread 
{
	
	private Random random = new Random();// for variation in Patron behaviour
	private CountDownLatch startSignal; //all start at once, actually shared
	private Barman theBarman; //the Barman is actually shared though
	private int ID; //thread ID 
	private int lengthOfOrder;
	private long startTime, endTime; //for all the metrics
	public static FileWriter fileW;
	private DrinkOrder [] drinksOrder;
	
	Patron( int ID,  CountDownLatch startSignal, Barman aBarman) {
		this.ID=ID;
		this.startSignal=startSignal;
		this.theBarman=aBarman;
		this.lengthOfOrder=random.nextInt(5)+1;//between 1 and 5 drinks
		drinksOrder=new DrinkOrder[lengthOfOrder];
	}
	
	public  void writeToFile(String data) throws IOException {
	    synchronized (fileW) {
	    	fileW.write(data);
	    }
	}

/**
 * This method is the main execution thread for each patron.
 * It simulates a patron arriving at the bar, submitting an order,
 * and waiting for the order to be processed.
 * @throws InterruptedException if the thread is interrupted while waiting
 * @throws IOException if an error occurs while writing to the file
 */
	public void run() {
		try {
			//Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); //this patron is ready
			startSignal.await(); //wait till everyone is ready
	        int arrivalTime = random.nextInt(300)+ID*100;  // patrons arrive gradually later
	        sleep(arrivalTime);// Patrons arrive at staggered  times depending on ID 
			System.out.println("thirsty Patron "+ this.ID +" arrived");
			//END do not change
			
	        //create drinks order
	        for(int i=0;i<lengthOfOrder;i++) {
	        	drinksOrder[i]=new DrinkOrder(this.ID);
	        	
	        }

			System.out.println("Patron "+ this.ID + " submitting order of " + lengthOfOrder +" drinks"); //output in standard format  - do not change this
	        startTime = System.currentTimeMillis();//started placing orders
			long firstResponseTime = -1;
            long totalWaitingTime = 0;

			for(int i=0;i<lengthOfOrder;i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				theBarman.placeDrinkOrder(drinksOrder[i]);
			}

			for(int i=0;i<lengthOfOrder;i++) 
			{
				drinksOrder[i].waitForOrder();
				if (i==0)
				{
					firstResponseTime = drinksOrder[i].getStartProcessingTime() - startTime;
				}
				totalWaitingTime += drinksOrder[i].getStartProcessingTime() - drinksOrder[i].getQueueEntryTime();
			}


			endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime; // Turnaround time

			// Output format: ID, ArrivalTime, TurnaroundTime, ResponseTime, WaitingTime
            writeToFile(String.format("%d, %d, %d, %d, %d\n", ID, arrivalTime, totalTime, firstResponseTime, totalWaitingTime));
			System.out.println("Patron "+ this.ID + " got order in " + totalTime);
			
		} catch (InterruptedException e1) 
		{  //do nothing
		} 
		catch (IOException e) 
		{
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	