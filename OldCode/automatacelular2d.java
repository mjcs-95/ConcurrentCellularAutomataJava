
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Manuel
 * @version 1.0.26112016
 */
public class automatacelular2d implements Runnable {
    private static int[][] automata;
    private static int[][] nextgen;
    private static int     celulas;
    private static AtomicInteger celulasvivas;
    private static AtomicInteger celulasmuertas;
    private static boolean fronteranula;
    private static int nNuc;
    private int thilo;
    private int linf;
    private int lsup;
    
    automatacelular2d(int liminf ,int limsup ,int th){ linf=liminf; lsup=limsup; thilo=th; }
    
    @Override
    public void run(){
        switch(thilo){
            case 0: //Constructor
                Random rand=new Random();
                for(int i = linf ; i < lsup ; i++ ){
                    for(int j = 0 ; j < celulas ; j++ ){ automata[i][j]= rand.nextInt(2); }
                }
            break;
            case 1://nextgen
                for(int i = linf ; i < lsup ; i++ ){
                    for(int j = 0 ; j < celulas ; j++ ){ nextgen[i][j]=descendiente(i,j); }
                }
            break;
            case 2://calculo de poblacion
                for(int i = linf ; i < lsup ; i++ ){
                    for(int j = 0 ; j < celulas ; j++ ){
                        if(automata[i][j]==1){  celulasvivas.incrementAndGet(); }
                    }
                }
            break;
        }
    }

    automatacelular2d(){ //Constructor por defecto ,no paralelizado .
        celulas = 200;
        Random rand= new Random();
        automata=new int[celulas][celulas];
        nextgen=new int[celulas][celulas];
        nNuc= Runtime.getRuntime().availableProcessors();
        celulasvivas=new AtomicInteger();
        celulasmuertas=new AtomicInteger();
        for (int i=0 ;i<celulas ; i++ ) {
            for(int j=0 ;j<celulas ; j++){
                automata[i][j]= rand.nextInt(2);
            }
        }
    }
    
    automatacelular2d(int ncelulas){
        celulas = ncelulas;
        celulasvivas=new AtomicInteger();
        celulasmuertas=new AtomicInteger();
        Random rand= new Random();
        automata = new int[celulas][celulas];
        nextgen  = new int[celulas][celulas];
        nNuc= Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = ncelulas / nNuc;
        
        automatacelular2d automatacelular[] = new automatacelular2d[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new automatacelular2d( i*inc , (i+1)*inc , 0 ); }            
        automatacelular[nNuc-1]=new automatacelular2d( (nNuc-1)*inc , celulas , 0 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}

    }
    
    public void setfrontera(boolean nula){ fronteranula=nula; }
    
    public int[][] actual(){ return automata;} 
    
    public int getcelulasvivas(){ return celulasvivas.get(); }
    
    public int getcelulasmuertas(){ return celulasmuertas.get(); }
    
    public void poblacion(){
        celulasvivas.set(0);
        nNuc= Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = celulas / nNuc;

        automatacelular2d automatacelular[] = new automatacelular2d[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new automatacelular2d( i*inc , (i+1)*inc , 2 ); }            
        automatacelular[nNuc-1]=new automatacelular2d( (nNuc-1)*inc , celulas ,2 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}

        celulasmuertas.set( (celulas*celulas) - getcelulasvivas() );

    }
    
