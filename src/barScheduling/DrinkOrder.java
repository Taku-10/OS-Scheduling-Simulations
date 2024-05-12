package barScheduling;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a drink order in a bar.
 * @author M. M. Kuttel (mkuttel@gmail.com)
 */
public class DrinkOrder  {

    //DO NOT change the code below
    public enum Drink { 
        Beer("Beer", 10),
        Cider("Cider", 10),
        GinAndTonic("Gin and Tonic", 30),
        Martini("Martini", 50),
        Cosmopolitan("Cosmopolitan", 80),
        BloodyMary("Bloody Mary", 90),
        Margarita("Margarita", 100),
        Mojito("Mojito", 120),
        PinaColada("Pina Colada", 200),
        LongIslandIcedTea("Long Island Iced Tea", 300),
    	B52("B52", 500);
    	
    	
        private final String name;
        private final int preparationTime; // in minutes
        

        Drink(String name, int preparationTime) {
            this.name = name;
            this.preparationTime = preparationTime;
        }

        public String getName() {
            return name;
        }

        public int getPreparationTime() {
            return preparationTime;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    private final Drink drink;
    private static final Random random = new Random();
    private int orderer;
    private AtomicBoolean orderComplete;
    private long queueEntryTime; // Time when the order was queued
    private long startProcessingTime; // Time when order processing starts

 //constructor
    public DrinkOrder(int patron) {
    	drink=getRandomDrink();
    	orderComplete = new AtomicBoolean(false);
    	orderer=patron;
    }
    
    public static Drink getRandomDrink() {
        Drink[] drinks = Drink.values();  // Get all enum constants
        int randomIndex = random.nextInt(drinks.length);  // Generate a random index
        return drinks[randomIndex];  // Return the randomly selected drink
    }
    

    public int getExecutionTime() {
        return drink.getPreparationTime();
    }
    
    //barman signals when order is done
    public synchronized void orderDone() {
    	orderComplete.set(true);
        this.notifyAll();
    }
    
    //patrons wait for their orders
    public synchronized void waitForOrder() throws InterruptedException {
    	while(!orderComplete.get()) {
    		this.wait();
    	}
    }

/**
 * Sets the time when the order was queued.
 * @param queueEntryTime the time when the order was queued
 */
    public void setQueueEntryTime(long queueEntryTime) {
        this.queueEntryTime = queueEntryTime;
    }

/**
 * Gets the time when the order was queued.
 * @return the time when the order was queued
 */
    public long getQueueEntryTime() {
        return queueEntryTime;
    }

/**
 * Sets the time when the order processing starts.
 * @param startProcessingTime the time when the order processing starts
 */
    public void setStartProcessingTime(long startProcessingTime) {
        this.startProcessingTime = startProcessingTime;
    }

/**
 * Gets the time when the order processing starts.
 * @return the time when the order processing starts
 */
    public long getStartProcessingTime() {
        return startProcessingTime;
    }
    
    @Override
    public String toString() {
        return Integer.toString(orderer) +": "+ drink.getName();
    }
}