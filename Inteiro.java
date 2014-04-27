class Inteiro{
	
	protected int[] bits;
	
	/**
	* construtor que incializa o interio com um valor pre-definido
	* @param valor inicial que o numero recebera
	*/
	Inteiro(int tamanho, int numero){
		this.bits = this.toBinario(tamanho, numero);
	}

	Inteiro(int tamanho, int[] numero){
		this.bits = new int[tamanho];
		for(int i = 0 ; i< tamanho; i++){
			if(i == numero.length)
				this.bits[i] = 0;
			else if(numero[i] == 0 || numero[i] == 1)
				this.bits[i] = numero[i];
			else
				System.err.print("Inteiro@contrutor: vetor contem numeros que não são 0 ou 1");
		}
	}

	public void Rshift(){
		int aux;
		for(int i = 0; i <	(this.bits.length - 1);i++){
			this.bits[i+1] = this.bits[i];
		}
	}

	public void Lshift(){
		int aux;
		for(int i = 0; i <	(this.bits.length - 1);i++){
			this.bits[i] = this.bits[i+1];
		}
	}

	public int[] toBinario(int tamanho,int numero){
		int binario[] = new int[tamanho];
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
		return binario;
	}

	public String toString(){
		String result = "";
		for(int i = 0; i < this.bits.length ; i++){
			result = result + String.valueOf(this.bits[this.bits.length-i-1]);
		}
		return result;
	}
	public int toInteger(){
		int p2 = 1;
		int result = 0;
		for(int i = 0 ; i < this.bits.length; i++){
			result += this.bits[i] * p2;
			p2 *= 2;
		}
		return result;
	}
	public static void main(String[] args){
		Inteiro i = new Inteiro(32,512123);
		System.out.println(i);
		System.out.println(i.toInteger());
	}
}