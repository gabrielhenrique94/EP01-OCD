
public class PontoFlutuante {

	private static final int TAMANHO_MANTISSA = 23;
	private static final int TAMANHO_EXPOENTE = 8;
	
	// true para positivo e false para negativo
	private boolean sinal;
	private Inteiro mantissa;
	private Inteiro expoente;
	

	
	public PontoFlutuante(int numero) {
		new Inteiro(numero).getBits();
	}
	
	
	
}
