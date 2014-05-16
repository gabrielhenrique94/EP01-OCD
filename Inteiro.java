class Inteiro{
	
	// a posição 0 corresponde ao digito mais a direita de um numero
	protected int[] bits;
	// true para numero negativo, false para numero positivo
	private boolean negativo;
	
	/**
	* construtor que incializa o interio com um valor pre-definido
	* @param valor inicial que o numero recebera
	*/
	public Inteiro(int tamanho, int numero){
		this.bits = this.toBinario(tamanho, numero);
	}
	
	public Inteiro(int numero) {
		this.bits = this.toBinario((numero+"").length(), numero);
	}
//
//	private Inteiro (int tamanho){
//		//aqui eu me aproveito do fato do java inicializar tudo com 0
//		this.bits = new int[tamanho];
//	}

	private Inteiro(int tamanho, int[] numero){
		this.bits = new int[tamanho];
		for(int i = 0 ; i < tamanho; i++){
			if(i >= numero.length)
				this.bits[i] = 0;
			else if(numero[i] == 0 || numero[i] == 1)
				this.bits[i] = numero[i];
			else{
				System.err.println("Inteiro.contrutor: vetor contem numeros que nao sao 0 ou 1");
				break;
			}
		}
	}

	public int max(int a, int b){
		return (a > b )? a : b;
	}

	public Inteiro complementoDe2(){
		return new Inteiro(this.bits.length, complementoDe2(this.bits));
	}
	
	private static int[] complementoDe2(int[] bits) {
		int[] otherBits = new int[bits.length];
		for(int a = 0; a < bits.length; a++)
			otherBits[a] = (bits[a] == 0)? 1 : 0;
		int[] soma1 = {1};
		return soma(otherBits.length,otherBits, soma1);
	}


	public static Inteiro subtrai(int tamanho, Inteiro int1, Inteiro int2){
		return soma(tamanho, int1, int2.complementoDe2());
	}
	
	public static int[] subtrai(int tamanho, int[] int1, int[] int2){
		return soma(tamanho, int1, new Inteiro(int2.length, int2).complementoDe2().bits);
	}

	public static Inteiro soma(int tamanho,Inteiro int1, Inteiro int2){
		return new Inteiro(tamanho,soma(tamanho, int1.bits, int2.bits));
	}
	
	public static int[] soma(int tamanho,int[] int1, int[] int2){
		int[] soma = new int[tamanho];
		boolean vem = false;//0
		boolean sum ;
		for(int i = 0 ; i < tamanho; i++){
			boolean B1 = (i < int1.length) ? (int1[i] == 1) : false;
			boolean B2 = (i < int2.length) ? int2[i] == 1 : false;
			vem = (B2 && B1) || (B2&&vem) || (B1&&vem);
			sum = (B2 ^ B1 ^ vem) || (B1&&B2&&vem);
			soma[i] = (sum)?1:0;
		}
		return soma;
	}


	public void Rshift(){
		this.bits = rightShift(this.bits);
	}

	public void Lshift(){
		this.bits = leftShift(this.bits);
	}
	
	public static int[] rightShift(int[] bits) {
		for(int i = 0; i < (bits.length - 1); i++)
			bits[i] = bits[i+1];
		if (bits[bits.length - 2] == 0)
			bits[bits.length - 1] = 0;
		else
			bits[bits.length - 1] = 1;
		return bits;
	}
	
	public static int[] leftShift(int[] bits) {
		for(int i = (bits.length - 1); i >0; i--)
			bits[i] = bits[i-1];
		bits[0] = 0;
		return bits;
	}
	
	/**
	 * Realiza a multiplicação de dois números binários através do algoritmo de Booth
	 * @param multiplicando
	 * @param multiplicador
	 * @return Objeto Inteiro com resultado da multiplicação
	 */
	public static Inteiro multiplica(Inteiro multiplicando, Inteiro multiplicador) {
		int anterior = 0;
		int[] multiplicadorBits = multiplicador.getBits();
		int multiplicadorLenghBits = multiplicador.getLenghofBits();
		int tamanhoProduto = multiplicadorLenghBits*2;
		int[] produto = soma(tamanhoProduto, new int[tamanhoProduto], multiplicadorBits);
		for (int x=0; x < multiplicadorLenghBits; x++) {
			int atual = multiplicadorBits[x];
			if (atual == 1 && anterior == 0) {
				produto = subtraiMetadeEsq(multiplicando.getBits(), produto);
			} else if (atual == 0 && anterior == 1){
				produto = somaMetadeEsq(multiplicando.getBits(), produto);
			}
			produto = rightShift(produto);
			anterior = atual;
		}
		return new Inteiro(tamanhoProduto, produto);
	}
	

	
	private static int[] somaMetadeEsq(int[] bitsMultiplicando, int[] produto) {
		if (produto.length  % 2 == 0) {
			int[] metadeEsq = getMetadeEsq(produto); 
			int[] metadeEsqSomada = soma(metadeEsq.length, bitsMultiplicando, metadeEsq);
			produto = novaMetadeEsq(produto, metadeEsqSomada);
		}
		return produto;
	}
	
	private static int[] subtraiMetadeEsq(int[] bitsMultiplicando, int[] produto) {
		if (produto.length % 2 == 0) {
			int[] metadeEsq = getMetadeEsq(produto); 
			int[] metadeEsqSubtraida = subtrai(metadeEsq.length, metadeEsq, bitsMultiplicando);
			produto = novaMetadeEsq(produto, metadeEsqSubtraida);
		}
		return produto;
	}
	/**
	 * Retorna a metade esquerda de um binario de 8 bits
	 */
	private static int[] getMetadeEsq(int[] bits) {
		int tamanhoBits = bits.length;
		int tamanhoMetadeEsq = tamanhoBits/2;
		int[] metadeEsq = new int[tamanhoMetadeEsq];
		for (int i=tamanhoMetadeEsq; i<tamanhoBits;i++) {
			metadeEsq[i-tamanhoMetadeEsq] = bits[i];
		}
		return metadeEsq;
	}
	
	/**
	 * Acrescenta a metade esquerda processada de volta a um binario de 8 bits
	 */
	private static int[] novaMetadeEsq(int[] bits, int[] metadeEsq) {
		int finalMetadeEsq = bits.length;
		int inicioMetadeEsq = finalMetadeEsq/2;
		for (int i=inicioMetadeEsq; i<finalMetadeEsq;i++) {
			bits[i] = metadeEsq[i-inicioMetadeEsq];
		}
		return bits;
	}
	
	public int[] getBits() {
		return bits.clone();
	}
	
	private int getLenghofBits() {
		return bits.length;
	}

	private int[] toBinario(int tamanho,int numero){
		int binario[] = new int[tamanho];
		this.negativo = false;
		if (numero<0) {
			negativo = true;
			numero *= -1;
		}
		int aux = numero;
		int pBinario = 0;
		while(aux >= 2){
			if(pBinario == tamanho) break;
			binario[pBinario] = aux%2;
			pBinario++;
			aux = aux/2;
		}
		if(aux == 1)
			if(pBinario < tamanho)
				binario[pBinario]  = 1;
		if (negativo)
			return complementoDe2(binario);
		return binario;
	}

	public String toString(){
		String result = "";
		for(int i = 0; i < this.bits.length ; i++)
			result = result + String.valueOf(this.bits[this.bits.length-i-1]);
		return result;
	}

	public int toInteger(){
		int p2 = 1;
		int result = 0;
		int[] bitsPraInt = this.bits;
		if (negativo)
			bitsPraInt = complementoDe2(bitsPraInt);
		for(int i = 0 ; i < this.bits.length; i++){
			result += bitsPraInt[i] * p2;
			p2 *= 2;
		}
		return result;
	}

	public static void main(String[] args){
		Inteiro i = new Inteiro(32, 2);
		Inteiro a = new Inteiro(32, 10);
		System.out.println(a);
		System.out.println(i.toInteger());
		Inteiro soma = Inteiro.soma(32, i,a);
		System.out.println(soma.toInteger());
		Inteiro subtracao = Inteiro.subtrai(32,i,a);
		System.out.println(multiplica(i, a).toInteger());
	}
}