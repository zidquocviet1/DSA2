import java.io.*;
import java.util.*;

public class WarehouseBook {
	
	protected static class WarehouseNode {
		private ProductRecord record;
		private WarehouseNode left, right;
		private int balance;
		private int height;
		private int size;

		public  WarehouseNode(ProductRecord record){
			this.record = record;
			this.left = null;
			this.right = null;
			this.height = 1;
		}
		public ProductRecord getRecord() {
			return record;
		}

		public void setRecord(ProductRecord record) {
			this.record = record;
		}

		public WarehouseNode getLeft() {
			return left;
		}

		public void setLeft(WarehouseNode left) {
			this.left = left;
		}

		public WarehouseNode getRight() {
			return right;
		}

		public void setRight(WarehouseNode right) {
			this.right = right;
		}

		public int getBalance() {
			return balance;
		}
		public int getHeight() { return this.height; }

		public void setBalance(int balance) {
			this.balance = balance;
		}

		public void setHeight(int height){	this.height = height; }

		public void setSize(int size){ this.size = size; }
	}
	
	private WarehouseNode root;
	private int size;
	private StringBuilder output = new StringBuilder();

	public WarehouseBook(){
		root = null;
		size = 0;
	}
	
	public int getSize(){
		return size;
	}
	public WarehouseBook(File file) throws IOException{
	}
	
	public void save(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(this.toString());
		fw.close();
	}

	public void process(File file) throws IOException{
		List<String> events = new ArrayList<>();
		String[] event;

		BufferedReader br = new BufferedReader(new FileReader(file));
		String input;

		while ((input = br.readLine()) != null){
			event = input.split(" ");
			events.addAll(Arrays.asList(event));
		}
		br.close();

		process(events);
	}
	
	public void process(List<String> events){
		for (String item : events){
			char idEvent;
			int idProduct, quantity;

			if (item.equals("0")) break;

			idEvent = item.charAt(0);
			switch (idEvent){
				case '1':
					idProduct = Integer.parseInt(item.substring(1, 4));
					quantity = Integer.parseInt(String.valueOf(item.charAt(4)));

					addProduct(quantity, idProduct);
					break;
				case '2':
					idProduct = Integer.parseInt(item.substring(1, 4));
					quantity = Integer.parseInt(String.valueOf(item.charAt(4)));

					orderProduct(idProduct,quantity);
					break;
				case '3':
					StringBuilder a = new StringBuilder();
					if (root != null)
						root = simpleAVL_BST(LNR(root, a));
					break;
				case '4':
					List<String> nrl = new ArrayList<>();
					if (root != null)
						root = complexAVL_BST(NRL(root, nrl));
					break;
				case '5':
					idProduct = Integer.parseInt(item.substring(1, 4));
					quantity = Integer.parseInt(String.valueOf(item.charAt(4)));

					if (root != null) {
						List<String> rln = new ArrayList<>();
						rln = RLN(root, rln);
						root = specialTraversal(rln, idProduct, quantity);
					}
					break;
				default:
					quantity = Integer.parseInt(String.valueOf(item.charAt(1)));
					if (root != null)
						removeRedundantProduct(root, quantity);
					break;
			}
		}
	}
	@Override
	public String toString(){
		if (root != null) {
			output.append("(");
			treeToString(root);
			output.append(")");
		}
		return output.toString();
	}

	private int getHeight(WarehouseNode x){
		if (x == null) return 0;
		return x.getHeight();
	}

	private int checkBalance(WarehouseNode x){
		return this.getHeight(x.getLeft()) - this.getHeight(x.getRight());
	}

	private WarehouseNode rotateLeft(WarehouseNode x){
		WarehouseNode y = x.getRight();

		x.setRight(y.getLeft());
		y.setLeft(x);
		x.setHeight(1 + Math.max(this.getHeight(x.getLeft()), this.getHeight(x.getRight())));
		y.setHeight(1 + Math.max(this.getHeight(y.getLeft()), this.getHeight(y.getRight())));

		return y;
	}

	private WarehouseNode rotateRight(WarehouseNode x){
		WarehouseNode y = x.getLeft();

		x.setLeft(y.getRight());
		y.setRight(x);
		x.setHeight(1 + Math.max(this.getHeight(x.getLeft()), this.getHeight(x.getRight())));
		y.setHeight(1 + Math.max(this.getHeight(y.getLeft()), this.getHeight(y.getRight())));

		return y;
	}
	//this method to check the current node is balance or not
	private WarehouseNode makeBalance(WarehouseNode x){
		if (checkBalance(x) < -1){
			if (checkBalance(x.getRight()) > 0)
				x.setRight(rotateRight(x.getRight()));
			x = rotateLeft(x);
		}
		else if (checkBalance(x) > 1){
			if (checkBalance(x.getLeft()) < 0)
				x.setLeft(rotateLeft(x.getLeft()));
			x = rotateRight(x);
		}
		return x;
	}
	// the main method to generates method orderProduct
	private void orderProduct(int idProduct, int quantity){
		root = orderProduct(root, idProduct, quantity);
	}

