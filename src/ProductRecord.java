public class ProductRecord {
	private int productID;
	private int quantity;
	
	public ProductRecord(int Id, int quantity){
		this.productID = Id;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getProductID() {
		return productID;
	}
	
	@Override
	public String toString(){
		return String.format("%03d%02d", productID, quantity);
	}
}