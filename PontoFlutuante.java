public class PontoFlutuante {

	private static final int TAMANHO_MANTISSA = 23;
	private static final int TAMANHO_EXPOENTE = 8;

	private static final int POSITIVO = 0;
	private static final int NEGATIVO = 1;

	private static final boolean MULTIPLICACAO = true;
	private static final boolean DIVISAO = false;

	// 1 para negativo e 0 para positivo
	private int sinal;
	// � utilizado o TAMANHO_EXPOENTE+1 pois o inteiro utiliza um bit para sinal
	// que � utilizado nesse ponto.
	private int[] expoente = new Inteiro(TAMANHO_EXPOENTE + 1, 128).getNumberBits();
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
		int[] mantissaAux = setBitsInArray(
				somaBits(i.getNumberBits(), resto.getNumberBits()),
				TAMANHO_MANTISSA * 2);
		expoente = Inteiro.somaSimples(expoente, i.getNumberLength());
		while (mantissaAux[mantissaAux.length - 1] == 0)
			mantissaAux = Inteiro.leftShift(mantissaAux);
		mantissaAux = removePrimeiroUmEsq(mantissaAux);
		mantissa = setNewBitsPelaEsq(mantissaAux, TAMANHO_MANTISSA);
	}

	/**
	 * Copia um vetor de bits para outro de tamanho diferente
	 * @param array vetor de bits a ser transposto.
	 * @param tamanho tamanho do novo vetor de bits
	 * @return novo vetor de bits com tamanho passado por parâmetro
	 */
	private static int[] setBitsInArray(int[] array, int tamanho) {
		int[] dest = new int[tamanho];
		for (int i = 0; i < dest.length && i < array.length; i++) {
			// atribui bits novos
			dest[i] = array[i];
		}
		return dest;
	}

	/**
	 * Substitui o numero binario antigo por um novo conservando de forma que o
	 * bit mais a direita também fique a direita nesse vetor de bits
	 * @param bits bits antigos
	 * @param tamanho tamanho do novo vetor
	 * @return novo vetor com bits a esquerda para mantissa
	 */
	public int[] setNewBitsPelaEsq(int[] bits, int tamanho) {
		// zera bits antigos
		int[] novosBits = new int[tamanho];
		for (int i = novosBits.length - 1; i >= 0
				&& (bits.length - (novosBits.length - i) - 1) >= 0; i--) {
			// atribui bits novos
			novosBits[i] = bits[bits.length - (novosBits.length - (i + 1)) - 1];
		}
		return novosBits;
	}

	private static int[] removePrimeiroUmEsq(int[] i) {
		return Inteiro.leftShift(i);
	}

	private static int[] somaBits(int[] b1, int[] b2) {
		int[] bits = new int[b1.length + b2.length];
		for (int i = 0; i < b2.length; i++) {
			bits[i] = b2[i];
		}
		for (int i = 0; i < b1.length; i++) {
			bits[b2.length + i] = b1[i];
		}
		return bits;
	}

	// /**
	// * Realiza a normalização da entrada junto a devidas correcoes decorrentes no expoente */
	// private static void normalizaEntrada(Inteiro mantissa, Inteiro expoente){
	//
	// }

	public static void main(String[] args) {
		PontoFlutuante a = new PontoFlutuante(23);
		System.out.println(a.getNumeroDecimal());
	}

	public int getNumeroDecimal() {
		int[] mantissaAux = new int[TAMANHO_MANTISSA + 1];
		int[] bitsMantissa = mantissa;
		int[] mantissaReal = new int[TAMANHO_MANTISSA + 1];
		for (int i = 0; i < bitsMantissa.length; i++) {
			mantissaReal[i] = bitsMantissa[i];
		}
		mantissaReal[mantissaReal.length - 1] = 1;
		mantissaAux = setBitsInArray(mantissaReal, TAMANHO_MANTISSA + 1);
		double numeroFloat = Inteiro.toSimpleInteger(mantissaAux)
				* Math.pow(2, getExpoente() - TAMANHO_MANTISSA - 1);
		return (int) (sinal == POSITIVO ? numeroFloat : -numeroFloat);
	}

	/**
	 * Retorna o expoente do float com 128 já subtraido.
	 * @return Expoente do float com 128 já subtraido.
	 */
	public int getExpoente() {
		int[] clone = expoente.clone();
		return Inteiro.somaSimplesDecimal(clone, -128);
	}

	/**
	 * Realiza a multiplicação de dois pontos flutuantes
	 * @return Objeto PontoFlutuante com resultado da multiplicação
	 */
	public static PontoFlutuante multiplicaFlutuante(PontoFlutuante flut1,
			PontoFlutuante flut2) {
		PontoFlutuante resultado = new PontoFlutuante(0);

		// Ajusta sinal do ponto flutuante
		resultado.sinal = (flut1.sinal == flut2.sinal) ? POSITIVO : NEGATIVO;
		
		int[] mantissaAjustada1 = new int[TAMANHO_MANTISSA + 1];
		int[] mantissaAjustada2 = new int[TAMANHO_MANTISSA + 1];
		mantissaAjustada1 = addBitEsqMantissa(flut1.mantissa);
		mantissaAjustada2 = addBitEsqMantissa(flut2.mantissa);

		// Se um dos pontos flutuantes for 1 o resultado será o outro ponto flutuante
		if (flutIsUm(flut1, mantissaAjustada1)) {
			resultado.expoente = flut2.expoente;
			resultado.mantissa = flut2.mantissa;
			return resultado;
		}
		if (flutIsUm(flut2, mantissaAjustada2)) {
			resultado.expoente = flut1.expoente;
			resultado.mantissa = flut1.mantissa;
			return resultado;
		}
		
		// Confere Underflow, Overflow, soma expoentes e subtrai excesso
		resultado.expoente = conferirSomar(flut1, flut2, MULTIPLICACAO);
		
		boolean estaDesnormalizado = true;
		
		while (estaDesnormalizado) {
			// Multiplica
			int[] mantissaTemp = multiplicaMantissa(mantissaAjustada1,
					mantissaAjustada2);

			// Normalizando o produto
			int localInicio = mantissaTemp.length - 1;
			for (int i = mantissaTemp.length - 1; i >= 0; i--) {
				if (mantissaTemp[i] == 1) {
					localInicio = i;
					break;
				}
			}
			
			// Estado normalizado
			if (localInicio <= mantissaTemp.length / 2) {
				for (int i = 0; i < localInicio; i++) {
					resultado.mantissa[i] = mantissaTemp[i];
				}
				estaDesnormalizado = false;
			}
			
			// Produto é maior, precisa dividir mantissa e somar expoente
			if (localInicio > mantissaTemp.length / 2) {
				int[] divisor = Inteiro.Inteiro(2);
				mantissaTemp = divideMantissa(mantissaTemp, divisor);
				PontoFlutuante parc = new PontoFlutuante(1);
				conferirSomar(resultado, parc, MULTIPLICACAO);
			}
		}

		return resultado;
	}

	private static int[] multiplicaMantissa(int[] multiplicando, int[] multiplicador) {

		int tamanho = Inteiro.max(multiplicando.length, multiplicador.length);
		multiplicador = Inteiro.copiaBinario(tamanho, multiplicador);
		multiplicando = Inteiro.copiaBinario(tamanho, multiplicando);

		int anterior = 0;
		int tamanhoProduto = multiplicador.length * 2;
		int[] produto = Inteiro.soma(tamanhoProduto, new int[tamanhoProduto],
				multiplicador);

		for (int x = 0; x < multiplicador.length; x++) {
			int atual = multiplicador[x];
			if (atual == 1 && anterior == 0) {
				produto = Inteiro.subtraiMetadeEsq(multiplicando, produto);
			} else if (atual == 0 && anterior == 1) {
				produto = Inteiro.somaMetadeEsq(multiplicando, produto);
			}
			produto = Inteiro.rightShift(produto);
			anterior = atual;
		}

		return produto;
	}

	/**
	 * Realiza a divisão de dois pontos flutuantes
	 * @return Objeto PontoFlutuante com resultado da resultado
	 */
	public static PontoFlutuante divideFlutuante(PontoFlutuante dividendo,
			PontoFlutuante divisor) {
		PontoFlutuante resultado = new PontoFlutuante(0);

		// Ajusta sinal do ponto flutuante
		resultado.sinal = (dividendo.sinal == divisor.sinal) ? POSITIVO : NEGATIVO;

		int[] mantAjustDividendo = new int[TAMANHO_MANTISSA + 1];
		int[] mantAjustDivisor = new int[TAMANHO_MANTISSA + 1];
		mantAjustDividendo = addBitEsqMantissa(dividendo.mantissa);
		mantAjustDivisor = addBitEsqMantissa(divisor.mantissa);

		// Se divisor for 1 o resultado será o outro ponto flutuante
		if (flutIsUm(divisor, mantAjustDivisor)) {
			resultado.expoente = dividendo.expoente;
			resultado.mantissa = dividendo.mantissa;
			return resultado;
		}	 

		// Confere Underflow, Overflow, subtrai expoentes e soma excesso
		resultado.expoente = conferirSubtrair(dividendo, divisor, DIVISAO);
		
		boolean estaDesnormalizado = true;

		while (estaDesnormalizado) {
			// Divide
			int[] mantissaTemp = divideMantissa(mantAjustDividendo, mantAjustDivisor);

			// Normalizando o quociente
			int localInicio = mantissaTemp.length - 1;
			for (int i = mantissaTemp.length - 1; i >= 0; i--) {
				if (mantissaTemp[i] == 1) {
					localInicio = i;
					break;
				}
			}
			// Estado normalizado
			if (localInicio <= mantissaTemp.length / 2) {
				for (int i = 0; i < localInicio; i++) {
					resultado.mantissa[i] = mantissaTemp[i];
				}
				estaDesnormalizado = false;
			}
			// Produto é maior, precisa dividir mantissa e somar expoente
			if (localInicio > mantissaTemp.length / 2) {
				int[] divisorNorm = Inteiro.Inteiro(2);
				mantissaTemp = divideMantissa(mantissaTemp, divisorNorm);
				PontoFlutuante parc = new PontoFlutuante(1);
				conferirSomar(resultado, parc, MULTIPLICACAO);
			}
		}
		return resultado;

	}

	private static int[] divideMantissa(int[] dividendo, int[] divisor) {

		int tamanhoAux = dividendo.length * 2;

		int[] dividendoAux = Inteiro.copiaBinario(tamanhoAux, dividendo);
		int[] divisorAux = Inteiro.novaMetadeEsq(new int[tamanhoAux], divisor);

		for (int i = 0; i < dividendo.length; i++) {
			dividendoAux = Inteiro.leftShift(dividendoAux);
			dividendoAux = Inteiro
					.subtrai(tamanhoAux, dividendoAux, divisorAux);
			// Verifica se o resultado da subtracao mudou o sinal do dividendo
			if (dividendoAux[tamanhoAux - 1] != (dividendo[dividendo.length - 1])) {
				// Se o sinal mudou, volta ao que estava antes
				dividendoAux = Inteiro.soma(tamanhoAux, dividendoAux,
						divisorAux);
				dividendoAux[0] = 0;
			} else
				dividendoAux[0] = 1;
		}
		int[] quociente = Inteiro.getMetadeDir(dividendoAux);
		return quociente;
	}

	private static boolean flutIsUm(PontoFlutuante flutuante, int[] mantAjustada) {
		if (flutuante.getExpoente() == 0) {
			return true;
		}
		if (mantissaIsUm(mantAjustada)) {
			if (flutuante.sinal == 0) {
				return true;
			}
			if (flutuante.sinal == 1 && (flutuante.getExpoente() % 2 == 0)) {
				return true;
			}
		}
		return false;
	}

	private static boolean mantissaIsUm(int[] mantissa) {
		int cont = -1;
		for (int i = 0; i < mantissa.length; i++) {
			if (mantissa[i] == 1)
				cont = i;
		}
		if (cont == 0)
			return true;
		return false;
	}

	private boolean isOverflow(int[] expoente) {
		int cont = 0;
		int i = 0;
		while (cont < TAMANHO_EXPOENTE + 1) {
			if (i == expoente.length)
				return false;
			if (expoente[i] == 1)
				cont = i + 1;
			i++;
		}
		return true;
	}

	private static int[] addBitEsqMantissa(int[] mantissa) {
		int[] resp = new int[TAMANHO_MANTISSA + 1];
		for (int i = 0; i < mantissa.length; i++) {
			resp[i] = mantissa[i];
		}
		resp[TAMANHO_MANTISSA] = 1;
		return resp;
	}

	public void dividirPorZero() {
		System.err.printf("Operação ilegal: Divisão por zero.");
		System.exit(1);
	}

	private static void confereUnderflow(PontoFlutuante flut1, PontoFlutuante flut2,
			boolean operacao) {

		int[] expoenteAux = new int[TAMANHO_EXPOENTE * 2];
		int[] expoenteFlutAux = new int[TAMANHO_EXPOENTE * 2];
		expoenteFlutAux = Inteiro.soma(expoenteFlutAux.length, flut2.expoente,
				Inteiro.Inteiro(128));
		int cont = 0;

		if (MULTIPLICACAO) { // é multiplicação?
			expoenteAux = Inteiro.soma(expoenteAux.length, flut1.expoente,
					expoenteFlutAux);
			for (int i = expoenteAux.length - 1; i >= 0; i++) {
				if (expoenteAux[i] == 1) {
					cont = i;
					break;
				}
			}
			if (cont == 0) {
				System.err.printf("Underflow: O expoente não pode ser representado em %d bits",
								TAMANHO_EXPOENTE);
				System.exit(1);
			}
		} else {
			expoenteAux = Inteiro.subtrai(expoenteAux.length, flut1.expoente,
					expoenteFlutAux);
			for (int i = expoenteAux.length - 1; i >= 0; i++) {
				if (expoenteAux[i] == 1) {
					cont = i;
					break;
				}
			}
			if (cont == 0) {
				System.err.printf("Underflow: O expoente não pode ser representado em %d bits",
								TAMANHO_EXPOENTE);
				System.exit(1);
			}
		}
	}

	private static void confereOverflow(PontoFlutuante flut1, PontoFlutuante flut2,
			boolean operacao) {

		int[] expoenteAux = new int[TAMANHO_EXPOENTE * 2];
		int cont = 0;

		if (MULTIPLICACAO) { // é multiplicação?
			expoenteAux = Inteiro.soma(expoenteAux.length, flut1.expoente,
					flut2.expoente);
			for (int i = expoenteAux.length - 1; i >= 0; i++) {
				if (expoenteAux[i] == 1) {
					cont = i;
					break;
				}
			}
			if (cont >= TAMANHO_EXPOENTE) {
				System.err.printf("Overflow: O expoente não pode ser representado em %d bits",
								TAMANHO_EXPOENTE);
				System.exit(1);
			}
		} else {
			expoenteAux = Inteiro.subtrai(expoenteAux.length, flut1.expoente,
					flut2.expoente);
			if (cont >= TAMANHO_EXPOENTE) {
				System.err.printf("Overflow: O expoente não pode ser representado em %d bits",
								TAMANHO_EXPOENTE);
				System.exit(1);
			}
		}
	}
	
	private static int[] conferirSomar(PontoFlutuante flut1, PontoFlutuante flut2, Boolean operador){
		
		// Confere Underflow e Overflow
		confereOverflow(flut1, flut2, operador);
		confereUnderflow(flut1, flut2, operador);

		// Soma os expoentes
		int[] expoenteTemp = Inteiro.somaSimples(TAMANHO_EXPOENTE,
				flut1.expoente, flut2.expoente);
		
		// Subtrai o excesso
		int[] expoente = Inteiro.somaSimples(expoenteTemp, -128);
		return expoente;
	}

	
	private static int[] conferirSubtrair(PontoFlutuante dividendo, PontoFlutuante divisor, Boolean operador){

		// Confere Underflow e Overflow
		confereOverflow(dividendo, divisor, operador);
		confereUnderflow(dividendo, divisor, operador);

		// Subtrai os expoentes
		int[] expoenteTemp = Inteiro.subtrai(TAMANHO_EXPOENTE,
				dividendo.expoente, divisor.expoente);

		// Soma o excesso
		int[] expoente = Inteiro.somaSimples(expoenteTemp, 128);
		return expoente;
	}
}
