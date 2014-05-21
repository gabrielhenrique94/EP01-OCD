
public class PontoFlutuante {

	
	private static final int TAMANHO_MANTISSA = 23;
	private static final int TAMANHO_EXPOENTE = 8;
	
	private static final int POSITIVO=0;
	private static final int NEGATIVO=1;
	// 1 para negativo e 0 para positivo
	private int sinal;
	// � utilizado o TAMANHO_EXPOENTE+1 pois o inteiro utiliza um bit para sinal que � utilizado nesse ponto.
	private int[] expoente = new Inteiro(TAMANHO_EXPOENTE+1, 128).getNumberBits();
	private int[] mantissa = new int[TAMANHO_MANTISSA];

	
	public PontoFlutuante(int numero) {
		inicializaPontoFlutuante(new Inteiro(numero), Inteiro.ZERO);
	}
	
	public PontoFlutuante(Inteiro i) {
		inicializaPontoFlutuante(i, Inteiro.ZERO);
	}
	
	public PontoFlutuante(Inteiro i, Inteiro resto) {
		inicializaPontoFlutuante(i, resto);
	}

	private void inicializaPontoFlutuante(Inteiro i, Inteiro resto) {
		this.sinal = i.isNegativo() ? NEGATIVO : POSITIVO;
		int[] mantissaAux = setBitsInArray(somaBits(i.getNumberBits(), resto.getNumberBits()), TAMANHO_MANTISSA*2);
		expoente = Inteiro.somaSimples(expoente, i.getNumberLength());
		while (mantissaAux[mantissaAux.length-1] == 0)
			mantissaAux = Inteiro.leftShift(mantissaAux);
		mantissaAux = removePrimeiroUmEsq(mantissaAux);
		mantissa = setNewBitsPelaEsq(mantissaAux, TAMANHO_MANTISSA);
	}
	
	/**
	 * Copia um vetor de bits para outro de tamanho diferente
	 * @param array vetor de bits a ser transposto.
	 * @param tamanho tamanho do novo vetor de bits
	 * @return novo vetor de bits com tamanho passado por par�metro
	 */
	private static int[] setBitsInArray(int[] array, int tamanho) {
		int[] dest = new int[tamanho];
		for(int i = 0; i < dest.length && i < array.length; i++) {
			// atribui bits novos
			dest[i] = array[i];
		}
		return dest;
	}
	
	/**
	 * Substitui o numero binario antigo por um novo conservando de forma que o bit mais a direita tamb�m fique a direita nesse vetor de bits
	 * @param bits bits antigos
	 * @param tamanho tamanho do novo vetor
	 * @return novo vetor com bits a esquerda para mantissa
	 */
	public int[] setNewBitsPelaEsq(int[] bits, int tamanho) {
		// zera bits antigos
		int[] novosBits = new int[tamanho];
		for(int i = novosBits.length-1; i >= 0 && (bits.length-(novosBits.length - i)-1) >= 0; i--) {
			// atribui bits novos
			novosBits[i] = bits[bits.length-(novosBits.length - (i + 1)) - 1];
		}
		return novosBits;
	}
	
	private static int[] removePrimeiroUmEsq(int[] i) {
		return Inteiro.leftShift(i);
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
//	 * Realiza a normaliza��o da entrada junto a devidas correcoes decorrentes no expoente
//	 */
//	private static void normalizaEntrada(Inteiro mantissa, Inteiro expoente) {
//		
//	}

	
	public static void main(String[] args) {
		PontoFlutuante a = new PontoFlutuante(23);
		System.out.println(a.getNumeroDecimal());
	}
	
	public int getNumeroDecimal() {
		int[] mantissaAux = new int[TAMANHO_MANTISSA+1];
		int[] bitsMantissa = mantissa;
		int[] mantissaReal = new int[TAMANHO_MANTISSA+1];
		for (int i=0; i<bitsMantissa.length; i++) {
			mantissaReal[i] = bitsMantissa[i];
		}
		mantissaReal[mantissaReal.length-1] = 1;
		mantissaAux = setBitsInArray(mantissaReal, TAMANHO_MANTISSA+1);
		double numeroFloat = Inteiro.toSimpleInteger(mantissaAux) * Math.pow(2, getExpoente()-TAMANHO_MANTISSA-1);
		return (int) (sinal==POSITIVO ? numeroFloat : -numeroFloat);
	}
	
	/**
	 * Retorna o expoente do float com 128 já subtraido.
	 * @return Expoente do float com 128 já subtraido.
	 */
	public int getExpoente() {
		int[] clone = expoente.clone();
		return Inteiro.somaSimplesDecimal(clone, -128);
	}
	
	
}