	//this method to remove the number of product is ordered by customer
	private WarehouseNode orderProduct(WarehouseNode root, Integer idProduct, Integer quantity){
		if (root == null) return null;

		idProduct = findNode(root, idProduct, Integer.MAX_VALUE, 0);
		int cmp = idProduct.compareTo(root.getRecord().getProductID());
		int finalQuantity;

		if (cmp < 0)
			orderProduct(root.getLeft(), idProduct, quantity);
		else if (cmp > 0)
			orderProduct(root.getRight(), idProduct, quantity);
		else{
			finalQuantity = root.getRecord().getQuantity() - quantity;
			setQuantity(root, finalQuantity);
		}
		return root;
	}

	// this method is defined to find the product with the closet id
	private int findNode(WarehouseNode root, Integer idProduct, Integer min, Integer parent){
		if (root == null) return parent;

		int value = Math.abs(root.getRecord().getProductID() - idProduct);
		int cmp = idProduct.compareTo(root.getRecord().getProductID());

		if (value < min) {
		    min = value;
		    parent = root.getRecord().getProductID();
		}

		if (cmp < 0)
		    return findNode(root.getLeft(), idProduct, min, parent);
		else if (cmp > 0)
		    return findNode(root.getRight(), idProduct, min, parent);
		return parent;
	}

	// this method to set the quantity of product
	private void setQuantity(WarehouseNode root, Integer finalQuantity){
		if (finalQuantity <= 0)
			delete(root.getRecord().getProductID());
		else
			root.getRecord().setQuantity(finalQuantity);
	}
	private void addProduct(Integer quantity, Integer idProduct){
		root = addProduct(quantity, idProduct, root);
	}
	private WarehouseNode addProductAVL(Integer quantity, Integer idProduct, WarehouseNode root){
		if (root == null)
			return new WarehouseNode(new ProductRecord(idProduct, quantity));

		int cmp = idProduct.compareTo(root.getRecord().getProductID());
		if (cmp < 0)
			root.setLeft(addProductAVL(quantity, idProduct, root.getLeft()));
		else if (cmp > 0)
			root.setRight(addProductAVL(quantity, idProduct, root.getRight()));
		else
			root.getRecord().setQuantity(root.getRecord().getQuantity() + quantity);

		root.setHeight(1 + Math.max(getHeight(root.getLeft()), getHeight(root.getRight())));
		root = makeBalance(root);
		return root;
	}
	private WarehouseNode addProduct(Integer quantity, Integer idProduct, WarehouseNode root){
		if (root == null)
			return new WarehouseNode(new ProductRecord(idProduct, quantity));

		int cmp = idProduct.compareTo(root.getRecord().getProductID());
		if (cmp < 0)
			root.setLeft(addProduct(quantity, idProduct, root.getLeft()));
		else if (cmp > 0)
			root.setRight(addProduct(quantity, idProduct, root.getRight()));
		else
			root.getRecord().setQuantity(root.getRecord().getQuantity() + quantity);
		root.setHeight(1 + Math.max(getHeight(root.getLeft()), getHeight(root.getRight())));
		return root;
	}

	private void delete(Integer idProduct){
		root = deleteProduct(root, idProduct);
	}
	private WarehouseNode deleteProduct(WarehouseNode root, Integer idProduct){
		if (root == null) return null;

		int cmp = idProduct.compareTo(root.getRecord().getProductID());
		if (cmp < 0)
			root.setLeft(deleteProduct(root.getLeft(), idProduct));
		else if (cmp > 0)
			root.setRight(deleteProduct(root.getRight(), idProduct));
		else{
			if (root.getRight() == null) return root.getLeft();
			if (root.getLeft() == null) return root.getRight();

			root.setRecord(minValue(root.getRight()));
			root.setRight(deleteProduct(root.getRight(), root.getRecord().getProductID()));
		}
		return root;
	}

	private ProductRecord minValue(WarehouseNode root) {
		ProductRecord minv = root.getRecord();
		while (root.left != null) {
			minv = root.getLeft().getRecord();
			root = root.left;
		}
		return minv;
	}

