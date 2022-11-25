
/**
 *
 * @author Owais, Hamza and Yunus
 */
public class PCB {

    private int id;
    private int priority;
    private String name;
    private int codeSize;
    private int dataSize;
    private PageTable codePT;
    private PageTable dataPT;
    private short[] GPR = new short[16];
    private short[] SPR = new short[16];
    private float waitTime;
    private float execTime;
    private boolean[] flagReg = new boolean[16];

    public boolean isTerminated() {
        return false;
    }

    public PCB() {
    }

    public PCB(int id, int priority, String name, int codeSize, int dataSize,
             PageTable codePT, PageTable dataPT)
    {
        this.priority = priority;
        this.id = id;
        this.name = name;
        this.codeSize = codeSize;
        this.dataSize = dataSize;
        this.codePT = codePT;
        this.dataPT = dataPT;
    }

    public int getPriority() {
        return this.priority;
    }
    
    public String getName(){
        return name;
    }
    
    
    @Override
    public String toString(){
        return String.format("ID: %d | Priority: %d | Name: %s | CodeSize: %d | DataSize: %d | CodePT: %s | DataPT: %s\n",
                id,priority,name,codeSize,dataSize,codePT,dataPT);
    }
    
    public short[] getGPR(){
        return this.GPR;
    }
    
    public short[] getSPR(){
        return this.SPR;
    }
    
    public boolean[] getFlags(){
        return this.flagReg;
    }
    
    public int [] getcodePage(){
        return this.codePT.getKeys();
    }
    
    public int [] getdataPage(){
        return this.dataPT.getKeys();
    }
    
    public int getCodeSize(){
        return this.codeSize;
    }
    
    public int getdataSize(){
        return this.dataSize;
    }
    
    public int [] getPages(){
        int [] codePages = this.getcodePage();
        int [] dataPages = this.getdataPage();
        
        int[] totalPages = new int[codePages.length +dataPages.length];
        
        int j = 0;
        for(int i: codePages){
            totalPages[j] = i;
            j++;
        }
        
        for(int i: dataPages){
            totalPages[j] = i;
            j++;
        }
        return totalPages;
        
    }

}
