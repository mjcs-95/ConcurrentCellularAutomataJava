import java.util.concurrent.*;
import java.util.concurrent.CyclicBarrier;
import java.util.Scanner;
import java.util.Random;

/*Calculo masivo, Coeficiente de bloqueo=0, el nº de hebras se mantiene*/

class ClaseExamen implements Runnable {
    private static int[][] actual;
    private static int[][] futura;
    private static int dimension;

    private int linf;
    private int lsup;
    private int thilo;

    private static int nnuc = Runtime.getRuntime().availableProcessors();

    private static CyclicBarrier barrera0;//1 nucleo
    private static CyclicBarrier barrera1;//n nucleos
    private static CyclicBarrier barrera2;//n/2 nucleos

    private static ThreadPoolExecutor ejecutor0;//1 nucleo
    private static ThreadPoolExecutor ejecutor1;//n nucleos
    private static ThreadPoolExecutor ejecutor2;//variación de nucleos

    public ClaseExamen(){//Por defecto
        barrera0=new CyclicBarrier(1+ 1);//1+ es por el hilo del main
        barrera1=new CyclicBarrier(1+ nnuc);
        barrera2= (nnuc==1) ? new CyclicBarrier(1+ 1) : new CyclicBarrier(1+ (int) nnuc/2 );
        ejecutor0=new ThreadPoolExecutor(   1, 
                                            1, 
                                            6000L, 
                                            TimeUnit.MILLISECONDS, 
                                            new LinkedBlockingQueue<Runnable>() );
        
        ejecutor1=new ThreadPoolExecutor(   nnuc, 
                                            nnuc, 
                                            6000L, 
                                            TimeUnit.MILLISECONDS, 
                                            new LinkedBlockingQueue<Runnable>() );
        
        ejecutor2=new ThreadPoolExecutor(   barrera2.getParties()-1, 
                                            barrera2.getParties()-1, 
                                            6000L, 
                                            TimeUnit.MILLISECONDS, 
                                            new LinkedBlockingQueue<Runnable>() );

        dimension = 0;
        actual=new int[dimension][dimension];
        futura=new int[dimension][dimension];
        for(int i=0; i < dimension-1; i++ ) {//Inicialización de la matriz
            for(int j=0; j < dimension-1; j++ ) {
                actual[i][j]=0;            
            }
        }
        //Cositas especiales
    }

    //public ClaseExamen(){}//Para manual

    public ClaseExamen(int liminf,int limsup,int tipohilo){//hilos
        linf=liminf;
        lsup=limsup;
        thilo=tipohilo;
    }

    public void run(){
        for (int i=linf; i<lsup ; i++) {
            for (int j=0; j<actual[0].length-1; j++) {
/*                if(i==0){
                    if(j==0){
                        algo =  
                                                                            actual[i][j+1]  ==2 ||
                                                       actual[i+1][j]==2 || actual[i+1][j+1]==2 ;        
                    }else if(j==dimension-1){
                        algo =  
                                actual[i][j-1]  ==2 ||                   
                                actual[i+1][j-1]==2 || actual[i+1][j]==2                        ;                            
                    }else{
                        algo =  
                                actual[i][j-1]  ==2 ||                      actual[i][j+1]  ==2 ||
                                actual[i+1][j-1]==2 || actual[i+1][j]==2 || actual[i+1][j+1]==2 ;        
                    }//if interno
                }else if(i==dimension-1){
                    if(j==0){
                        algo =                         actual[i-1][j]==2 || actual[i-1][j+1]==2 ||
                                                                            actual[i][j+1]  ==2   
                                                                                                ;        
                    }else if(j==dimension-1){
                        algo =  actual[i-1][j-1]==2 || actual[i-1][j]==2 ||
                                actual[i][j-1]  ==2                      
                                                                                                ;        
                    }else{
                        algo =  actual[i-1][j-1]==2 || actual[i-1][j]==2 || actual[i-1][j+1]==2 ||
                                actual[i][j-1]  ==2 ||                      actual[i][j+1]  ==2 
                                                                                                ;        
                    }//if interno
                }else{
                    if(j==0){
                        algo =                         actual[i-1][j]==2 || actual[i-1][j+1]==2 ||
                                                                            actual[i][j+1]  ==2 ||
                                                       actual[i+1][j]==2 || actual[i+1][j+1]==2 ;        
                    }else if(j==dimension-1){
                        algo =  actual[i-1][j-1]==2 || actual[i-1][j]==2 || 
                                actual[i][j-1]  ==2 ||                      
                                actual[i+1][j-1]==2 || actual[i+1][j]==2 ;        
                    }else{
                        algo =  actual[i-1][j-1]==2 || actual[i-1][j]==2 || actual[i-1][j+1]==2 ||
                                actual[i][j-1]  ==2 ||                      actual[i][j+1]  ==2 ||
                                actual[i+1][j-1]==2 || actual[i+1][j]==2 || actual[i+1][j+1]==2 ;
                    }//if interno
                }//if externo
*/
            }//for interno
        }//for externo
    }