	// region tree traversal
	private List<String> RLN(WarehouseNode root, List<String> output){
		if (root == null) return null;
		else{
			RLN(root.getRight(), output);
			RLN(root.getLeft(), output);

			String quantity = String.valueOf(root.getRecord().getQuantity());
			quantity = quantity.length() == 1 ? "0"+quantity : quantity;
			output.add(root.getRecord().getProductID() + "" + quantity);
		}

		return output;
	}
	private List<String> NRL(WarehouseNode root, List<String> output){
		if (root == null) return null;
		else{
			String quantity = String.valueOf(root.getRecord().getQuantity());
			quantity = quantity.length() == 1 ? "0"+quantity : quantity;
			output.add(root.getRecord().getProductID() + "" + quantity);

			NRL(root.getRight(), output);
			NRL(root.getLeft(), output);
		}
		return output;
	}
	private String LNR(WarehouseNode root, StringBuilder output){
		if (root == null) return "";

		else{
			LNR(root.getLeft(), output);

			String quantity = String.valueOf(root.getRecord().getQuantity());
			quantity = quantity.length() == 1 ? "0"+quantity : quantity;
			output.append(root.getRecord().getProductID()).append(quantity).append(" ");

			LNR(root.getRight(), output);
		}

		return output.toString();
	}
	private WarehouseNode simpleAVL_BST(String Inorder){
		String[] temp = Inorder.split(" ");
		List<Integer> infor;
		int size = temp.length;
		infor = getInforProduct(temp[size/2]);
		int idProduct = infor.get(0);
		int quantity = infor.get(1);

		ProductRecord pr = new ProductRecord(idProduct, quantity);
		WarehouseNode root = new WarehouseNode(pr);

		for (int i = 0; i < size; i++){
			if (i != size / 2) {
				infor = getInforProduct(temp[i]);
				idProduct = infor.get(0);
				quantity = infor.get(1);

				root = addProduct(quantity, idProduct, root);
			}
		}
		return root;
	}
	private WarehouseNode complexAVL_BST(List<String> NRL){
		List<Integer> infor;
		int idProduct, quantity;

		ProductRecord pr = new ProductRecord(getInforProduct(NRL.get(0)).get(0), getInforProduct(NRL.get(0)).get(1));
		WarehouseNode root = new WarehouseNode(pr);

		root = addProduct(getInforProduct(NRL.get(1)).get(1), getInforProduct(NRL.get(1)).get(0), root);

		for (int i = 2; i < NRL.size(); i++){
			infor = getInforProduct(NRL.get(i));
			idProduct = infor.get(0);
			quantity = infor.get(1);

			root = addProductAVL(quantity, idProduct, root);
		}
		return root;
	}
	private WarehouseNode specialTraversal(List<String> RLN, Integer idProduct, Integer quantity){
		Map<Integer, Integer> product = new HashMap<>();
		List<Integer> infor;
		int stockQuantity = quantity;

		for (String s : RLN){
			infor = getInforProduct(s);
			product.put(infor.get(0), infor.get(1));
		}

		if (product.containsKey(idProduct)){
			stockQuantity += product.get(idProduct);

			String productQuantity = String.valueOf(product.get(idProduct));
			productQuantity = productQuantity.length() == 1 ? "0"+productQuantity : productQuantity;
			RLN.remove(idProduct + "" + productQuantity);
		}

		ProductRecord pr = new ProductRecord(idProduct, stockQuantity);
		WarehouseNode root = new WarehouseNode(pr);

		for (String item : RLN){
			infor = getInforProduct(item);
			root = addProduct(infor.get(1), infor.get(0), root);
		}
		return root;
	}
	//this method to remove the redundant products in warehouse
	private void removeRedundantProduct(WarehouseNode root, Integer depth){
		Map<Integer, Integer> listDepth = new HashMap<>();
		listDepth = getLevel(root, 1, listDepth);

		for (Map.Entry<Integer, Integer> d : listDepth.entrySet()){
			if (d.getValue() >= depth)
				root = deleteProduct(root, d.getKey());
		}
	}

	private Map<Integer, Integer> getLevel(WarehouseNode root, Integer level, Map<Integer, Integer> listDepth){
		if (root == null) return null;
		else
			listDepth.put(root.getRecord().getProductID(), level);

		getLevel(root.getLeft(), level + 1, listDepth);
		getLevel(root.getRight(), level + 1, listDepth);

		return listDepth;
	}

	private List<Integer> getInforProduct(String product){
		List<Integer> infor = new ArrayList<>();
		StringBuilder idProduct = new StringBuilder();
		int size = product.length();

		for (int i = 0; i < size - 2; i++){
			idProduct.append(product.charAt(i));
		}

		infor.add(Integer.parseInt(idProduct.toString()));
		infor.add(Integer.parseInt(product.charAt(size-2)+""+product.charAt(size-1)));

		return infor;
	}
	// convert tree to string
	private void treeToString(WarehouseNode root) {
		if (root == null) return;

		String quantity = String.valueOf(root.getRecord().getQuantity());
		String idProduct = String.valueOf(root.getRecord().getProductID());

		quantity = quantity.length() == 1 ? "0"+quantity : quantity;
		while (idProduct.length() != 3)
			idProduct = "0"+idProduct;

		output.append(idProduct + "" + quantity);

		if (root.getLeft() == null && root.getRight() == null) return;

		output.append(" (");
		if (root.getLeft() == null)
			output.append("N");
		treeToString(root.getLeft());

		if (root.getRight() == null)
			output.append(" N)");

		if (root.getRight() != null){
			output.append(" ");
			treeToString(root.getRight());
			output.append(")");
		}
	}
	// end region
	public static void main(String[] args){
		try{
			WarehouseBook wb = new WarehouseBook(new File("warehouse.txt"));
			wb.process(new File("events.txt"));
			wb.save(new File("warehouse_new.txt"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
