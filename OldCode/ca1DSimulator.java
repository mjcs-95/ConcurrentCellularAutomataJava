/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Esta clase representa las operaciones realizables con un automata celular .
 * La salida de estas operaciones se realizan en la consola de comandos .
 * @author Manuel Jesús Corbacho Sánchez
 * @version 1.2.26112016
 */
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Manuel Jesús Corbacho Sánchez
 */
public class ca1DSimulator implements Runnable {
    private static int[][] automata;
    private static int[] celulasvivas;
    private static int[] celulasmuertas;
    private static int[] reglabinaria;
    private static int ncelulas;
    private static int ngeneraciones;
    private static int genactual;
    private static AtomicInteger cont_entropia_celula;
    private static int id_celula_entropia;
    private static double[] entropia_generaciones;//entropia de generaciones del automata
    private static double[] distancia_humming;
    private int linf;
    private int lsup;
    private int thilo;  //0 automata , 1 entropia automata , 2 distancia humming , 3 entropia celula .

    public static double log2 (double x){ return ( Math.log(x)/Math.log(2) ) ; }
        
    public ca1DSimulator(){}
    
    public ca1DSimulator(int Regla){
        genactual=1;
        ncelulas=250;
        ngeneraciones=250;
        entropia_generaciones=new double[ngeneraciones];
        distancia_humming=new double[ngeneraciones-1];
        automata=new int[250][250];
        automata[0][125]=1;
        setregla(Regla);
    }
    
    public ca1DSimulator(int n_celulas,int generaciones){
        genactual=1;
        ncelulas=n_celulas;
        ngeneraciones=generaciones;
        entropia_generaciones=new double[ngeneraciones];
        distancia_humming=new double[ngeneraciones-1];
        automata=new int[generaciones][n_celulas];
        Random rand= new Random();
        for (int i=0 ;i<automata[0].length ; i++ ) { automata[0][i]= rand.nextInt(2); }
    }
    
    public ca1DSimulator(int liminf, int limsup ,int tipohilo ){
        thilo=tipohilo;
        linf=liminf;
        lsup=limsup;
    }
    
    public void setregla(int r){
        int regla=r;
        reglabinaria=new int[8];
        for(int i=7;i>=0;i--){
            reglabinaria[i]=regla%2;
            regla/=2;
        }
    }
    
    public int getngen(){ return ngeneraciones; }

    public int getncelulas(){ return ncelulas; }

    public int[][] getautomata(){ return ca1DSimulator.automata; }
   
    public void mostrargeneracion(int gen){ //Hecho
        System.out.print("[");
        for (int i=0 ;i<ncelulas ; i++ ){
            System.out.print( ca1DSimulator.automata[gen][i]+ ",");
        }
        System.out.println("]");
    }