    public static void main(String[] args) {
        /*Menu de usuario*/



        /*****************/
        int inc1= dimension/(barrera1.getParties()-1);
        int i;
        ClaseExamen[] hilos1=new ClaseExamen[barrera1.getParties()-1];
        for(i=0; i<hilos1.length-1; i++ ){
            hilos1[i]=new ClaseExamen( i*inc1, (i+1)*inc1, 1 ); 
        }
        hilos1[barrera1.getParties()-2]=new ClaseExamen( inc1*i, dimension, 1);

        int inc2= dimension/(barrera2.getParties()-1);
        int j;
        ClaseExamen[] hilos2=new ClaseExamen[barrera2.getParties()-1];
        for(j=0; j<hilos2.length-1; j++ ){ 
            hilos2[j]=new ClaseExamen( j*inc2, (j+1)*inc2, 2 );
        }
        hilos2[barrera2.getParties()-2] = new ClaseExamen( inc2*j, dimension, 2);

        long t1=0,t2=0,t3=0;
        long ini,fin;

        try{
            ini=System.nanoTime();              
            for (i=0;i<g_ ;i++ ) {
                ejecutor0.execute( new ClaseExamen(0, dimension, 0) );
                barrera0.await();
                for(int k=0;k<actual.length;k++){ System.arraycopy(futura[k], 0, actual[k], 0, dimension); }
            }
            fin=System.nanoTime();    
            t1=fin-ini;
            System.out.println("Acabado 1");

            ini=System.nanoTime();    
            for (i=0;i<g_ ;i++ ) {    
                for(j=0; j<hilos1.length; j++ ){ ejecutor1.execute(hilos1[j]); }
                barrera1.await();    
                for(int k=0;k<actual.length;k++){ System.arraycopy(futura[k], 0, actual[k], 0, dimension); }
            }
            fin=System.nanoTime();    
            t2=fin-ini;
            System.out.println("Acabado 2"); 

            ini=System.nanoTime();    
            for (i=0;i<g_ ;i++ ) {
                for(j=0; j<hilos2.length; j++ ){ ejecutor2.execute(hilos2[j]); }
                barrera2.await();        
                for(int k=0;k<actual.length;k++){ System.arraycopy(futura[k], 0, actual[k], 0, dimension); }
            }
            fin=System.nanoTime();
            t3=fin-ini;
            System.out.println("Acabado 3");  

        }catch(Exception e){
            System.out.println("Error de barrera...");
        }

        System.out.println("***************SpeedUps***************");
        System.out.println("t_1nucleo /t_Nnucleos     = "+(double) t1/t2);
        System.out.println("t_1nucleo /t_Xnucleos = "+(double) t1/t3);
        System.out.println("**************************************");
        ejecutor0.shutdown(); while(!ejecutor0.isTerminated() ){}
        ejecutor1.shutdown(); while(!ejecutor1.isTerminated() ){}
        ejecutor2.shutdown(); while(!ejecutor2.isTerminated() ){}
    }
}