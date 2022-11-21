
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Owais, Hamza and Yunus
 */
public class VEnvTest {

    public VEnvTest() {
    }

    @Test
    public void test1_FileCode() { // Test 1 for check file code
        var main = new VEnv();
        main.loadIntoMemoryAndExecute("30 01 00 01 30 02 7F FF 19 01 02 F3");
        assertEquals(32767, main.getGPR()[1]);
        assertEquals(32767, main.getGPR()[2]);
    }

    @Test
    public void test2_R2R() { // Test 2 for mov, add, sub
        var main = new VEnv();
        String code = "30 01 00 05 16 02 01 17 01 02 18 03 02 F3";
        main.loadIntoMemoryAndExecute(code);
        assertEquals(5, main.getGPR()[2]);  //mov
        assertEquals(10, main.getGPR()[1]); //add
        assertEquals(-5, main.getGPR()[3]); //sub
    }

    @Test
    public void test3_R2R() { // Test 3 for mul, div, and, or
        var main = new VEnv();
        String code = "30 01 00 05 30 02 00 06 19 01 02 F3";
        main.loadIntoMemoryAndExecute(code);
        assertEquals(30, main.getGPR()[1]); //mul

        main.loadIntoMemoryAndExecute("1A 01 02 F3");
        assertEquals(5, main.getGPR()[1]); //div

        main.loadIntoMemoryAndExecute("1B 01 02 F3");
        assertEquals(4, main.getGPR()[1]); //and

        main.loadIntoMemoryAndExecute("1C 01 02 F3");
        assertEquals(6, main.getGPR()[1]); //or
    }

    @Test
    public void test4_R2I() { // Test 4 for Register to Immediate 
        var main = new VEnv();
        main.loadIntoMemoryAndExecute("31 00 00 0A");
        assertEquals(10, main.getGPR()[0]); //Addi

        main.loadIntoMemoryAndExecute("32 01 00 0B F3");
        assertEquals(-11, main.getGPR()[1]); //subi

        main.loadIntoMemoryAndExecute("33 01 00 02 F3");
        assertEquals(-22, main.getGPR()[1]); //muli

        main.loadIntoMemoryAndExecute("34 01 00 02 F3");
        assertEquals(-11, main.getGPR()[1]); //divi
        
        main.loadIntoMemoryAndExecute("35 00 00 08 F3");
        assertEquals(8, main.getGPR()[0]); //andi
        
        main.loadIntoMemoryAndExecute("36 00 00 0E F3");
        assertEquals(14, main.getGPR()[0]); //ori
    }
    
    @Test
    public void test5_SO() { // Test 4 for Singal Operand
        var main = new VEnv();
        main.loadIntoMemoryAndExecute("30 00 00 05"); //mov 5 in r0
        main.loadIntoMemoryAndExecute("71 00");
        assertEquals(10, main.getGPR()[0]); //shl
        
        main.loadIntoMemoryAndExecute("72 00 F2");
        assertEquals(5,main.getGPR()[0]); //shr
        
        main.loadIntoMemoryAndExecute("73 00 F2");
        assertEquals(10,main.getGPR()[0]); //rtl
        
        main.loadIntoMemoryAndExecute("74 00 F2");
        assertEquals(5,main.getGPR()[0]); //rtr
        
        main.loadIntoMemoryAndExecute("75 00 75 00 F3");
        assertEquals(7,main.getGPR()[0]); //inc
        
        main.loadIntoMemoryAndExecute("76 00 F3");
        assertEquals(6,main.getGPR()[0]); //dec

    }
    
    @Test
    public void test6_MemInc() { // Test 6 for memory instructions 
        var main = new VEnv();
        String code = "30 00 00 0F F3";
        main.loadIntoMemoryAndExecute(code);
        assertEquals(15,main.getGPR()[0]);
        
        main.loadIntoMemoryAndExecute("52 00 00 10 F2");
        assertEquals(15, main.getMem()[16]);  //movs
        
        main.loadIntoMemoryAndExecute("51 02 00 10 F3");
        assertEquals(15, main.getGPR()[2]); //movl
    }
    
      @Test
    public void test7_Stack() { // Test 7 for Stack
        var main = new VEnv();
        String code = "75 01 75 01 77 01 78 02 F3";
        main.loadIntoMemoryAndExecute(code);
        assertEquals(02,main.getGPR()[2]);
    }

}