    public void nextGen(){
        if( genactual < ngeneraciones ){
            int nNuc = Runtime.getRuntime().availableProcessors();
            ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
            int inc   = ncelulas / nNuc;

            ca1DSimulator automatacelular[] = new ca1DSimulator[nNuc];            
            for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new ca1DSimulator( i*inc , (i+1)*inc , 0 ); }            
            automatacelular[nNuc-1]=new ca1DSimulator( (nNuc-1)*inc , ncelulas ,0 );

            for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
            ejecutor.shutdown();
            while( !ejecutor.isTerminated() ){}
            genactual++;
        }
    }
    
    public void caComputation(int nGen){ 
        for(int i=0 ; i<nGen && genactual<getngen() ; i++){ nextGen(); }
    } 

    @Override
    public void run(){   //0 automata , 1 entropia automata , 2 distancia humming , 3 entropia celula .
        switch(this.thilo){
            case 0:
                for(int i=linf;i<lsup;i++){
                    int g=genactual-1 , ant = Math.floorMod(i-1,ncelulas) , sig = Math.floorMod(i+1,ncelulas) , actual = Math.floorMod(i,ncelulas);                   
                    if( automata[g][ant]==0 && automata[g][actual]==0 && automata[g][sig]==0 ) automata[genactual][i]=reglabinaria[7]; else
                    if( automata[g][ant]==0 && automata[g][actual]==0 && automata[g][sig]==1 ) automata[genactual][i]=reglabinaria[6]; else 
                    if( automata[g][ant]==0 && automata[g][actual]==1 && automata[g][sig]==0 ) automata[genactual][i]=reglabinaria[5]; else 
                    if( automata[g][ant]==0 && automata[g][actual]==1 && automata[g][sig]==1 ) automata[genactual][i]=reglabinaria[4]; else 
                    if( automata[g][ant]==1 && automata[g][actual]==0 && automata[g][sig]==0 ) automata[genactual][i]=reglabinaria[3]; else 
                    if( automata[g][ant]==1 && automata[g][actual]==0 && automata[g][sig]==1 ) automata[genactual][i]=reglabinaria[2]; else 
                    if( automata[g][ant]==1 && automata[g][actual]==1 && automata[g][sig]==0 ) automata[genactual][i]=reglabinaria[1]; else 
                    if( automata[g][ant]==1 && automata[g][actual]==1 && automata[g][sig]==1 ) automata[genactual][i]=reglabinaria[0]; 
                }
            break;            
            case 1:
                int[][] matrix = this.getautomata();
                for(int i=linf ; i < lsup ; i++ ){
                    int cont=0;
                    for(int j=0 ; j < matrix[0].length;j++){
                        if(matrix[i][j]==1){ cont++; }
                    }
                    double p1 =(double) cont/ncelulas;
                    double p0 = 1-p1;
                    double entropia;
                    if( p1==0 || p0==0 ){ entropia_generaciones[i] = 0; }
                    else{ entropia_generaciones[i] = -(p1*log2(p1)+p0*log2(p0)); }
                }
            break;

            case 2:
                int[][] matrix2=this.getautomata();
                for(int i=linf ; i < lsup ; i++ ){
                    int cont=0;
                    for(int j=0 ; j < matrix2[0].length;j++){
                        if(matrix2[i+1][j]!=matrix2[i][j]){ cont++; }
                    }
                    distancia_humming[i]=cont;
                }
            break;
                
            case 3:
                int[][] auxiliar = this.getautomata();
                for(int i=linf ; i<lsup ; i++){
                    if( auxiliar[i][id_celula_entropia]==1 ){ cont_entropia_celula.incrementAndGet(); }
                }        
            break;
            case 4:
                for(int i=linf ; i < lsup ; i++ ){
                    int vivas=0;
                    for(int j=0 ; j < ncelulas ;j++){
                        if(automata[i][j]==1 ){ vivas++; }
                    }
                    celulasvivas[i]=vivas;
                    celulasmuertas[i]=ncelulas-vivas;
                }
            break;
        }
        
    }
    
    public static double entropia_celula(int celula){
        id_celula_entropia=celula;
        cont_entropia_celula =new AtomicInteger(0);
        int nNuc = Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = ngeneraciones / nNuc;

        ca1DSimulator automatacelular[] = new ca1DSimulator[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new ca1DSimulator( i*inc , (i+1)*inc , 3 ); }            
        automatacelular[nNuc-1]=new ca1DSimulator( (nNuc-1)*inc , ngeneraciones , 3 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}

        int cont = cont_entropia_celula.intValue();

        double p1 =(double) cont/ngeneraciones;
        double p0 = 1-p1;
        double entropia;
        if( p1==0 || p0==0 ){ entropia=0; }
        else{ entropia = -(p1*log2(p1)+p0*log2(p0)); }
        return entropia;
    }
    
    public static double[] entropia_automata(){
        int nNuc = Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = ngeneraciones / nNuc;

        ca1DSimulator automatacelular[] = new ca1DSimulator[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new ca1DSimulator( i*inc , (i+1)*inc , 1 ); }            
        automatacelular[nNuc-1]=new ca1DSimulator( (nNuc-1)*inc , ngeneraciones , 1 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}

        return ca1DSimulator.entropia_generaciones;
    }
    
    public static double[] distancia_humming(){
        distancia_humming=new double[ngeneraciones-1];
        int nNuc = Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = (ngeneraciones-1) / nNuc;

        ca1DSimulator automatacelular[] = new ca1DSimulator[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new ca1DSimulator( i*inc , (i+1)*inc , 2 ); }            
        automatacelular[nNuc-1]=new ca1DSimulator( (nNuc-1)*inc , ngeneraciones-1 , 2 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}        
        return distancia_humming;
    }
    
    public static void poblacion(){
        celulasvivas=new int[ngeneraciones];
        celulasmuertas=new int[ngeneraciones];
        int nNuc = Runtime.getRuntime().availableProcessors();
        ExecutorService ejecutor = Executors.newFixedThreadPool(nNuc);
        int inc   = (ngeneraciones-1) / nNuc;

        ca1DSimulator automatacelular[] = new ca1DSimulator[nNuc];            
        for(int i=0; i < nNuc-1 ; i++ ){ automatacelular[i]=new ca1DSimulator( i*inc , (i+1)*inc , 4 ); }            
        automatacelular[nNuc-1]=new ca1DSimulator( (nNuc-1)*inc , ngeneraciones , 4 );

        for(int k=0;k < nNuc ; k++ ){ ejecutor.execute(automatacelular[k]); }
        ejecutor.shutdown();
        while( !ejecutor.isTerminated() ){}
        
        
        
        
        
    }
    
    public static int[][] getpoblacion(){
        int[][] pob=new int[2][];
        pob[0]=celulasvivas;
        pob[1]=celulasmuertas;
        return pob;
    }
}