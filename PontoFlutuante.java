
public class PontoFlutuante {

	
	private static final int TAMANHO_MANTISSA = 23;
	private static final int TAMANHO_EXPOENTE = 8;
	
	// true para positivo e false para negativo
	private boolean sinal;
	private Inteiro expoente = new Inteiro(TAMANHO_EXPOENTE, 127);
	private Inteiro mantissa = new Inteiro(TAMANHO_MANTISSA, 0);

	
	public PontoFlutuante(int numero) {
		inicializaPontoFlutuante(new Inteiro(numero), Inteiro.ZERO, numero>= 0);
	}
	
	public PontoFlutuante(Inteiro i) {
		inicializaPontoFlutuante(i, Inteiro.ZERO, !i.isNegativo());
	}
	
	public PontoFlutuante(Inteiro i, Inteiro resto) {
		inicializaPontoFlutuante(i, resto, !i.isNegativo());
	}

	private void inicializaPontoFlutuante(Inteiro i, Inteiro resto, boolean sinal) {
		Inteiro mantissaAux = new Inteiro(TAMANHO_MANTISSA*2, 0);
		mantissaAux.setNewBits(somaBits(i.getBits(), resto.getBits()));
		if (i.isNegativo())
			sinal = false;
		expoente.soma(new Inteiro(i.getNumberLength()-1));
		mantissaAux.setNewBitsEsq(mantissaAux.getBits());
		removePrimeiroUmEsq(mantissaAux);
		mantissa.setNewBitsPelaEsq(mantissaAux.getBits());
	}
	
	private static void removePrimeiroUmEsq(Inteiro i) {
		i.Lshift();
	}
	
	private static int[] somaBits(int[] b1, int[] b2) {
		int[] bits = new int[b1.length + b2.length];
		for (int i=0; i<b2.length; i++) {
			bits[i] = b2[i];
		}
		for (int i=0; i<b1.length; i++) {
			bits[b2.length+i] = b1[i];
		}
		return bits;
	}

//	/**
//	 * Realiza a normalização da entrada junto a devidas correcoes decorrentes no expoente
//	 */
//	private static void normalizaEntrada(Inteiro mantissa, Inteiro expoente) {
//		
//	}

	
	public static void main(String[] args) {
		PontoFlutuante a = new PontoFlutuante(23);
		System.out.println(a.getNumeroDecimal());
	}
	
	public int getNumeroDecimal() {
		Inteiro mantissaAux = new Inteiro(TAMANHO_MANTISSA+1, 0);
		int[] bitsMantissa = mantissa.getBits();
		int[] mantissaReal = new int[TAMANHO_MANTISSA+1];
		for (int i=0; i<bitsMantissa.length; i++) {
			mantissaReal[i] = bitsMantissa[i];
		}
		mantissaReal[mantissaReal.length-1] = 1;
		mantissaAux.setNewBits(mantissaReal);
		double numeroFloat = mantissaAux.toInteger() * Math.pow(2, getExpoente().toInteger()-TAMANHO_MANTISSA);
		return (int) (sinal ? -numeroFloat : numeroFloat);
	}
	
	/**
	 * Retorna o expoente do float com 127 já somado.
	 * @return Expoente do float com 127 já somado.
	 */
	public Inteiro getExpoente() {
		Inteiro clone = expoente.clone();
		clone.soma(new Inteiro(clone.getNumberLength(), -127));
		return clone;
	}
	
	
}
