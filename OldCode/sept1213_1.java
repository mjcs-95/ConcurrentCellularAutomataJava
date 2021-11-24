/*
* Programa que aplica una matriz para representar una imagen y aplica un suavizado
* de forma concurrente diviendo el trabajo en hilos según el nº de nucleos.
*/ 
import java.util.*;
import java.util.concurrent.*;

class sept1213_1 implements Runnable{
	private static int[][] actual;
	private static int[][] futura;
	private static int nnuc;
	private Random rand;
	int filamin;
	int filamax;

	public void sept1213_1(int filmin,int filmax){
		filamin=filmin;
		filamax=filmax;
	}


	public sept1213_1(){
		actual = new int[500][500];
		futura = new int[500][500];
		rand = new Random();
		for(int i=0;i<500;i++){
			for(int j=0;j<500;j++){
				actual[i][j]=rand.nextInt(256);
			}
		}
		nnuc= Runtime.getRuntime().availableProcessors();
	}
	
	public sept1213_1(int[][] imag){
		actual = new int[imag.length][imag[0].length];
		futura = new int[imag.length][imag[0].length];
		rand = new Random();
		for(int i=0;i<imag.length;i++){
			for(int j=0;j<imag[0].length;j++){
				actual[i][j]=imag[i][j];
			}
		}
		nnuc= Runtime.getRuntime().availableProcessors();
	}
	
	public void run(){
		for(int i=filamin;i<filamax;i++){
			for(int j=0;j<actual.length;j++){
				if(i==0){
					if(j==0){
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i][j+1])/8;					
					}else if(j==actual[0].length-1){
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i][j-1])/8;
					}else{
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i][j+1]+actual[i][j-1])/8;
					}
				}else if(i==actual.length-1){
					if(j==0){
						futura[i][j]=  (4*actual[i][j]+actual[i][j+1]+actual[i-1][j])/8;
					}else if(j==actual[0].length-1){
						futura[i][j]=  (4*actual[i][j]+actual[i-1][j]+actual[i][j-1])/8;
					}else{
						futura[i][j]=  (4*actual[i][j]+actual[i][j+1]+actual[i-1][j]+actual[i][j-1])/8;						
					}
				}else{
					if(j==0){
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i][j+1]+actual[i-1][j])/8;
					}else if(j==actual[0].length-1){
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i-1][j]+actual[i][j-1])/8;
					}else{
						futura[i][j]=  (4*actual[i][j]+actual[i+1][j]+actual[i][j+1]+actual[i-1][j]+actual[i][j-1])/8;
					}
				}
			}	
		}
	}
	
	public static void main(String[] args){
		int op=-1;
		int h=0;
		int w=0;
		Scanner leer= new Scanner(System.in);

		System.out.println("Seleccione una opción:\n");
		System.out.println("   1.Imagen aleatoria:\n");
		System.out.println("   2.Imagen manual:\n");
		while(op!=1 && op!=2){
			switch(op){
				case 1:
					h=500;
					w=500;
					new sept1213_1();
					break;
				case 2:
					System.out.println("Introduzca la altura de la imagen");
					h=leer.nextInt(); leer.nextLine();
					System.out.println("Introduzca la anchura de la imagen");				
					w=leer.nextInt(); leer.nextLine();
					int[][] aux=new int[h][w];
					for(int i=0;i<aux.length;i++){
						for(int j=0;j<aux[0].length;j++){
							System.out.println("Introduzca un número:");				
							aux[i][j]=leer.nextInt(); leer.nextLine();
						}
					}
					new sept1213_1(aux);
					break;
				default:
					System.out.println("Opción Introducida no válida,saliendo del programa...");
					return;
			}
		}
		
		ThreadPoolExecutor ejecutor=new ThreadPoolExecutor();
		if(h<nnuc){
			ejecutor.execute( new sept1213_1(0,h) );
		}else if( h%nnuc == 0 ){
			
		}else{
			
		}
	}
}