    public int nvecinos(int i,int j ){// Frontera nula
        int vecinos=0;
        if(fronteranula){
            if(i==0){//primera fila
                if(j==0){//primera fila y primera columna
                    vecinos+=0;
                    vecinos+=                                    automata[i  ][j+1];
                    vecinos+=                   automata[i+1][j]+automata[i+1][j+1];            
                }else if(j==celulas-1){//primera fila y ultima columna
                    vecinos+=0;
                    vecinos+=automata[i  ][j-1];
                    vecinos+=automata[i+1][j-1]+automata[i+1][j];
                }else{//primera fila
                    vecinos+=0;
                    vecinos+=automata[i  ][j-1]                 +automata[i  ][j+1];
                    vecinos+=automata[i+1][j-1]+automata[i+1][j]+automata[i+1][j+1];                        
                }
            }else if(i==celulas-1){//ultima fila
                if(j==0){//ultima fila y primera columna
                    vecinos+=                   automata[i-1][j]+automata[i-1][j+1];
                    vecinos+=                                   +automata[i  ][j+1];            
                    vecinos+=0;
                }else if(j==celulas-1){//ultima fila y ultima columna
                    vecinos+=automata[i-1][j-1]+automata[i-1][j];
                    vecinos+=automata[i  ][j-1]                 ;
                    vecinos+=0;
                }else{
                    vecinos+=automata[i-1][j-1]+automata[i-1][j]+automata[i-1][j+1];
                    vecinos+=automata[i  ][j-1]                 +automata[i  ][j+1];            
                    vecinos+=0;
                }
            }else{
                if(j==0){//primera columna
                    vecinos+=                   automata[i-1][j]+automata[i-1][j+1];
                    vecinos+=                                   +automata[i  ][j+1];
                    vecinos+=                   automata[i+1][j]+automata[i+1][j+1];            
                }else if(j==celulas-1){//ultima columna
                    vecinos+=automata[i-1][j-1]+automata[i-1][j];
                    vecinos+=automata[i  ][j-1]                 ;
                    vecinos+=automata[i+1][j-1]+automata[i+1][j];                        
                }else{
                    vecinos+=automata[i-1][j-1]+automata[i-1][j]+automata[i-1][j+1];
                    vecinos+=automata[i  ][j-1]                 +automata[i  ][j+1];
                    vecinos+=automata[i+1][j-1]+automata[i+1][j]+automata[i+1][j+1];            
                }
            }
        }else{
            vecinos=automata[Math.floorMod(i-1,celulas)][Math.floorMod(j-1,celulas)]+automata[Math.floorMod(i-1,celulas)][Math.floorMod(j,celulas)]+automata[Math.floorMod(i-1,celulas)][Math.floorMod(j+1,celulas)]+
                    automata[Math.floorMod(i  ,celulas)][Math.floorMod(j-1,celulas)]                                                               +automata[Math.floorMod(i  ,celulas)][Math.floorMod(j+1,celulas)]+
                    automata[Math.floorMod(i+1,celulas)][Math.floorMod(j-1,celulas)]+automata[Math.floorMod(i+1,celulas)][Math.floorMod(j,celulas)]+automata[Math.floorMod(i+1,celulas)][Math.floorMod(j+1,celulas)];
        }
        return vecinos;
    }
    
    public int descendiente(int i, int j){
        int descendiente;
        int vecinos=nvecinos(i,j);
        if(automata[i][j]==0){ 
            if(vecinos==3){ descendiente=1; 
            }else{          descendiente=0; }
        }else{
            if(vecinos<2 || vecinos>3  ){ descendiente=0;
            }else{descendiente=1; }
        }
        return descendiente;
    }
    
    public void nextGen(){
        nNuc= Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = celulas / nNuc;

        automatacelular2d automatacelular[] = new automatacelular2d[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new automatacelular2d( i*inc , (i+1)*inc , 1 ); }            
        automatacelular[nNuc-1]=new automatacelular2d( (nNuc-1)*inc , celulas ,1 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}
        
        for(int i = 0 ; i < celulas ; i++){ System.arraycopy(nextgen[i], 0, automata[i], 0, celulas); }
        poblacion();
    }
    
    public void vivir(int gen){
        for(int i=0;i<gen;i++){ nextGen(); }
    }
    
    public void mostrargen(){
        System.out.flush();
        for(int i = 0 ; i < celulas ; i++){
            for(int j = 0 ; j < celulas ; j++){
                System.out.println(automata[i][j]+" , ");
            }
            System.out.println("");
        }
    }
}


