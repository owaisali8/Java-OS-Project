
import java.util.Arrays;


class CircularQueue<E> {

    private E[] circularQueueAr;
    private int maxSize;   //Maximum Size of Circular Queue

    private int rear;   //elements will be added/queued at rear.
    private int front;   //elements will be removed/dequeued from front      
    private int number; //number of elements currently in Priority Queue

    /**
     * Constructor
     */
    public CircularQueue(int maxSize) {
        this.maxSize = maxSize;
        circularQueueAr = (E[]) new Object[this.maxSize];
        number = 0; //Initially number of elements in Circular Queue are 0.
        front = 0;
        rear = 0;
    }

    /**
     * Adds element in Circular Queue(at rear)
     */
    public void enqueue(E item) {
        if (isFull()) {
            System.out.println("Queue is Full");
        } else {
            circularQueueAr[rear] = item;
            rear = (rear + 1) % circularQueueAr.length;
            number++; // increase number of elements in Circular queue
        }
    }

    /**
     * Removes element from Circular Queue(from front)
     */
    public E dequeue() {
        E deQueuedElement = null;
        if (isEmpty()) {
            System.out.println("Circular Queue is empty");
        } else {
            deQueuedElement = circularQueueAr[front];
            circularQueueAr[front] = null;
            front = (front + 1) % circularQueueAr.length;
            number--; // Reduce number of elements from Circular queue
        }
        return deQueuedElement;
    }

    /**
     * Return true if Circular Queue is full.
     */
    public boolean isFull() {
        return (number == circularQueueAr.length);
    }

    /**
     * Return true if Circular Queue is empty.
     */
    public boolean isEmpty() {
        return (number == 0);
    }

    public String toString(){
        String s = "[ ";
        for(Object obj: this.circularQueueAr){
            if(obj != null){
                s += obj.toString() +",";
            }
        }
        return s.substring(0, s.length()-1) + "]";
    }
}
