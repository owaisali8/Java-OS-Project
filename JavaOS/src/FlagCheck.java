
/**
 *
 * @author Owais, Hamza and Yunus
 */
public class FlagCheck {
    
    protected static boolean checkCarry(int val){
        return val < 0; //Check MSB by looking if the number is signed
    }
    
    protected static boolean checkZero(int val){
        return val == 0;
    }
    
    protected static boolean checkSign(int val){
        return val < 0;
    }
    
    protected static boolean checkOverflow(int val){
        return val > Short.MAX_VALUE || val < Short.MIN_VALUE;
    }
    
    
}
